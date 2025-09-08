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

/**
 * A step subtype in which the student must acknowledge a message from the
 * tutor.
 * 
 * @author rickb
 */
public class InformationStep {
    /**
     * The message the student must acknowledge.
     */
    private String msg;

    /**
     * This is the information step method that establishes the message
     * the student must acknowledge.
     */
    public InformationStep() {
        msg = "";
    }

    /**
     * This is the string method that gets the message the student must
     * acknowledge
     * 
     * @return the message in this method. The message returned is the one
     *         the student must acknowledge.
     */
    public String getMsg() {
        return msg;
    }

    /**
     * This is a void method that sets the message object as the one the student
     * must acknowledge.
     * 
     * @param msg establishes the local value for msg.
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
