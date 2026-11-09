package edu.regis.shatu.svc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.gson.Gson;

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
}
