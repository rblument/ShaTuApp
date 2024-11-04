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
import edu.regis.shatu.model.StudentModelFieldKind;
import edu.regis.shatu.model.aol.Assessment;
import edu.regis.shatu.model.aol.AssessmentLevel;
import edu.regis.shatu.model.aol.ScaffoldLevel;
import edu.regis.shatu.model.aol.StudentModel;
import edu.regis.shatu.svc.CourseSvc;
import edu.regis.shatu.svc.ServiceFactory;
import edu.regis.shatu.svc.StudentModelSvc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A Data Access Object implementing {@link StudentModelSvc } behaviors.
 * 
 * @author rickb
 */
public class StudentModelDAO extends MySqlDAO implements StudentModelSvc {

    /**
     * Initialize this DAO via the parent constructor.
     */
    public StudentModelDAO() {
        super();
    }

    @Override
    public void create(StudentModel model) throws IllegalArgException, NonRecoverableException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public StudentModel retrieve(String userId) throws ObjNotFoundException, NonRecoverableException {
        final String sql = "SELECT ScaffoldLevel FROM StudentModel WHERE UserId = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DriverManager.getConnection(URL);
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, userId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                StudentModel studentModel = new StudentModel(userId);

                studentModel.setScaffoldLevel(ScaffoldLevel.fromString(rs.getString(1)));

                for (Assessment assessment : retrieveAssessments(userId, conn)) {
                    studentModel.addAssessment(assessment);
                }

                return studentModel;

            } else {
                throw new ObjNotFoundException("Student Id:" + userId);
            }
        } catch (SQLException e) {
            throw new NonRecoverableException("UserDAO-ERR-5" + e.toString(), e);
        } finally {
            close(conn, stmt);
        }
    }

    @Override
    public void update(StudentModel model) throws ObjNotFoundException, NonRecoverableException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateAssessment(StudentModel model, Assessment assessment, StudentModelFieldKind field)
            throws NonRecoverableException {

        String sql = "";

        int assessmentId = assessment.getId();
        String userId = model.getUserId();
        int knowledgeComponentId = assessment.getOutcome().getId();

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DriverManager.getConnection(URL);

            switch (field) {
                case ASSESSMENT_LEVEL:
                    sql = "UPDATE Assessment SET AssementLevel = ? WHERE Id = ? ";
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, assessment.getAssessment().title());

                    break;
                case ATTEMPTS:
                    sql = "UPDATE Assessment SET Exposures = ? WHERE Id = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, assessment.getExposures());
                    break;
                    
                case SUCCESSES:
                    sql = "UPDATE Assessment SET Successes = ? WHERE Id = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, assessment.getSuccessess());
                    break;
            }

            stmt.setInt(2, assessmentId);
            
            System.out.println("STMT: **" + stmt.toString() + "**");
            
            stmt.execute();

        } catch (SQLException e) {
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("SQL Error Code: " + e.getErrorCode());
            throw new NonRecoverableException("UserDAO-ERR-5" + e.toString(), e);
        } finally {
            close(conn, stmt);
        }
    }

    @Override
    public void delete(String userId) throws NonRecoverableException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean exists(String userId) throws NonRecoverableException {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(URL);
            return exists(userId, conn);

        } catch (SQLException e) {
            throw new NonRecoverableException("UserDAO-ERR-3" + e.toString(), e);
        } finally {
            close(conn);
        }
    }

    /**
     * Utility that returns whether the student model exists for the given user
     * id in the database.
     *
     * As all students are created with an associated student model, this should
     * always return true.
     *
     * @param userId the user id of the student whose student model is being
     * checked.
     * @param conn an existing connection to the database, which is not closed
     * by this method.
     * @return true, if the student model for the given user id exists in the
     * database, which should always be the case, otherwise false
     * @throws NonRecoverableException (see ex.getCause().getErrorCode())
     */
    private boolean exists(String userId, Connection conn) throws NonRecoverableException {
        final String sql = "SELECT UserId FROM StudentModel WHERE UserId = ?";

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, userId);

            ResultSet rs = stmt.executeQuery();

            return rs.next();

        } catch (SQLException ex) {
            throw new NonRecoverableException("StudentModelDAO-ERR-3" + ex.toString(), ex);
        } finally {
            close(stmt);
        }
    }

    private ArrayList<Assessment> retrieveAssessments(String userId, Connection conn)
            throws ObjNotFoundException, SQLException, NonRecoverableException {

        final String sql = "SELECT Id,KnowledgeComponentId,AssessmentLevel,Exposures,Successes FROM Assessment WHERE UserId = ?";

        CourseSvc courseSvc = ServiceFactory.findCourseSvc();
        Course course = courseSvc.retrieve(1); // Note only one course possible.

        ArrayList<Assessment> assessments = new ArrayList<>();

        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setString(1, userId);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            int assessmentId = rs.getInt(1);

            KnowledgeComponent outcome = course.findKnowledgeComponent(rs.getInt(2));

            AssessmentLevel level = AssessmentLevel.fromString(rs.getString(3));

            Assessment assessment = new Assessment(outcome, level);

            assessment.setId(assessmentId);
            assessment.setExposures(rs.getInt(4));
            assessment.setSuccessess(rs.getInt(5));

            assessments.add(assessment);
        }

        return assessments;
    }
}
