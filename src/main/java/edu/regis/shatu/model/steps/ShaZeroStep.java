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
 * The tutoring step model for the ShaZeroView that models the parameters of the
 * Sigma0 function. Contains operand a
 * and the result that comes back from the server
 */
public class ShaZeroStep extends Step {
    /**
     * The operand that the Sigma0 function operates on
     */
    private String operandA;

    /**
     * Length of the binary string to solve
     */
    private int bitLength;

    public ShaZeroStep() {
        super(1, 0, StepSubType.SHA_ZERO);
    }

    /**
     * Getter for operand A in the Sigma0 function
     *
     * @return operand A for the Sigma0 function
     */
    public String getOperandA() {
        return operandA;
    }

    /**
     * Setter for operand A in the Sigma0 function
     *
     * @param operandA The operand for the Sigma0 function
     */
    public void setOperandA(String operandA) {
        this.operandA = operandA;
    }

    /**
     * Getter for the length of bits for the problem
     *
     * @return The length of bits for the problem
     */
    public int getBitLength() {
        return bitLength;
    }

    /**
     * Setter for the length of bits for the problem
     *
     * @param bitLength The length of bits for the problem
     */
    public void setBitLength(int bitLength) {
        this.bitLength = bitLength;
    }
}
