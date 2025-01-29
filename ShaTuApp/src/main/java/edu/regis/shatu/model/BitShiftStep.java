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
 * A bit shift problem encoded as a step
 *
 * @author Chandon Hamel
 */
public class BitShiftStep
{
     /**
     * The object on which the operation is performed.
     */
    private String operand;
    
     /**
     * The result of the bit shift.
     */
    private String result;
    
     /**
     * The length of the current bit.
     */
    private int bitLength;
    
     /**
     * The length the bit is shifted.
     */
    private int shiftLength;
    
     /**
     * The direction of the bit shift Right not Not Right.
     */
    private boolean shiftRight;
    
    // Getter and Setter for operand
    public String getOperand() {
        return operand;
    }

    public void setOperand(String operand) {
        this.operand = operand;
    }

    // Getter and Setter for result
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
    
    // Getter and Setter for bitLength
    public int getBitLength() {
        return bitLength;
    }

    public void setBitLength(int bitLength) {
        this.bitLength = bitLength;
    }

    // Getter and Setter for shiftLength
    public int getShiftLength() {
        return shiftLength;
    }

    public void setShiftLength(int shiftLength) {
        this.shiftLength = shiftLength;
    }

    // Getter and Setter for shiftRight
    public boolean isShiftRight() {
        return shiftRight;
    }

    public void setShiftRight(boolean shiftRight) {
        this.shiftRight = shiftRight;
    }
}
