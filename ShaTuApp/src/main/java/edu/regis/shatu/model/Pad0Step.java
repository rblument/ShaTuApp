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
public class Pad0Step {
    
    private int messageLength;
    
    private String question;
    
    private int correctAnswer;
    
    private String userAnswer;
    
    /**
     * Constructor
     */
    public Pad0Step() {
        
    }
    
    /**
     * Setter method for the message length
     * @param messageLength 
     */
    public void setMessageLength(int messageLength) {
        this.messageLength = messageLength;
    }
    
    /**
     * Getter method for the message length
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
     * Setter method for the correct answer
     * @param newResult 
     */
    public void setResult(int newResult) {
        this.correctAnswer = newResult;
    }
    
    /**
     * Getter method for the correct answer
     * @return 
     */
    public int getResult() {
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
     * Getter method for the user's answer
     * @return 
     */
    public String getUserAnswer() {
        return this.userAnswer;
    }
}
