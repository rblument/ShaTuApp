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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.KnowledgeComponentKind;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.steps.AddOneStep;
import edu.regis.shatu.svc.TutorReply;

/**
 * Unit tests for how {@link AddOne} builds a new example.
 *
 * Add1View only sends the message length the student typed, so the tutor
 * has to make a question of that length.
 *
 * {@code genericExample(...)} updates the student model in the database, so
 * these tests override it to keep the step that example() built and skip
 * the database.
 *
 * @see AddOne
 */
@DisplayName("AddOne new example tests")
public class AddOneExampleTest {

    /** The length AddOne uses when the client does not ask for a valid one. */
    private static final int DEFAULT_LENGTH = 35;

    private final Gson gson = new Gson();

    private AddOneStep built;

    private AddOne objective;

    @BeforeEach
    void setUp() {
        built = null;
        objective = new AddOne(new Student(new Account("test@regis.edu"))) {
            @Override
            public TutorReply genericExample(Object subStep, StepSubType subType, ProblemType probType,
                    KnowledgeComponentKind kind, String description) {
                built = (AddOneStep) subStep;
                return new TutorReply(":Success");
            }
        };
    }

    @Test
    @DisplayName("a request with only a length gets a question of that length")
    void lengthOnlyRequestMakesQuestion() {
        AddOneStep request = new AddOneStep();
        request.setMessageLength(5);

        AddOneStep step = example(gson.toJson(request));

        assertNotNull(step.getQuestion(), "no question was made for the requested length");
        assertEquals(5, step.getQuestion().length());
        assertEquals(5, step.getMessageLength());
        assertEquals(objective.addOneFunction(step.getQuestion()), step.getResult());
    }

    @Test
    @DisplayName("no step data still makes a default example")
    void nullStepMakesDefaultExample() {
        AddOneStep step = example(null);

        assertNotNull(step.getQuestion());
        assertEquals(DEFAULT_LENGTH, step.getQuestion().length());
        assertEquals(DEFAULT_LENGTH, step.getMessageLength());
        assertEquals(objective.addOneFunction(step.getQuestion()), step.getResult());
    }

    @Test
    @DisplayName("a question sent by the client is kept and its length and result are worked out from it")
    void existingQuestionIsKept() {
        AddOneStep request = new AddOneStep();
        request.setQuestion("Hi");
        request.setMessageLength(9);     // does not match the question

        AddOneStep step = example(gson.toJson(request));

        assertEquals("Hi", step.getQuestion());
        assertEquals(2, step.getMessageLength());
        assertEquals("01001000 01101001 1", step.getResult());
    }

    @Test
    @DisplayName("the shortest and longest allowed lengths both get a question")
    void boundaryLengthsMakeQuestions() {
        for (int length : new int[] { 1, AddOne.MAX_MESSAGE_LENGTH }) {
            AddOneStep request = new AddOneStep();
            request.setMessageLength(length);

            AddOneStep step = example(gson.toJson(request));

            assertNotNull(step.getQuestion(), "no question was made for length " + length);
            assertEquals(length, step.getQuestion().length());
            assertEquals(objective.addOneFunction(step.getQuestion()), step.getResult());
        }
    }

    @Test
    @DisplayName("a missing, zero, negative or too large length is rejected with a message")
    void invalidLengthIsRejected() {
        for (String jsonData : new String[] { "{}", lengthOnly(0), lengthOnly(-1),
                lengthOnly(AddOne.MAX_MESSAGE_LENGTH + 1), lengthOnly(Integer.MAX_VALUE) }) {
            built = null;

            TutorReply reply = objective.example(null, jsonData);

            assertEquals(":ERR", reply.getStatus(), jsonData);
            assertEquals("Enter a message length between 1 and " + AddOne.MAX_MESSAGE_LENGTH + ".",
                    reply.getErrorMessage(), jsonData);
            assertNull(built, "no example should be built for " + jsonData);
        }
    }

    /** JSON for a step with only a message length, the way Add1View sends it. */
    private String lengthOnly(int length) {
        AddOneStep request = new AddOneStep();
        request.setMessageLength(length);
        return gson.toJson(request);
    }

    /** Run example() and return the step it handed to genericExample. */
    private AddOneStep example(String jsonData) {
        TutorReply reply = objective.example(null, jsonData);

        assertEquals(":Success", reply.getStatus());
        assertNotNull(built, "example() did not build a step");

        return built;
    }
}
