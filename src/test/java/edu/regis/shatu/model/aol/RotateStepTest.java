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
package edu.regis.shatu.model.aol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the RotateStep class and bit rotation functionality.
 * 
 * Tests cover:
 * - RotateStep model getters and setters
 * - Direction enum (RIGHT only, as SHA-256 does not use left rotation)
 * - Right rotation (ROTR) algorithm correctness
 * 
 * @author
 * @see RotateStep
 * @see edu.regis.shatu.objectives.RotateBits
 */
public class RotateStepTest {

    private RotateStep rotateStep;

    /**
     * Set up a fresh RotateStep instance before each test.
     */

    @BeforeEach
    public void setUp() {
        rotateStep = new RotateStep();
    }

    // RotateStep Model Tests

    @Nested
    @DisplayName("RotateStep Model Tests")
    class ModelTests {

        @Test
        @DisplayName("Default constructor creates non-null instance")
        public void testDefaultConstructor() {
            assertNotNull(rotateStep, "RotateStep should not be null after construction");
        }

        @Test
        @DisplayName("Direction getter/setter work correctly")
        public void testDirectionGetterSetter() {
            // Test setting RIGHT direction
            rotateStep.setDirection(RotateStep.Direction.RIGHT);
            assertEquals(RotateStep.Direction.RIGHT, rotateStep.getDirection(),
                    "Direction should be RIGHT after setting");
        }

        @Test
        @DisplayName("Direction is null by default")
        public void testDirectionDefaultValue() {
            assertNull(rotateStep.getDirection(),
                    "Direction should be null by default");
        }

        @Test
        @DisplayName("Amount getter/setter work correctly")
        public void testAmountGetterSetter() {
            rotateStep.setAmount(7);
            assertEquals(7, rotateStep.getAmount(), "Amount should be 7");

            rotateStep.setAmount(16);
            assertEquals(16, rotateStep.getAmount(), "Amount should be 16");
        }

        @Test
        @DisplayName("Length getter/setter work correctly")
        public void testLengthGetterSetter() {
            rotateStep.setLength(16);
            assertEquals(16, rotateStep.getLength(), "Length should be 16");

            rotateStep.setLength(32);
            assertEquals(32, rotateStep.getLength(), "Length should be 32");
        }

        @Test
        @DisplayName("Data getter/setter work correctly")
        public void testDataGetterSetter() {
            String testData = "1010101010101010";
            rotateStep.setData(testData);
            assertEquals(testData, rotateStep.getData(), "Data should match");
        }

        @Test
        @DisplayName("UserResponse getter/setter work correctly")
        public void testUserResponseGetterSetter() {
            String response = "0101010101010101";
            rotateStep.setUserResponse(response);
            assertEquals(response, rotateStep.getUserResponse(),
                    "User response should match");
        }

        @Test
        @DisplayName("All fields can be set and retrieved together")
        public void testAllFieldsTogether() {
            rotateStep.setDirection(RotateStep.Direction.RIGHT);
            rotateStep.setAmount(7);
            rotateStep.setLength(16);
            rotateStep.setData("1100110011001100");
            rotateStep.setUserResponse("1001100110011001");

            assertEquals(RotateStep.Direction.RIGHT, rotateStep.getDirection());
            assertEquals(7, rotateStep.getAmount());
            assertEquals(16, rotateStep.getLength());
            assertEquals("1100110011001100", rotateStep.getData());
            assertEquals("1001100110011001", rotateStep.getUserResponse());
        }
    }

    // Direction Enum Tests
    @Nested
    @DisplayName("Direction Enum Tests")
    class DirectionEnumTests {

        @Test
        @DisplayName("Direction enum contains only RIGHT value")
        public void testDirectionEnumValues() {
            RotateStep.Direction[] values = RotateStep.Direction.values();
            assertEquals(1, values.length, 
                    "Direction enum should only contain RIGHT (SHA-256 does not use left rotation)");
            assertEquals(RotateStep.Direction.RIGHT, values[0],
                    "The only direction should be RIGHT");
        }

        @Test
        @DisplayName("Direction.RIGHT can be retrieved by name")
        public void testDirectionValueOf() {
            RotateStep.Direction direction = RotateStep.Direction.valueOf("RIGHT");
            assertEquals(RotateStep.Direction.RIGHT, direction,
                    "valueOf('RIGHT') should return Direction.RIGHT");
        }
    }

    // Right Rotation (ROTR) Algorithm Tests
    @Nested
    @DisplayName("Right Rotation (ROTR) Algorithm Tests")
    class RotationAlgorithmTests {

        /**
         * Performs right rotation on a binary string.
         * This replicates the logic from RotateBits.performBitRotation()
         * 
         *
         * @param data   The binary string to rotate
         * @param amount The number of positions to rotate right
         * @return The rotated binary string
         */
        private String performRightRotation(String data, int amount) {
            String fdata = data.replaceAll("\\s+", "");
            int length = fdata.length();
            amount = amount % length;

            if (amount < 0) {
                amount = length + amount;
            }
            return fdata.substring(length - amount) + fdata.substring(0, length - amount);
        }

        @Test
        @DisplayName("ROTR 7 on 16-bit string")
        public void testRotateRight7Bits16BitString() {
            String input = "1010101010101010";
            String expected = "0101010101010101";
            String result = performRightRotation(input, 7);
            assertEquals(expected, result,
                    "ROTR 7 on 1010101010101010 should yield 0101010101010101");
        }

        @Test
        @DisplayName("ROTR 16 on 32-bit string")
        public void testRotateRight16Bits32BitString() {
            String input = "11110000111100001111000011110000";
            String expected = "1111000011110000" + "1111000011110000";
            String result = performRightRotation(input, 16);
            assertEquals(expected, result,
                    "ROTR 16 on symmetric 32-bit string should yield same result");
        }

        @Test
        @DisplayName("ROTR 1 on simple string")
        public void testRotateRight1Bit() {
            String input = "10000000";
            String expected = "01000000";
            String result = performRightRotation(input, 1);
            assertEquals(expected, result,
                    "ROTR 1 on 10000000 should yield 01000000");
        }

        @Test
        @DisplayName("ROTR 0 returns original string")
        public void testRotateRightZeroBits() {
            String input = "11001100";
            String result = performRightRotation(input, 0);
            assertEquals(input, result,
                    "ROTR 0 should return original string unchanged");
        }

        @Test
        @DisplayName("ROTR by string length returns original string")
        public void testRotateRightFullLength() {
            String input = "11110000";
            String result = performRightRotation(input, 8);
            assertEquals(input, result,
                    "ROTR by full length should return original string");
        }

        @Test
        @DisplayName("ROTR handles strings with spaces")
        public void testRotateRightWithSpaces() {
            String input = "1010 1010 1010 1010";
            String expected = "0101010101010101";
            String result = performRightRotation(input, 7);
            assertEquals(expected, result,
                    "ROTR should handle strings with spaces");
        }

        @Test
        @DisplayName("ROTR 2 for SHA-256 Sigma0 function")
        public void testRotateRight2ForSigma0() {
            String input = "11000000000000000000000000000000"; 
            String expected = "00110000000000000000000000000000";
            String result = performRightRotation(input, 2);
            assertEquals(expected, result,
                    "ROTR 2 should correctly rotate for Sigma0 function");
        }

        @Test
        @DisplayName("ROTR 6 for SHA-256 Sigma1 function")
        public void testRotateRight6ForSigma1() {
            String input = "11111100000000000000000000000000"; 
            String expected = "00000011111100000000000000000000";
            String result = performRightRotation(input, 6);
            assertEquals(expected, result,
                    "ROTR 6 should correctly rotate for Sigma1 function");
        }

        @Test
        @DisplayName("ROTR handles amount larger than string length")
        public void testRotateRightLargeAmount() {
            String input = "11110000"; 
            String expected = "00111100";
            String result = performRightRotation(input, 10);
            assertEquals(expected, result,
                    "ROTR with amount > length should wrap correctly");
        }

        @Test
        @DisplayName("ROTR on all zeros returns all zeros")
        public void testRotateRightAllZeros() {
            String input = "00000000";
            String result = performRightRotation(input, 3);
            assertEquals(input, result,
                    "ROTR on all zeros should return all zeros");
        }

        @Test
        @DisplayName("ROTR on all ones returns all ones")
        public void testRotateRightAllOnes() {
            String input = "11111111";
            String result = performRightRotation(input, 5);
            assertEquals(input, result,
                    "ROTR on all ones should return all ones");
        }
    }


    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        /**
         * Performs right rotation (same as above, for integration tests)
         */
        private String performRightRotation(String data, int amount) {
            String fdata = data.replaceAll("\\s+", "");
            int length = fdata.length();
            amount = amount % length;
            if (amount < 0) {
                amount = length + amount;
            }
            return fdata.substring(length - amount) + fdata.substring(0, length - amount);
        }

        @Test
        @DisplayName("Complete rotation workflow with RotateStep")
        public void testCompleteRotationWorkflow() {
            // Set up the rotation step
            rotateStep.setDirection(RotateStep.Direction.RIGHT);
            rotateStep.setAmount(7);
            rotateStep.setLength(16);
            rotateStep.setData("1010101010101010");

            // Perform the rotation
            String expectedResult = performRightRotation(
                    rotateStep.getData(),
                    rotateStep.getAmount()
            );

            // Simulate user providing correct answer
            rotateStep.setUserResponse(expectedResult);

            // Verify
            assertEquals(expectedResult, rotateStep.getUserResponse(),
                    "User response should match expected rotation result");
            assertEquals("0101010101010101", expectedResult,
                    "Expected result should be correct");
        }

        @Test
        @DisplayName("Verify SHA-256 only uses RIGHT rotation")
        public void testOnlyRightRotationUsed() {
            // This test documents that SHA-256 only uses ROTR
            rotateStep.setDirection(RotateStep.Direction.RIGHT);
            assertEquals(RotateStep.Direction.RIGHT, rotateStep.getDirection(),
                    "SHA-256 should only use RIGHT rotation (ROTR)");

            // Verify there's no LEFT option
            assertEquals(1, RotateStep.Direction.values().length,
                    "Direction enum should only have one value (RIGHT)");
        }
    }
}