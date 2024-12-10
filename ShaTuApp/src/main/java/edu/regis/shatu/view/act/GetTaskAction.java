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
import edu.regis.shatu.model.Task;
import edu.regis.shatu.model.User;
import static edu.regis.shatu.model.aol.ExampleType.PAD_ZEROS;
import edu.regis.shatu.svc.ClientRequest;
import edu.regis.shatu.svc.ServerRequestType;
import edu.regis.shatu.svc.SvcFacade;
import edu.regis.shatu.svc.TutorReply;
import edu.regis.shatu.view.MainFrame;
import edu.regis.shatu.view.SplashFrame;
import edu.regis.shatu.view.StepSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import static javax.swing.Action.MNEMONIC_KEY;
import static javax.swing.Action.SHORT_DESCRIPTION;

/**
 *
 * @author chand
 */
public class GetTaskAction extends ShaTuGuiAction 
{
    
    /**
     * Exceptions occurring in this class are also logged to this logger.
     */
    private static final Logger LOGGER
            = Logger.getLogger(GetTaskAction.class.getName());

    /**
     * The single instance of this sign-in action.
     */
    private static final GetTaskAction SINGLETON;

    /**
     * Create the singleton for this action, which occurs when this class is
     * loaded by the Java class loaded, as a result of the class being
     * referenced by executing SignInAction.instance() in the
     * initializeComponents() method of the SplashPanel class.
     */
    static {
        SINGLETON = new GetTaskAction();
    }

    /**
     * Return the singleton instance of this sign-in action.
     *
     * @return
     */
    public static GetTaskAction instance() {
        return SINGLETON;
    }

    /**
     * Initialize action with the "Sign In" text and set its text.
     */
    private GetTaskAction() {
        super("Get Task");
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        User user = SplashFrame.instance().getUser();
        ClientRequest request = new ClientRequest(ServerRequestType.GET_TASK);
        request.setUserId(user.getUserId());
        request.setSessionId(MainFrame.instance().getModel().getSecurityToken());
        TutorReply reply = SvcFacade.instance().tutorRequest(request);
        
        switch (reply.getStatus()) {
            case "ERR":
                    // If we get here, there is a coding error in the tutor svc
                    //frame.displayError("Ooops, an unexpected error occurred: SI_1");
                    System.out.println("Coding error  status: " + reply.getStatus());

                    break;
                    
            default:
                Task task = gson.fromJson(reply.getData(), Task.class);
                switch (task.getType()) {
                    case ADD_ONE_BIT:
                        StepSelection.ADD1.getLabel().select();
                        break;
                    case PAD_ZEROS:
                        StepSelection.PAD.getLabel().select();
                        break;
                    case ADD_MSG_LENGTH:
                        StepSelection.LENGTH.getLabel().select();
                        break;
                    case PREPARE_SCHEDULE:
                        StepSelection.PREPARE.getLabel().select();
                        break;
                    case INITIALIZE_VARS:
                        StepSelection.INIT_VARS.getLabel().select();
                        break;
                    case COMPRESS_ROUND:
                        StepSelection.COMPRESS.getLabel().select();
                        break;
                    case ROTATE_BITS:
                        StepSelection.ROTATE_BITS.getLabel().select();
                        break;
                    case SHIFT_BITS:
                        StepSelection.SHIFT_RIGHT.getLabel().select();
                        break;
                    case XOR_BITS:
                        StepSelection.XOR.getLabel().select();
                        break;
                    case ADD_BITS:
                        StepSelection.ADD_TWO_BIT.getLabel().select();
                        break;
                    case MAJORITY_FUNCTION:
                        StepSelection.MAJ_FUNCTION.getLabel().select();
                        break;
                    case CHOICE_FUNCTION:
                        StepSelection.CHOICE_FUNCTION.getLabel().select();
                        break;
                    default:
                        StepSelection.ENCODE.getLabel().select();
                        break;
                }
        }
    }
    
}
