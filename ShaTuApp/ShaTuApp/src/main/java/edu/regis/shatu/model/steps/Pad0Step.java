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
 * Stores created question, correct answer, student's answer and length of
 * randomly generated message used for testing students knowledge. Provides
 * getter and setter methods to access class fields.
 * 
 * @author rickb
 */
public class Pad0Step extends Step {

    /**
     * Length of random message created to test user knowledge of the Pad 0 Step.
     */
    private int messageLength;

    /**
     * Constructor
     */
    public Pad0Step() {
        super(1, 0, StepSubType.PAD_ZEROS);
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
