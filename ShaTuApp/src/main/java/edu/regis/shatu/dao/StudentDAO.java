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
package edu.regis.shatu.dao;

import static edu.regis.shatu.dao.MySqlDAO.URL;
import edu.regis.shatu.err.IllegalArgException;
import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;
import edu.regis.shatu.model.Course;
import edu.regis.shatu.model.KnowledgeComponent;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.aol.Assessment;
import edu.regis.shatu.model.aol.AssessmentLevel;
import edu.regis.shatu.model.aol.ScaffoldLevel;
import edu.regis.shatu.model.aol.StudentModel;
import edu.regis.shatu.svc.ServiceFactory;
import edu.regis.shatu.svc.StudentSvc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object implementing {@link StudentSvc} behaviors (CRUD persistence).
 * 
 * @author rickb
 */
public class StudentDAO extends MySqlDAO implements StudentSvc {
     /**
     * Data directory containing student user account files.
     */
   // private static final String DATA_DIRECTORY = "src/main/java/resources/Data/";
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void create(Student student) throws IllegalArgException, NonRecoverableException {
        final String sql1 = "INSERT INTO Student (UserId, FirstName, LastName) VALUES (?,?,?)";
        final String sql2 = "INSERT INTO StudentModel (UserId, ScaffoldLevel) VALUES (?,?)";

        Connection conn = null;
        PreparedStatement stmt1, stmt2 = null;
        
        String userId = student.getUserId();
        System.out.println("create() in Student DAO");
        // 1. Create the Student into the DB
        try {
            conn = DriverManager.getConnection(URL);
            if (exists(userId)) {
                throw new IllegalArgException("User exists " + userId);
            }
            stmt1 = conn.prepareStatement(sql1);
            
            stmt1.setString(1, userId);
            stmt1.setString(2, student.getFirstName());
            stmt1.setString(3, student.getLastName());

            stmt1.executeUpdate();
       
        } catch (SQLException e) {
            throw new NonRecoverableException("StudentDAO-ERR-1" + e.toString(), e);
        }

        // 2. Create student model in the DB
        try {
            stmt2 = conn.prepareStatement(sql2);
            
            stmt2.setString(1, userId);
            stmt2.setString(2, ScaffoldLevel.EXTREME.toString());
            
            stmt2.executeUpdate();
            
        } catch (SQLException e) {
            throw new NonRecoverableException("StudentDAO-ERR-2" + e.toString(), e); 
        } finally {
            close(stmt2);
            close(conn, stmt1);
        }        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String userId) throws NonRecoverableException {
        boolean exists = false;
        
        Connection conn = null;
        PreparedStatement stmt1 = null;
        
        // SQL statement to check if userId exists in Student table
        final String sql1 = "SELECT '" + userId + 
                "' FROM Student WHERE UserId = '" + userId + "';";
        
        // Check if the UserId is in the Student Table
        try {
            conn = DriverManager.getConnection(URL);
            stmt1 = conn.prepareStatement(sql1);

            ResultSet rs = stmt1.executeQuery();
            
            exists = rs.isBeforeFirst();  
            
            System.out.println("The result of ResultSet is " + exists);
           
       
        } catch (SQLException e) {
            throw new NonRecoverableException("StudentDAO-ERR-1" + e.toString(), e);
        
        } finally {
            close(conn, stmt1);
        }
        
         return exists;
    }
    
   
    /**
     * {@inheritDoc}
     */
    @Override
    public Student retrieve(String userId) throws ObjNotFoundException, NonRecoverableException {
        final String sql = "SELECT FirstName, LastName FROM Student WHERE UserId = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DriverManager.getConnection(URL);
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, userId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Student student = new Student();
                student.setUserId(userId);

                student.setFirstName(rs.getString(1));
                student.setFirstName(rs.getString(2));
    
                student.setStudentModel(ServiceFactory.findStudentModelSvc().retrieve(userId));
                   
                return student;

            } else {
                throw new ObjNotFoundException("Student Id:" + userId);
            }
        } catch (SQLException e) {
            throw new NonRecoverableException("UserDAO-ERR-5" + e.toString(), e);
        } finally {
            close(conn, stmt);
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public StudentModel findModelById(String userId) throws ObjNotFoundException, NonRecoverableException {
        final String sql1 = "SELECT ScaffoldLevel FROM StudentModel WHERE UserId = ?";
        final String sql2 = "SELECT Id,KnowledgeComponentId,AssessmentLevel,Exposures,Successes,Hints FROM Assessment WHERE UserId = ?";

        
        Connection conn = null;
        PreparedStatement stmt1, stmt2 = null;

        StudentModel studentModel;
        
        // 1. Retrieve the Student Model (i.e., Student Model table)
        try {
            conn = DriverManager.getConnection(URL);
            stmt1 = conn.prepareStatement(sql1);

            stmt1.setString(1, userId);

            ResultSet rs = stmt1.executeQuery();

            if (rs.next()) {
                studentModel = new StudentModel(userId);
   
                ScaffoldLevel level = ScaffoldLevel.fromString(rs.getString(1));
                studentModel.setScaffoldLevel(level);

            } else {
                throw new ObjNotFoundException("Student Id:" + userId);
            }
        } catch (SQLException e) {
            throw new NonRecoverableException("UserDAO-ERR-5" + e.toString(), e);
        }
        
        // 2. Get the current course            
        // Note: there is only one course and this might throw excpetions
        // which are not caught in this method.
        Course course = ServiceFactory.findCourseSvc().retrieve(1);
        
        // 3. Retrieve the Assessments (i.e., Assessment table)
        try {    
            stmt2 = conn.prepareStatement(sql1);

            stmt1.setString(1, userId);

            ResultSet rs = stmt1.executeQuery();

            while (rs.next()) {
                KnowledgeComponent knowledgeComponent = course.findOutcome(rs.getInt(2)); 
                AssessmentLevel aLevel = AssessmentLevel.fromString(rs.getString(3));
                
                Assessment assessment = new Assessment(knowledgeComponent, aLevel);
                assessment.setId(rs.getInt(1));
                assessment.setExposures(rs.getInt(4));
                assessment.setSuccessess(rs.getInt(5));
                assessment.setHints(rs.getInt(6));
                
                studentModel.addAssessment(assessment);
            } 
        } catch (SQLException e) {
            throw new NonRecoverableException("UserDAO-ERR-5" + e.toString(), e);
        } finally {
            close(stmt2);
            close(conn, stmt1);
        }
        
        return studentModel;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String userId) throws NonRecoverableException {
        // ToDo: add funcationality
        throw new UnsupportedOperationException("Not supported yet.");
    }
}


