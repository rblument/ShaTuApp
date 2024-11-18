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
import java.awt.GridBagConstraints;
import javax.swing.JButton;


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
        //button for returning to dashboard
        dashboardButton = new JButton("Go to Dashboard");
        dashboardButton.addActionListener(e -> navigateToDashboard());

    }
    /**
     * Layout the child components in this view
     */
    private void layoutComponents() {
        addc(stepSelectorView, 0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                5, 5, 5, 5);
        addc(stepView, 1, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                5, 5, 5, 5);
        addc(dashboardButton, 0, 2, 1, 1, 0.1, 0.1,
                GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE,
                5, 5, 5, 5);
    }
    /**
     * A class called by dashboardButton to return to dashboard.
     */
    public void navigateToDashboard() {
        SplashFrame.instance().selectDashboard(this.getModel());
    }
}
