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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.regis.shatu.err.IllegalArgException;
import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.StepCompletionReply;
import edu.regis.shatu.model.Task;
import edu.regis.shatu.model.User;
import edu.regis.shatu.model.aol.ExampleType;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.svc.ClientRequest;
import edu.regis.shatu.svc.ServerRequestType;
import edu.regis.shatu.svc.SvcFacade;
import edu.regis.shatu.svc.TutorReply;
import edu.regis.shatu.view.GuiController;
import edu.regis.shatu.view.MainFrame;
import edu.regis.shatu.view.SplashFrame;
import edu.regis.shatu.view.StepSelection;
import edu.regis.shatu.view.UserRequestView;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import static javax.swing.Action.MNEMONIC_KEY;
import static javax.swing.Action.SHORT_DESCRIPTION;
import javax.swing.JOptionPane;

/**
 * An (MVC) controller handling a GUI gesture representing a user's request to 
 * completed the current step, which is the finest grained interface gesture
 * a user can perform that is sent to the tutor.
 *
 * If successful, the appropriate view will be updated with the tutor's reply.
 * 
 * @author rickb
 */
public class StepCompletionAction extends ShaTuGuiAction {

    /**
     * Exceptions occurring in this class are also logged to this logger.
     */
    private static final Logger LOGGER
            = Logger.getLogger(StepCompletionAction.class.getName());

    /**
     * The single instance of this sign-in action.
     */
    private static final StepCompletionAction SINGLETON;

    /**
     * Create the singleton for this action, which occurs when this class is
     * loaded by the Java class loaded, as a result of the class being
     * referenced by executing SignInAction.instance() in the
     * initializeComponents() method of the SplashPanel class.
     */
    static {
        SINGLETON = new StepCompletionAction();
    }

    /**
     * Return the singleton instance of this sign-in action.
     *
     * @return
     */
    public static StepCompletionAction instance() {
        return SINGLETON;
    }

    /**
     * Initialize action with the "Sign In" text and set its text.
     */
    private StepCompletionAction() {
        super("Check");
        putValue(SHORT_DESCRIPTION, "Check Example");
        putValue(MNEMONIC_KEY, KeyEvent.VK_C);
    }

    /**
     * Handle the user's request for a new example by sending it to the tutor.
     *
     * If successful, the current view is updated with the contents of the new 
     * example (and associated task) in the reply from the tutor.
     *
     * @param evt ignored
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        User user = SplashFrame.instance().getUser();
        //Catches a possible IllegalArgumentException thrown by the 
        //getUserRequestView() method
        try{
            //Get the current view that initiated the StepCompletionRequest
            UserRequestView exView = GuiController.instance().getStepView().getUserRequestView();
            //Call the overridden newRequest() method to generate an appropriate
            //request to the tutor based on the current view
            StepCompletion ex = exView.stepCompletion();
            //Construct the request with the users data and NewExampleRequest
            //returned by the newRequest() method
            ClientRequest request = new ClientRequest(ServerRequestType.COMPLETED_STEP);
            request.setUserId(user.getUserId());
            request.setSessionId(MainFrame.instance().getModel().getSecurityToken());
            request.setData(gson.toJson(ex));
           
            //Send the request to the tutor and save the reply
            TutorReply reply = SvcFacade.instance().tutorRequest(request);

           
            switch (reply.getStatus()) {
                case "ERR":
                    // If we get here, there is a coding error in the tutor svc
                    //frame.displayError("Ooops, an unexpected error occurred: SI_1");
                    System.out.println("Coding error  status: " + reply.getStatus());

                    break;

                default:               
                    Task task = gson.fromJson(reply.getData(), Task.class);

                    if (task.getType() == ExampleType.STEP_COMPLETION_REPLY) {
                        String selection1 = "Move on to Next Task";
                        String selection2 = "Try Same Problem Again";
                        String selection3 = "Try a Similar Problem";
                        String selection4 = "Show the correct Answer";

                        Step step = task.getCurrentStep();
                        if (step.getSubType() == StepSubType.STEP_COMPLETION_REPLY) {
                            StepCompletionReply stepReply = gson.fromJson(step.getData(), StepCompletionReply.class);

                            if (stepReply.isCorrect()) {
                                if (stepReply.isNewStep()) {
                                    String prompt = "Congratulations, the anser you submitted is correct. " +
                                            "As I believe you've mastered this outcome, I suggest moving on to a different task. " +
                                            "However, if you'd like you can try a similar problem again.";
                                    String[] options = {selection1, selection3};
                                    int choice = JOptionPane.showOptionDialog(MainFrame.instance(),
                                            prompt, "Tutor Reply", 0, 3, null, options, options[0]);

                                    switch (choice)
                                    {
                                        case 0 -> {
                                            System.out.println("Next Task");
                                            switch (GuiController.instance().getStepView().getSelectedPanel()) {
                                                case ENCODE -> {
                                                    task.setType(ExampleType.ADD_ONE_BIT);
                                                    StepSelection.ADD1.getLabel().select();
                                                }
                                                case ADD1 -> {
                                                    task.setType(ExampleType.PAD_ZEROS);
                                                    StepSelection.PAD.getLabel().select();
                                                }
                                                case PAD -> {
                                                    task.setType(ExampleType.ADD_MSG_LENGTH);
                                                    StepSelection.LENGTH.getLabel().select();
                                                }
                                                case LENGTH -> {
                                                    task.setType(ExampleType.PREPARE_SCHEDULE);
                                                    StepSelection.PREPARE.getLabel().select();
                                                }
                                                case PREPARE -> {
                                                    task.setType(ExampleType.INITIALIZE_VARS);
                                                    StepSelection.INIT_VARS.getLabel().select();
                                                }
                                                case INIT_VARS -> {
                                                    task.setType(ExampleType.COMPRESS_ROUND);
                                                    StepSelection.COMPRESS.getLabel().select();
                                                }
                                                case COMPRESS -> {
                                                    task.setType(ExampleType.ROTATE_BITS);
                                                    StepSelection.ROTATE_BITS.getLabel().select();
                                                }
                                                case ROTATE_BITS -> {
                                                    task.setType(ExampleType.SHIFT_BITS);
                                                    StepSelection.SHIFT_RIGHT.getLabel().select();
                                                }
                                                case SHIFT_RIGHT -> {
                                                    task.setType(ExampleType.XOR_BITS);
                                                    StepSelection.XOR.getLabel().select();
                                                }
                                                case XOR -> {
                                                    task.setType(ExampleType.ADD_BITS);
                                                    StepSelection.ADD_TWO_BIT.getLabel().select();
                                                }
                                                case ADD_TWO_BIT -> {
                                                    task.setType(ExampleType.CHOICE_FUNCTION);
                                                    StepSelection.CHOICE_FUNCTION.getLabel().select();
                                                }
                                                case CHOICE_FUNCTION -> {
                                                    task.setType(ExampleType.MAJORITY_FUNCTION);
                                                    StepSelection.MAJ_FUNCTION.getLabel().select();
                                                }
                                                case MAJ_FUNCTION -> {
                                                    task.setType(ExampleType.SHA_ZERO);
                                                    StepSelection.SHA_ZERO.getLabel().select();
                                                }
                                                case SHA_ZERO -> {
                                                    task.setType(ExampleType.SHA_ONE);
                                                    StepSelection.SHA_ONE.getLabel().select();
                                                }
                                                case SHA_ONE -> {
                                                    // This is the last step
                                                    System.out.println("All steps completed");
                                                }
                                            }
                                        }
                                        default -> {
                                            System.out.println("try similar problem");
                                            NewExampleAction.instance().actionPerformed(null);
                                        }
                                    }
                                }
                            } else if (stepReply.getResponse().isEmpty()) {
                                String prompt = "Please enter an answer";
                                JOptionPane.showMessageDialog(MainFrame.instance(),
                                        prompt, "Tutor Reply", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                String prompt = "Unfortunately, your answer was incorrect. Please try agian.";
                                String[] options = {selection2, selection3, selection4};
                                int choice = JOptionPane.showOptionDialog(MainFrame.instance(),
                                        prompt, "Tutor Reply", 0, 3, null, options, options[0]);

                                switch (choice)
                                {
                                    case 1 -> {
                                        System.out.println("try similar problem");
                                        NewExampleAction.instance().actionPerformed(null);
                                    }
                                    case 2 -> {
                                        System.out.println("show answer");
                                        JOptionPane.showMessageDialog(MainFrame.instance(),
                                        stepReply.getCorrectAnswer(), "Tutor Reply", JOptionPane.INFORMATION_MESSAGE);
                                    }
                                    default -> System.out.println("try again");
                                }
                            }

                        }
                    }

                //exView.setCurrentTask(task);  



            }
        }catch(IllegalArgException e){
           System.out.println("Illegal arg exception " + e);
        }
    }
}
