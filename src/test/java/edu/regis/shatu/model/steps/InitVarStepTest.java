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
package edu.regis.shatu.model.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the answers and hints held by {@link InitVarStep}.
 *
 * The correct values are the SHA-256 initial hash values H0 to H7 from
 * FIPS 180-4, section 5.3.3.
 *
 * @see InitVarStep
 */
@DisplayName("InitVarStep answer and hint tests")
public class InitVarStepTest {

    /** H0 to H7 from FIPS 180-4, section 5.3.3. */
    private static final String[] FIPS_VALUES = {
        "6a09e667", "bb67ae85", "3c6ef372", "a54ff53a",
        "510e527f", "9b05688c", "1f83d9ab", "5be0cd19"
    };

    private InitVarStep step;

    @BeforeEach
    void setUp() {
        step = new InitVarStep();
    }

    @Test
    @DisplayName("Correct answers are stored under H0 to H7 and match FIPS 180-4")
    void correctAnswersMatchFips() {
        assertEquals(8, step.getCorrectAnswers().size());

        for (int i = 0; i < 8; i++) {
            assertEquals("0x" + FIPS_VALUES[i], step.getAnswer("H" + i), "H" + i);
        }
    }

    @Test
    @DisplayName("Answer is accepted with or without 0x, in upper or lower case")
    void acceptsPrefixAndCaseVariations() {
        String[] forms = { "0x6a09e667", "6a09e667", "0X6A09E667", "6A09E667", " 0x6a09e667 " };

        for (String form : forms) {
            step.setUserAnswer("H0", form);
            assertTrue(step.isUserCorrect("H0"), form);
        }
    }

    @Test
    @DisplayName("Wrong, empty and missing answers are rejected")
    void rejectsWrongAnswers() {
        step.setUserAnswer("H0", "0x6a09e666");
        assertFalse(step.isUserCorrect("H0"));

        step.setUserAnswer("H0", "");
        assertFalse(step.isUserCorrect("H0"));

        step.setUserAnswer("H0", "0x");
        assertFalse(step.isUserCorrect("H0"));

        assertFalse(step.isUserCorrect("H1")); // never answered
    }

    @Test
    @DisplayName("All answers correct only when every one of H0 to H7 is right")
    void allAnswersCorrect() {
        for (int i = 0; i < 8; i++) {
            step.setUserAnswer("H" + i, FIPS_VALUES[i]);
        }
        assertTrue(step.allAnswersCorrect());

        step.setUserAnswer("H7", "00000000");
        assertFalse(step.allAnswersCorrect());
    }

    @Test
    @DisplayName("All 24 hints exist for H0 to H7")
    void everyHintHasText() {
        for (int i = 0; i < 8; i++) {
            for (int level = 1; level <= 3; level++) {
                String hint = step.getHint("H" + i, level);
                assertNotNull(hint, "H" + i + " level " + level);
                assertFalse(hint.isEmpty(), "H" + i + " level " + level);
            }
        }
    }

    @Test
    @DisplayName("Level 3 hint binary converts to the correct answer")
    void levelThreeHintMatchesAnswer() {
        for (int i = 0; i < 8; i++) {
            String hint = step.getHint("H" + i, 3);
            String bits = hint.replace("Convert ", "").replace(" to Hexadecimal", "");

            assertEquals(32, bits.length(), "H" + i);
            assertEquals(FIPS_VALUES[i], String.format("%08x", Long.parseLong(bits, 2)), "H" + i);
        }
    }
}
