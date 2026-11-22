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
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.KnowledgeComponentKind;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.steps.PrepScheduleStep;
import edu.regis.shatu.model.steps.Step;
import edu.regis.shatu.svc.TutorReply;

/**
 * Unit tests for how {@link PrepareSchedule} grades an answer and builds a
 * new example.
 *
 * {@code genericComplete(...)} and {@code genericExample(...)} update the
 * student model in the database, so these tests override them to keep what
 * the objective passed in and skip the database.
 */
@DisplayName("PrepareSchedule grading and new example tests")
public class PrepareScheduleTest {

    private final Gson gson = new Gson();

    private String gradedCorrect;
    private String gradedAnswer;
    private PrepScheduleStep built;

    private PrepareSchedule objective;

    @BeforeEach
    void setUp() {
        gradedCorrect = null;
        gradedAnswer = null;
        built = null;
        objective = new PrepareSchedule(new Student(new Account("test@regis.edu"))) {
            @Override
            public TutorReply genericComplete(String correctAnswer, String studentAnswer,
                    KnowledgeComponentKind stepName) {
                gradedCorrect = correctAnswer;
                gradedAnswer = studentAnswer;
                return new TutorReply(":Success");
            }

            @Override
            public TutorReply genericExample(Object subStep, StepSubType subType, ProblemType probType,
                    KnowledgeComponentKind kind, String description) {
                built = (PrepScheduleStep) subStep;
                return new TutorReply(":Success");
            }
        };
    }

    private TutorReply answer(int questionNumber, String answer) {
        PrepScheduleStep data = new PrepScheduleStep();
        data.setQuestionNumber(questionNumber);
        data.setAnswer(answer);
        return objective.completeStep(new StepCompletion(new Step(1, 0, StepSubType.PREPARE_SCHEDULE),
                gson.toJson(data)));
    }

    private TutorReply example(int questionNumber) {
        PrepScheduleStep data = new PrepScheduleStep();
        data.setQuestionNumber(questionNumber);
        return objective.example(null, gson.toJson(data));
    }

    @Test
    @DisplayName("A wrong pick is graded against the correct choice")
    void wrongPickIsGradedAgainstTheCorrectChoice() {
        String wrong = PrepScheduleStep.CHOICES[1][2];

        answer(2, wrong);

        assertEquals(PrepScheduleStep.CHOICES[1][0], gradedCorrect);
        assertEquals(wrong, gradedAnswer);
    }

    @Test
    @DisplayName("Each question is graded against its own correct choice")
    void eachQuestionUsesItsOwnCorrectChoice() {
        for (int q = 1; q <= PrepScheduleStep.QUESTIONS.length; q++) {
            answer(q, PrepScheduleStep.CHOICES[q - 1][0]);

            assertEquals(PrepScheduleStep.CHOICES[q - 1][0], gradedCorrect);
            assertEquals(gradedCorrect, gradedAnswer);
        }
    }

    @Test
    @DisplayName("No pick is graded as an empty answer")
    void noPickIsGradedAsEmpty() {
        answer(1, null);

        assertEquals(PrepScheduleStep.CHOICES[0][0], gradedCorrect);
        assertEquals("", gradedAnswer);
    }

    @Test
    @DisplayName("A question number outside 1 to 4 is rejected")
    void unknownQuestionIsRejected() {
        for (int q : new int[] {0, PrepScheduleStep.QUESTIONS.length + 1}) {
            TutorReply reply = answer(q, PrepScheduleStep.CHOICES[0][0]);

            assertEquals(":ERR", reply.getStatus());
            assertNull(gradedCorrect, "nothing should be graded");
        }
    }

    @Test
    @DisplayName("A new example keeps the question that was asked for")
    void newExampleKeepsTheRequestedQuestion() {
        TutorReply reply = example(3);

        assertEquals(":Success", reply.getStatus());
        assertEquals(3, built.getQuestionNumber());
        assertEquals("", built.getAnswer());
    }

    @Test
    @DisplayName("A new example for a question outside 1 to 4 is rejected")
    void newExampleForUnknownQuestionIsRejected() {
        TutorReply reply = example(5);

        assertEquals(":ERR", reply.getStatus());
        assertNull(built);
    }

    @Test
    @DisplayName("A new example with no data starts at question 1")
    void newExampleWithNoDataStartsAtQuestionOne() {
        objective.example(null, null);

        assertEquals(1, built.getQuestionNumber());
    }
}
