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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import edu.regis.shatu.model.CompletedStep;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.aol.TutoringMode;
import edu.regis.shatu.model.steps.Step;

/**
 * Tests {@link CompletedStepDAO} against a real ShaTuDB that was created with
 * setup_ShaTuDB.sql. The tests are skipped when that database is not reachable.
 */
@DisplayName("CompletedStepDAO tests")
public class CompletedStepDAOTest {

    private static final String USER_ID = "completed-step-test@regis.edu";
    private static final int COURSE_ID = 1;

    private CompletedStepDAO dao;

    @BeforeEach
    void setUp() throws Exception {
        dao = new CompletedStepDAO();

        boolean reachable = false;
        try (Connection conn = DriverManager.getConnection(MySqlDAO.URL);
                PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM CompletedStep")) {
            stmt.executeQuery();
            reachable = true;
        } catch (Exception e) {
            // Not reachable, or setup_ShaTuDB.sql has not been run
        }
        assumeTrue(reachable, "ShaTuDB with the CompletedStep table is not available");

        cleanUp();
        try (Connection conn = DriverManager.getConnection(MySqlDAO.URL);
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO Account (UserId, Password, FirstName, LastName, Question, Answer, IsStudent) "
                        + "VALUES (?, 'pw', 'Test', 'User', 0, 'a', 1)")) {
            stmt.setString(1, USER_ID);
            stmt.executeUpdate();
        }
    }

    @AfterEach
    void cleanUp() throws Exception {
        if (MySqlDAO.URL == null) {
            return;
        }
        try (Connection conn = DriverManager.getConnection(MySqlDAO.URL);
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM Account WHERE UserId = ?")) {
            stmt.setString(1, USER_ID); // CompletedStep rows cascade
            stmt.executeUpdate();
        } catch (Exception e) {
            // Database unavailable, in which case the test was skipped
        }
    }

    private static StepCompletion completion(int stepId, StepSubType subType, int hints) {
        StepCompletion completion = new StepCompletion(new Step(stepId, 0, subType), "{}");
        completion.setHintsGiven(hints);
        return completion;
    }

    @Test
    @DisplayName("a seeded step is stored against its own task and the unit for the mode")
    void seededStepIsLinkedToTaskAndUnit() throws Exception {
        CompletedStep done = dao.record(USER_ID, COURSE_ID, TutoringMode.SEE_ONE,
                completion(1, StepSubType.ENCODE_ASCII, 0));

        assertTrue(done.getId() > 0);
        assertEquals(1, done.getStepId());
        assertEquals(10, done.getTaskId());
        assertEquals(0, done.getUnitId());
    }

    @Test
    @DisplayName("a generated step id falls back to the first task of that sub type")
    void generatedStepFallsBackToSubType() throws Exception {
        // example() builds steps with id 1, which is not an ADD_ONE_BIT step.
        CompletedStep done = dao.record(USER_ID, COURSE_ID, TutoringMode.DO_ONE,
                completion(1, StepSubType.ADD_ONE_BIT, 2));

        assertEquals(2, done.getStepId());
        assertEquals(20, done.getTaskId());
        assertEquals(1, done.getUnitId());
        assertEquals(2, done.getHintsGiven());
    }

    @Test
    @DisplayName("each tutoring mode maps to its own unit")
    void modesMapToUnits() throws Exception {
        assertEquals(0, dao.record(USER_ID, COURSE_ID, TutoringMode.SEE_ONE,
                completion(110, StepSubType.CHOICE_FUNCTION, 0)).getUnitId());
        assertEquals(1, dao.record(USER_ID, COURSE_ID, TutoringMode.DO_ONE,
                completion(110, StepSubType.CHOICE_FUNCTION, 0)).getUnitId());
        assertEquals(2, dao.record(USER_ID, COURSE_ID, TutoringMode.TEACH_ONE,
                completion(110, StepSubType.CHOICE_FUNCTION, 0)).getUnitId());
    }

    @Test
    @DisplayName("a sub type with no Step row is still recorded, with unresolved ids")
    void unknownStepIsStillRecorded() throws Exception {
        // ADD_BITS has no Task or Step row in the seed data.
        CompletedStep done = dao.record(USER_ID, COURSE_ID, TutoringMode.DO_ONE,
                completion(1, StepSubType.ADD_BITS, 0));

        assertEquals(-1, done.getTaskId());
        assertEquals(-1, done.getStepId());

        List<CompletedStep> stored = dao.retrieve(USER_ID);
        assertEquals(1, stored.size());
        assertEquals(-1, stored.get(0).getTaskId());
        assertEquals(StepSubType.ADD_BITS, stored.get(0).getSubType());
    }

    @Test
    @DisplayName("retrieve returns a student's completions, optionally for one unit")
    void retrieveByUserAndUnit() throws Exception {
        dao.record(USER_ID, COURSE_ID, TutoringMode.SEE_ONE, completion(1, StepSubType.ENCODE_ASCII, 0));
        dao.record(USER_ID, COURSE_ID, TutoringMode.DO_ONE, completion(2, StepSubType.ADD_ONE_BIT, 1));
        dao.record(USER_ID, COURSE_ID, TutoringMode.DO_ONE, completion(30, StepSubType.PAD_ZEROS, 0));

        assertEquals(3, dao.retrieve(USER_ID).size());
        assertEquals(1, dao.retrieve(USER_ID, 0).size());

        List<CompletedStep> doOne = dao.retrieve(USER_ID, 1);
        assertEquals(2, doOne.size());
        assertEquals(StepSubType.ADD_ONE_BIT, doOne.get(0).getSubType());
        assertEquals(TutoringMode.DO_ONE, doOne.get(0).getTutoringMode());
        assertEquals(StepSubType.PAD_ZEROS, doOne.get(1).getSubType());

        assertTrue(dao.retrieve("nobody@regis.edu").isEmpty());
    }

    @Test
    @DisplayName("a completion with no date is stored with the current time")
    void missingDateFallsBackToNow() throws Exception {
        StepCompletion completion = completion(1, StepSubType.ENCODE_ASCII, 0);
        completion.setDate(0);

        long before = System.currentTimeMillis();
        CompletedStep done = dao.record(USER_ID, COURSE_ID, TutoringMode.SEE_ONE, completion);

        assertTrue(done.getCompletionDate() >= before);
        assertEquals(1, dao.retrieve(USER_ID).size());
    }
}
