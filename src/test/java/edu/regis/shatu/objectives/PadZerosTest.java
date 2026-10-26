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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.model.steps.Pad0Step;
import edu.regis.shatu.model.steps.StepCompletionReply;
import edu.regis.shatu.svc.TutorReply;

/**
 * Unit tests for how {@link PadZeros} works out and grades the number of
 * zeros needed to pad a message (issue #253).
 *
 * A correct answer through {@code completeStep(...)} updates the student
 * model in the database, so the correct case is checked against the padding
 * formula directly. A wrong answer does not touch the database, so that case
 * goes through {@code completeStep(...)}.
 *
 * @see PadZeros
 */
@DisplayName("PadZeros grading tests")
public class PadZerosTest {

    private final Gson gson = new Gson();

    private PadZeros objective;

    @BeforeEach
    void setUp() {
        objective = new PadZeros(new Student(new Account("test@regis.edu")));
    }

    @Test
    @DisplayName("1 character needs 439 zeros")
    void oneCharacter() {
        assertEquals(439, objective.calculateZerosNeededForPadding(1));
    }

    @Test
    @DisplayName("55 characters need 7 zeros")
    void fiftyFiveCharacters() {
        assertEquals(7, objective.calculateZerosNeededForPadding(55));
    }

    @Test
    @DisplayName("56 characters need 511 zeros, not a negative number")
    void fiftySixCharacters() {
        assertEquals(511, objective.calculateZerosNeededForPadding(56));
    }

    @Test
    @DisplayName("64 characters need 447 zeros")
    void sixtyFourCharacters() {
        assertEquals(447, objective.calculateZerosNeededForPadding(64));
    }

    @Test
    @DisplayName("every length from 0 to 200 fills out a whole number of 512 bit blocks")
    void everyLengthFillsWholeBlocks() {
        for (int length = 0; length <= 200; length++) {
            int zeros = objective.calculateZerosNeededForPadding(length);

            // message bits + the 1 bit + zeros + 64 bit length
            int totalBits = 8 * length + 1 + zeros + 64;

            assertEquals(0, totalBits % 512, length + " characters");
            assertTrue(zeros >= 0 && zeros <= 511, length + " characters gave " + zeros);
        }
    }

    @Test
    @DisplayName("the right answer for a 1 character question matches the expected answer")
    void rightAnswerMatches() {
        String typedAnswer = "439";

        assertEquals(Integer.toString(objective.calculateZerosNeededForPadding("A".length())), typedAnswer);
    }

    @Test
    @DisplayName("a wrong answer is marked wrong and the correct answer is worked out on the server")
    void wrongAnswerIsMarkedWrong() {
        Pad0Step step = new Pad0Step();
        step.setQuestion("A");
        step.setMessageLength(1);
        step.setResult("12");      // a wrong correct answer sent back by the client
        step.setUserAnswer("12");  // matches it, but should still be marked wrong

        StepCompletionReply reply = grade(step);

        assertFalse(reply.isCorrect());
        assertEquals("439", reply.getCorrectAnswer());
        assertEquals("12", reply.getStudentAnswer());
    }

    @Test
    @DisplayName("the student's answer is graded, not the message length")
    void userAnswerIsGraded() {
        Pad0Step step = new Pad0Step();
        step.setQuestion("Hello");
        step.setMessageLength(5);
        step.setUserAnswer("400");

        StepCompletionReply reply = grade(step);

        assertEquals("400", reply.getStudentAnswer());
        assertEquals("407", reply.getCorrectAnswer());
        assertFalse(reply.isCorrect());
    }

    /** Send the step through completeStep and pull out the StepCompletionReply. */
    private StepCompletionReply grade(Pad0Step step) {
        TutorReply reply = objective.completeStep(new StepCompletion(step, gson.toJson(step)));

        PendingTask task = gson.fromJson(reply.getData(), PendingTask.class);
        String replyData = task.getCurrentStep().getStep().getData();

        return gson.fromJson(replyData, StepCompletionReply.class);
    }
}
