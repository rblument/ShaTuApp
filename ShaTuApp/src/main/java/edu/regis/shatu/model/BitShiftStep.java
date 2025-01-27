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
    private String operand;         //Operation to perfomr on bit 
    private String result;          //Result of bit shift
    private int bitLength;          //Length of bit
    private int shiftLength;        //Length bit is shifted
    private boolean shiftRight;     //Direction of shift Right or Not Right
    
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
