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

import edu.regis.shatu.model.aol.StudentModel;

/**
<<<<<<< HEAD
 * A Student user who is being tutored by the ShaTu tutor.
 * 
 * @author rickb
 */
public class Student extends User {    
    /**
     * The first name of this user.
     */
    protected String firstName;
    
    /**
     * The last name of this student user.
     */
    protected String lastName;
=======
 * A Student is a user who is being tutored by the ShaTu tutor and
 * consequently has an associated student model.
 * 
 * @author rickb
 */
public class Student {    
    /**
     * The account associated with this student.
     */
    private final Account account;
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    
    /**
     * Convenience reference to this Student's student model.
     */
    private StudentModel studentModel;
    
<<<<<<< HEAD
    public Student() {
        this("test@regis.edu", "HelloWorld");
    }
  
    /**
     * Initialize this Student with a default student model and the given user
     * id and password.
     * 
     * @param userId a String (e.g. "name@university.edu").
     * @param password an SHA-256 encrypted password.
     */
    public Student(String userId, String password) { 
        this.userId = userId;
        this.password = password;
        studentModel = new StudentModel();
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
     * Assign this Student User's last name.
     * @param lastName the name String
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
=======
    /**
     * Initialize this student with the given account information.
     * 
     * @param account the account associated with this student.
     */
    public Student(Account account) {
        this.account = account;
        studentModel = new StudentModel(account.getUserId());
    }
    
    /**
     * Return the account associated with this student
     * 
     * @return the Account associated with this student
     */
    public Account getAccount() {
        return account;
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }
    
    /**
     * Return this student's student model
     * 
     * @return a StudentModel
     */
    public StudentModel getStudentModel() {
        return studentModel;
    }

<<<<<<< HEAD
=======
    /**
     * Return this student's student model.
     * 
     * @param studentModel the StudentModel for this student.
     */
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    public void setStudentModel(StudentModel studentModel) {
        this.studentModel = studentModel;
    }

    @Override
    public String toString() {
<<<<<<< HEAD
        return "Student: " + userId;
=======
        return "Student: " + account.getUserId();
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }
}