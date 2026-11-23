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
package edu.regis.shatu.svc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import edu.regis.shatu.dao.MySqlDAO;
import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.CompletedStep;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.aol.TutoringMode;
import edu.regis.shatu.model.steps.AddOneStep;
import edu.regis.shatu.model.steps.Step;

/**
 * Checks that ShaTuTutor.completedStep() persists correctly answered steps for
 * the student, in the unit matching their tutoring mode.
 *
 * Needs a ShaTuDB created with setup_ShaTuDB.sql, otherwise the test is skipped.
 */
@DisplayName("ShaTuTutor completed step persistence")
public class CompletedStepTutorTest {

    private static final String USER_ID = "completed-step-tutor-test@regis.edu";
    private static final Gson GSON = new Gson();

    private ShaTuTutor tutor;

    @BeforeEach
    void setUp() throws Exception {
        boolean reachable = false;
        try {
            ServiceFactory.findCompletedStepSvc().retrieve(USER_ID);
            reachable = true;
        } catch (Exception e) {
            // No database, or setup_ShaTuDB.sql has not been run
        }
        assumeTrue(reachable, "ShaTuDB with the CompletedStep table is not available");

        cleanUp();

        tutor = new ShaTuTutor();
        Account account = new Account(USER_ID, "pw", "Test", "User", 0, "a");
        assertEquals("Created", tutor.createAccount(GSON.toJson(account)).getStatus());
        assertEquals("Authenticated", tutor.signIn(GSON.toJson(new Account(USER_ID, "pw"))).getStatus());

        // request() does this before every step request; the test calls the tutor directly.
        java.lang.reflect.Field model = ShaTuTutor.class.getDeclaredField("studentModel");
        model.setAccessible(true);
        Field student = ShaTuTutor.class.getDeclaredField("student");
        student.setAccessible(true);
        model.set(tutor, ((edu.regis.shatu.model.Student) student.get(tutor)).getStudentModel());
    }

    @AfterEach
    void cleanUp() throws Exception {
        if (MySqlDAO.URL == null) {
            return;
        }
        try (Connection conn = DriverManager.getConnection(MySqlDAO.URL)) {
            for (String table : new String[]{"StudentModel", "Student", "Account"}) {
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM " + table + " WHERE UserId = ?")) {
                    stmt.setString(1, USER_ID); // Account cascades to CompletedStep, Assessment, TutoringSession
                    stmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            // Database unavailable, in which case the test was skipped
        }
    }

    private String addOneCompletion(String answer) {
        AddOneStep data = new AddOneStep();
        data.setQuestion("ab");
        data.setUserAnswer(answer);

        return GSON.toJson(new StepCompletion(new Step(1, 0, StepSubType.ADD_ONE_BIT), GSON.toJson(data)));
    }

    @Test
    @DisplayName("correct answers are recorded, moving from See One to Do One as the student progresses")
    void correctAnswerIsRecorded() throws Exception {
        tutor.completedStep(addOneCompletion("01100001 01100010 1"));
        tutor.completedStep(addOneCompletion("01100001 01100010 1"));

        List<CompletedStep> done = ServiceFactory.findCompletedStepSvc().retrieve(USER_ID);
        assertEquals(2, done.size());

        assertEquals(StepSubType.ADD_ONE_BIT, done.get(0).getSubType());
        assertEquals(TutoringMode.SEE_ONE, done.get(0).getTutoringMode());
        assertEquals(0, done.get(0).getUnitId());
        assertEquals(20, done.get(0).getTaskId());
        assertEquals(2, done.get(0).getStepId());

        assertEquals(TutoringMode.DO_ONE, done.get(1).getTutoringMode());
        assertEquals(1, done.get(1).getUnitId());
    }

    @Test
    @DisplayName("an incorrect answer is not recorded")
    void incorrectAnswerIsNotRecorded() throws Exception {
        tutor.completedStep(addOneCompletion("0"));

        assertEquals(0, ServiceFactory.findCompletedStepSvc().retrieve(USER_ID).size());
    }
}
