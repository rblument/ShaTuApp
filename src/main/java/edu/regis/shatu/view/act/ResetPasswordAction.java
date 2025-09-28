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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import java.awt.Component;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.svc.ClientRequest;
import edu.regis.shatu.svc.ServerRequestType;
import edu.regis.shatu.svc.SvcFacade;
import edu.regis.shatu.svc.TutorReply;
import edu.regis.shatu.view.MainFrame;

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
        Gson gson = getGson();
        
        MainFrame mainFrame = MainFrame.instance();
        
        Account account = mainFrame.getAccount();
        
        String token = mainFrame.getModel().getSecurityToken();

        ClientRequest request = new ClientRequest(ServerRequestType.RESET_PASSWORD);
        request.setUserId(account.getUserId());
        request.setSecurityToken(token);
        request.setData(gson.toJson(account));

        request.setUserId(account.getUserId()); //required for session tracking
        request.setData(gson.toJson(account));

        System.out.println(">>> Submitting RESET_PASSWORD request for: " + account.getUserId());
        System.out.println(">>> Security token: " + token);
        System.out.println(">>> Full JSON account data: " + gson.toJson(account));
  
        TutorReply reply = SvcFacade.instance().tutorRequest(request);

        String msg;
        String status = reply.getStatus();
        System.out.println("ResetPasswordAction: Server reply status = " + status);
        System.out.println("Account JSON sent: " + gson.toJson(account));

        if (status == null) {
            msg = "Server response was invalid. Please try again or contact support.";
            JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        switch (status) {
            case "PasswordReset":
                mainFrame.clearNewAccountPanel();
                msg = "Student user account password successfully reset\n\n" +
                    "Press OK and we'll return you to the sign-in screen\n\n" +
                    "Then, please sign-in to the tutor using this account.";
                JOptionPane.showMessageDialog(mainFrame, msg);
                mainFrame.displayView(MainFrame.ViewName.SPLASH);
                break;

            case "IllegalUserId":
                msg = "User ID does not exist: " + account.getUserId();
                JOptionPane.showMessageDialog(null, msg, "Information", JOptionPane.INFORMATION_MESSAGE);
                break;

            default:
                msg = "An unexpected error occurred. Server responded with status: " + status;
                JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }

    }
}

