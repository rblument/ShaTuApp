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
package edu.regis.shatu.view;

import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.view.act.HintAction;
import edu.regis.shatu.view.act.NewExampleAction;
import edu.regis.shatu.view.act.StepCompletionAction;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * A abstract view that supports various user gestures that results in a request 
 * being made to the tutor.
 * 
 * The implementation of the abstract methods in this class allows the various
 * Java actions, such NewExampleAction, to obtain the data to be used in 
 * constructing the request being sent to the tutor.
 * 
 * @author Oskar Thiede
 */
public abstract class UserRequestView extends GPanel {
    /**
     * The current task and step in this tutoring session are displayed in this 
     * view.
     */
    protected TutoringSession model;
    
    /**
     * Universal 'Check', 'New Example', 'Hint' buttons.
     */
    protected JButton checkButton, newExampleButton, hintButton;
    
    protected boolean checkHintEnabled = false;
    
    /**
     * The Panel for each view's buttons to sit in
     */
    protected GPanel buttonPanel;
   
    /**
     * Convenience utility for converting between Java and JSon objects.
     */
    protected Gson gson = new GsonBuilder().setPrettyPrinting().create();;
    
    /**
     * Convenience utility for generating pseudo-random numbers.
     */
    protected Random random;

    /**
     * Create and return a new example request associated with the tutoring
     * topic presented to the student in this view. 
     * 
     * This new example request can be sent to the tutor, which will reply
     * with a task containing the new example problem to be presented to the
     * student.
     * 
     * @return NewExampleRequest
     */
    public abstract NewExampleRequest newRequest();
    
    /**
     * Update this (subclass) view to display the current tutoring session model.
     */
    protected abstract void updateView();

    /**
     * Defines each view classes' standard method for updating in the Practice View
     */
    protected abstract void updatePracticeView();

    /**
     * Defines each view classes' standard method for updating in the Teach Me View
     */
    protected abstract void updateTeachView();

    /**
     * Defines each view classes' standard method for updating in the Teach Me View
     */
    protected abstract void updateQuizView();

    /**
     * Create and return a new step completion request, which indicates the
     * student is asking the tutor to check their work.
     * 
     * @return StepCompletion
     */
    public abstract StepCompletion stepCompletion();
    
    public UserRequestView() {
        initializePracticeButtons();
        createButtonPanel();
    }

    public TutoringSession getModel() {
        return this.model;
    }
    
    public void setModel(TutoringSession model) {
        random = new Random();
        this.model = model;
        updateView();
    }
    
    /**
     * Assign the given task as the current task in our tutoring session model
     * and display this task and associated step(s) in the view.
     * 
     * @param task 
     */
    public void setCurrentTask(PendingTask task) {
       model.addCurrentTask(task);
       updateView();
    }
    
      /**
     * Create the child GUI components appearing in this frame.
     */
    private void initializePracticeButtons() {
        checkButton = new JButton(StepCompletionAction.instance());
        hintButton = new JButton(HintAction.instance());
        newExampleButton = new JButton (NewExampleAction.instance());
        
        newExampleButton.addActionListener(e -> {
            checkButton.setEnabled(true);
            hintButton.setEnabled(true);
        });
    }
    
    private void createButtonPanel() {
        buttonPanel = new GPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
       
        buttonPanel.addc(newExampleButton, 0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                5, 5, 5, 5);
       
        buttonPanel.addc(checkButton, 1, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                5, 5, 5, 5);
        
        buttonPanel.addc(hintButton, 2, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                5, 5, 5, 5);
    }
    
     /**
     * These buttons are used universally by each view
     * Each view attaches its own listeners, and must be cleaned up
     * before interacting with a new view.
     * 
     * Failure to clean up the additional listeners will result in unnecessary,
     * or redundant, operations executed.
     */
    public void resetButtonListeners() {
        // Keep track of the known initial listeners
        ActionListener hintAction = HintAction.instance();
        ActionListener checkAction = StepCompletionAction.instance();
        ActionListener newExampleAction = NewExampleAction.instance();

        // Clear all listeners for the check button and re-add the known listener
        for (ActionListener listener : checkButton.getActionListeners()) {
            checkButton.removeActionListener(listener);
        }
        checkButton.addActionListener(checkAction);

        // Clear all listeners for the hint button and re-add the known listener
        for (ActionListener listener : hintButton.getActionListeners()) {
            hintButton.removeActionListener(listener);
        }
        hintButton.addActionListener(hintAction);

        // Clear all listeners for the new example button and re-add the known listeners
        for (ActionListener listener : newExampleButton.getActionListeners()) {
            newExampleButton.removeActionListener(listener);
        }
        newExampleButton.addActionListener(newExampleAction);
        newExampleButton.addActionListener(e -> {
            checkButton.setEnabled(true);
            hintButton.setEnabled(true);
        }); // Re-add the lambda listener

        // Reset button text
        checkButton.setText("Check");
        hintButton.setText("Hint");
        newExampleButton.setText("New Example");

        // Set the default button states
        checkButton.setEnabled(false);
        hintButton.setEnabled(false);
        newExampleButton.setEnabled(true); // Enable New Example by default
    }
    
    /*
     public JButton getCheckButton() {
        return checkButton;
    }
    
    public JButton getNewExampleButton(){
        newExampleButton.setEnabled(true);
        return newExampleButton;
    }

    public JButton getHintButton() {
        return hintButton;
    }
    */
}
