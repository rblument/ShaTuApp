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

import edu.regis.shatu.model.Account;
import edu.regis.shatu.svc.ClientRequest;
import edu.regis.shatu.svc.ServerRequestType;
import edu.regis.shatu.svc.SvcFacade;
import edu.regis.shatu.svc.TutorReply;
import edu.regis.shatu.view.SplashFrame;

/**
 * An MVC controller handling a user GUI gesture requesting to verify the user, 
 * which switches to the reset password panel in the GUI
 * (Modeled after NewUserAction)
 * 
 * @author mandyroskelley
 */
public class CheckSecurityQuestAction extends ShaTuGuiAction {
    /**
     * The single instance of this check security question action.
     */
    private static final CheckSecurityQuestAction SINGLETON;
    
    public Account model;
    
    
    /**
     * Create the singleton for this action, which occurs when this class
     * is loaded by the Java class loaded, as a result of the class being 
     * referenced by executing CheckSecurityQuestAction.instance() in the 
     * initializeComponents() method of the SplashPanel class.
     */
    static {
        SINGLETON = new CheckSecurityQuestAction();
    }

    /**
     * Return the singleton instance of this check security question action.
     * 
     * @return 
     */
    public static CheckSecurityQuestAction instance() {
	return SINGLETON;
    }

    /**
     * Initialize this check security question action.
     */
    public CheckSecurityQuestAction() {
        super("Verify User");
        putValue(SHORT_DESCRIPTION, "Verify user with security question and answer");
        putValue(MNEMONIC_KEY, KeyEvent.VK_U);
    }
    
    /**
     * Handle the user's request to verify their userID, security question and answer
     * match by displaying the reset password panel.
     * 
     * @param evt ignored
     */
    @Override
    public void actionPerformed(ActionEvent evt) { 
        Gson gson = new Gson();
        
        SplashFrame frame = SplashFrame.instance();
        Account account = frame.getAccount();
        

        ClientRequest request = new ClientRequest(ServerRequestType.VERIFY_USER);
        request.setData(gson.toJson(account));
       
        TutorReply reply = SvcFacade.instance().tutorRequest(request);

        String msg;
        switch (reply.getStatus()) {
            case "Verified":
            // Extract token from reply data
            String token = gson.fromJson(reply.getData(), String.class);
    
            frame.initializeResetPassword(account.getUserId(), token);
            frame.selectResetPassword(account.getUserId());

            msg = "Success!\n\n" +
                "Press OK to create a new password\n\n";
            JOptionPane.showMessageDialog(frame, msg);
            break;

            case "IllegalUserId":
                msg = "User id does not exist: " + account.getUserId();
                JOptionPane.showMessageDialog(null, msg, "Information",
                                              JOptionPane.INFORMATION_MESSAGE);
                break;
            case "InvalidAnswer":
                msg = "Answer does not match, please try again. ";
                JOptionPane.showMessageDialog(null, msg, "Information",
                                              JOptionPane.INFORMATION_MESSAGE);
                break;
            case "UnknownUser":
                msg = "UnknownUser ";
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