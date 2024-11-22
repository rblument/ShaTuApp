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
 * A user with sign-in credentials consisting of a user id and password.
 * 
 * During account creation, an Account object is used instead.
 * 
 * @author rickb
 */
public class User extends Model {
    /**
     * The user's login id (e.g. "name@university.edu").
     */
    protected String userId;

    /**
     * SHA-256 encrypted password.
     */
    protected String password;
    
    /**
     * The security question the user chose to answer.
     */
    protected int securityQuestion;
    
    /**
     * SHA-256 encrypted securityAnswer.
     */
    protected String securityAnswer;
    
    // ToDo: Check if this is Needed for gson.fromJson???
    public User() {
        this("", "", 0, "");
    }
    
    /**
     * Initialize this user with the given user id and default password.
     * 
     * @param userId 
     */
    public User(String userId) {
        this(userId, "", 0, "");
    }
    
    /**
     * Initialize this user with the given user id and password
     * 
     * @param userId a String (e.g. "name@university.edu").
     * @param password an SHA-256 encrypted password.
     * @param securityQuestion
     * @param securityAnswer
     */
    public User(String userId, String password, int securityQuestion, 
            String securityAnswer) { 
	this.userId = userId;
	this.password = password;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
    }
    
    /**
     * Return this user's user id.
     * 
     * @return a String with the format "name@university.edu"
     */
    public String getUserId() {
	return userId;
    }

    /**
     * Assign this user's user id.
     * @param userId String "name@university.edu"
     */
    public void setUserId(String userId) {
	this.userId = userId;
    }

    /**
     * Return this user's password.
     * 
     * @return a SHA-256 encrypted String
     */
    public String getPassword() {
	return password;
    }

    /**
     * Assign this user's password.
     * @param password a SHA-256 encrypted String
     */
    public void setPassword(String password) {
	this.password = password;
    }
    
    /**
     * Return this user's security question.
     * 
     * @return int representing the question the user answered
     */
    public int getSecurityQuestion() {
	return securityQuestion;
    }

    /**
     * Assign this user's security answer.
     * 
     * @param securityQuestion
     */
    public void setSecurityQuestion(int securityQuestion) {
	this.securityQuestion = securityQuestion;
    }
    
    /**
     * Return this user's security answer.
     * 
     * @return a SHA-256 encrypted String
     */
    public String getSecurityAnswer() {
	return securityAnswer;
    }

    /**
     * Assign this user's security answer.
     * @param securityAnswer
     */
    public void setSecurityAnswer(String securityAnswer) {
	this.securityAnswer = securityAnswer;
    }
    
    /**
     * Return the id and user id of this student.
     * @return 
     */
    @Override
    public String toString() {
	return "User: " + userId;
    } 
}
