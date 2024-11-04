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
 * A Decorator wrapping user and student information sans the student model.
 * 
 * Separating user and student allows keeping the user's password separate 
 * from the Student object with the exception of during initial account creation
 * within this account object.
 * 
 * @author rickb
 */
public class Account {
  /**
     * The user's login id (e.g. "name@university.edu").
     */
    protected String userId;
    protected String password;
    protected String firstName;
    protected String lastName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
     public void clear(){
        this.userId = null;
        this.password = null;
        this.firstName = null;
        this.lastName = null;
    }
    
    public Account() {
    }
}
