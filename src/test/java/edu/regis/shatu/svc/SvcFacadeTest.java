package edu.regis.shatu.svc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

class SvcFacadeTest {

    @Test
    void nullReplyBecomesAnErrorReply() {
        // send() returns null when the tutor closes the socket without replying.
        TutorReply reply = SvcFacade.parseReply(null);

        assertNotNull(reply);
        assertEquals(":ERR", reply.getStatus());
        assertEquals(SvcFacade.NO_REPLY_MESSAGE, reply.getErrorMessage());
    }

    @Test
    void emptyReplyBecomesAnErrorReply() {
        TutorReply reply = SvcFacade.parseReply("");

        assertNotNull(reply);
        assertEquals(":ERR", reply.getStatus());
        assertEquals(SvcFacade.NO_REPLY_MESSAGE, reply.getErrorMessage());
    }

    @Test
    void malformedReplyBecomesAnErrorReply() {
        TutorReply reply = SvcFacade.parseReply("{\"status\":\"Authenticated\",\"data\":");

        assertNotNull(reply);
        assertEquals(":ERR", reply.getStatus());
        assertEquals(SvcFacade.BAD_REPLY_MESSAGE, reply.getErrorMessage());
    }

    @Test
    void jsonNullReplyBecomesAnErrorReply() {
        TutorReply reply = SvcFacade.parseReply("null");

        assertNotNull(reply);
        assertEquals(":ERR", reply.getStatus());
        assertEquals(SvcFacade.BAD_REPLY_MESSAGE, reply.getErrorMessage());
    }

    @Test
    void nullStatusBecomesAnErrorReply() {
        TutorReply reply = SvcFacade.parseReply("{\"status\":null,\"data\":\"error\"}");

        assertEquals(":ERR", reply.getStatus());
        assertEquals(SvcFacade.BAD_REPLY_MESSAGE, reply.getErrorMessage());
    }

    @Test
    void blankReplyBecomesAnErrorReply() {
        TutorReply reply = SvcFacade.parseReply("   \n");

        assertEquals(":ERR", reply.getStatus());
        assertEquals(SvcFacade.NO_REPLY_MESSAGE, reply.getErrorMessage());
    }

    @Test
    void normalReplyIsUnchanged() {
        Gson gson = new Gson();
        TutorReply sent = new TutorReply("Authenticated", "{\"userId\":\"test@regis.edu\"}");

        TutorReply received = SvcFacade.parseReply(gson.toJson(sent));

        assertEquals("Authenticated", received.getStatus());
        assertEquals("{\"userId\":\"test@regis.edu\"}", received.getData());
    }

    @Test
    void socketErrorFallbackStillParses() {
        // The reply send() builds itself when the socket throws an IOException.
        String fallback = "{'status':':ERR','data':'A non-recoverable error occurred in the socket connection (see logs)'}";

        TutorReply reply = SvcFacade.parseReply(fallback);

        assertEquals(":ERR", reply.getStatus());
        assertEquals("A non-recoverable error occurred in the socket connection (see logs)",
                     reply.getErrorMessage());
    }

    /** Text that must never show up in the console or the logs. */
    private static final String SECRET_TOKEN = "SECRET-TOKEN-8f3a";
    private static final String SECRET_PASSWORD_HASH = "SECRET-PASSWORD-HASH-77c1";
    private static final String SECRET_REPLY_DATA = "SECRET-REPLY-DATA-b942";

    /** Collects everything SvcFacade writes to its logger, at every level. */
    private static class CapturingHandler extends Handler {
        final StringBuilder text = new StringBuilder();

        @Override
        public void publish(LogRecord record) {
            text.append(record.getLevel()).append(' ')
                .append(record.getMessage()).append('\n');
        }

        @Override
        public void flush() { }

        @Override
        public void close() { }
    }

    @Test
    void requestAndReplyAreNotWrittenToTheConsoleOrLog() throws Exception {
        // A stand-in tutor on the real port. Skip when something (e.g., the
        // running app) already owns the port.
        ServerSocket tutor = null;
        try {
            tutor = new ServerSocket(ShaTuServer.PORT);
        } catch (IOException e) {
            assumeTrue(false, "Port " + ShaTuServer.PORT + " is in use");
        }

        final ServerSocket listening = tutor;
        Thread standIn = new Thread(() -> {
            try (Socket client = listening.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                 PrintWriter out = new PrintWriter(client.getOutputStream())) {
                in.readLine();
                out.println(new Gson().toJson(new TutorReply("Authenticated", SECRET_REPLY_DATA)));
                out.flush();
            } catch (IOException e) {
                // the test fails below if no reply arrives
            }
        });
        standIn.start();

        Logger logger = Logger.getLogger(SvcFacade.class.getName());
        CapturingHandler handler = new CapturingHandler();
        Level oldLevel = logger.getLevel();
        logger.addHandler(handler);
        logger.setLevel(Level.ALL);

        PrintStream oldOut = System.out;
        PrintStream oldErr = System.err;
        ByteArrayOutputStream console = new ByteArrayOutputStream();
        PrintStream captured = new PrintStream(console, true);
        System.setOut(captured);
        System.setErr(captured);

        TutorReply reply;
        try {
            ClientRequest request = new ClientRequest(ServerRequestType.SIGN_IN);
            request.setUserId("test@regis.edu");
            request.setSecurityToken(SECRET_TOKEN);
            request.setData("{\"password\":\"" + SECRET_PASSWORD_HASH + "\"}");

            reply = SvcFacade.instance().tutorRequest(request);
        } finally {
            System.setOut(oldOut);
            System.setErr(oldErr);
            logger.removeHandler(handler);
            logger.setLevel(oldLevel);
            listening.close();
            standIn.join(2000);
        }

        // The reply still reaches the caller untouched...
        assertEquals("Authenticated", reply.getStatus());
        assertEquals(SECRET_REPLY_DATA, reply.getData());

        // ...but neither the request nor the reply is printed or logged.
        String output = console.toString() + handler.text;
        assertFalse(output.contains(SECRET_TOKEN), "security token leaked: " + output);
        assertFalse(output.contains(SECRET_PASSWORD_HASH), "password leaked: " + output);
        assertFalse(output.contains(SECRET_REPLY_DATA), "reply data leaked: " + output);
    }

    @Test
    void unusableRepliesAreLoggedWithoutTheirBody() {
        Logger logger = Logger.getLogger(SvcFacade.class.getName());
        CapturingHandler handler = new CapturingHandler();
        logger.addHandler(handler);
        try {
            // Valid JSon with a null status, and malformed JSon
            SvcFacade.parseReply("{\"status\":null,\"data\":\"" + SECRET_REPLY_DATA + "\"}");
            SvcFacade.parseReply("{\"status\":\"Authenticated\",\"data\":\"" + SECRET_REPLY_DATA);
        } finally {
            logger.removeHandler(handler);
        }

        assertFalse(handler.text.length() == 0, "the failures should still be logged");
        assertFalse(handler.text.toString().contains(SECRET_REPLY_DATA),
                    "reply body leaked: " + handler.text);
    }
}
