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
 * @author chand
 */
public class PrepScheduleStep
{
     /**
     * Length of random message created to test user knowledge of the Pad 0 Step.
     */
    private int messageLength;
    
    /**
     * Current question for student to answer.
     */
    private String question;
    
    /**
     * Correct answer to the presented question.  Checked against userAnswer
     */
    private String correctAnswer;
    
    /**
     * Answer given by the student.  Checked against correctAnswer
     */
    private String userAnswer;
    
    /**
     * Current step of the 4 step problem
     */
    private int stepNumber;
    
    /**
     * Constructor
     */
    public PrepScheduleStep() {
        
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
    public void setResult(String newResult) {
        this.correctAnswer = newResult;
    }
    
    /**
     * Getter method for the correct answer
     * @return 
     */
    public String getResult() {
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
    
        /**
     * Setter method for the user's answer
     * @param correctAnswer 
     */
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    
    /**
     * Getter method for the user's answer
     * @return 
     */
    public String getCorrectAnswer() {
        return this.correctAnswer;
    }
    
    /**
     * Setter method for the user's answer
     * @param stepNumber 
     */
    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }
    
    /**
     * Getter method for the user's answer
     * @return 
     */
    public int getStepNumber() {
        return this.stepNumber;
    }
}
