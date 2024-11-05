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

import java.util.Random;

/**
 * An example that represents a bit-level operation on one or more bit operands
 * with its result as bit strings and int values.
 * 
 * In general, the operands are expected to be unsigned 32-bit values, but such
 * values might actually be a long value in Java, so we use longs.
 * 
 * Use setOperand1Val(int), setOperandVal2(int), and setResultVal(long) to
 * assign bit string (e.g., operand1, operand2, and result).
 * 
 * @author rickb
 */
public class BitOpExample {
    /**
     * The bit-level operation demonstrated in this example.
     */
    private ExampleType operation;
    
    /**
     * The number of bits in this example's initial operands.
     */
    private int preSize;
    
    /**
     *Operand 1, as a bit string.
     */
    private String operand1;
    
    /**
     * Operand 1, as an int value.
     */
    private long operand1Val;
    
    /**
     * Operand 2, as a bit string.
     */
    private String operand2;
    
    /**
     * Operand 2, as an int value.
     */
    private long operand2Val;
    
    /**
     * The size of the answer string, which my be greater than the pre-size.
     */
    private int postSize;
    
    /**
     * The answer after the operation is performed on the operand(s), as a bit string.
     */
    private String result;
    
    /**
     * The answer after the operation is performed, as a long value.
     */
    private long resultVal;
    
    /**
     * If non-zero, the number of seconds the student has to complete the
     * example. 
     */
    private int timeOut;
    
    public BitOpExample() {
        
    }

    public ExampleType getOperation() {
        return operation;
    }

    public void setOperation(ExampleType operation) {
        this.operation = operation;
    }

    public int getPreSize() {
        return preSize;
    }

    public void setPreSize(int preSize) {
        this.preSize = preSize;
    }

    public String getOperand1() {
        return operand1;
    }

    /**
     * Use setOperand1Val(int) instead.
     * 
     * @param operand1 ignored
     */
    public void setOperand1(String operand1) {
    }

    public long getOperand1Val() {
        return operand1Val;
    }

    public void setOperand1Val(long operand1Val) {
        this.operand1Val = operand1Val;
        
        operand1 = padLeftZeros(Long.toBinaryString(operand1Val), preSize);
    } 

    public String getOperand2() {
        return operand2;
    }

    /**
     * Use setOperand2Val(int) instead.
     * 
     * @param operand2 ignored
     */
    public void setOperand2(String operand2) {
    }
     
    public long getOperand2Val() {
        return operand2Val;
    }

    public void setOperand2Val(long operand2Val) {
        this.operand2Val = operand2Val;
        
        operand2 = padLeftZeros(Long.toBinaryString(operand2Val), preSize);
    }

    public int getPostSize() {
        return postSize;
    }

    public void setPostSize(int postSize) {
        this.postSize = postSize;
    }

    public String getResult() {
        return result;
    }

    /**
     * Use setResultVal(int) instead.
     * 
     * @param result ignored
     */
    public void setResult(String result) {
        this.result = result;
    }

    public long getResultVal() {
        return resultVal;
    }

    public void setResultVal(long resultVal) {
        this.resultVal = resultVal;
        
        result = padLeftZeros(Long.toBinaryString(resultVal), postSize);
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }
    
    /**
     * Generates two random operand bit strings and associated values.
     * 
     * @param bitSize the number of bits (preSize) in the operands.
     */
    public void generatedRandomOperands(int bitSize) {
        Random rnd = new Random();
        
        preSize = preSize;
        
        int maxOperandVal = (int) Math.pow(2.0d, preSize) - 1; // e.g., 2^8 - 1 = 255

        setOperand1Val(rnd.nextInt((maxOperandVal - 1) + 1));

        setOperand2Val(rnd.nextInt((maxOperandVal - 1) + 1));
    }
    
    private String padLeftZeros(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
    
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);

        return sb.toString();
    }
    
    @Override
    public String toString() {
        return operand1 + ":" + operand1Val + 
                "\n" + operand2 + ":" + operand2Val +
                "\n" + result + ":" + resultVal;
    }
}
