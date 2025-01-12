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
 * The sign-in credentials and basic information associated with a user.
 *
 * @author rickb
 */
public class Account {
    /**
     * The user's login id with the format: name@university.edu.
     */
    protected String userId;

    /**
     * An SHA-256 encrypted password.
     */
    protected String password;
    
     /**
     * The first name of this user for this account.
     */
    protected String firstName;
    
    /**
     * The last name of this user for this account.
     */
    protected String lastName;

    /**
     * The security question the user chose to answer.
     */
    protected int securityQuestion;

    /**
     * An SHA-256 encrypted answer to the the security question.
     */
    protected String securityAnswer;

    /**
     * True, if this user is a student.
     */
    protected boolean isStudent;

    /**
     * 
     */
    public Account() {
        this("", "", 0, "");
    }

    /**
     * Initialize this user with the given user id and default password.
     *
     * @param userId
     */
    public Account(String userId) {
        this(userId, "", 0, "");
    }
    
   /**
     * Initialize this user with the given user id and default password.
     *
     * @param userId
     * @param password an SHA-256 encrypted password.
     */
    public Account(String userId, String password) {
        this(userId, password, 0, "");
    }

    /**
     * Initialize this user with the given user id and password
     *
     * @param userId a String (e.g. "name@university.edu").
     * @param password an SHA-256 encrypted password.
     * @param securityQuestion
     * @param securityAnswer
     */
    public Account(String userId, String password, int securityQuestion,
            String securityAnswer) {
        this.userId = userId;
        this.password = password;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        isStudent = true;
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
     *
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
     *
     * @param password a SHA-256 encrypted String
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
      /**
     * Return this StudentUser's first name.
     * @return the name String
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Assign this Student User's first name.
     * @param firstName the name String
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Return this Student User's last name
     * @return the name String
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Assign this Student User's first name.
     * @param lastName the name String
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
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
     * Assign this user's security question.
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
     *
     * @param securityAnswer
     */
    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    /**
     * Return whether this user is a student.
     *
     * @return true, this user is a student, false otherwise
     */
    public boolean isStudent() {
        return isStudent;
    }

    /**
     * Return whether this user is a student.
     *
     * @return true, this user is a student, false otherwise
     */
    public boolean getIsStudent() {
        return isStudent;
    }

    /**
     * Assign whether this user is a student.
     *
     * @param isStudent true, the user is a student.
     */
    public void setIsStudent(boolean isStudent) {
        this.isStudent = isStudent;
    }
    
    public void clear(){
        this.userId = null;
        this.password = null;
        this.firstName = null;
        this.lastName = null;
        this.securityQuestion = 0;
        this.securityAnswer = null;
    }

    /**
     * Return the id and user id of this student.
     *
     * @return
     */
    @Override
    public String toString() {
        return "User: " + userId;
    }
}
