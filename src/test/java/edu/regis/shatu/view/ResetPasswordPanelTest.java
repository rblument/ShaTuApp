package edu.regis.shatu.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import edu.regis.shatu.model.Account;

/**
 * Checks that the reset password panel shows the user that was just verified
 * and keeps the security token needed to reset the password.
 */
class ResetPasswordPanelTest {

    @BeforeAll
    static void setupHeadlessMode() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void panelDisplaysTheVerifiedUser() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ResetPasswordPanel panel = new ResetPasswordPanel();
            assertEquals("", panel.userId.getText());

            panel.setModel(new Account("student@regis.edu"));
            panel.setSecurityToken("a-token");

            assertEquals("student@regis.edu", panel.userId.getText());
            assertFalse(panel.userId.isEditable());
            assertEquals("a-token", panel.getSecurityToken());
        });
    }

    @Test
    void theAccountSentToTheTutorHasTheUserIdAndTheHashedPassword() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ResetPasswordPanel panel = new ResetPasswordPanel();
            panel.setModel(new Account("student@regis.edu"));
            panel.pass1.setText("NewPassw0rd!");
            panel.pass2.setText("NewPassw0rd!");

            Account account = panel.getModel();

            assertEquals("student@regis.edu", account.getUserId());
            assertNotEquals("", account.getPassword());
            assertNotEquals("NewPassw0rd!", account.getPassword()); // hashed
        });
    }

    @Test
    void settingAnEmptyAccountForgetsTheUserAndPassword() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ResetPasswordPanel panel = new ResetPasswordPanel();
            panel.setModel(new Account("student@regis.edu"));
            panel.setSecurityToken("a-token");
            panel.pass1.setText("NewPassw0rd!");

            panel.setModel(new Account());
            panel.setSecurityToken(null);

            assertEquals("", panel.userId.getText());
            assertEquals(0, panel.pass1.getPassword().length);
            assertNull(panel.getSecurityToken());
        });
    }
}
