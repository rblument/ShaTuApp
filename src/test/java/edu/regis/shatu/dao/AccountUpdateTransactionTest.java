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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;
import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.Student;

/**
 * Tests that AccountDAO.update(Account, Student) updates the Account and
 * Student tables in a single transaction.
 *
 * Needs a ShaTuDB created with setup_ShaTuDB.sql, otherwise the tests are skipped.
 */
@DisplayName("Account and Student update transaction tests")
public class AccountUpdateTransactionTest {

    private static final String USER_ID = "update-transaction-test@regis.edu";

    private AccountDAO dao;

    @BeforeEach
    void setUp() throws Exception {
        dao = new AccountDAO();

        boolean reachable = false;
        try (Connection conn = DriverManager.getConnection(MySqlDAO.URL)) {
            reachable = true;
        } catch (Exception e) {
            // No database
        }
        assumeTrue(reachable, "ShaTuDB is not available");

        cleanUp();
        execute("INSERT INTO Account (UserId, Password, FirstName, LastName, Question, Answer, IsStudent) "
                + "VALUES (?, 'pw', 'Old', 'Name', 0, 'a', 1)");
        execute("INSERT INTO Student (UserId, FirstName, LastName) VALUES (?, 'Old', 'Name')");
    }

    @AfterEach
    void cleanUp() throws Exception {
        if (MySqlDAO.URL == null) {
            return;
        }
        try {
            execute("DELETE FROM Student WHERE UserId = ?");
            execute("DELETE FROM Account WHERE UserId = ?");
        } catch (Exception e) {
            // Database unavailable, in which case the test was skipped
        }
    }

    private void execute(String sql) throws Exception {
        try (Connection conn = DriverManager.getConnection(MySqlDAO.URL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, USER_ID);
            stmt.executeUpdate();
        }
    }

    private String firstName(String table) throws Exception {
        try (Connection conn = DriverManager.getConnection(MySqlDAO.URL);
                PreparedStatement stmt = conn.prepareStatement("SELECT FirstName FROM " + table + " WHERE UserId = ?")) {
            stmt.setString(1, USER_ID);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getString(1);
            }
        }
    }

    private Account accountNamed(String firstName) {
        return new Account(USER_ID, "pw", firstName, "Name", 0, "a");
    }

    @Test
    @DisplayName("both tables are updated")
    void bothTablesUpdated() throws Exception {
        Account account = accountNamed("New");

        dao.update(account, new Student(account));

        assertEquals("New", firstName("Account"));
        assertEquals("New", firstName("Student"));
    }

    @Test
    @DisplayName("a failed Student update rolls back the Account update")
    void failedStudentUpdateRollsBackAccount() throws Exception {
        // Fits Account.FirstName (VARCHAR 256) but not Student.FirstName (VARCHAR 30).
        Account account = accountNamed("X".repeat(40));

        assertThrows(NonRecoverableException.class, () -> dao.update(account, new Student(account)));

        assertEquals("Old", firstName("Account"));
        assertEquals("Old", firstName("Student"));
    }

    @Test
    @DisplayName("an unknown account is reported and nothing changes")
    void unknownAccountIsReported() throws Exception {
        Account account = new Account("nobody-" + USER_ID, "pw", "New", "Name", 0, "a");

        assertThrows(ObjNotFoundException.class, () -> dao.update(account, new Student(account)));

        assertEquals("Old", firstName("Student"));
    }

    @Test
    @DisplayName("ShaTuTutor.updateAccount() updates both tables, or neither on failure")
    void tutorUpdateAccountIsAtomic() throws Exception {
        com.google.gson.Gson gson = new com.google.gson.Gson();
        edu.regis.shatu.svc.ShaTuTutor tutor = new edu.regis.shatu.svc.ShaTuTutor();

        assertEquals("Success", tutor.updateAccount(gson.toJson(accountNamed("New"))).getStatus());
        assertEquals("New", firstName("Account"));
        assertEquals("New", firstName("Student"));

        assertEquals(":ERR", tutor.updateAccount(gson.toJson(accountNamed("X".repeat(40)))).getStatus());
        assertEquals("New", firstName("Account"));
        assertEquals("New", firstName("Student"));
    }
}
