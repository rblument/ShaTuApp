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
package edu.regis.shatu.view.act;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.regis.shatu.err.IllegalArgException;
import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.Hint;
import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.PendingStep;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.svc.ClientRequest;
import edu.regis.shatu.svc.ServerRequestType;
import edu.regis.shatu.svc.SvcFacade;
import edu.regis.shatu.svc.TutorReply;
import edu.regis.shatu.view.GuiController;
import edu.regis.shatu.view.MainFrame;
import edu.regis.shatu.view.SplashFrame;
import edu.regis.shatu.view.UserRequestView;

/**
 * An (MVC) controller handling a GUI gesture representing a user's request to 
 * receive a hint.
 * 
 * If successful, the appropriate view will be updated with the tutor's reply.
 * 
 * @author Chandon Hamel
 */
public class HintAction extends ShaTuGuiAction {
    
    /**
     * Exceptions occurring in this class are also logged to this logger.
     */
    private static final Logger LOGGER
            = Logger.getLogger(StepCompletionAction.class.getName());
    
    /**
     * The single instance of this hint action.
     */
    private static final HintAction SINGLETON;
    
    /**
     * Create the singleton for this action, which occurs when this class is
     * loaded by the Java class loaded, as a result of the class being
     * referenced by executing HintAction.instance().
     */
    static {
        SINGLETON = new HintAction();
    }
    
    /**
     * Return the singleton instance of this hint action.
     *
     * @return
     */
    public static HintAction instance() {
        return SINGLETON;
    }
    
    /**
     * Initialize action with the "Sign In" text and set its text.
     */
    private HintAction() {
        super("Hint");

        putValue(SHORT_DESCRIPTION, "Hint");

        putValue(MNEMONIC_KEY, KeyEvent.VK_H);
        //putValue(ACCELERATOR_KEY, getAcceleratorKeyStroke());
    }

    /**
     * Handle the user's request for a hint by sending it to the tutor.
     *
     * If successful, the current view is updated with the contents of the hint.
     *
     * @param evt ignored
     */
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        Account account = SplashFrame.instance().getAccount();
        
        try {
            //Get the current view that initiated the StepCompletionRequest
            UserRequestView exView = GuiController.instance().getStepView().getUserRequestView();
   
            //Call the overridden newRequest() method to generate an appropriate
            //request to the tutor based on the current view
            StepCompletion ex = exView.stepCompletion();
            
            //Construct the request with the users data and NewExampleRequest
            //returned by the newRequest() method
            ClientRequest request = new ClientRequest(ServerRequestType.REQUEST_HINT);
            request.setUserId(account.getUserId());
            request.setSecurityToken(MainFrame.instance().getModel().getSecurityToken());
            request.setData(gson.toJson(ex));
           
            //Send the request to the tutor and save the reply
            TutorReply reply = SvcFacade.instance().tutorRequest(request);
            
            switch (reply.getStatus()) {
                case ":ERR":
                    // If we get here, there is a coding error in the tutor svc
                    //frame.displayError("Ooops, an unexpected error occurred: SI_1");
                    System.out.println("Coding error  status: " + reply.getStatus());

                    break;

                default:
                    PendingStep pendingStep = gson.fromJson(reply.getData(), PendingStep.class);
                    Step step = pendingStep.getStep();
                    if (step.getSubType() == StepSubType.REQUEST_HINT) {
                        Hint hint = pendingStep.getCurrentHint();
                        exView.getModel().currentTask().currentStep().setCurrentHintIndex(pendingStep.getCurrentHintIndex() + 1);
                        String prompt = hint.getText();
                        JOptionPane.showMessageDialog(MainFrame.instance(),
                                prompt, "Tutor Reply", JOptionPane.INFORMATION_MESSAGE);
                    }
            }
        } catch(IllegalArgException e) {
            System.out.println("Illegal arg exception " + e);
        }
    }
}
