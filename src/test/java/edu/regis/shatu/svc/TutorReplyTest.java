package edu.regis.shatu.svc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.Gson;

import org.junit.jupiter.api.Test;

class TutorReplyTest {

    @Test
    void errorMessageIsTheReplyData() {
        TutorReply reply = new TutorReply(":ERR", "Illegal Security Token");

        assertEquals("Illegal Security Token", reply.getErrorMessage());
    }

    @Test
    void errorMessageSurvivesGsonRoundTrip() {
        // The server sends gson.toJson(reply) and SvcFacade parses it back.
        Gson gson = new Gson();
        TutorReply sent = new TutorReply(":ERR", "Session not found for: test@regis.edu");

        TutorReply received = gson.fromJson(gson.toJson(sent), TutorReply.class);

        assertEquals(":ERR", received.getStatus());
        assertEquals("Session not found for: test@regis.edu", received.getErrorMessage());
    }

    @Test
    void errorMessageIsTrimmed() {
        TutorReply reply = new TutorReply(":ERR", "  Unknown course: 1 \n");

        assertEquals("Unknown course: 1", reply.getErrorMessage());
    }

    @Test
    void errorMessageFallsBackWhenDataIsEmpty() {
        TutorReply reply = new TutorReply();

        assertEquals(TutorReply.DEFAULT_ERROR_MESSAGE, reply.getErrorMessage());
    }

    @Test
    void errorMessageFallsBackWhenDataIsBlank() {
        TutorReply reply = new TutorReply(":ERR", "   ");

        assertEquals(TutorReply.DEFAULT_ERROR_MESSAGE, reply.getErrorMessage());
    }

    @Test
    void errorMessageFallsBackWhenDataIsNull() {
        TutorReply reply = new Gson().fromJson("{\"status\":\":ERR\",\"data\":null}", TutorReply.class);

        assertEquals(TutorReply.DEFAULT_ERROR_MESSAGE, reply.getErrorMessage());
    }
}
