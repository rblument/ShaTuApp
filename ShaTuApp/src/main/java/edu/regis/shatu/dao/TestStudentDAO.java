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
// TESTSTUDENTDAO.java by Brent Krous
// REVIEW: STUDENTDAO.java - ADJUST USER & PASSWORD as necessary.
//      Successfully tested, Sept 28.
//  ******* SEE setup.sql for full SQL command list for DB setup. *******
package edu.regis.shatu.dao;

import edu.regis.shatu.err.IllegalArgException;
import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;
import edu.regis.shatu.model.Student;

public class TestStudentDAO {
    public static void main(String[] args) {
        StudentDAO studentDAO = new StudentDAO();
        // Create a new student
        Student student = new Student();
        student.setUserId("test@student.com");
        student.setFirstName("Test");
        student.setLastName("Student");
        try {
            // Test Create
            System.out.println("Inserting student...");
            studentDAO.create(student);
            System.out.println("Student created!");
            // Test Exists
            System.out.println("Checking if student exists...");
            if (studentDAO.exists("test@student.com")) {
                System.out.println("Student exists!");
            } else {
                System.out.println("Student does not exist.");
            }
            // Test Retrieve
            System.out.println("Retrieving student...");
            Student retrievedStudent = studentDAO.retrieve("test@student.com");
            System.out.println("Student Retrieved: " + retrievedStudent.getFirstName() 
                    + " " + retrievedStudent.getLastName());
            // Test Delete
            System.out.println("Deleting student...");
            studentDAO.delete("test@student.com");
            System.out.println("Student deleted!");

            // Check if student still exists after delete
            System.out.println("Checking if student exists after delete...");
            if (studentDAO.exists("test@student.com")) {
                System.out.println("Student still exists (something went wrong).");
            } else {
                System.out.println("Student does not exist anymore (delete successful).");
            }
        } catch (IllegalArgException | NonRecoverableException | ObjNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
