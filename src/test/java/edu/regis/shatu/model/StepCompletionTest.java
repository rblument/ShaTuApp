package edu.regis.shatu.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.steps.Step;

/**
 * Tests for StepCompletion, the object a view sends to the tutor when the
 * student checks an answer or asks for a hint.
 */
class StepCompletionTest {
    // Same settings the GUI actions and ShaTuTutor use.
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    void constructorKeepsTheStepAndTheData() {
        Step step = new Step(7, 3, StepSubType.PREPARE_SCHEDULE);

        StepCompletion completion = new StepCompletion(step, "{\"answer\":\"42\"}");

        assertSame(step, completion.getStep());
        assertEquals("{\"answer\":\"42\"}", completion.getData());
    }

    @Test
    void stepSurvivesTheTripToTheTutor() {
        Step step = new Step(7, 3, StepSubType.PREPARE_SCHEDULE);
        step.setData("{\"question\":\"q\"}");

        // The view builds the completion and the action sends it as JSON,
        // then ShaTuTutor reads it back with fromJson.
        String json = gson.toJson(new StepCompletion(step, "\"answer\""));
        StepCompletion received = gson.fromJson(json, StepCompletion.class);

        assertNotNull(received.getStep(), "the tutor needs the step to route the request");
        assertEquals(7, received.getStep().getId());
        assertEquals(3, received.getStep().getSequenceIndex());
        assertEquals(StepSubType.PREPARE_SCHEDULE, received.getStep().getSubType());
        assertEquals("\"answer\"", received.getData());
    }
}
