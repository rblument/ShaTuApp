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
package edu.regis.shatu.view.act;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import edu.regis.shatu.model.Hint;
import edu.regis.shatu.model.aol.PendingStep;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.steps.Step;

/**
 * Checks that pressing Hint again moves on to the next hint.
 *
 * Every objective's hint(...) replies with the same hint list and a hint
 * index of 0, so each press below gets a new reply built that way. The
 * client's step starts at index 0, the same as a step from a new example.
 *
 * @see HintAction#nextHint(PendingStep, PendingStep)
 */
@DisplayName("HintAction hint order tests")
public class HintActionTest {

    private static final Gson GSON = new Gson();

    /**
     * Build a reply the same way the objectives do, and read it back the same
     * way HintAction does.
     */
    private static PendingStep tutorReply(String... hintTexts) {
        Step step = new Step(1, 0, StepSubType.REQUEST_HINT);

        for (int i = 0; i < hintTexts.length; i++) {
            Hint hint = new Hint();
            hint.setId(i);
            hint.setText(hintTexts[i]);
            step.addHint(hint);
        }

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);

        return GSON.fromJson(GSON.toJson(pendingStep), PendingStep.class);
    }

    private static PendingStep clientStep() {
        PendingStep pendingStep = new PendingStep(new Step(1, 0, StepSubType.ENCODE_ASCII));
        pendingStep.setCurrentHintIndex(0);
        return pendingStep;
    }

    @Test
    @DisplayName("1st, 2nd and 3rd presses show hints 0, 1 and 2")
    void successivePressesWalkThroughTheHints() {
        PendingStep client = clientStep();
        String[] hints = {"Hint 0", "Hint 1", "Hint 2"};

        assertEquals("Hint 0", HintAction.nextHint(client, tutorReply(hints)).getText());
        assertEquals("Hint 1", HintAction.nextHint(client, tutorReply(hints)).getText());
        assertEquals("Hint 2", HintAction.nextHint(client, tutorReply(hints)).getText());
    }

    @Test
    @DisplayName("After the last hint the next press starts over at hint 0")
    void pressAfterTheLastHintWrapsToTheFirst() {
        PendingStep client = clientStep();
        String[] hints = {"Hint 0", "Hint 1"};

        assertEquals("Hint 0", HintAction.nextHint(client, tutorReply(hints)).getText());
        assertEquals("Hint 1", HintAction.nextHint(client, tutorReply(hints)).getText());
        assertEquals("Hint 0", HintAction.nextHint(client, tutorReply(hints)).getText());
        assertEquals("Hint 1", HintAction.nextHint(client, tutorReply(hints)).getText());
    }

    @Test
    @DisplayName("A single hint, as from genericHint, is shown on every press")
    void singleHintIsShownEveryTime() {
        PendingStep client = clientStep();

        for (int press = 0; press < 3; press++) {
            assertEquals("Only hint",
                    HintAction.nextHint(client, tutorReply("Only hint")).getText());
        }
    }

    @Test
    @DisplayName("An empty hint list shows the no hints message")
    void emptyHintListShowsNoHintsMessage() {
        PendingStep client = clientStep();

        for (int press = 0; press < 3; press++) {
            assertEquals("Sorry, no hints available",
                    HintAction.nextHint(client, tutorReply()).getText());
        }
    }
}
