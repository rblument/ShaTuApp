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
 *
 * @author rickb
 */
public class AddOneStep {
    
    private int messageLength;
    
    private String question;
    
    private String correctAnswer; // Could be removed after question persistence is fleshed out.
    
     private String encodeAsciiAnswer;
    
    private String addOneBitAnswer;
    
    private String padWithZerosAnswer;
    
    private String addMessageLengthAnswer;
    
    private String userAnswer;
   
    /**
     * Constructor
     */
    public AddOneStep() {
        if (this.question == null) {
            this.question = "";  
        }
    }

    /**
     * Setter method for message length
     * @param messageLength 
     */
    public void setMessageLength(int messageLength) {
        this.messageLength = messageLength;
    }
    
    /**
     * Getter method for message length
     * @return 
     */
    public int getMessageLength() {
        return this.messageLength;
    }
    
    /**
     * Setter method for the question
     * @param question 
     */
    public void setQuestion(String question) {
        this.question = question;
    }
    
    /**
     * Getter method for the question
     * @return 
     */
    public String getQuestion() {
        return this.question;
    }
    
    /**
     * Setter method for the correct answer. This was initially introduced
     * before question persistence, this could potentially be refactored/deleted
     * once question persistence is fully fleshed out.
     * @param newResult 
     */
    public void setCorrectAnswer(String newResult) {
        this.correctAnswer = newResult;
    }
    
    /**
     * Getter method for the correct answer. This was initially introduced
     * before question persistence, this could potentially be refactored/deleted
     * once question persistence is fully fleshed out.
     * @return 
     */
    public String getCorrectAnswer() {
        return this.correctAnswer;
    }
    
    /**
     * Setter method for the user's answer
     * @param userResponse 
     */
    public void setUserAnswer(String userResponse) {
        this.userAnswer = userResponse;
    }
    
    /**
     * Getter method for the user answer
     * @return 
     */
    public String getUserAnswer() {
        return this.userAnswer;
    }
    
    /**
     * Setter method for the encode ascii answer
     * @param newEncodeASCIIAnswer 
     */
    public void setEncodeASCIIAnswer(String newEncodeASCIIAnswer) {
        this.encodeAsciiAnswer = newEncodeASCIIAnswer;
    }
    
    /**
     * Setter method for the add one bit answer
     * @param newAddOneBitAnswer 
     */
    public void setAddOneBitAnswer(String newAddOneBitAnswer) {
        this.addOneBitAnswer = newAddOneBitAnswer;
    }
    
    /**
     * Setter method for the pad with zeros answer
     * @param newPadWithZerosAnswer 
     */
    public void setPadWithZerosAnswer(String newPadWithZerosAnswer) {
        this.padWithZerosAnswer = newPadWithZerosAnswer;
    }
    
    /**
     * Setter method for the add message length answer
     * @param newAddMessLenAnswer 
     */
    public void setAddMessageLengthAnswer(String newAddMessLenAnswer) {
        this.addMessageLengthAnswer = newAddMessLenAnswer;
    }
    
    /**
     * Getter method for the encode ascii answer
     * @return 
     */
    public String getEncodeASCIIAnswer() {
        return this.encodeAsciiAnswer;
    }
    
    /**
     * Getter method for the add one bit answer
     * @return 
     */
    public String getAddOneBitAnswer() {
        return this.addOneBitAnswer;
    }
    
    /**
     * Getter method for the pad with zeros answer
     * @return 
     */
    public String getPadWithZerosAnswer() {
        return this.padWithZerosAnswer;
    }
    
    /**
     * Getter method for the add message length answer
     * @return 
     */
    public String getAddMessLenAnswer() {
        return this.addMessageLengthAnswer;
    }
}
