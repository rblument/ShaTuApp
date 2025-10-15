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
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.PendingStep;
import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.aol.TutoringMode;

/**
 * Displays a tutoring session.
 *
 * Different aspects of the tutoring session are displayed in the child 
 * components, while the types of actions allowed are constrained by the 
 * tutoring mode (e.g., SEE_ONE) within the model tutoring sessions.
 *
 * @author rickb
 */
public class TutoringSessionView extends GPanel {
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
     * The Split Pane to split step and main view panes
     */
    private JSplitPane splitPane;

    /**
     * Add panel for the dashboard button
     */
    private JPanel controlPanel;
    
    /**
     * Triggers returning to the dashboard.
     */
    private JButton dashboardButton;

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
     * @param model a TutoringSession or null when signing out.
     */
    public void setModel(TutoringSession model) {
        this.model = model;
        
        if (model != null) {
            PendingTask pTask = model.currentTask();
            PendingStep pStep = pTask.getCurrentStep();
            StepSubType subType = pStep.getStep().getSubType();
            
            displayStep(subType.getViewName());

            stepView.setModel(model);
        }
        
        configureModeSpecificUI();
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
     * Setup components
     */
    private void initializeComponents() {
        // StepView must be created before StepSelectorView since the later
        // references the EncodeView in StepView by selecting it.
        stepView = new StepView();
        stepSelectorView = new StepSelectorView();

        dashboardButton = new JButton("Go to Dashboard");
        dashboardButton.addActionListener(e -> navigateToDashboard());

        scrollPane = new JScrollPane(stepSelectorView);

        stepViewContainer = new JPanel(new BorderLayout());

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, stepViewContainer);

        controlPanel = new JPanel(new BorderLayout());
        
        //ToDo: Are there any special components that depend on the Tutoring Mode?
    }

    /**
     * New Method to
     */
    private void configureModeSpecificUI() {
        
        //Check if no model, nothing to configure
        if (model == null) {
            return;
        }
        
        
        TutoringMode mode = model.getTutoringMode();
        
        switch (mode) {
            case SEE_ONE:
                for (StepSelection step : StepSelection.values()) {
                    HighlightLabel label = step.getLabel();
                    label.setEnabled(false);
                }
                break;
            
            //Separated just in case logic later requires separate behavior    
            case DO_ONE: {
                for (StepSelection step : StepSelection.values()) {
                    HighlightLabel label = step.getLabel();
                    label.setEnabled(true);
                }
                break;
            }
            case TEACH_ONE:
                for (StepSelection step : StepSelection.values()) {
                    HighlightLabel label = step.getLabel();
                    label.setEnabled(true);
                }
                break;
        }
    }
    
    
    /**
     * Layout the components
     */
    private void layoutComponents() {

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Set minimum sizes for scrollPane and stepView
        scrollPane.setMinimumSize(new Dimension(100, 200)); // Minimum size for the scroll pane
        stepView.setMinimumSize(new Dimension(740, 200));   // Minimum size for the step view

        stepViewContainer.add(stepView, BorderLayout.CENTER);

        splitPane.setDividerLocation(259); // Initial width of the left panel in pixels
        splitPane.setOneTouchExpandable(true); // Allow collapsing and expanding by the user

        // Set the layout for this panel
        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);

        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0)); // Adds padding
        controlPanel.add(dashboardButton, BorderLayout.WEST);
        dashboardButton.setPreferredSize(new Dimension(150, 25)); // Adjustable initial size
        add(controlPanel, BorderLayout.SOUTH);
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
     * A class called by dashboardButton to return to dashboard.
     */
    public void navigateToDashboard() {
        MainFrame.instance().displayView(MainFrame.ViewName.DASHBOARD);
    }
}