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
import edu.regis.shatu.model.User;
import edu.regis.shatu.svc.ClientRequest;
import edu.regis.shatu.svc.ServerRequestType;
import edu.regis.shatu.svc.SvcFacade;
import edu.regis.shatu.svc.TutorReply;
import edu.regis.shatu.view.SplashFrame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import static javax.swing.Action.MNEMONIC_KEY;
import static javax.swing.Action.SHORT_DESCRIPTION;
import javax.swing.JOptionPane;

/**
 * An MVC controller handling a user GUI gesture requesting to reset password, 
 * which switches to the forgot password in the GUI
 * (see CreateAcctAction). (Modeled after NewUserAction)
 * 
 * @author mandyroskelley
 */
public class CheckSecurityQuestAction extends ShaTuGuiAction {
    /**
     * The single instance of this new user action.
     */
    private static final CheckSecurityQuestAction SINGLETON;
    
    /**
     * Create the singleton for this action, which occurs when this class
     * is loaded by the Java class loaded, as a result of the class being 
     * referenced by executing ForgotPasswordAction.instance() in the 
     * initializeComponents() method of the SplashPanel class.
     */
    static {
        SINGLETON = new CheckSecurityQuestAction();
    }

    /**
     * Return the singleton instance of this forgot password action.
     * 
     * @return 
     */
    public static CheckSecurityQuestAction instance() {
	return SINGLETON;
    }

    /**
     * Initialize this forgot password action.
     */
    private CheckSecurityQuestAction() {
        super("Verify User");
        putValue(SHORT_DESCRIPTION, "Verify user with security question and answer");
        putValue(MNEMONIC_KEY, KeyEvent.VK_U);
       // putValue(ACCELERATOR_KEY, getAcceleratorKeyStroke());
    }
    
    /**
     * Handle the user's request to reset the password by displaying the new
     * user panel.
     * 
     * @param evt ignored
     */
    @Override
    public void actionPerformed(ActionEvent evt) { 
        Gson gson = new Gson();
        
        SplashFrame frame = SplashFrame.instance();
        User user = frame.getUser();
        Account account = frame.getAccount();
        

        ClientRequest request = new ClientRequest(ServerRequestType.VERIFY_USER);
        request.setData(gson.toJson(account));
        System.out.println("Test CheckSecurityQuest- " + account.getUserId());
       
        TutorReply reply = SvcFacade.instance().tutorRequest(request);

        String msg;
        switch (reply.getStatus()) {
            case "Verified":
                frame.clearNewAccountPanel();
                msg = "UserId and Security Question match credentials.\n\n" +
                        "Press okay and create a new password\n\n";
                JOptionPane.showMessageDialog(SplashFrame.instance(), msg);
                SplashFrame.instance().selectResetPassword();
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
