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
    
    /** The user's login id with the format: name@university.edu. */
    protected String userId;

    /** A SHA-256 encrypted password. */
    protected String password;
    
    /** The first name of this user for this account. */
    protected String firstName;
    
    /** The last name of this user for this account. */
    protected String lastName;

    /** The security question the user chose to answer. */
    protected int securityQuestion;

    /** A SHA-256 encrypted answer to the the security question. */
    protected String securityAnswer;

    /** True, if this user is a student. */
    protected boolean isStudent;

    /**
     * Default constructor for the Account class. Sets up a new account with
     * blank/default values for all fields.
     */
    public Account() {
        this("", "", "", "", 0, ""); // Calls the detailed constructor with empty/default values for all fields.
    }

    /**
     * Constructor that takes only a userID.
     * Allows user to create an account only specifying the login ID.
     * The rest of the values are default.
     * 
     * @param userId The user's login ID (e.g., "name@university.edu").
     */
    public Account(String userId) {
        this(userId, "", "", "", 0, "");
    }
    
    /**
     * Constructor that takes a userId and a password.
     * Allows setting both the user's login ID and password, while leaving
     * the rest of the fields as default values.
     *
     * @param userId The user's login ID (e.g., "name@university.edu").
     * @param password The user's SHA-256 encrypted password.
     */
    public Account(String userId, String password) {
        this(userId, password, "", "", 0, "");
    }

    /**
    * Full constructor for creating an Account.
    * 
    * Sets up all the fields of the account with provided values.
    *
    * @param userId The user's login ID (e.g., "name@university.edu").
    * @param password The user's SHA-256 encrypted password.
    * @param firstName The user's first name.
    * @param lastName The user's last name.
    * @param securityQuestion The ID of the security question selected by the user.
    * @param securityAnswer The user's SHA-256 encrypted answer to the security question.
    */
    public Account(String userId, String password, String firstName,
                String lastName, int securityQuestion, String securityAnswer) {
        this.userId = userId; 
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        isStudent = true;
    }
    
    /**
     * A copy constructor that is used to help ensure that original objects are not
     * overwritten.
     * 
     * @param copy an Account object
     */
    public Account(Account copy) {
        this.userId = copy.userId;
        this.firstName = copy.firstName;
        this.lastName = copy.lastName;
        this.securityQuestion = copy.securityQuestion;
        this.securityAnswer = copy.securityAnswer;
        this.isStudent = copy.isStudent;
    }

    /**
     * Returns this user's user id.
     *
     * @return a String with the format "name@university.edu"
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Assigns this user's user id.
     *
     * @param userId String "name@university.edu"
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns this user's encrypted password.
     *
     * @return a SHA-256 encrypted String
     */
    public String getPassword() {
        return password;
    }

    /**
     * Assigns this user's password.
     *
     * @param password a SHA-256 encrypted String
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Returns this user's first name.
     * 
     * @return the name String
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Assigns this user's first name.
     * 
     * @param firstName a String object
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns this user's last name.
     * 
     * @return the last name as a String
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Assigns this user's first name.
     * 
     * @param lastName a String object
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns this user's security question.
     *
     * @return int representing the question the user answered
     */
    public int getSecurityQuestion() {
        return securityQuestion;
    }

    /**
     * Assigns this user's security question.
     *
     * @param securityQuestion an int object
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
     * Assigns this user's security answer.
     *
     * @param securityAnswer a SHA-256 encrypted String
     */
    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    /**
     * Returns a Boolean value indicating if a user is a student.
     *
     * @return a Boolean value
     */
    public boolean isStudent() {
        return isStudent;
    }

    /**
     * Assigns a Boolean value indicating if this user is a student.
     *
     * @param isStudent a Boolean value
     */
    public void setIsStudent(boolean isStudent) {
        this.isStudent = isStudent;
    }
    
    /**
     * Sets all fields to null and the security question value to zero.
     */
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
