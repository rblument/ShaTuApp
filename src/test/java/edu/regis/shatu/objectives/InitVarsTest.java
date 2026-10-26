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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.Hint;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.steps.InitVarStep;
import edu.regis.shatu.model.steps.Step;

/**
 * Unit tests for how {@link InitVars} grades answers and builds hints.
 *
 * The methods under test are private, so reflection is used to invoke them
 * directly rather than going through {@code completeStep(...)} and
 * {@code hint(...)}, which have database side-effects via
 * {@code ServiceFactory.findStudentModelSvc()}.
 *
 * @see InitVars
 */
@DisplayName("InitVars grading and hint tests")
public class InitVarsTest {

    private static final Gson GSON = new Gson();

    /** H0 to H7 from FIPS 180-4, section 5.3.3. */
    private static final String[] FIPS_VALUES = {
        "6a09e667", "bb67ae85", "3c6ef372", "a54ff53a",
        "510e527f", "9b05688c", "1f83d9ab", "5be0cd19"
    };

    private static InitVars initVars;
    private static Method gradedStep;
    private static Method addHints;

    @BeforeAll
    static void setUpReflection() throws Exception {
        initVars = new InitVars(new Student(new Account("test@regis.edu")));

        gradedStep = InitVars.class.getDeclaredMethod("gradedStep", InitVarStep.class);
        gradedStep.setAccessible(true);

        addHints = InitVars.class.getDeclaredMethod("addHints", Step.class);
        addHints.setAccessible(true);
    }

    /** Build the JSON the same way InitVarView.stepCompletion() does. */
    private static String clientJson(String[] answers) {
        InitVarStep step = new InitVarStep();

        for (int i = 0; i < answers.length; i++) {
            step.setUserAnswer("H" + i, answers[i]);
        }

        return GSON.toJson(step);
    }

    /** Read the JSON the way completeStep() does and grade it. */
    private static InitVarStep grade(String json) throws Exception {
        InitVarStep submitted = GSON.fromJson(json, InitVarStep.class);
        return (InitVarStep) gradedStep.invoke(initVars, submitted);
    }

    @Test
    @DisplayName("All eight FIPS 180-4 values are graded correct")
    void correctAnswersPass() throws Exception {
        assertTrue(grade(clientJson(FIPS_VALUES)).allAnswersCorrect());
    }

    @Test
    @DisplayName("Answers with 0x prefix and upper case are graded correct")
    void prefixAndUpperCasePass() throws Exception {
        String[] answers = new String[8];
        for (int i = 0; i < 8; i++) {
            answers[i] = "0x" + FIPS_VALUES[i].toUpperCase();
        }

        assertTrue(grade(clientJson(answers)).allAnswersCorrect());
    }

    @Test
    @DisplayName("One wrong value fails the step")
    void oneWrongAnswerFails() throws Exception {
        String[] answers = FIPS_VALUES.clone();
        answers[3] = "a54ff53b";

        InitVarStep graded = grade(clientJson(answers));

        assertFalse(graded.allAnswersCorrect());
        assertFalse(graded.isUserCorrect("H3"));
        assertTrue(graded.isUserCorrect("H2"));
    }

    @Test
    @DisplayName("Missing answers fail and are recorded as empty")
    void missingAnswersFail() throws Exception {
        InitVarStep graded = grade(clientJson(new String[] { FIPS_VALUES[0] }));

        assertFalse(graded.allAnswersCorrect());
        assertTrue(graded.isUserCorrect("H0"));
        assertEquals("", graded.getUserAnswer("H7"));
    }

    @Test
    @DisplayName("Correct answers sent by the client are ignored")
    void clientCorrectAnswersAreIgnored() throws Exception {
        // A client that changes the correct answers to match its own guesses
        JsonObject json = GSON.fromJson(clientJson(new String[] {
            "0", "0", "0", "0", "0", "0", "0", "0" }), JsonObject.class);

        JsonObject forged = new JsonObject();
        for (int i = 0; i < 8; i++) {
            forged.addProperty("H" + i, "0");
        }
        json.add("answers", forged);

        InitVarStep graded = grade(json.toString());

        assertFalse(graded.allAnswersCorrect());
        assertEquals("0x6a09e667", graded.getAnswer("H0"));
    }

    @Test
    @DisplayName("Hint request adds 24 hints, none with empty text")
    void hintsAreNotEmpty() throws Exception {
        Step step = new Step(1, 0, StepSubType.INITIALIZE_VARS);

        addHints.invoke(initVars, step);

        assertEquals(24, step.getHints().size());

        for (Hint hint : step.getHints()) {
            assertNotNull(hint.getText());
            assertFalse(hint.getText().isEmpty());
        }

        assertEquals("First prime number is 2", step.getHints().get(0).getText());
    }

    @Test
    @DisplayName("Hints sent by the client are replaced by the tutor's hints")
    void clientHintsAreReplaced() throws Exception {
        Step step = new Step(1, 0, StepSubType.INITIALIZE_VARS);
        Hint clientHint = new Hint();
        clientHint.setId(1);
        clientHint.setText("The answer is 0");
        step.addHint(clientHint);

        addHints.invoke(initVars, step);

        assertEquals(24, step.getHints().size());
        assertEquals("First prime number is 2", step.getHints().get(0).getText());
    }
}
