/*
 * SHATU: SHA-256 Tutor
 * 
 *  (C) Johanna & Richard Blumenthal, All rights reserved
 * 
 *  Unauthorized use, duplication or distribution without the authors'
 *  permission is strictly prohibted.
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
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import edu.regis.shatu.util.ImgFactory;
import edu.regis.shatu.view.MainFrame;

/**
 * Handler for GUI gestures requesting to save the current session.
 * 
 * @author rickb
 */
public class SaveSessionAction extends ShaTuGuiAction {
    
    /**
     * Create the singleton for this action, which occurs when this class
     * is loaded by the Java class loaded, as a result of the class being 
     * referenced by executing SaveSession.instance()
     */
    static {
        SINGLETON = new SaveSessionAction();
    }
    /**
     * The singleton for this action.
     */
    public static SaveSessionAction SINGLETON;
    
    /**
     * Return the singleton for this action.
     * 
     * @return the SaveSessionAction singleton
     */
    public static SaveSessionAction instance() {
        return SINGLETON;
    }
    
    /**
     * Initialize this action.
     */
    private SaveSessionAction() {
        super("Save");
        
        putValue(SMALL_ICON, ImgFactory.createIcon("Save16.gif", "Save Tutoring Session"));
        putValue(SHORT_DESCRIPTION, "Save the current tutoring session");
        putValue(MNEMONIC_KEY, KeyEvent.VK_S);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('S', KeyEvent.CTRL_DOWN_MASK));
    }
    
    /**
     * Save the current session when this action is invoked.
     * 
     * @param evt 
     */
    public void actionPerformed(ActionEvent evt) {
        // SHAT-362: Save functionality.
        Gson gson = getGsonPretty();
        Account account = MainFrame.instance().getAccount();

        ClientRequest request = new ClientRequest(ServerRequestType.SAVE_SESSION);
        // Get the user account.
        request.setUserId(account.getUserId());
        // Get the security token for the user.
        request.setSecurityToken(MainFrame.instance().getModel().getSecurityToken());
        request.setData(gson.toJson(account));

        TutorReply reply = SvcFacade.instance().tutorRequest(request);
        
        // Alert if unable to save for some reason.
        if (!reply.getStatus().equals("SAVED")) {
            System.err.println("Something went wrong: " + reply.getData());
        }
        
        
    }
}