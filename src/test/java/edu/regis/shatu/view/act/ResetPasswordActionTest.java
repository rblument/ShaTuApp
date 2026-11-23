package edu.regis.shatu.view.act;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Checks which security token a reset password request is sent with.
 */
class ResetPasswordActionTest {

    @BeforeAll
    static void setupHeadlessMode() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void forgotPasswordUsesTheTokenFromVerification() {
        // Nobody is signed in
        assertEquals("verified", ResetPasswordAction.chooseToken(null, "verified"));
        assertEquals("verified", ResetPasswordAction.chooseToken("", "verified"));
    }

    @Test
    void changePasswordUsesTheSignedInSessionsToken() {
        // The reset panel was never initialized by a verification
        assertEquals("session", ResetPasswordAction.chooseToken("session", null));
    }

    @Test
    void aStaleVerificationTokenDoesNotOverrideTheSessionToken() {
        assertEquals("session", ResetPasswordAction.chooseToken("session", "stale"));
    }

    @Test
    void noTokenAtAllIsNull() {
        assertNull(ResetPasswordAction.chooseToken(null, null));
    }
}
