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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.PendingStep;
import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.aol.ViewType;
import edu.regis.shatu.view.act.HintAction;
import edu.regis.shatu.view.act.NewExampleAction;
import edu.regis.shatu.view.act.StepCompletionAction;


/**
 * Displays a tutoring session that allows a student to practice.
 *
 * The "Do One" view that allows either a student to select a task to practice 
 * or the tutor selects a task based on the current student model.
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
    
    /*
     * Universal button for logging off. 
     */    
    private JButton logoutButton;
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
     * The scroll pane for the step selector view
     */
    private JScrollPane scrollPane;

    /**
     * The container for the StepView
     */
    private JPanel stepViewContainer;

    /**
     * The Panel for each view's buttons to sit in
     */
    private JPanel buttonPanel;

    /**
     * The Split Pane to split step and main view panes
     */
    private JSplitPane splitPane;

    /**
     * Add panel for the dashboard button
     */
    private JPanel dashboardPanel;

    /**
     * Tracks the current view type so child views can implement logic based on view
     */
    private ViewType currentViewType;

    /**
     * Initialize this view including creating and laying out its child components.
     */
    public TutoringSessionView(ViewType viewType) {

        this.currentViewType = viewType;

        //Call to initialize and layout components used across all views to avoid code duplication
        initializeUniversalComponents();
        layoutUniversalComponents();

        switch(viewType)
        {
            case DO_ONE:
                setupPracticeView();
                break;

            case SEE_ONE:
                setupTeachView();
                break;

            case TEACH_ONE:
                setupQuizView();
                break;

            default:
                throw new IllegalArgumentException("No View of type: " + viewType.toString());
        }

    }

    /**
     * Set up the Practice View screen
     */
    private void setupPracticeView() {
        initializePracticeComponents();
        layoutPracticeComponents();
    }

    /**
     * Set up the Teach View screen
     */
    private void setupTeachView() {
        initializeTeachComponents();
        layoutTeachComponents();
    }

    /**
     * Set up the Quiz Me View
     */
    private void setupQuizView() {
        initializeQuizComponents();
        layoutQuizComponents();
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
        
        PendingTask pTask = model.currentTask();
        PendingStep pStep = pTask.getCurrentStep();
        StepSubType subType = pStep.getStep().getSubType();
        displayStep(subType.getViewName());

        stepView.setModel(model);
    }
    
    /**
     * Display the given selection's view in the StepView handling appropriate
     * highlighting of the associated JLabel selector (see StepSelection enum).
     * 
     * @param selection 
     */
    public void displayStep(StepSelection selection) {
        stepSelectorView.selectStep(selection);
        stepView.selectPanel(selection);
    }

    /**
     * Setup components that will be used across all views
     */
    private void initializeUniversalComponents() {

        // StepView must be created before StepSelectorView since the later
        // references the EncodeView in StepView by selecting it.
        stepView = new StepView();
        stepSelectorView = new StepSelectorView();

        //Button for going back to dashboard
        dashboardButton = new JButton("Go to Dashboard");
        dashboardButton.addActionListener(e -> navigateToDashboard());

        //Button for logging out
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        // Wrap StepSelectorView in a JScrollPane
        scrollPane = new JScrollPane(stepSelectorView);

        // Wrap stepView in a panel with BorderLayout to position buttons at the bottom
        stepViewContainer = new JPanel(new BorderLayout());

        // Create a button panel for Check and Hint buttons
        buttonPanel = new JPanel(); // Default FlowLayout

        // Create a JSplitPane to allow resizing of the left panel
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, stepViewContainer);

        // Add a JPanel for the dashboard button at the bottom
        dashboardPanel = new JPanel(new BorderLayout());
    }

    /**
     * Layout the components used for all views
     */
    private void layoutUniversalComponents() {

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Set minimum sizes for scrollPane and stepView
        scrollPane.setMinimumSize(new Dimension(100, 200)); // Minimum size for the scroll pane
        stepView.setMinimumSize(new Dimension(740, 200));   // Minimum size for the step view

        stepViewContainer.add(stepView, BorderLayout.CENTER);

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        splitPane.setDividerLocation(259); // Initial width of the left panel in pixels
        splitPane.setOneTouchExpandable(true); // Allow collapsing and expanding by the user

        // Set the layout for this panel
        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);

        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0)); // Adds padding
        dashboardPanel.add(dashboardButton, BorderLayout.WEST);
        dashboardButton.setPreferredSize(new Dimension(150, 25)); // Adjustable initial size
        add(dashboardPanel, BorderLayout.SOUTH);
    }

    /**
     * Create the child GUI components appearing in this frame.
     */
    private void initializePracticeComponents() {
        
        checkButton = this.initializeButton(StepCompletionAction.instance());
        hintButton = this.initializeButton(HintAction.instance());
        newExampleButton = this.initializeButton(NewExampleAction.instance());
        newExampleButton.addActionListener(e -> {
            checkButton.setEnabled(true);
            hintButton.setEnabled(true);
        });
    }
    /**
    * Layout the child components in this view
    */
   private void layoutPracticeComponents() {

       // Add buttons to the button panel
       buttonPanel.add(checkButton);
       buttonPanel.add(newExampleButton);
       buttonPanel.add(hintButton);

       // Add the button panel to the bottom of the stepViewContainer
       stepViewContainer.add(buttonPanel, BorderLayout.SOUTH);
   }

    /**
     * Initialize Teach Me View specific components
     */
   private void initializeTeachComponents() {
       //TODO: Setup Teach Me View specific components here
   }

    /**
     * Set up the layout for Teach Me View components
     */
   private void layoutTeachComponents() {
       buttonPanel.removeAll();
       stepViewContainer.removeAll();

       // Add the button panel to the bottom of the stepViewContainer
       stepViewContainer.add(buttonPanel, BorderLayout.SOUTH);


       //TODO: Layout Teach Me View specific components here
   }

    /**
     * Initialize Quiz Me View specific components
     */
    private void initializeQuizComponents() {
        //TODO: Setup Teach Me View specific components here
    }

    /**
     * Set up the layout for Quiz Me View components
     */
    private void layoutQuizComponents() {
        buttonPanel.removeAll();
        stepViewContainer.removeAll();

        // Add the button panel to the bottom of the stepViewContainer
        stepViewContainer.add(buttonPanel, BorderLayout.SOUTH);


        //TODO: Layout Teach Me View specific components here
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
    
    /**
     * Toggles the given button on/off
     * Ensures the GUI updates to display the button's new state
     **/
    public void toggleButton(JButton button) {
        button.setEnabled(!button.isEnabled());
        button.repaint();
        button.revalidate();
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

    public ViewType getCurrentViewType()
    {
        return this.currentViewType;
    }
    
    /**
     * A class called by dashboardButton to return to dashboard.
     */
    public void navigateToDashboard() {
        SplashFrame.instance().selectDashboard(this.getModel());
    }
    /**
     * A class called by logout Button to logout
     */
    public void logout() {
        SplashFrame.instance().logout();
    }
}
