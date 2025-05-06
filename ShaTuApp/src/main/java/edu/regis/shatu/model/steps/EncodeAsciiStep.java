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
public class EncodeAsciiStep extends Step {

    private int messageLength;

    /**
     * Constructor
     */
    public EncodeAsciiStep() {
        super(1, 0, StepSubType.ENCODE_ASCII);
        if (this.question == null) {
            this.question = "";
        }
    }

    /**
     * Setter method for message length
     * 
     * @param messageLength
     */
    public void setMessageLength(int messageLength) {
        this.messageLength = messageLength;
    }

    /**
     * Getter method for message length
     * 
     * @return
     */
    public int getMessageLength() {
        return this.messageLength;
    }
}
