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
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.svc.ClientRequest;
import edu.regis.shatu.svc.ServerRequestType;
import edu.regis.shatu.svc.SvcFacade;
import edu.regis.shatu.svc.TutorReply;
import edu.regis.shatu.view.SplashFrame;
import edu.regis.shatu.model.Student;

public class UpdateAcctAction extends ShaTuGuiAction {
    /**
     * Exceptions occurring in this class are also logged to this logger.
     */
    private static final Logger LOGGER
            = Logger.getLogger(UpdateAcctAction.class.getName());

    /**
     * The single instance of this update account action.
     */
    private static final UpdateAcctAction SINGLETON;

    
    static {
        SINGLETON = new UpdateAcctAction();
    }

    /**
     * Return the singleton instance of this sign-in action.
     *
     * @return
     */
    public static UpdateAcctAction instance() {
        return SINGLETON;
    }

    /**
     * Initialize this update account action with the "Update Account" text.
     */
    private UpdateAcctAction() {
        super("Update Account");

        putValue(SHORT_DESCRIPTION, "Update user data");
        putValue(MNEMONIC_KEY, KeyEvent.VK_A);
    }

    /**
     * Handle the user's request to update a student user account by
     * forwarding the account information in the UpdateAccountPanel to the tutor.
     *
     * @param evt ignored
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        Gson gson = new Gson();
        
        SplashFrame frame = SplashFrame.instance();
        
        Account account = frame.getAccount();

        ClientRequest request = new ClientRequest(ServerRequestType.UPDATE_ACCOUNT);
        request.setData(gson.toJson(account));
       
        TutorReply reply = SvcFacade.instance().tutorRequest(request);

        String msg;
        switch (reply.getStatus()) {
            case "Updated":
                frame.clearNewAccountPanel();
                msg = "Student user account successfully updated\n\n" +
                        "Press okay and we'll return you to the sign-in screen\n\n" +
                        "Then, please sign-in to the tutor using this account.";
                JOptionPane.showMessageDialog(SplashFrame.instance(), msg);
                frame.selectSplash();
                break;
            case "IllegalUserId":
                msg = "User id already exists: " + account.getUserId();
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

