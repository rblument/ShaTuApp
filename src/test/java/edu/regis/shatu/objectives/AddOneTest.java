/*
 * SHATU: SHA-256 Tutor
 *
 *  (C) Johanna & Richard Blumenthal, All rights reserved
 *
 *  Unauthorized use, duplication or distribution without the authors'
 *  permission is strictly prohibited.
 *
 *  Unless required by applicable law or agreed to in writing, this
 *  software is distributed on an "AS IS" basis without warranties
 *  or conditions of any kind, either expressed or implied.
 */
package edu.regis.shatu.objectives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.model.steps.AddOneStep;
import edu.regis.shatu.model.steps.StepCompletionReply;
import edu.regis.shatu.svc.TutorReply;

/**
 * Unit tests for how {@link AddOne} grades an answer (issue #253).
 *
 * The student converts the question to binary, one byte per character
 * separated by spaces, and adds a single 1 bit at the end.
 *
 * A correct answer through {@code completeStep(...)} updates the student
 * model in the database, so the correct case is checked against
 * {@code addOneFunction(...)} directly. A wrong answer does not touch the
 * database, so that case goes through {@code completeStep(...)}.
 *
 * @see AddOne
 */
@DisplayName("AddOne grading tests")
public class AddOneTest {

    private final Gson gson = new Gson();

    private AddOne objective;

    @BeforeEach
    void setUp() {
        objective = new AddOne(new Student(new Account("test@regis.edu")));
    }

    @Test
    @DisplayName("one character: A is 01000001 followed by the 1 bit")
    void oneCharacter() {
        assertEquals("01000001 1", objective.addOneFunction("A"));
    }

    @Test
    @DisplayName("two characters: each byte is separated by a space")
    void twoCharacters() {
        assertEquals("01100001 01100010 1", objective.addOneFunction("ab"));
    }

    @Test
    @DisplayName("a space character is encoded as 00100000")
    void spaceCharacter() {
        assertEquals("00100000 1", objective.addOneFunction(" "));
    }

    @Test
    @DisplayName("the right answer matches the expected answer")
    void rightAnswerMatches() {
        String typedAnswer = "01001000 01101001 1";

        assertEquals(objective.addOneFunction("Hi"), typedAnswer);
    }

    @Test
    @DisplayName("a wrong answer is marked wrong and the correct answer is worked out on the server")
    void wrongAnswerIsMarkedWrong() {
        AddOneStep step = new AddOneStep();
        step.setQuestion("A");
        step.setMessageLength(1);
        step.setResult("01000001 1");
        step.setUserAnswer("01000001 0");

        StepCompletionReply reply = grade(step);

        assertFalse(reply.isCorrect());
        assertEquals("01000001 1", reply.getCorrectAnswer());
        assertEquals("01000001 0", reply.getStudentAnswer());
    }

    @Test
    @DisplayName("an answer that only matches the result sent back by the client is still wrong")
    void answerMatchingClientResultIsStillWrong() {
        AddOneStep step = new AddOneStep();
        step.setQuestion("A");
        step.setMessageLength(1);
        step.setResult("garbage");      // before the fix both answers came from here
        step.setUserAnswer("garbage");

        StepCompletionReply reply = grade(step);

        assertFalse(reply.isCorrect());
        assertEquals("01000001 1", reply.getCorrectAnswer());
    }

    /** Send the step through completeStep and pull out the StepCompletionReply. */
    private StepCompletionReply grade(AddOneStep step) {
        TutorReply reply = objective.completeStep(new StepCompletion(step, gson.toJson(step)));

        PendingTask task = gson.fromJson(reply.getData(), PendingTask.class);
        String replyData = task.getCurrentStep().getStep().getData();

        return gson.fromJson(replyData, StepCompletionReply.class);
    }
}
