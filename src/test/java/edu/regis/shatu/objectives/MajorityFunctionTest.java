package edu.regis.shatu.objectives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.Student;

class MajorityFunctionTest {
    private final MajorityFunction objective =
            new MajorityFunction(new Student(new Account("test@regis.edu")));

    @Test
    void computesMajorityForEachProblemWidth() {
        for (int width : new int[] {4, 8, 16, 32}) {
            String x = "10".repeat(width / 2);
            String y = "11".repeat(width / 2);
            String z = "00".repeat(width / 2);
            assertEquals(x, majority(x, y, z, width));
            assertEquals("0".repeat(width), majority(x, z, z, width));
            assertEquals("1".repeat(width), majority(y, y, z, width));
        }
    }

    @Test
    void acceptsFormattedBinaryWords() {
        assertEquals("1010", majority("10 10", "11 00", "00 11", 4));
    }

    @Test
    void rejectsWidthsAndOperandsThatDoNotMatchTheQuestion() {
        assertThrows(IllegalArgumentException.class, () -> majority("1010", "1010", "1010", 0));
        assertThrows(IllegalArgumentException.class, () -> majority("1010", "1010", "1010", 5));
        assertThrows(IllegalArgumentException.class, () -> majority(null, "1010", "1010", 4));
        assertThrows(IllegalArgumentException.class, () -> majority("101", "1010", "1010", 4));
        assertThrows(IllegalArgumentException.class, () -> majority("1010", "10101", "1010", 4));
        assertThrows(IllegalArgumentException.class, () -> majority("1010", "1010", "10x0", 4));
    }

    private String majority(String x, String y, String z, int width) {
        try {
            Method method = MajorityFunction.class.getDeclaredMethod(
                    "majorityFunction", String.class, String.class, String.class, int.class);
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
