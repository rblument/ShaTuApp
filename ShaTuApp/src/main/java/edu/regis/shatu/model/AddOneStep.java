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
    
    /* The length of the message */
    private int messageLength;
    
    /* Question prompted to user for this step */
    private String question;
    
    /* The corect result */
    private String result; // Correct Answer
    
    /* The answer provided by the user  */
    private String userAnswer;
   
    /**
     * Constructor
     */
    public AddOneStep() {
        
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
     * @return Integer
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
     * @return String
     */
    public String getQuestion() {
        return this.question;
    }
    
    /**
     * Setter method for the correct answer (result)
     * @param newResult 
     */
    public void setResult(String newResult) {
        this.result = newResult;
    }
    
    /**
     * Getter method for the correct answer (result)
     * @return String
     */
    public String getResult() {
        return this.result;
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
     * @return String
     */
    public String getUserAnswer() {
        return this.userAnswer;
    }
}
