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
    
    /**
     * Convenience reference to this Student's student model.
     */
    private StudentModel studentModel;
    
    // ToDo: What are these being used for. If it's for the purpose of 
    // tutoring assessment, then they should be in the studentModel???
    // These are time in milliseonces
    //  Date date = new Date(milliseconds);
    // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z", Locale.US);
    // Optional: Set the time zone if you need a specific one (e.g., UTC)
    // sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    // String formattedDate = sdf.format(date);
    private long lastLogin;
    private long lastLogout;
    
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
    }
    
    /**
     * Return this student's student model
     * 
     * @return a StudentModel
     */
    public StudentModel getStudentModel() {
        return studentModel;
    }

    /**
     * Return this student's student model.
     * 
     * @param studentModel the StudentModel for this student.
     */
    public void setStudentModel(StudentModel studentModel) {
        this.studentModel = studentModel;
    }

    /**
     * 
     * @return time in milliseconds (see comments on this field)
     */
    public long getLastLogin() { 
        return lastLogin; 
    }
    
    /**
     * 
     * @param lastLogin time in milliseconds (see comments on this field)
     */
    public void setLastLogin(long lastLogin) { 
        this.lastLogin = lastLogin; 
    }

    /**
     * 
     * @return time in milliseconds (see comments on this field)
     */
    public long getLastLogout() { 
        return lastLogout; 
    }
    
    /**
     * 
     * @param lastLogout time in milliseconds (see comments on this field)
     */
    public void setLastLogout(long lastLogout) { 
        this.lastLogout = lastLogout; 
    }
    
    @Override
    public String toString() {
        return "Student: " + account.getUserId();
    }
}