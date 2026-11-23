package edu.regis.shatu.svc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import edu.regis.shatu.model.Account;

/**
 * Checks that the tutor's ":ResetPassword" handling changes only the password.
 * Skipped when no MySQL database is available.
 */
class ResetPasswordTutorTest {

    private static final String USER_ID = "reset-password-test@regis.edu";
    private static final String OLD_PASSWORD = "old-password-hash";
    private static final String NEW_PASSWORD = "new-password-hash";
    private static final String ANSWER_HASH = "security-answer-hash";
    private static final int QUESTION = 1;

    private final Gson gson = new Gson();
    private AccountSvc accountSvc;

    @BeforeEach
    void createAccount() throws Exception {
        try {
            accountSvc = ServiceFactory.findAccountSvc();
            accountSvc.delete(accountSvc.getPrimaryKey(), USER_ID);
            accountSvc.create(new Account(USER_ID, OLD_PASSWORD, "Reset", "Tester", QUESTION, ANSWER_HASH));
        } catch (Exception | Error e) {
            Assumptions.assumeTrue(false, "No database available: " + e);
        }
    }

    @AfterEach
    void deleteAccount() throws Exception {
        if (accountSvc != null) {
            accountSvc.delete(accountSvc.getPrimaryKey(), USER_ID);
        }
    }

    @Test
    void resetChangesThePasswordButKeepsTheSecurityQuestionAndAnswer() throws Exception {
        // What the reset password panel sends: the user id and the new
        // password, but a blank security question and answer.
        Account fromPanel = new Account(USER_ID, NEW_PASSWORD);

        TutorReply reply = new ShaTuTutor().resetPassword(gson.toJson(fromPanel));

        assertEquals("PasswordReset", reply.getStatus());

        Account stored = accountSvc.retrieve(USER_ID);
        assertEquals(NEW_PASSWORD, stored.getPassword());
        assertEquals(ANSWER_HASH, stored.getSecurityAnswer());
        assertEquals(QUESTION, stored.getSecurityQuestion());
        assertEquals("Reset", stored.getFirstName());
        assertEquals("Tester", stored.getLastName());
    }

    @Test
    void resetForAnUnknownUserIsRejected() throws Exception {
        accountSvc.delete(accountSvc.getPrimaryKey(), USER_ID);

        TutorReply reply = new ShaTuTutor().resetPassword(gson.toJson(new Account(USER_ID, NEW_PASSWORD)));

        assertEquals("IllegalUserId", reply.getStatus());
    }
}
