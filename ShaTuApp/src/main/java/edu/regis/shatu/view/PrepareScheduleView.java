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
package edu.regis.shatu.view;

import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.NewExampleRequest;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextPane;

/**
 *
 * @author rickb
 */
public class PrepareScheduleView extends UserRequestView implements ActionListener {
    
    private TutoringSessionView view;
    private JTextPane descriptionTextPane;
    
    /**
     * Generates the prepare schedule view.
     */
    public PrepareScheduleView() {
        initializeComponents();
        initializeLayout();
    }
    
    /**
     * Overridden method, can be used for buttons later in development.
     * @param event 
     */
    @Override
    public void actionPerformed(ActionEvent event) {
       System.out.println("Prepare Schedule action performed method called"); //Error testing
    }
 
    /**
     * Create the child GUI components appearing in this frame.
     */
    private void initializeComponents() {
        setupDescriptionSection();
    }
    
    /**
     * Adds views to the prepare schedule view, currently in development,
     * a actual question and process for the student hasn't been decided/developed,
     * only a description of the prepare schedule operation is displayed.
     */
    private void initializeLayout() {
        addc(descriptionTextPane, 0, 0, 1, 1, 
                1.0, 0.0, GridBagConstraints.CENTER, 
                GridBagConstraints.HORIZONTAL, 5, 5, 5, 5);
    }
    
     /**
     * Sets up the description section of the view, explaining the purpose of 
     * the Prepare Schedule operation.
     */
    private void setupDescriptionSection() {
        descriptionTextPane = new JTextPane();
        descriptionTextPane.setContentType("text/html");
        
        // TEMPORARY UNTIL WE LOAD THE MODEL DATA DESCRIPTION
        descriptionTextPane.setText(
                    "<html>" +
                    "<body>" +
                    "<h2 style='text-align: center;'>Prepare Schedule Operation</h2>" +
                    "<p>The Prepare Schedule operation (also known as the message schedule) "
                            + "has a couple of steps you should know.  "
                            + "This operation is too large to teach here, "
                            + "but you should know the basic idea of what happens "
                            + "before moving on to the compression phase of the "
                            + "ShaTu256 algorithm (rotations, the choice function, etc.).  "
                            + "The basics of the prepare schedule operation is as follows: "
                            + "<br>1: Pad the input message and divide into 512 bit chunks. "
                            + "<br>2: Break each chunk into 32 bit words (16 words initially). "
                            + "<br>3: We need 64 words, so using the initial 16 words, "
                            + "we will use various operations (too many and too complex to "
                            + "explain here) to go from 16 to 63 words (starts at 0, not 1).  "
                            + "The remaining words are a combination of the original 16 "
                            + "words and some operations such as shifts, rotations, "
                            + "and bitwise operations. <br>4: We will use these words in the "
                            + "compression phase (the remaining operations) at the end we get "
                            + "a unique and irreversible hash output.</p>" +
                    "</body>" +
                    "</html>"
            );
        
        descriptionTextPane.setEditable(false);
        descriptionTextPane.setBackground(null);
        descriptionTextPane.setBorder(null);
    }
    
    @Override
    /**
     * Updates the description, question, and hints from the model
     * 
     * TODO: THIS IS A PLACEHOLDER UNTIl WE HAVE HAVE THE MODEL CODE COMPLETED
     */
    protected void updateView() {
        view = SplashFrame.instance().getView(); // Accessing view to use universal buttons
        view.resetButtonListeners(); // Clear any listeners applied from other views
        
        // Universal buttons (hint, check, new example) are not needed in this view.
        view.getCheckButton().setEnabled(false);
        view.getHintButton().setEnabled(false);
        view.getNewExampleButton().setEnabled(false);
        
        if (model != null) {
            // ****TO-DO*****
            // Update the view's information from the model
            // Debugging dynamic updates to the model can be done here.
            System.out.println("PrepareScheduleView updateView called"); // Error checking
        }
    }

    /**
     * Overridden method for a newRequest that gets called when a New Example button
     * is clicked, the tutor file will handle the rest and send it back here for 
     * the view to utilize, currently not implemented for the prepare schedule view.
     * @return 
     */
    @Override
    public NewExampleRequest newRequest() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    /**
     * Overridden method for a stepCompletion that gets called when a Check button
     * is clicked, the tutor file will handle the rest and send it back here for 
     * the view to utilize, currently not implemented for the prepare schedule view.
     * @return 
     */
    @Override
    public StepCompletion stepCompletion() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
