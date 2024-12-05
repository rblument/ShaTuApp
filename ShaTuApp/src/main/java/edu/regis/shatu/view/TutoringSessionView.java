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

import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.view.act.HintAction;
import edu.regis.shatu.view.act.NewExampleAction;
import edu.regis.shatu.view.act.StepCompletionAction;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;


/**
 * Displays a tutoring session (the top-level GUI view for the application).
 *
 * Various aspects of the tutoring session are displayed in the child components
 * of this view.
 *
 * @author rickb
 */
public class TutoringSessionView extends GPanel {
    /**
     * Universal button for returning to the dashboard.
     */
    private JButton dashboardButton;
    
    /**
     * Universal 'Check', 'New Example', 'Hint' buttons.
     */
    private JButton checkButton, newExampleButton, hintButton;
    /**
     * The tutoring session model displayed in this view.
     */
    private TutoringSession model;

    /**
     * The step selector view displayed in the left panel of this view
     */
    private StepSelectorView stepSelectorView;

    /**
     * A card panel capable of displaying different views of various steps.
     */
    private StepView stepView;

    /**
     * Initialize this view including creating and laying out its child components.
     */
    public TutoringSessionView() {
        initializeComponents();
        layoutComponents();
    }

    /**
     * Return the model currently displayed in this view.
     *
     * @return a TutoringSession
     */
    public TutoringSession getModel() {
        return model;
    }

    /**
     * Display the given model in this view.
     *
     * @param model a TutoringSession.
     */
    public void setModel(TutoringSession model) {
        this.model = model;

        stepView.setModel(model);
    }

    /**
     * Create the child GUI components appearing in this frame.
     */
    private void initializeComponents() {
        // StepView must be created before StepSelectorView since the later
        // references the EncodeView in StepView by selecting it.
        stepView = new StepView(); 
        stepSelectorView = new StepSelectorView();
        
        // Buttons for 'check', 'new example', and 'hint'
        checkButton = this.initializeButton(StepCompletionAction.instance());
        newExampleButton = this.initializeButton(NewExampleAction.instance());
        hintButton = this.initializeButton(HintAction.instance());
        
        //button for returning to dashboard
        dashboardButton = new JButton("Go to Dashboard");
        dashboardButton.addActionListener(e -> navigateToDashboard());

    }
    /**
    * Layout the child components in this view
    */
   private void layoutComponents() {
       // Wrap StepSelectorView in a JScrollPane
       JScrollPane scrollPane = new JScrollPane(stepSelectorView);
       scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
       scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

       // Set minimum sizes for scrollPane and stepView
       scrollPane.setMinimumSize(new Dimension(100, 200)); // Minimum size for the scroll pane
       stepView.setMinimumSize(new Dimension(740, 200));   // Minimum size for the step view

       // Wrap stepView in a panel with BorderLayout to position buttons at the bottom
       JPanel stepViewContainer = new JPanel(new BorderLayout());
       stepViewContainer.add(stepView, BorderLayout.CENTER);

       // Create a button panel for Check and Hint buttons
       JPanel buttonPanel = new JPanel(); // Default FlowLayout
       buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

       // Add buttons to the button panel
       buttonPanel.add(checkButton);
       buttonPanel.add(newExampleButton);
       buttonPanel.add(hintButton);

       // Add the button panel to the bottom of the stepViewContainer
       stepViewContainer.add(buttonPanel, BorderLayout.SOUTH);

       // Create a JSplitPane to allow resizing of the left panel
       JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, stepViewContainer);
       splitPane.setDividerLocation(259); // Initial width of the left panel in pixels
       splitPane.setOneTouchExpandable(true); // Allow collapsing and expanding by the user

       // Set the layout for this panel
       setLayout(new BorderLayout());
       add(splitPane, BorderLayout.CENTER);

       // Add a JPanel for the dashboard button at the bottom
       JPanel dashboardPanel = new JPanel(new BorderLayout());
       dashboardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0)); // Adds padding
       dashboardPanel.add(dashboardButton, BorderLayout.WEST);
       dashboardButton.setPreferredSize(new Dimension(150, 25)); // Adjustable initial size
       add(dashboardPanel, BorderLayout.SOUTH);
   }
   
   /**
     * Initializes the universal StepView buttons requiring Strings.
     * Initializing these buttons via this method aids in consistency.
     * @param buttonText the text to display within the button.
     * @return an initialized button according to the button type.
     */
    public JButton initializeButton(String buttonText){
            return new JButton(buttonText);
    }
    
    /**
     * Initializes the universal StepView buttons requiring actions.
     * Initializing these buttons via this method aids in consistency.
     * @param buttonInstance the text to display within the button.
     * @return an initialized button according to the button type.
     */
    public JButton initializeButton(Action buttonInstance){
            return new JButton(buttonInstance);
    }
    
    public void toggleButton(JButton button) {
        button.setEnabled(!button.isEnabled());
        button.repaint();
        button.revalidate();
    }
    
    public void resetButtonListeners() {
        // Store current button text to preserve it
        String checkText = checkButton.getText();
        String hintText = hintButton.getText();
        String newExampleText = newExampleButton.getText();

        // Remove all ActionListeners for each button
        for (ActionListener al : checkButton.getActionListeners()) {
            checkButton.removeActionListener(al);
        }
        for (ActionListener al : hintButton.getActionListeners()) {
            hintButton.removeActionListener(al);
        }
        for (ActionListener al : newExampleButton.getActionListeners()) {
            newExampleButton.removeActionListener(al);
        }

        // Restore button text
        checkButton.setText(checkText);
        hintButton.setText(hintText);
        newExampleButton.setText(newExampleText);

        // Disable all buttons initially
        checkButton.setEnabled(false);
        hintButton.setEnabled(false);
        newExampleButton.setEnabled(true); // Enable New Example by default
    }
   
    public JButton getCheckButton() {
        return checkButton;
    }
    
    public JButton getNewExampleButton(){
        return newExampleButton;
    }

    public JButton getHintButton() {
        return hintButton;
    }

    /**
     * A class called by dashboardButton to return to dashboard.
     */
    public void navigateToDashboard() {
        SplashFrame.instance().selectDashboard(this.getModel());
    }
}
