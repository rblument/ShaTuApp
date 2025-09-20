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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.svc.ClientRequest;
import edu.regis.shatu.svc.ServerRequestType;
import edu.regis.shatu.svc.SvcFacade;
import edu.regis.shatu.svc.TutorReply;
import edu.regis.shatu.view.MainFrame;
import javax.swing.JOptionPane;

/**
 * An (MVC) controller handling a GUI gesture representing a user's request to
 * login to the tutor via the WelcomePanel.
 *
 * If successful, a trial will be started or resumed for the student via launch
 * session.
 *
 * @author rickb
 */
public class SignInAction extends ShaTuGuiAction {
    
    private final InactivityManager inactivityManager
            = new InactivityManager();


    /**
     * Exceptions occurring in this class are also logged to this logger.
     */
    private static final Logger LOGGER
            = Logger.getLogger(SignInAction.class.getName());

    /**
     * The single instance of this sign-in action.
     */
    private static final SignInAction SINGLETON;

    /**
     * Create the singleton for this action, which occurs when this class is
     * loaded by the Java class loaded, as a result of the class being
     * referenced by executing SignInAction.instance() in the
     * initializeComponents() method of the SplashPanel class.
     */
    static {
        SINGLETON = new SignInAction();
    }

    /**
     * Return the singleton instance of this sign-in action.
     *
     * @return
     */
    public static SignInAction instance() {
        return SINGLETON;
    }

    /**
     * Initialize action with the "Sign In" text and set its text.
     */
    private SignInAction() {
        super("Sign In");
        putValue(SHORT_DESCRIPTION, "Sign-in to the tutor");
        putValue(MNEMONIC_KEY, KeyEvent.VK_S);
        //putValue(ACCELERATOR_KEY, getAcceleratorKeyStroke());
    }

    /**
     * Handle the user's request to sign-in by sending it to the DICE tutor.
     *
     * If successful, the MainFrame with the Courtroom View is displayed.
     *
     * @param evt ignored
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Account account = MainFrame.instance().getAccount();
        
        ClientRequest request = new ClientRequest(ServerRequestType.SIGN_IN);
        request.setData(gson.toJson(account));
        TutorReply reply = SvcFacade.instance().tutorRequest(request);

        switch (reply.getStatus()) {
            case "Authenticated":
                TutoringSession session = gson.fromJson(reply.getData(), TutoringSession.class);
                
                MainFrame frame = MainFrame.instance();

                frame.setModel(session);
                
                // Start tracking user inactivity
                inactivityManager.startTracking();
                
                String welcomeMessage = "Welcome, "
                    + session.getStudent().getAccount().getFirstName()
                    + "! Your session has successfully started.";
                JOptionPane.showMessageDialog(null, welcomeMessage, "Welcome", JOptionPane.INFORMATION_MESSAGE);

                
                break;
                
            case "InvalidPassword":
                MainFrame.instance().invalidPass();
                break;
            case "UnknownUser":
                 JOptionPane.showMessageDialog(MainFrame.instance(), 
                    account.getUserId() + " is not a known user.\n\nPerhaps, try creating a 'New User' first.",
                    "Warning", JOptionPane.ERROR_MESSAGE);
                break;
            default:
                System.out.println("Coding error  status: " + reply.getStatus());
        }
    }
}