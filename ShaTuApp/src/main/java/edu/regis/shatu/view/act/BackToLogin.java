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

import edu.regis.shatu.view.SplashFrame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import static javax.swing.Action.MNEMONIC_KEY;
import static javax.swing.Action.SHORT_DESCRIPTION;

/**
 * An MVC controller handling a user GUI gesture requesting to return to the
 * login screen from the NewAccountPanel.
 * 
 * @author Amanda Roskelley
 */
public class BackToLogin extends ShaTuGuiAction {
    /**
     * Exceptions occurring in this class are also logged to this logger.
     */
    private static final Logger LOGGER
            = Logger.getLogger(BackToLogin.class.getName());

    /**
     * The single instance of this create account action.
     */
    private static final BackToLogin SINGLETON;

    /**
     * Create the singleton for this action, which occurs when this class
     * is loaded by the Java class loaded, as a result of the class being 
     * referenced by executing BackToLogin.instance() in the 
     * initializeComponents() method of the NewAccountPanel class.
     */
    static {
        SINGLETON = new BackToLogin();
    }

    /**
     * Return the singleton instance of this back to login action.
     *
     * @return
     */
    public static BackToLogin instance() {
        return SINGLETON;
    }

    /**
     * Initialize this back to login action with the "Back" text.
     */
    private BackToLogin() {
        super("Back");

        putValue(SHORT_DESCRIPTION, "Return to Login Screen");
        putValue(MNEMONIC_KEY, KeyEvent.VK_A);
    }

    /**
     * Handle the user's request to return to the login window from the new user
     * window.
     *
     * @param evt ignored
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        SplashFrame.instance().logout();
    }
}