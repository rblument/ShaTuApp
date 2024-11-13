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

import edu.regis.shatu.err.IllegalArgException;
import edu.regis.shatu.model.TutoringSession;

import edu.regis.shatu.view.act.NewExampleAction;
import java.awt.CardLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * A view displaying a current task and step in the tutoring session, which
 * typically allows a student to practice a specific aspect of the SHA-256
 * algorithm, such as performing the XOR function.
 * 
 * @author rickb
 */
public class StepView extends JPanel {       
    /**
     * The name of the card (child view) currently displayed in this view.
     * Pass this value to getUserRequestView(...) to obtain the actual view.
     */
    private StepSelection selectedPanel;
    
    /**
     * The child ASCII child view (card) that can be displayed in this view 
     */
    private EncodeView encodeView;
    
    private CompressionCanvasView compressionView;
    
    private PrepareScheduleView prepareScheduleView;
    
    private Add1View add1View;
    
    private Pad0View padView;
    
    private RotateView rotateView;
    
    private InitVarView initVarView;

    private ShiftRightView shiftRightView;
    
    private ExclusiveOrView exclusiveOrView;

    private AddTwoBitView addTwoBitView;
    
    private MajFunctionView majFunction;
    
    private ShaZeroView shaZero;
    
    private ShaOneView shaOne;
    
    private ChoiceFunctionView choiceFunctionView;
    
    private StepCompletionReplyView stepReplyView;
    
    private TutoringSession model;
    
    private MessageLenView messageLenView;


    /**
     * Initialize and layout the child components (cards) displayed in this view.
     */
    public StepView() {
        GuiController.instance().setStepView(this);
        
        setLayout(new CardLayout());
        
        initializeComponents();
        initializeLayout();
        
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        selectPanel(StepSelection.ENCODE);   
    }

    public TutoringSession getModel() {
        return model;
    }

    public void setModel(TutoringSession model) {
        this.model = model;

        try {
            getUserRequestView().setModel(model);
        } catch (IllegalArgException e) {
            // If we get here, we're somehow displaying a view we don't know
            // about, which is a clear coding error.
            System.out.println("StepView.setModel " + e);
        }
    }
    
    /**
     * Display the child view with the given name. Request a task from the 
     * tutor for views that display a problem to be solved for the student
     * 
     * @param name a StepSelection
     */
    public void selectPanel(StepSelection name){
        CardLayout cl = (CardLayout) getLayout();
       
        cl.show(this, name.toString());
        
        selectedPanel = name;
        
        try {
            getUserRequestView().setModel(model);
        } catch (IllegalArgException e) {
            // If we get here, we're somehow displaying a view we don't know
            // about, which is a clear coding error.
            System.out.println("StepView.setModel " + e);
        }
        
        //ToDo: Unfinished switch statement for views that need to get a task from 
        //the tutor when selected
        // DONT DO THIS. Should not ask the tutor everytime a view is displayed.
        //switch(selectedPanel){
        //   case ROTATE_BITS:
        //      NewExampleAction.instance();
        //   default:
        //}
    }
    
    
    /**
     * Method invoked when a new task is requested of the tutor. If the current
     * selected panel is a view that can make a request to the tutor, return 
     * the view. Otherwise, throws an Illegal Argument Exception
     * 
     * @return a UserRequestView object 
     * @throws IllegalArgException when the selected panel is not part of the 
     * UserRequestView Class
     */
    public UserRequestView getUserRequestView() throws IllegalArgException{
        // ToDo: Need to add the view for each function 
        switch(selectedPanel){
            case ADD1:
                return add1View;
            case ADD_TWO_BIT:
                return addTwoBitView;
            case CHOICE_FUNCTION: 
                return choiceFunctionView;
            case COMPRESS:
                return compressionView;
            case ENCODE:
                return encodeView;
            case INIT_VARS:
                    return initVarView;
            case MAJ_FUNCTION:
                return majFunction;
            case ROTATE_BITS:
                return rotateView;
            case PAD:
                return padView;
            case PREPARE:
                return prepareScheduleView;
            case SHA_ZERO:
                return shaZero;
            case SHA_ONE:
                return shaOne;
            case SHIFT_RIGHT:                
                return shiftRightView;
            case STEP_REPLY:
                    return stepReplyView;
            case XOR:
                return exclusiveOrView;
            case LENGTH:
                return messageLenView;
   
            default:

                String msg = "Illegal User RequestView in StepView selection " + selectedPanel;
                throw new IllegalArgException(msg);
       }
    }
    
    public StepSelection getSelectedPanel() {
        return selectedPanel;
    }

    /**
     * Create the child GUI components appearing in this frame.
     */
    private void initializeComponents() { 
        encodeView = new EncodeView();
        prepareScheduleView = new PrepareScheduleView();
        compressionView = new CompressionCanvasView();
        rotateView = new RotateView();
        shiftRightView = new ShiftRightView();
        exclusiveOrView = new ExclusiveOrView();
        addTwoBitView = new AddTwoBitView();
        majFunction = new MajFunctionView();
        shaZero = new ShaZeroView();
        shaOne = new ShaOneView();
        add1View = new Add1View();
        padView = new Pad0View();
        rotateView = new RotateView();
        initVarView = new InitVarView();
        exclusiveOrView = new ExclusiveOrView();
        choiceFunctionView = new ChoiceFunctionView();
        stepReplyView = new StepCompletionReplyView();
        messageLenView = new MessageLenView();
    }
    
    /**
     * Layout the child components in this view
     */
    private void initializeLayout() {
        add(encodeView, StepSelection.ENCODE.toString());
        add(prepareScheduleView, StepSelection.PREPARE.toString());
        add(compressionView, StepSelection.COMPRESS.toString());
        add(shiftRightView, StepSelection.SHIFT_RIGHT.toString());
        add(exclusiveOrView, StepSelection.XOR.toString());
        add(addTwoBitView, StepSelection.ADD_TWO_BIT.toString());
        add(majFunction, StepSelection.MAJ_FUNCTION.toString());
        add(shaZero, StepSelection.SHA_ZERO.toString());
        add(shaOne, StepSelection.SHA_ONE.toString());
        add(add1View, StepSelection.ADD1.toString());
        add(padView, StepSelection.PAD.toString());
        add(rotateView, StepSelection.ROTATE_BITS.toString());
        add(initVarView, StepSelection.INIT_VARS.toString());
        add(exclusiveOrView, StepSelection.XOR.toString());
        add(choiceFunctionView, StepSelection.CHOICE_FUNCTION.toString());
        add(messageLenView, StepSelection.LENGTH.toString());


        add(stepReplyView, StepSelection.STEP_REPLY.toString());
    }
}
