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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import edu.regis.shatu.err.InconsistentDBException;
import edu.regis.shatu.model.aol.Timeout;

/**
 * Tests that ProblemDAO reports a Step that refers to a missing InfoMsgStep or
 * Timeout row as a database inconsistency.
 *
 * The methods under test are private, so they are invoked through reflection.
 * A ShaTuDB created with setup_ShaTuDB.sql is needed, otherwise the tests are
 * skipped.
 */
@DisplayName("ProblemDAO database inconsistency tests")
public class ProblemDAOInconsistencyTest {

    /** An id that no seeded row uses. */
    private static final int MISSING_ID = 987654;

    private ProblemDAO dao;
    private Connection conn;
    private Method extractInfoMsgData;
    private Method retrieveTimeout;

    @BeforeEach
    void setUp() throws Exception {
        dao = new ProblemDAO();

        try {
            conn = DriverManager.getConnection(MySqlDAO.URL);
        } catch (Exception e) {
            conn = null;
        }
        assumeTrue(conn != null, "ShaTuDB is not available");

        extractInfoMsgData = ProblemDAO.class.getDeclaredMethod("extractInfoMsgData", int.class, Connection.class);
        extractInfoMsgData.setAccessible(true);

        retrieveTimeout = ProblemDAO.class.getDeclaredMethod("retrieveTimeout", int.class, Connection.class);
        retrieveTimeout.setAccessible(true);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (conn != null) {
            conn.close();
        }
    }

    /** Invoke, unwrapping the reflection wrapper so the real exception is seen. */
    private static Object invoke(Method method, Object target, Object... args) throws Throwable {
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @Test
    @DisplayName("an existing info message is returned")
    void existingInfoMessageIsReturned() throws Throwable {
        // SubStepId 0 is the welcome message in setup_ShaTuDB.sql
        String text = (String) invoke(extractInfoMsgData, dao, 0, conn);

        assertNotNull(text);
        assertTrue(!text.isEmpty());
    }

    @Test
    @DisplayName("a missing info message is an InconsistentDBException with a clear message")
    void missingInfoMessageIsInconsistent() {
        InconsistentDBException ex = assertThrows(InconsistentDBException.class,
                () -> invoke(extractInfoMsgData, dao, MISSING_ID, conn));

        assertTrue(ex.getMessage().contains("ProblemDAO-ERR-12"));
        assertTrue(ex.getMessage().contains("InfoMsgStep"));
        assertTrue(ex.getMessage().contains(String.valueOf(MISSING_ID)));
        assertTrue(!ex.getMessage().contains("ToDo"));
    }

    @Test
    @DisplayName("an existing timeout is returned")
    void existingTimeoutIsReturned() throws Throwable {
        // Timeout 0 is the information message timeout in setup_ShaTuDB.sql
        Timeout timeout = (Timeout) invoke(retrieveTimeout, dao, 0, conn);

        assertNotNull(timeout);
        assertEquals("Info Message", timeout.getType());
    }

    @Test
    @DisplayName("a missing timeout is an InconsistentDBException with a clear message")
    void missingTimeoutIsInconsistent() {
        InconsistentDBException ex = assertThrows(InconsistentDBException.class,
                () -> invoke(retrieveTimeout, dao, MISSING_ID, conn));

        assertTrue(ex.getMessage().contains("ProblemDAO-ERR-13"));
        assertTrue(ex.getMessage().contains("Timeout"));
        assertTrue(ex.getMessage().contains(String.valueOf(MISSING_ID)));
    }
}
