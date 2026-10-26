package edu.regis.shatu.objectives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.steps.MessageLenStep;
import edu.regis.shatu.model.steps.MessageLengthEncoding;
import edu.regis.shatu.svc.TutorReply;

class AddMsgLenExampleTest {
    private final Gson gson = new Gson();

    @Test
    void invalidRequestsReturnErrorsBeforeGeneratingQuestions() {
        AddMsgLen objective = new AddMsgLen(new Student(new Account("test@regis.edu")));
        for (int length : new int[] { 0, -1, MessageLengthEncoding.MAX_CHARACTERS + 1 }) {
            MessageLenStep request = new MessageLenStep();
            request.setMessageLength(length);
            TutorReply reply = objective.example(null, gson.toJson(request));
            assertEquals(":ERR", reply.getStatus());
            assertTrue(reply.getErrorMessage().contains("message length"));
        }
        assertEquals(":ERR", objective.example(null, "bad json").getStatus());
    }
}
