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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.svc.SHA_256;

/**
 * Unit tests for {@link ShaOne}'s Sigma 1 calculation.
 *
 * Sigma 1 is defined by FIPS 180-4 as
 * {@code ROTR^6(x) XOR ROTR^11(x) XOR ROTR^25(x)}, which is a different
 * function from Sigma 0 ({@code ROTR^2 XOR ROTR^13 XOR ROTR^22}). The two
 * must not agree.
 *
 * The 32-bit cases are checked against {@code SHA_256.bigSig1}, the verified
 * implementation already used by the tutor's own digest code, so the test has
 * an independent oracle rather than hard-coded expectations alone.
 *
 * The method under test is private, so reflection is used to invoke it
 * directly rather than going through {@code completeStep(...)}, which has
 * database side-effects via {@code ServiceFactory.findStudentModelSvc()}.
 *
 * @see ShaOne
 * @see ShaZero
 */
@DisplayName("ShaOne Sigma 1 tests")
public class ShaOneTest {

    private static ShaOne shaOne;
    private static ShaZero shaZero;
    private static Method shaOneSigma;
    private static Method shaZeroSigma;
    private static Method referenceBigSig1;

    /**
     * Resolve the private {@code calculateSigma} methods once for the class.
     */
    @BeforeAll
    static void setUpReflection() throws Exception {
        shaOne = new ShaOne(new Student(new Account("test@regis.edu")));
        shaZero = new ShaZero(new Student(new Account("test@regis.edu")));

        shaOneSigma = ShaOne.class.getDeclaredMethod(
                "calculateSigma", String.class, int.class);
        shaOneSigma.setAccessible(true);

        shaZeroSigma = ShaZero.class.getDeclaredMethod(
                "calculateSigma", String.class, int.class);
        shaZeroSigma.setAccessible(true);

        // The tutor's own verified digest implementation, used here as an
        // oracle independent of the objectives package.
        referenceBigSig1 = SHA_256.class.getDeclaredMethod("bigSig1", int.class);
        referenceBigSig1.setAccessible(true);
    }

    /** Sigma 1 as computed by the tutor's verified SHA_256 implementation. */
    private static int sha256BigSig1(int x) throws Exception {
        return (int) referenceBigSig1.invoke(SHA_256.instance(), x);
    }

    /** Convenience wrapper so each test reads like a normal method call. */
    private static String sigma1(String bits, int bitLength) throws Exception {
        return (String) shaOneSigma.invoke(shaOne, bits, bitLength);
    }

    /** Convenience wrapper for the Sigma 0 objective. */
    private static String sigma0(String bits, int bitLength) throws Exception {
        return (String) shaZeroSigma.invoke(shaZero, bits, bitLength);
    }

    /** Left-pad an int to a 32 character binary string. */
    private static String bin32(int value) {
        return String.format("%32s", Integer.toBinaryString(value)).replace(' ', '0');
    }

    /**
     * The reference definition of Sigma 1 from FIPS 180-4, matching
     * {@code SHA_256.bigSig1}.
     */
    private static int referenceSigma1(int x) {
        return Integer.rotateRight(x, 6)
                ^ Integer.rotateRight(x, 11)
                ^ Integer.rotateRight(x, 25);
    }

    @Nested
    @DisplayName("32-bit results match the FIPS 180-4 definition")
    class ReferenceTests {

        @Test
        @DisplayName("Sigma 1 of the SHA-256 initial hash value H(1)")
        void matchesReferenceForKnownWord() throws Exception {
            int x = 0xbb67ae85;
            assertEquals(bin32(referenceSigma1(x)), sigma1(bin32(x), 32));
        }

        @Test
        @DisplayName("Sigma 1 of 0x12345678")
        void matchesReferenceForArbitraryWord() throws Exception {
            int x = 0x12345678;
            assertEquals(bin32(referenceSigma1(x)), sigma1(bin32(x), 32));
        }

        @Test
        @DisplayName("Sigma 1 of all ones is all ones")
        void allOnesIsAllOnes() throws Exception {
            // Each rotation of 0xFFFFFFFF is itself, so a XOR b XOR c == a.
            assertEquals(bin32(referenceSigma1(-1)), sigma1(bin32(-1), 32));
        }

        @Test
        @DisplayName("Sigma 1 of zero is zero")
        void zeroIsZero() throws Exception {
            assertEquals(bin32(0), sigma1(bin32(0), 32));
        }

        @Test
        @DisplayName("agrees with the tutor's own SHA_256.bigSig1")
        void agreesWithProjectImplementation() throws Exception {
            for (int x : new int[] {0, -1, 1, 0x12345678, 0x6a09e667,
                                    0xbb67ae85, Integer.MIN_VALUE, Integer.MAX_VALUE}) {
                assertEquals(bin32(sha256BigSig1(x)), sigma1(bin32(x), 32),
                        "disagreed with SHA_256.bigSig1 for input " + Integer.toHexString(x));
                assertEquals(referenceSigma1(x), sha256BigSig1(x),
                        "SHA_256.bigSig1 itself disagreed with FIPS 180-4");
            }
        }
    }

    @Nested
    @DisplayName("Sigma 1 is not Sigma 0")
    class DistinctFromSigmaZero {

        @Test
        @DisplayName("the two objectives disagree on a 32-bit word")
        void sigmaOneDiffersFromSigmaZero() throws Exception {
            String input = bin32(0x12345678);
            assertNotEquals(sigma0(input, 32), sigma1(input, 32),
                    "Sigma 1 must not compute the same value as Sigma 0");
        }

        @Test
        @DisplayName("Sigma 0 still matches its own FIPS definition")
        void sigmaZeroUnchanged() throws Exception {
            int x = 0x6a09e667;
            int expected = Integer.rotateRight(x, 2)
                    ^ Integer.rotateRight(x, 13)
                    ^ Integer.rotateRight(x, 22);
            assertEquals(bin32(expected), sigma0(bin32(x), 32));
        }
    }

    @Nested
    @DisplayName("output width equals the problem's bit length")
    class WidthTests {

        @Test
        @DisplayName("a 32-bit problem returns exactly 32 characters")
        void width32() throws Exception {
            assertEquals(32, sigma1(bin32(0x9b05688c), 32).length());
        }

        @Test
        @DisplayName("a 16-bit problem returns exactly 16 characters")
        void width16() throws Exception {
            assertEquals(16, sigma1("1011001110001101", 16).length());
        }

        @Test
        @DisplayName("an 8-bit problem returns exactly 8 characters")
        void width8() throws Exception {
            assertEquals(8, sigma1("10110011", 8).length());
        }

        @Test
        @DisplayName("a 4-bit problem returns exactly 4 characters")
        void width4() throws Exception {
            assertEquals(4, sigma1("1011", 4).length());
        }

        @Test
        @DisplayName("an 8-bit problem returns the correct 8-bit value")
        void value8() throws Exception {
            // 8-bit rotations: 6%8=6, 11%8=3, 25%8=1
            int x = 0b10110011;
            int r6 = ((x >>> 6) | (x << 2)) & 0xFF;
            int r3 = ((x >>> 3) | (x << 5)) & 0xFF;
            int r1 = ((x >>> 1) | (x << 7)) & 0xFF;
            String expected = String.format("%8s", Integer.toBinaryString(r6 ^ r3 ^ r1))
                    .replace(' ', '0');
            assertEquals(expected, sigma1("10110011", 8));
        }

        @Test
        @DisplayName("a 4-bit problem returns the correct 4-bit value")
        void value4() throws Exception {
            // 4-bit rotations: 6%4=2, 11%4=3, 25%4=1
            int x = 0b1011;
            int r2 = ((x >>> 2) | (x << 2)) & 0xF;
            int r3 = ((x >>> 3) | (x << 1)) & 0xF;
            int r1 = ((x >>> 1) | (x << 3)) & 0xF;
            String expected = String.format("%4s", Integer.toBinaryString(r2 ^ r3 ^ r1))
                    .replace(' ', '0');
            assertEquals(expected, sigma1("1011", 4));
        }

        @Test
        @DisplayName("results contain only binary digits")
        void onlyBinaryDigits() throws Exception {
            String result = sigma1(bin32(0x1f83d9ab), 32);
            assertEquals(result.replaceAll("[^01]", ""), result,
                    "result should contain only '0' and '1'");
        }
    }

    @Nested
    @DisplayName("input handling")
    class InputTests {

        @Test
        @DisplayName("whitespace in the input is ignored")
        void whitespaceIgnored() throws Exception {
            int x = 0x5be0cd19;
            String spaced = bin32(x).replaceAll("(.{8})", "$1 ").trim();
            assertEquals(sigma1(bin32(x), 32), sigma1(spaced, 32));
        }

        @Test
        @DisplayName("rotation wraps within the problem width, not the machine word")
        void rotationWrapsWithinWidth() throws Exception {
            // For a 4-bit problem every rotation amount reduces modulo 4, so
            // the result must still fit in 4 bits.
            String result = sigma1("1000", 4);
            assertEquals(4, result.length());
        }
    }
}
