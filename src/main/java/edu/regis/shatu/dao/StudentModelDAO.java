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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;
import edu.regis.shatu.model.Course;
import edu.regis.shatu.model.KnowledgeComponent;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.StudentModelFieldKind;
import edu.regis.shatu.model.aol.Assessment;
import edu.regis.shatu.model.aol.AssessmentLevel;
import edu.regis.shatu.model.aol.ScaffoldLevel;
import edu.regis.shatu.model.aol.StudentModel;
import edu.regis.shatu.svc.CourseSvc;
import edu.regis.shatu.svc.ServiceFactory;
import edu.regis.shatu.svc.StudentModelSvc;

public class StudentModelDAO extends MySqlDAO implements StudentModelSvc {

    public StudentModelDAO() {
        super("StudentModel", "UserId");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(Student student) throws NonRecoverableException {
        final String sql1 = "INSERT INTO StudentModel (UserId, ScaffoldLevel) VALUES (?,?)";
        final String sql2 = 
                "INSERT INTO Assessment " +
                "(UserId, KnowledgeComponentId, AssessmentLevel, Exposures, Successes, Hints, CorrectAnswersRequested) " +
                "VALUES (?,?,?,?,?,?,?)";
        final String sql3 = 
                "INSERT INTO Student " + 
                "(UserId, FirstName, LastName, LastLogin, LastLogout) " +
                "VALUES (?, ?, ?, ?, ?)";
        
        String userId = student.getAccount().getUserId();
        StudentModel studentModel = student.getStudentModel();
        
        try(Connection conn = DriverManager.getConnection(URL)){
            conn.setAutoCommit(false);    // Only commit if all insertions succeed
            
            try(PreparedStatement stmt1 = conn.prepareStatement(sql1);
                PreparedStatement stmt2 = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement stmt3 = conn.prepareStatement(sql3);
                ){
                // Insert into StudentModel
                stmt1.setString(1, userId);
                stmt1.setString(2, ScaffoldLevel.EXTREME.toString());
                stmt1.executeUpdate();
                
                // Insert into Assessment
                for (Assessment assessment : studentModel.getAssessments().values()) {
                    stmt2.setString(1, userId);
                    stmt2.setInt(2, assessment.getOutcome().getId());
                    stmt2.setString(3, "Not Started");
                    stmt2.setInt(4, assessment.getExposures());
                    stmt2.setInt(5, assessment.getSuccessess());
                    stmt2.setInt(6, assessment.getHints());
                    stmt2.setInt(7, assessment.getCorrectAnswersRequested());
                    stmt2.executeUpdate();

                    ResultSet rs = stmt2.getGeneratedKeys();
                    if (rs.next()) {
                        assessment.setId(rs.getInt(1));
                    }
                }
                
                // Insert into Student
                stmt3.setString(1, userId);
                stmt3.setString(2, student.getAccount().getFirstName());
                stmt3.setString(3, student.getAccount().getLastName());
                stmt3.setNull(4, java.sql.Types.TIMESTAMP);    // No login is recorded yet
                stmt3.setNull(5, java.sql.Types.TIMESTAMP);    // No logout is recorded yet
                stmt3.executeUpdate();
                
                conn.commit();
            }
            catch (SQLException e){
                conn.rollback();    // Do not commit if any insertion fails
                throw new NonRecoverableException("UserDAO-ERR-5" + e.toString(), e);
            }
        }
        catch (SQLException e){
            throw new NonRecoverableException("UserDAO-ERR-5" + e.toString(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAssessment(StudentModel model, Assessment assessment, StudentModelFieldKind field)
            throws NonRecoverableException {
        String sql = "";
        int assessmentId = assessment.getId();

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DriverManager.getConnection(URL);
            switch (field) {
                case ASSESSMENT_LEVEL:
                    sql = "UPDATE Assessment SET AssessmentLevel = ? WHERE KnowledgeComponentId = ? ";
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, assessment.getAssessment().title());
                    break;
                case ATTEMPTS:
                    sql = "UPDATE Assessment SET Exposures = ? WHERE KnowledgeComponentId = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, assessment.getExposures());
                    break;
                case SUCCESSES:
                    sql = "UPDATE Assessment SET Successes = ? WHERE KnowledgeComponentId = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, assessment.getSuccessess());
                    break;
                case HINTS:
                    sql = "UPDATE Assessment SET Hints = ? WHERE KnowledgeComponentId = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, assessment.getHints());
                    break;
                case CORRECT_ANSWERS_REQUESTED:
                    sql = "UPDATE Assessment SET CorrectAnswersRequested = ? WHERE KnowledgeComponentId = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, assessment.getCorrectAnswersRequested());
                    break;
                default:
                    break;
            }
            stmt.setInt(2, assessmentId);
            stmt.execute();
        } catch (SQLException e) {
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("SQL Error Code: " + e.getErrorCode());
            throw new NonRecoverableException("UserDAO-ERR-5" + e.toString(), e);
        } finally {
            close(conn, stmt);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> retrieveIncompleteLessons(String userId, String learningCategory)
            throws ObjNotFoundException, NonRecoverableException {

        List<String> lessons = new ArrayList<>();

        // SQL Query: Retrieve all lessons and their assessment levels
        final String sql = """
                    SELECT kc.Title, a.AssessmentLevel
                    FROM Assessment a
                    JOIN KnowledgeComponent kc ON a.KnowledgeComponentId = kc.KnowledgeComponentId
                    WHERE a.UserId = ?
                """;

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String lessonTitle = rs.getString("Title");
                    String assessmentLevel = rs.getString("AssessmentLevel");

                    // Determine category based on AssessmentLevel
                    switch (AssessmentLevel.fromString(assessmentLevel)) {
                        case NOT_STARTED:
                            if (learningCategory.equalsIgnoreCase("Teach Me")) {
                                lessons.add(lessonTitle);
                            } else {
                                // If user is in "Practice" or "Quiz Me" but hasn't done Teach Me
                                lessons.add(lessonTitle + " (Complete in Teach Me first)");
                            }
                            break;
                        case IN_PROGRESS:
                            if (learningCategory.equalsIgnoreCase("Practice")) {
                                lessons.add(lessonTitle);
                            } else {
                                // If user is in "Quiz Me" but hasn't completed Practice
                                lessons.add(lessonTitle + " (Complete in Practice first)");
                            }
                            break;
                        case COMPLETED:
                        case VERY_LOW:
                        case LOW:
                        case MEDIUM:
                        case HIGH:
                        case VERY_HIGH:
                            if (learningCategory.equalsIgnoreCase("Quiz Me")) {
                                lessons.add(lessonTitle);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (SQLException e) {
            throw new NonRecoverableException("Error retrieving incomplete lessons: " + e, e);
        }

        // If no lessons are found, return a single message to indicate completion
        if (lessons.isEmpty()) {
            lessons.add("All lessons completed!");
        }

        return lessons;
    }

    /**
     * Retrieves all lesson titles (excluding lessons with IDs 0, 10, and 20).
     *
     * @param userId the user id.
     * @return a list of all lesson titles.
     * @throws NonRecoverableException if an error occurs during retrieval.
     */
    //@Override
    public List<String> retrieveAllLessons(String userId) throws NonRecoverableException {
        List<String> lessons = new ArrayList<>();
        final String sql = 
                "SELECT kc.Title " +
                "FROM Assessment a " +
                "JOIN KnowledgeComponent kc ON a.KnowledgeComponentId = kc.KnowledgeComponentId " +
                "WHERE a.UserId = ? AND kc.KnowledgeComponentId NOT IN (0, 10, 20) " +
                "ORDER BY kc.KnowledgeComponentId";
        
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lessons.add(rs.getString("Title"));
                }
            }
        } catch (SQLException e) {
            throw new NonRecoverableException("Error retrieving all lessons: " + e.toString(), e);
        }
        return lessons;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssessmentLevel retrieveAssessmentLevel(String userId, String lesson)
            throws ObjNotFoundException, NonRecoverableException {
        final String sql = 
                "SELECT a.AssessmentLevel " +
                "FROM Assessment a " +
                "JOIN KnowledgeComponent kc ON a.KnowledgeComponentId = kc.KnowledgeComponentId " +
                "WHERE a.UserId = ? AND kc.Title = ?";
        
        try (Connection conn = DriverManager.getConnection(URL);
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, lesson);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String levelStr = rs.getString("AssessmentLevel");
                    AssessmentLevel level = AssessmentLevel.fromString(levelStr);
                    if (level != null) {
                        return level;
                    } else {
                        throw new ObjNotFoundException("Invalid AssessmentLevel for lesson: " + lesson);
                    }
                } else {
                    throw new ObjNotFoundException("No assessment record found for lesson: " + lesson);
                }
            }
        } catch (SQLException e) {
            throw new NonRecoverableException("Error retrieving assessment level: " + e.toString(), e);
        }
    }

   /**
     * {@inheritDoc}
     */
    @Override
    public void updateScaffoldLevel(String userId, ScaffoldLevel level)
            throws ObjNotFoundException, NonRecoverableException {
        
        final String sql = "UPDATE StudentModel SET ScaffoldLevel = ? WHERE UserId = ?";
        
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             stmt.setString(1, level.toString());
             stmt.setString(2, userId);
             int rows = stmt.executeUpdate();
             if (rows == 0) {
                 throw new ObjNotFoundException("No student model found for user: " + userId);
             }
        } catch (SQLException e) {
             throw new NonRecoverableException("Error updating scaffold level: " + e.toString(), e);
        }
    }
    
    /**
     * Retrieve all assessments associated with a student.
     * 
     * @param userId a String object representing the student's user id
     * @param conn a database connection object
     * @return an ArrayList of Assessment objects
     * @throws ObjNotFoundException
     * @throws SQLException
     * @throws NonRecoverableException
     */
    private ArrayList<Assessment> retrieveAssessments(String userId, Connection conn)
            throws ObjNotFoundException, SQLException, NonRecoverableException {

        final String sql =
                "SELECT AssessmentId, KnowledgeComponentId, AssessmentLevel, Exposures, Successes, Hints, CorrectAnswersRequested " +
                "FROM Assessment WHERE UserId = ?";

        CourseSvc courseSvc = ServiceFactory.findCourseSvc();
        Course course = courseSvc.retrieve(1); // Note only one course possible.

        ArrayList<Assessment> assessments = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, userId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int assessmentId = rs.getInt(1);
            int knowledgeComponentId = rs.getInt(2);
            KnowledgeComponent outcome = course.findKnowledgeComponent(knowledgeComponentId);
            AssessmentLevel level = AssessmentLevel.fromString(rs.getString(3));
            Assessment assessment = new Assessment(outcome, level);
            assessment.setId(knowledgeComponentId);
            assessment.setExposures(rs.getInt(4));
            assessment.setSuccessess(rs.getInt(5));
            assessment.setHints(rs.getInt(6));
            assessment.setCorrectAnswersRequested(rs.getInt(7));
            assessments.add(assessment);
        }
        return assessments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String userId) throws NonRecoverableException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String userId) throws NonRecoverableException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recordLoginEvent(String userId, long timestamp) throws NonRecoverableException {
        String sql = "UPDATE student SET LastLogin = ? WHERE userId = ?";
        Timestamp tStamp = new Timestamp(timestamp);    // Store date in a readable format
        
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setTimestamp(1, tStamp);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        }
        catch (SQLException ex) {
            throw new NonRecoverableException("Error recording login event: " + ex.toString(), ex);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void recordLogoutEvent(String userId, long timestamp) throws NonRecoverableException {
        String sql = "UPDATE student SET LastLogout = ? WHERE userId = ?";
        Timestamp tStamp = new Timestamp(timestamp);    // Store date in a readable format
        
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setTimestamp(1, tStamp);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        }
        catch (SQLException ex) {
            throw new NonRecoverableException("Error recording logout event: " + ex.toString(), ex);
        }
    }
}
