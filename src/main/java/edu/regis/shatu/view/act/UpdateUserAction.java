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

import edu.regis.shatu.view.MainFrame;

/**
 * An MVC controller handling a user GUI gesture requesting to update a 
 * user account, which switches to the update account panel in the GUI
 * (see UpdateAcctAction).
 * 
 * @author rickb
 * @author Henry Ordonez
 */
public class UpdateUserAction extends ShaTuGuiAction {
    
    /** The single instance of this new user action. */
    private static final UpdateUserAction SINGLETON;
    
    /**
     * Create the singleton for this action, which occurs when this class
     * is loaded by the Java class, as a result of the class being 
     * referenced by executing UpdateUserAction.instance() in the 
     * createFileMenu() method of the ShaTuMenuBar class.
     */
    static {
        SINGLETON = new UpdateUserAction();
    }

    /**
     * Return the singleton instance of this update user action.
     * 
     * @return a singleton object of this class
     */
    public static UpdateUserAction instance() {
	return SINGLETON;
    }
    
    /**
     * Initialize this update user action.
     */
    private UpdateUserAction() {
        super("Edit Account");
        putValue(SHORT_DESCRIPTION, "Request to update user information");
        putValue(MNEMONIC_KEY, KeyEvent.VK_E);
    }

    /**
     * Switches to the Update Account Panel view.
     * 
     * @param e is ignored
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        MainFrame.instance().displayView(MainFrame.ViewName.UPDATE_ACCOUNT);
    }
    
}
