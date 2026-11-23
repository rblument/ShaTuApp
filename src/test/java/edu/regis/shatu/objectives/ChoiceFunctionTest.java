package edu.regis.shatu.objectives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.Student;

class ChoiceFunctionTest {
    private final ChoiceFunction objective =
            new ChoiceFunction(new Student(new Account("test@regis.edu")));

    @Test
    void choosesYWhenXIsOneAndZWhenXIsZeroAtEveryWidth() {
        for (int width : new int[] {4, 8, 16, 32}) {
            String x = "10".repeat(width / 2);
            String y = "11".repeat(width / 2);
            String z = "00".repeat(width / 2);
            assertEquals(x, choice(x, y, z, width));
            assertEquals(y, choice("1".repeat(width), y, z, width));
            assertEquals(z, choice("0".repeat(width), y, z, width));
        }
        assertEquals("1010", choice("01 01", "0000", "1111", 4));
    }

    @Test
    void rejectsUnsupportedWidthsAndMismatchedOrMalformedOperands() {
        assertThrows(IllegalArgumentException.class, () -> choice("1010", "1010", "1010", 0));
        assertThrows(IllegalArgumentException.class, () -> choice("1010", "1010", "1010", 5));
        assertThrows(IllegalArgumentException.class, () -> choice(null, "1010", "1010", 4));
        assertThrows(IllegalArgumentException.class, () -> choice("101", "1010", "1010", 4));
        assertThrows(IllegalArgumentException.class, () -> choice("1010", "10101", "1010", 4));
        assertThrows(IllegalArgumentException.class, () -> choice("1010", "1010", "10x0", 4));
    }

    private String choice(String x, String y, String z, int width) {
        try {
            Method method = ChoiceFunction.class.getDeclaredMethod(
                    "choiceFunction", String.class, String.class, String.class, int.class);
            method.setAccessible(true);
            return (String) method.invoke(objective, x, y, z, width);
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
