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
package edu.regis.shatu.view.act;

import com.google.gson.Gson;
import edu.regis.shatu.model.Account;
import edu.regis.shatu.svc.ClientRequest;
import edu.regis.shatu.svc.ServerRequestType;
import edu.regis.shatu.svc.SvcFacade;
import edu.regis.shatu.svc.TutorReply;
import edu.regis.shatu.view.SplashFrame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import static javax.swing.Action.MNEMONIC_KEY;
import static javax.swing.Action.SHORT_DESCRIPTION;
import javax.swing.JOptionPane;

/**
 * An MVC controller handling a user GUI gesture requesting to reset their
 * password within the ResetPasswordPanel. (Modeled after NewUserAction)
 *
 * @author mandyroskelley
 */
public class ResetPasswordAction extends ShaTuGuiAction {
    /**
     * The single instance of this reset password action.
     */
    private static final ResetPasswordAction SINGLETON;

    /**
     * Create the singleton for this action, which occurs when this class
     * is loaded by the Java class loaded, as a result of the class being 
     * referenced by executing ResetPassword.instance() in the 
     * initializeComponents() method of the NewAccountPanel class.
     */
    static {
        SINGLETON = new ResetPasswordAction();
    }

    /**
     * Return the singleton instance of this reset password action.
     *
     * @return
     */
    public static ResetPasswordAction instance() {
        return SINGLETON;
    }

    /**
     * Initialize this reset password action with the "Reset Password" text.
     */
    private ResetPasswordAction() {
        super("Reset Password");

        putValue(SHORT_DESCRIPTION, "Reset password for user");
        putValue(MNEMONIC_KEY, KeyEvent.VK_A);
    }

    /**
     * Handle the user's request to reset their password. After password is reset,
     * user will be forwarded to the SplashFrame, where they can log in.
     *
     * @param evt ignored
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        Gson gson = new Gson();
        
        SplashFrame frame = SplashFrame.instance();
        
        Account account = frame.getAccount();

        ClientRequest request = new ClientRequest(ServerRequestType.RESET_PASSWORD);
        request.setData(gson.toJson(account));
       
        TutorReply reply = SvcFacade.instance().tutorRequest(request);

        String msg;
        switch (reply.getStatus()) {
            case "PasswordReset":
                frame.clearNewAccountPanel();
                msg = "Student user account password successfully reset\n\n" +
                        "Press okay and we'll return you to the sign-in screen\n\n" +
                        "Then, please sign-in to the tutor using this account.";
                JOptionPane.showMessageDialog(SplashFrame.instance(), msg);
                frame.selectSplash();
                break;
            case "IllegalUserId":
                msg = "User id does not exist: " + account.getUserId();
                JOptionPane.showMessageDialog(null, msg, "Information",
                                              JOptionPane.INFORMATION_MESSAGE);
                break;
            default: // "ERR" Error should have been logged in tutor.
                msg = "An unexpected error occurred. Please contact ShaTu support";
                JOptionPane.showMessageDialog(null, msg, "Error",
                                              JOptionPane.ERROR_MESSAGE);
        }
    }
}

