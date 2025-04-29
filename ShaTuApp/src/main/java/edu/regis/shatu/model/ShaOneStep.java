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
package edu.regis.shatu.model;

/**
 * The tutoring step model for the ShaOneView that models the parameters of the Sigma1 function. Contains operand a
 * and the result that comes back from the server
 */
public class ShaOneStep {
        /**
     * The operand that the Sigma0 function operates on
     */
    private String operandA;

    /**
     * Length of the binary string to solve
     */
    private int bitLength;

    /**
     * The result of the Sigma0 function
     */
    private String result;

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
     * Getter for the Sigma0 function result
     *
     * @return The result value of the Sigma0 function
     */
    public String getResult() {
        return result;
    }

    /**
     * Setter for the Sigma0 function result
     *
     * @param result The result of the Sigma0 function
     */
    public void setResult(String result) {
        this.result = result;
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
