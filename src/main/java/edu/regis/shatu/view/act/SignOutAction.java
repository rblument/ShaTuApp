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

import edu.regis.shatu.view.MainFrame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import static javax.swing.Action.MNEMONIC_KEY;
import static javax.swing.Action.SHORT_DESCRIPTION;

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
     * Initialize the action with the "Log out" text.
     */
    public SignOutAction() {
        super("Sign out");
        putValue(SHORT_DESCRIPTION, "Sign out of the tutor");
        putValue(MNEMONIC_KEY, KeyEvent.VK_O);
    }
    
    /**
     * Handle the user's request to sign out.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        MainFrame.instance().setModel(null);
        
    // TO-DO: Notify the Tutor that the student has signed out 
    }
}
