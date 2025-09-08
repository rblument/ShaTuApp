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

import edu.regis.shatu.model.aol.StepSubType;

/**
 * MajorityStep represents a single step in the SHA-256 hashing process,
 * specifically for calculating the "majority" function of the algorithm.
 * This class holds three operands (A, B, C) and calculates their majority
 * value as part of the hashing computation. It also supports optional
 * visualization through a truth table.
 * 
 * @author mwemapowanga
 */

public class MajorityStep extends Step {

    /**
     * First input operand for the majority function.
     * Represents a binary string.
     */
    private String operandA;

    /**
     * Second input operand for the majority function.
     * Represents a binary string.
     */
    private String operandB;

    /**
     * Third input operand for the majority function.
     * Represents a binary string.
     */
    private String operandC;

    /**
     * Flag indicating whether the truth table (used for teaching/visualization) is
     * visible.
     */
    private boolean isTruthTableVisible;

    /**
     * The number of bits to compare in each operand during the majority
     * computation.
     * Typically, this aligns with the bit width of the SHA-256 algorithm (32 bits).
     */
    private int bitLength;

    public MajorityStep() {
        super(1, 0, StepSubType.MAJORITY_FUNCTION);
    }

    /**
     * Retrieves the first operand (A).
     *
     * @return operandA as a binary string
     */
    public String getOperandA() {
        return operandA;
    }

    /**
     * Sets the first operand (A).
     *
     * @param operandA binary string representing the first operand
     */
    public void setOperandA(String operandA) {
        this.operandA = operandA;
    }

    /**
     * Retrieves the second operand (B).
     *
     * @return operandB as a binary string
     */
    public String getOperandB() {
        return operandB;
    }

    /**
     * Sets the second operand (B).
     *
     * @param operandB binary string representing the second operand
     */
    public void setOperandB(String operandB) {
        this.operandB = operandB;
    }

    /**
     * Retrieves the third operand (C).
     *
     * @return operandC as a binary string
     */
    public String getOperandC() {
        return operandC;
    }

    /**
     * Sets the third operand (C).
     *
     * @param operandC binary string representing the third operand
     */
    public void setOperandC(String operandC) {
        this.operandC = operandC;
    }

    /**
     * Retrieves the number of bits involved in the computation.
     *
     * @return the bit length
     */
    public int getBitLength() {
        return bitLength;
    }

    /**
     * Sets the number of bits to be used in the majority computation.
     *
     * @param bitLength the number of bits to compare (e.g., 32 for SHA-256)
     */
    public void setBitLength(int bitLength) {
        this.bitLength = bitLength;
    }

}
