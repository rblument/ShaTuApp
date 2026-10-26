package edu.regis.shatu.objectives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.model.steps.MessageLenStep;
import edu.regis.shatu.model.steps.StepCompletionReply;
import edu.regis.shatu.svc.TutorReply;

class AddMsgLenTest {
    private final Gson gson = new Gson();
    private AddMsgLen objective;

    @BeforeEach
    void setUp() {
        objective = new AddMsgLen(new Student(new Account("test@regis.edu")));
    }

    @Test
    void lengthUsesBitsAndKeepsThePracticeAnswerUnpadded() {
        assertEquals("1000", AddMsgLen.binaryForLength(1));
        assertEquals("110111000", AddMsgLen.binaryForLength(55));
        assertEquals("111000000", AddMsgLen.binaryForLength(56));
        assertEquals("1000000000", AddMsgLen.binaryForLength(64));
    }

    @Test
    void forgedResultAndLengthDoNotDetermineGrade() {
        MessageLenStep step = new MessageLenStep();
        step.setQuestion("Hello");
        step.setMessageLength(1);
        step.setResult("garbage");
        step.setUserAnswer("garbage");

        StepCompletionReply reply = grade(step);

        assertFalse(reply.isCorrect());
        assertEquals("101000", reply.getCorrectAnswer());
        assertEquals("garbage", reply.getStudentAnswer());
    }

    @Test
    void missingAndMalformedAnswersAreWrong() {
        MessageLenStep step = new MessageLenStep();
        step.setQuestion("A");

        StepCompletionReply missing = grade(step);
        assertFalse(missing.isCorrect());
        assertEquals("", missing.getStudentAnswer());

        step.setUserAnswer("1020");
        assertFalse(grade(step).isCorrect());
    }

    @Test
    void missingQuestionReturnsAnErrorInsteadOfCrashing() {
        MessageLenStep step = new MessageLenStep();
        TutorReply reply = objective.completeStep(new StepCompletion(step, gson.toJson(step)));
        assertEquals(":ERR", reply.getStatus());
    }

    @Test
    void whitespaceInBinaryAnswerIsIgnored() {
        MessageLenStep step = new MessageLenStep();
        step.setQuestion("A");
        // Avoid the database-backed successful completion; exercise normalization
        // with an otherwise incorrect answer instead.
        step.setUserAnswer("1 0\n0 1");
        StepCompletionReply reply = grade(step);
        assertEquals("1001", reply.getStudentAnswer());
        assertFalse(reply.isCorrect());
    }

    private StepCompletionReply grade(MessageLenStep step) {
        TutorReply reply = objective.completeStep(new StepCompletion(step, gson.toJson(step)));
        PendingTask task = gson.fromJson(reply.getData(), PendingTask.class);
        return gson.fromJson(task.getCurrentStep().getStep().getData(), StepCompletionReply.class);
    }
}
