package edu.regis.shatu.svc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

import com.google.gson.Gson;

import org.junit.jupiter.api.Test;

class ShaTuServerTest {

    private final Gson gson = new Gson();

    @Test
    void tutorExceptionBecomesAnErrorReply() throws Exception {
        int port = startServer(() -> request -> {
            throw new IllegalArgumentException("bad operand");
        });

        TutorReply reply = gson.fromJson(send(port, gson.toJson(new ClientRequest(ServerRequestType.NEW_EXAMPLE))),
                TutorReply.class);

        assertNotNull(reply);
        assertEquals(":ERR", reply.getStatus());
        assertEquals(ShaTuServer.REQUEST_FAILED_MESSAGE, reply.getErrorMessage());
    }

    @Test
    void serverKeepsAnsweringAfterAnException() throws Exception {
        // Throws for a request with no type, answers OK otherwise.
        int port = startServer(() -> request -> {
            if (request.getRequestType() == null) {
                throw new NullPointerException("no request type");
            }
            return new TutorReply("OK");
        });

        TutorReply first = gson.fromJson(send(port, "{}"), TutorReply.class);
        TutorReply second = gson.fromJson(send(port, gson.toJson(new ClientRequest(ServerRequestType.NEW_EXAMPLE))),
                TutorReply.class);

        assertEquals(":ERR", first.getStatus());
        assertEquals("OK", second.getStatus());
    }

    @Test
    void unreadableRequestBecomesAnErrorReply() throws Exception {
        int port = startServer(() -> request -> new TutorReply("OK"));

        TutorReply first = gson.fromJson(send(port, "{\"requestType\":"), TutorReply.class);
        TutorReply second = gson.fromJson(send(port, gson.toJson(new ClientRequest(ServerRequestType.NEW_EXAMPLE))),
                TutorReply.class);

        assertEquals(":ERR", first.getStatus());
        assertEquals(ShaTuServer.REQUEST_FAILED_MESSAGE, first.getErrorMessage());
        assertEquals("OK", second.getStatus());
    }

    @Test
    void realTutorRequestWithNoTypeBecomesAnErrorReply() throws Exception {
        // ShaTuTutor.request reads the request type before anything else,
        // so this fails without touching the database.
        int port = startServer(ShaTuTutor::new);

        TutorReply first = gson.fromJson(send(port, "{}"), TutorReply.class);
        TutorReply second = gson.fromJson(send(port, "{}"), TutorReply.class);

        assertEquals(":ERR", first.getStatus());
        assertEquals(":ERR", second.getStatus());
    }

    private static int startServer(Supplier<TutorSvc> tutors) throws Exception {
        int port;
        try (ServerSocket probe = new ServerSocket(0)) {
            port = probe.getLocalPort();
        }

        Thread thread = new Thread(new ShaTuServer(port, tutors), "ShaTuServerTest");
        thread.setDaemon(true);
        thread.start();

        // Wait for the server to start listening.
        for (int i = 0; i < 50; i++) {
            try (Socket socket = new Socket("localhost", port)) {
                return port;
            } catch (IOException e) {
                Thread.sleep(20);
            }
        }
        throw new IllegalStateException("Server did not start on port " + port);
    }

    private static String send(int port, String request) throws IOException {
        try (Socket socket = new Socket("localhost", port)) {
            socket.setSoTimeout(5000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println(request);
            return in.readLine();
        }
    }
}
