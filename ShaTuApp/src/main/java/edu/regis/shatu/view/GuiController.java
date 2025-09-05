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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
<<<<<<< HEAD
import edu.regis.shatu.model.StepCompletionReply;
=======

import edu.regis.shatu.model.steps.StepCompletionReply;
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
import edu.regis.shatu.svc.ClientRequest;
import edu.regis.shatu.svc.SHA_256;
import edu.regis.shatu.svc.SvcFacade;
import edu.regis.shatu.svc.TutorReply;

/**
 * A mediator between the GUI and the SHA-256 algorithm.
 * 
 * @author rickb
 */
public class GuiController {
    /**
     * The singleton instance of this controller.
     */
    private static GuiController SINGLETON;
<<<<<<< HEAD
    
    static {
        SINGLETON = new GuiController();
    }
    
=======

    static {
        SINGLETON = new GuiController();
    }

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * Return the singleton instanced of this controller.
     * 
     * @return GuiController
     */
    public static GuiController instance() {
        return SINGLETON;
    }
<<<<<<< HEAD
    
=======

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * A convenience reference to the step selector view.
     */
    private StepSelectorView stepSelectorView;
<<<<<<< HEAD
    
=======

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * A convenience reference to the step view.
     */
    private StepView stepView;
<<<<<<< HEAD
    
=======

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * The SHA-256 algorithm.
     */
    private final SHA_256 sha256Alg;

    /**
<<<<<<< HEAD
     * Utility reference used to convert between Java and JSon. 
     */
    private final Gson gson;
    
=======
     * Utility reference used to convert between Java and JSon.
     */
    private final Gson gson;

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * Return the SHA-256 algorithm.
     * 
     * @return SHA_256
     */
    public SHA_256 getSha256Alg() {
        return sha256Alg;
    }

    public StepSelectorView getStepSelectorView() {
        return stepSelectorView;
    }

    public void setStepSelectorView(StepSelectorView stepSelectionView) {
        this.stepSelectorView = stepSelectionView;
    }

    public StepView getStepView() {
        return stepView;
    }

    public void setStepView(StepView stepView) {
        this.stepView = stepView;
    }
<<<<<<< HEAD
    
     public TutorReply tutorRequest(ClientRequest request) {      
         
        TutorReply reply = SvcFacade.instance().tutorRequest(request);
         
        switch(reply.getStatus()) {
=======

    public TutorReply tutorRequest(ClientRequest request) {

        TutorReply reply = SvcFacade.instance().tutorRequest(request);

        switch (reply.getStatus()) {
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
            case "StepCompletionReply":
                handleStepCompletionReply(reply);
                return null;
        }
<<<<<<< HEAD
         
        // TO_DO: This is probably temporary until all GUIs are converted
        return reply;
     }
     
     private void handleStepCompletionReply(TutorReply reply) {
         String data = reply.getData();
         
         StepCompletionReply stepReply = gson.fromJson(data, StepCompletionReply.class);
     }
    
=======

        // TO_DO: This is probably temporary until all GUIs are converted
        return reply;
    }

    private void handleStepCompletionReply(TutorReply reply) {
        String data = reply.getData();

        StepCompletionReply stepReply = gson.fromJson(data, StepCompletionReply.class);
    }

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * If not already initialed, initialize the SHA-256 algorithm.
     */
    private GuiController() {
        gson = new GsonBuilder().setPrettyPrinting().create();
<<<<<<< HEAD
        
=======

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        sha256Alg = SHA_256.instance();
    }
}
