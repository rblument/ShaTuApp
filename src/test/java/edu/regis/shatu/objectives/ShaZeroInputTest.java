package edu.regis.shatu.objectives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.Student;

class ShaZeroInputTest {
    private final ShaZero objective = new ShaZero(new Student(new Account("test@regis.edu")));

    @Test
    void matchesSigmaZeroReferenceFor32Bits() {
        for (int word : new int[] {0, -1, 0x6a09e667, 0x12345678}) {
            String input = bits(word, 32);
            int expected = Integer.rotateRight(word, 2)
                    ^ Integer.rotateRight(word, 13) ^ Integer.rotateRight(word, 22);
            assertEquals(bits(expected, 32), sigma(input, 32));
        }
    }

    @Test
    void rotatesWithinEachSelectedWidthAndAcceptsGroupedBits() {
        for (int width : new int[] {4, 8, 16, 32}) {
            int word = 1 << (width - 1);
            int mask = width == 32 ? -1 : (1 << width) - 1;
            int expected = (rotate(word, 2, width) ^ rotate(word, 13, width)
                    ^ rotate(word, 22, width)) & mask;
            assertEquals(bits(expected, width), sigma(bits(word, width), width));
        }
        assertEquals(sigma("10110011", 8), sigma("1011 0011", 8));
    }

    @Test
    void rejectsWrongLengthInvalidDigitsAndUnsupportedWidths() {
        assertThrows(IllegalArgumentException.class, () -> sigma("1010", 0));
        assertThrows(IllegalArgumentException.class, () -> sigma("1010", 5));
        assertThrows(IllegalArgumentException.class, () -> sigma(null, 4));
        assertThrows(IllegalArgumentException.class, () -> sigma("", 4));
        assertThrows(IllegalArgumentException.class, () -> sigma("101", 4));
        assertThrows(IllegalArgumentException.class, () -> sigma("10101", 4));
        assertThrows(IllegalArgumentException.class, () -> sigma("10x0", 4));
    }

    private static int rotate(int value, int positions, int width) {
        int shift = positions % width;
        int mask = width == 32 ? -1 : (1 << width) - 1;
        return ((value >>> shift) | (value << (width - shift))) & mask;
    }

    private static String bits(int value, int width) {
        String binary = Integer.toBinaryString(value);
        return "0".repeat(Math.max(0, width - binary.length()))
                + binary.substring(Math.max(0, binary.length() - width));
    }

    private String sigma(String input, int width) {
        try {
            Method method = ShaZero.class.getDeclaredMethod("calculateSigma", String.class, int.class);
            method.setAccessible(true);
            return (String) method.invoke(objective, input, width);
        } catch (InvocationTargetException ex) {
            if (ex.getCause() instanceof RuntimeException runtime) {
                throw runtime;
            }
            throw new AssertionError(ex.getCause());
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError(ex);
        }
    }
}
