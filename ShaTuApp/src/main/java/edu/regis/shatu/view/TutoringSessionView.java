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
import java.awt.BorderLayout;
import java.awt.Dimension;
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
        // Wrap StepSelectorView in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(stepSelectorView);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Set minimum sizes for scrollPane and stepView
        scrollPane.setMinimumSize(new Dimension(100, 200)); // Minimum size for the scroll pane
        stepView.setMinimumSize(new Dimension(740, 200));   // Minimum size for the step view

        // Create a JSplitPane to allow resizing of the left panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, stepView);
        splitPane.setDividerLocation(259); // Initial width of the left panel in pixels
        splitPane.setOneTouchExpandable(true); // Allow collapsing and expanding by the user

        // Set the layout for this panel
        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);

        // Add a JPanel for the dashboard button at the bottom
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0)); // Adds padding (top, left, bottom, right)
        buttonPanel.add(dashboardButton, BorderLayout.WEST);
        dashboardButton.setPreferredSize(new Dimension(150, 25)); // Adjustable initial size
        add(buttonPanel, BorderLayout.SOUTH);
    }
    /**
     * A class called by dashboardButton to return to dashboard.
     */
    public void navigateToDashboard() {
        SplashFrame.instance().selectDashboard(this.getModel());
    }
}
