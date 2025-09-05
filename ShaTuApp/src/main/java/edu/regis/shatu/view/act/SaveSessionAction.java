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

<<<<<<< HEAD
import edu.regis.shatu.util.ImgFactory;
import edu.regis.shatu.view.GuiController;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;

=======
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import edu.regis.shatu.util.ImgFactory;

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
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
        // ToDo: what happens on a save
        System.out.println("Save not implemented");
<<<<<<< HEAD
       // GuiController.instance().getStepView().selectPanel("RotateView");
=======
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }
}
