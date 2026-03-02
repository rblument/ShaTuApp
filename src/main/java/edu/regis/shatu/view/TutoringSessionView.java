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
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.google.gson.Gson;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.PendingStep;
import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.aol.TutoringMode;
import edu.regis.shatu.model.steps.InformationStep;
import edu.regis.shatu.model.steps.Step;
import edu.regis.shatu.svc.ClientRequest;
import edu.regis.shatu.svc.ServerRequestType;
import edu.regis.shatu.svc.ServiceFactory;
import edu.regis.shatu.svc.SvcFacade;
import edu.regis.shatu.svc.TutorReply;
import edu.regis.shatu.svc.TutorSvc;

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
     * Triggers requesting a new problem.
     */
    private JButton requestProblemButton;
    
    /**
     * Utility for conversion of java objects to/from JSON.
     */
    private Gson gson;

    /**
     * Initialize this view including creating and laying out its child components.
     */
    public TutoringSessionView() {
        gson = new Gson();
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
            if (pTask == null) {
                // No pending task loaded (e.g., DB state incomplete). Avoid crashing the UI.
                // The user can request a new example to continue.
                return;
            }
            PendingStep pStep = pTask.getCurrentStep();
            StepSubType subType = pStep.getStep().getSubType();
            
            
            // Handle INFO_MESSAGE steps with a dialog
            if (subType == StepSubType.INFO_MESSAGE) {
                // Store the step to show dialog after UI loads
                final PendingStep stepToShow = pStep;
                
                // Use SwingUtilities.invokeLater to show dialog AFTER the UI is fully displayed
                javax.swing.SwingUtilities.invokeLater(() -> {
                    handleInfoMessage(stepToShow);
                });
            } else {
                StepSelection viewName = subType.getViewName();
                if (viewName != null) {
                    displayStep(viewName);
                }
            }

        }
        stepView.setModel(model);
        configureModeSpecificUI();
    }
    
    /**
     * Handle an information step by displaying its message in a dialog box.
     * 
     * @param pStep 
     */
    private void handleInfoMessage(PendingStep pStep) {
        Step step = pStep.getStep();
        
        // Check for null step data defensively
        if (step == null || step.getData() == null) {
            System.err.println("Warning: INFO_MESSAGE step has null data");
            return;
        }
        
        try {
            // Parses JSON data to get the InformationStep
            InformationStep infoStep = gson.fromJson(step.getData(), InformationStep.class);
            
            if (infoStep != null && infoStep.getMsg() != null && !infoStep.getMsg().isEmpty()) {
                // Show dialog with "Acknowledged" button
                String[] options = {"Acknowledged"};
                JOptionPane.showOptionDialog(
                    this,                           
                    infoStep.getMsg(),             
                    "Information",                  
                    JOptionPane.DEFAULT_OPTION,    
                    JOptionPane.INFORMATION_MESSAGE, 
                    null,                          
                    options,                       
                    options[0]                     
                );
                
                // Mark the step as complete after acknowledgment
                completeInfoMessageStep(step,infoStep);

            } else {
                System.err.println("Warning: INFO_MESSAGE has empty or null message");
            }
        } catch (Exception e) {
            System.err.println("Error parsing INFO_MESSAGE data: " + e.getMessage());
            e.printStackTrace();
        }
    }



    /**
     * Complete the INFO_MESSAGE step after user acknowledgment.
     * @param step 
     * @param infoStep
     */

     private void completeInfoMessageStep(Step step, InformationStep infoStep) {
        try {
            // Creates a StepCompletion object w/current step data
            StepCompletion completion = new StepCompletion(step, gson.toJson(infoStep));
            completion.setStep(step);
            ClientRequest request = new ClientRequest(ServerRequestType.COMPLETED_STEP);
            Account account = MainFrame.instance().getAccount();

            if (account == null) {
                System.err.println("Error! No account found when completing INFO_MESSAGE");
                return;
            }
            
            TutoringSession session = MainFrame.instance().getModel();
            if (session == null) {
                System.err.println("Error! No tutoring session found when completing INFO_MESSAGE");
                return;
            }
            
            request.setUserId(account.getUserId());
            request.setSecurityToken(session.getSecurityToken());
            request.setData(gson.toJson(completion));
            
            TutorReply reply = SvcFacade.instance().tutorRequest(request);
            

            if (reply != null) {
                handleStepCompletionReply(reply);
            } else {
                System.err.println("Error: Received null reply from tutor");
            }
            
        } catch (Exception e) {
            System.err.println("Error completing INFO_MESSAGE step: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Handle the reply from the server after completing a step.
     * @param reply from tutorReply from server
     */
    private void handleStepCompletionReply(TutorReply reply) {
        try {
            switch (reply.getStatus()) {
                case ":Success":
                    PendingTask pendingTask = gson.fromJson(reply.getData(), PendingTask.class);
                    
                    if (pendingTask != null) {
                        // Update w/pending task
                        TutoringSession session = MainFrame.instance().getModel();
                        session.addCurrentTask(pendingTask);
                        
                        setModel(session);
                    } else {
                        System.err.println("Error! Received null pending task in reply");
                    }
                    break;
                    
                case ":ERR":
                    System.err.println("Error from tutor: " + reply.getData());
                    JOptionPane.showMessageDialog(
                        this,
                        "An error occurred while processing your acknowledgement.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    break;
                    
                default:
                    System.err.println("Unexpected reply status: " + reply.getStatus());
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error handling step completion reply: " + e.getMessage());
            e.printStackTrace();
        }
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

        requestProblemButton = new JButton("Request New Problem");
        requestProblemButton.addActionListener(e -> requestProblem());


        scrollPane = new JScrollPane(stepSelectorView);

        stepViewContainer = new JPanel(new BorderLayout());

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, stepViewContainer);

        controlPanel = new JPanel(new BorderLayout());
        
        //ToDo: Are there any special components that depend on the Tutoring Mode?
    }

    /**
     * Configure the UI based on the current tutoring mode.
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
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.add(dashboardButton);
        buttonPanel.add(requestProblemButton);
        controlPanel.add(buttonPanel, BorderLayout.WEST);
        dashboardButton.setPreferredSize(new Dimension(150, 25)); // Adjustable initial size
        add(controlPanel, BorderLayout.SOUTH);
    }

    /**
     * Toggles the given button on/off
     * Ensures the GUI updates to display the button's new state
     * @param button
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

    /**
     * Requests a new suggested problem from the tutor based on the student's progress.
     */
    
    private void requestProblem() {
        if (model == null) {
            System.err.println("requestProblem: model is null");
            return;
        }

        try {
            TutorSvc tutor = ServiceFactory.findTutorSvc();

            String userId = model.getStudent().getAccount().getUserId();
            String token = model.getSecurityToken();

            String json = "{\"exampleType\":\"ASCII_ENCODE\",\"data\":null}";

            ClientRequest req = new ClientRequest(ServerRequestType.NEW_EXAMPLE);
            req.setUserId(userId);
            req.setSecurityToken(token);
            req.setData(json);

            TutorReply reply = tutor.request(req);

            if (reply == null) {
                System.err.println("requestProblem: null reply from tutor");
                return;
            }

            if (":ERR".equals(reply.getStatus())) {
                System.err.println("Error requesting problem: " + reply.getData());
                return;
            }

            // --- NEW_EXAMPLE returns a wrapper object ---
            PendingTask wrapper = gson.fromJson(reply.getData(), PendingTask.class);

            if (wrapper != null) {

                TutoringSession session = MainFrame.instance().getModel();

                // Clear current task stack before inserting new problem
                session.addCurrentTask(wrapper);

                setModel(session);
            } else {
                System.err.println("requestProblem: parsed wrapper was null");
            }

        } catch (Exception ex) {
            System.err.println("requestProblem: exception while requesting problem");
            ex.printStackTrace();
        }
    }

}
