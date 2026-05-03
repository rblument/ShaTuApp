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

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.Student;

/**
 * Unit tests for {@link XorBits#xorBitsFunction(String, String)}.
 *
 * The method under test is private, so reflection is used to invoke it
 * directly rather than going through {@code completeStep(...)}, which has
 * database side-effects via {@code ServiceFactory.findStudentModelSvc()}.
 *
 * @see XorBits
 */
@DisplayName("XorBits xorBitsFunction tests")
public class XorBitsTest {

    private static XorBits xorBits;
    private static Method xorBitsFunction;

    /**
     * Resolve the private {@code xorBitsFunction} once for the whole class.
     */
    @BeforeAll
    static void setUpReflection() throws Exception {
        xorBits = new XorBits(new Student(new Account("test@regis.edu")));
        xorBitsFunction = XorBits.class.getDeclaredMethod(
                "xorBitsFunction", String.class, String.class);
        xorBitsFunction.setAccessible(true);
    }

    /**
     * Convenience wrapper so each test reads like a normal method call.
     */
    private static String xor(String a, String b) throws Exception {
        return (String) xorBitsFunction.invoke(xorBits, a, b);
    }

    @Nested
    @DisplayName("Single-bit truth table")
    class TruthTable {

        @Test
        @DisplayName("0 XOR 0 = 0")
        public void zeroXorZero() throws Exception {
            assertEquals("0", xor("0", "0"));
        }

        @Test
        @DisplayName("0 XOR 1 = 1")
        public void zeroXorOne() throws Exception {
            assertEquals("1", xor("0", "1"));
        }

        @Test
        @DisplayName("1 XOR 0 = 1")
        public void oneXorZero() throws Exception {
            assertEquals("1", xor("1", "0"));
        }

        @Test
        @DisplayName("1 XOR 1 = 0")
        public void oneXorOne() throws Exception {
            assertEquals("0", xor("1", "1"));
        }
    }

    @Nested
    @DisplayName("Multi-bit operands")
    class MultiBit {

        @Test
        @DisplayName("Identical operands XOR to all zeros")
        public void identicalOperandsXorToZero() throws Exception {
            assertEquals("00000000", xor("10101010", "10101010"));
        }

        @Test
        @DisplayName("Disjoint operands XOR to all ones")
        public void disjointOperandsXorToOnes() throws Exception {
            assertEquals("11111111", xor("10101010", "01010101"));
        }

        @Test
        @DisplayName("Mixed operands XOR bit-by-bit")
        public void mixedOperands() throws Exception {
            assertEquals("0110", xor("1100", "1010"));
        }

        @Test
        @DisplayName("32-bit operand (SHA-256 word size)")
        public void thirtyTwoBitWord() throws Exception {
            String a        = "11111111000000001111111100000000";
            String b        = "00000000111111110000000011111111";
            String expected = "11111111111111111111111111111111";
            assertEquals(expected, xor(a, b));
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("Empty first operand returns empty string")
        public void emptyFirstOperand() throws Exception {
            assertEquals("", xor("", "1010"));
        }

        @Test
        @DisplayName("Empty second operand returns empty string")
        public void emptySecondOperand() throws Exception {
            assertEquals("", xor("1010", ""));
        }

        @Test
        @DisplayName("Result length is min(len(a), len(b))")
        public void shorterOperandTruncates() throws Exception {
            assertEquals("01", xor("1100", "10"));
        }

        @Test
        @DisplayName("Method returns non-null for valid input")
        public void resultIsNotNull() throws Exception {
            assertNotNull(xor("1", "0"));
        }
    }
}
