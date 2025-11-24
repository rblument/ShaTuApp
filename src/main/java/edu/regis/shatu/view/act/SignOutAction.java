/*
 *  SHATU: SHA-256 Tutor
 * 
 *   (C) Johanna & Richard Blumenthal, All rights reserved
 * 
 *   Unauthorized use, duplication or distribution without the authors'
 *   permission is strictly prohibited.
 * 
 *   Unless required by applicable law or agreed to in writing, this
 *   software is distributed on an "AS IS" basis without warranties
 *   or conditions of any kind, either expressed or implied.
 */
package edu.regis.shatu.view.act;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import com.google.gson.Gson;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.svc.ClientRequest;
import edu.regis.shatu.svc.ServerRequestType;
import edu.regis.shatu.svc.SvcFacade;
import edu.regis.shatu.svc.TutorReply;
import edu.regis.shatu.view.MainFrame;

/**
 * A (MVC) controller handling a GUI gesture representing a user's request to
 * sign-out of the tutor.
 *
 * @author rickb
 * @author Henry Ordonez
 */
public class SignOutAction extends ShaTuGuiAction {

    /**
     * Exceptions occurring in this class are also logged to this logger.
     */
    private static final Logger LOGGER
            = Logger.getLogger(SignOutAction.class.getName());
    
    /**
     * The single instance of this sign-out action.
     */
    private static final SignOutAction SINGLETON;

    /**
     * Create the singleton for this action, which occurs when this class is
     * loaded by the Java class, as a result of the class being
     * referenced by executing SignOutAction.instance() in the
     * createFileMenu() method of the ShaTuMenuBar class.
     */
    static {
        SINGLETON = new SignOutAction();
    }

    /**
     * Returns the singleton instance of this sign-out action.
     *
     * @return a singleton instance
     */
    public static SignOutAction instance() {
        return SINGLETON;
    }
    
    /**
     * Initialize the action with the "Sign out" text.
     */
    public SignOutAction() {
        super("Sign out");
        putValue(SHORT_DESCRIPTION, "Sign out of the tutor");
        putValue(MNEMONIC_KEY, KeyEvent.VK_O);
    }
    
    /**
     * Handle the user's request to sign out.
     * 
     * @param e is ignored
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Gson gson = getGsonPretty();
        Account account = MainFrame.instance().getAccount();
        
        ClientRequest request = new ClientRequest(ServerRequestType.SIGN_OUT);
        request.setData(gson.toJson(account));
        TutorReply reply = SvcFacade.instance().tutorRequest(request);
        
        if(reply.getStatus().equals("SIGN_OUT")){
            MainFrame.instance().setModel(null);
        }
        else {
            System.err.println("Something went wrong: " + reply.getData());
        }
    }
}
