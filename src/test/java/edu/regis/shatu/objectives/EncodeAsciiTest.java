package edu.regis.shatu.objectives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.model.steps.EncodeAsciiStep;
import edu.regis.shatu.model.steps.StepCompletionReply;
import edu.regis.shatu.svc.TutorReply;

class EncodeAsciiTest {
    private final Gson gson = new Gson();
    private EncodeAscii objective;

    @BeforeEach
    void setUp() {
        objective = new EncodeAscii(new Student(new Account("test@regis.edu")));
    }

    @Test
    void convertsPrintableCharactersToExactlyEightBitsEach() {
        assertEquals("010000010010000001111110", EncodeAscii.toBinaryFunction("A ~"));
        assertEquals(24, EncodeAscii.toBinaryFunction("A ~").length());
    }

    @Test
    void rejectsCharactersThatCannotBeEncodedAsPrintableAscii() {
        for (String question : new String[] { "", "café", "\u0100", "A\nB", "\u007f" }) {
            assertFalse(EncodeAsciiStep.isSupportedQuestion(question));
            assertThrows(IllegalArgumentException.class, () -> EncodeAscii.toBinaryFunction(question));
        }
        assertTrue(EncodeAsciiStep.isSupportedQuestion(" A "));
    }

    @Test
    void invalidCustomQuestionsReturnAnErrorBeforeMakingAnExample() {
        EncodeAsciiStep step = new EncodeAsciiStep();
        step.setQuestion("caf\u00e9");
        TutorReply reply = objective.example(null, gson.toJson(step));

        assertEquals(":ERR", reply.getStatus());
        assertTrue(reply.getErrorMessage().contains("printable ASCII"));
    }

    @Test
    void requestedLengthMustBeBounded() {
        for (int length : new int[] { 0, -1, EncodeAsciiStep.MAX_MESSAGE_LENGTH + 1 }) {
            EncodeAsciiStep step = new EncodeAsciiStep();
            step.setMessageLength(length);
            assertEquals(":ERR", objective.example(null, gson.toJson(step)).getStatus());
        }
    }

    @Test
    void incorrectAnswerUsesTheQuestionRatherThanReturnedResult() {
        EncodeAsciiStep step = new EncodeAsciiStep();
        step.setQuestion("A");
        step.setResult("wrong");
        step.setAscii("wrong");

        StepCompletionReply reply = grade(step);
        assertFalse(reply.isCorrect());
        assertEquals("01000001", reply.getCorrectAnswer());
        assertEquals("wrong", reply.getStudentAnswer());
    }

    @Test
    void missingAnswerDoesNotThrow() {
        EncodeAsciiStep step = new EncodeAsciiStep();
        step.setQuestion("A");
        StepCompletionReply reply = grade(step);
        assertFalse(reply.isCorrect());
        assertEquals("", reply.getStudentAnswer());
    }

    @Test
    void missingQuestionReturnsAnErrorInsteadOfCrashing() {
        EncodeAsciiStep step = new EncodeAsciiStep();
        TutorReply reply = objective.completeStep(new StepCompletion(step, gson.toJson(step)));
        assertEquals(":ERR", reply.getStatus());
    }

    private StepCompletionReply grade(EncodeAsciiStep step) {
        TutorReply reply = objective.completeStep(new StepCompletion(step, gson.toJson(step)));
        PendingTask task = gson.fromJson(reply.getData(), PendingTask.class);
        return gson.fromJson(task.getCurrentStep().getStep().getData(), StepCompletionReply.class);
    }
}
