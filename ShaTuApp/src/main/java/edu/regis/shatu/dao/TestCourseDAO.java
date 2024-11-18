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
// TESTCOURSEDAO.java by Brent Krous
// REVIEW: COURSEDAO.java - ADJUST USER & PASSWORD as necessary.
//      Successfully tested, Sept 29.
//  ******* SEE setup.sql for full SQL command list for DB setup. *******
package edu.regis.shatu.dao;

import edu.regis.shatu.model.Course;
import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;

public class TestCourseDAO {
    public static void main(String[] args) {
        CourseDAO courseDAO = new CourseDAO();
        try {
            System.out.println("Retrieving course with ID 1...");
            // Test retrieving a course with id = 1
            Course course = courseDAO.retrieve(1);
            // Display retrieved course info
            System.out.println("Course Title: " + course.getTitle());
            System.out.println("Course Description: " + course.getDescription());
            // Display units
            System.out.println("Units:");
            course.getUnits().forEach(unit -> {
                System.out.println("Unit Title: " + unit.getTitle());
                System.out.println("Unit Description: " + unit.getDescription());
            });
            // Display knowledge components
            System.out.println("Outcomes:");
            course.getOutcomes().forEach(outcome -> {
                System.out.println("Outcome Title: " + outcome.getTitle());
                // Assume BloomLevel is an enum or string ???
                System.out.println("Bloom Level: " + outcome.getBloomLevel());
                System.out.println("Is Domain Focused: " + outcome.isDomainFocus());
            });
        } catch (ObjNotFoundException e) {
            System.err.println("Course not found: " + e.getMessage());
        } catch (NonRecoverableException e) {
            System.err.println("Error retrieving course: " + e.getMessage());
        }
    }
}
