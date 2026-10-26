package edu.regis.shatu.model.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

class MessageLengthEncodingTest {
    @Test
    void practiceAnswerIsUnpadded() {
        assertEquals("1000", MessageLengthEncoding.practiceAnswer(1));
        assertEquals("110111000", MessageLengthEncoding.practiceAnswer(55));
        assertEquals("111000000", MessageLengthEncoding.practiceAnswer(56));
        assertEquals("1000000000", MessageLengthEncoding.practiceAnswer(64));
    }

    @Test
    void actualLengthFieldIs64BitsAndContainsTheUnpaddedAnswer() {
        for (int length : new int[] { 1, 55, 56, 64, 1024 }) {
            String field = MessageLengthEncoding.lengthBits64(length);
            assertEquals(64, field.length());
            assertTrue(field.endsWith(MessageLengthEncoding.practiceAnswer(length)));
            assertTrue(field.substring(0, 64 - MessageLengthEncoding.practiceAnswer(length).length())
                    .chars().allMatch(bit -> bit == '0'));
        }
    }

    @Test
    void lengthFieldSurvivesStepSerialization() {
        MessageLenStep step = new MessageLenStep();
        step.setMessageLength(56);
        step.setLengthBits64(MessageLengthEncoding.lengthBits64(56));

        Gson gson = new Gson();
        MessageLenStep restored = gson.fromJson(gson.toJson(step), MessageLenStep.class);
        assertEquals(56, restored.getMessageLength());
        assertEquals(64, restored.getLengthBits64().length());
        assertTrue(restored.getLengthBits64().endsWith("111000000"));
    }

    @Test
    void invalidLengthsAreRejectedBeforeGeneratingQuestions() {
        for (int length : new int[] { -1, 0, MessageLengthEncoding.MAX_CHARACTERS + 1 }) {
            assertThrows(IllegalArgumentException.class, () -> MessageLengthEncoding.lengthBits64(length));
        }
    }
}
