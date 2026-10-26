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
 *
 * @author rickb
 */
public class MessageLenStep extends Step {

    private String lengthBits64;
    private int messageLength;

    /**
     * Constructor
     */
    public MessageLenStep() {
        super(1, 0, StepSubType.ADD_MSG_LENGTH);
    }

    /** The 64-bit length field appended during SHA-256 preprocessing. */
    public String getLengthBits64() {
        return lengthBits64;
    }

    public void setLengthBits64(String lengthBits64) {
        this.lengthBits64 = lengthBits64;
    }

    /**
     * Setter method for the message length
     * 
     * @param messageLength
     */
    public void setMessageLength(int messageLength) {
        this.messageLength = messageLength;
    }

    /**
     * Getter method for the message length
     * 
     * @return
     */
    public int getMessageLength() {
        return this.messageLength;
    }
}
