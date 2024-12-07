/**
 * SHATU: SHA-256 Tutor
 * <p>
 * (C) Johanna & Richard Blumenthal, All rights reserved
 * <p>
 * Unauthorized use, duplication, or distribution without the authors' permission is strictly prohibited.
 * <p>
 * Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" basis
 * without warranties or conditions of any kind, either expressed or implied.
 */
package edu.regis.shatu.view;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.StepCompletionReply;
import edu.regis.shatu.model.Task;
import edu.regis.shatu.model.aol.ExampleType;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.aol.RotateStep;
import edu.regis.shatu.view.act.NewExampleAction;
import edu.regis.shatu.svc.TutorReply;
import edu.regis.shatu.view.act.StepCompletionAction;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;


/**
 * RotateView class represents the GUI view for rotating strings using ROTR (Right Rotate).
 * It extends GPanel and implements ActionListener and KeyListener interfaces.
 * <p>
 * The class provides a user interface for performing right rotations on strings and checking the results.
 * Inline comments have been added for better understanding of the code.
 *
 * @author rickb
 */
public class RotateView extends UserRequestView implements ActionListener, KeyListener {
    TutoringSessionView view;
    private String problemString;
    private int numRotations;
    private String answer;
    private JLabel prompt;
    private JLabel problem;
    private JTextField answerField;
    private JButton checkButton; // Add the check button
    private JButton hintButton;
    private JButton nextButton;
    private boolean checkHintEnabled = false;
    private JRadioButton shortProblem;
    private JRadioButton longProblem;
    private JRadioButton rightRotate;
    private JRadioButton leftRotate;
    private JRadioButton rotate7Bits;
    private JRadioButton rotate16Bits;
    private ButtonGroup lengthType;
    private ButtonGroup rotationType;
    private ButtonGroup rotateAmount;
    private RotateStep currentStep;
    /**
     * Initializes the RotateView by creating and laying out its child components.
     */
    public RotateView() {
        initializeComponents();
        initializeLayout();
    }

    /**
     * Generates a NewExampleRequest to be sent to the tutor based on the 
     * conditions selected by the user when newRequest() is called.
     * @return The NewExampleRequest object to be sent to the tutor
     */
    @Override
    public NewExampleRequest newRequest(){
        NewExampleRequest ex = new NewExampleRequest();
       
        ex.setExampleType(ExampleType.ROTATE_BITS);
        RotateStep newStep = new RotateStep();
        if(shortProblem.isSelected()) {
            newStep.setLength(16);
        }
        else {
            newStep.setLength(32);
        }
       
        newStep.setDirection(RotateStep.Direction.RIGHT);

        if(rotate7Bits.isSelected()) {
            newStep.setAmount(7);
        }
        else {
            newStep.setAmount(16);
        }
        String rotateStepJson = gson.toJson(newStep);

        ex.setData(rotateStepJson);
       
        return ex;
    }
    
    @Override
    public StepCompletion stepCompletion() {
        Step currentStep = model.currentTask().currentStep();
        
        RotateStep example = gson.fromJson(currentStep.getData(), RotateStep.class);
        
        String userResponse = answerField.getText().replaceAll("\\s", "");
        
        example.setUserResponse(userResponse);
        
        StepCompletion step = new StepCompletion(currentStep, gson.toJson(example));
        step.setStep(currentStep);
        return step;
    }
    
    /**
     * Creates and initializes the child GUI components appearing in this frame.
     */
    private void initializeComponents() {
        //Prompt and Problem labels should be populated by the result of 
        //a tutor reply for a new example. Done by pressing the New Example
        //button and on selection of the Panel
        prompt = new JLabel("Default Prompt Text");
        problem = new JLabel("Default Problem Text");
        
        answerField = new JTextField(10);
        answerField.addKeyListener(this);
        answerField.setHorizontalAlignment(JTextField.CENTER);

        checkButton = new JButton(StepCompletionAction.instance());
        checkButton.addActionListener(this);

        hintButton = new JButton("Hint");
        hintButton.addActionListener(this);
        
        nextButton = new JButton(NewExampleAction.instance());
        nextButton.setToolTipText("Generate New Example Problem");
        
        shortProblem = new JRadioButton("16-bit");
        shortProblem.setSelected(true);
        
        longProblem = new JRadioButton("32-bit");
        
        rightRotate = new JRadioButton("Right Rotation");
        rightRotate.setSelected(true);
        
        leftRotate = new JRadioButton("Left Rotation");
        
        rotate7Bits = new JRadioButton("Rotate 7 bits");
        rotate7Bits.setSelected(true);
        
        rotate16Bits = new JRadioButton("Rotate 16 bits");
        
        lengthType = new ButtonGroup();
        lengthType.add(shortProblem);
        lengthType.add(longProblem);
        
        rotationType = new ButtonGroup();
        rotationType.add(rightRotate);
        rotationType.add(leftRotate);
        
        rotateAmount = new ButtonGroup();
        rotateAmount.add(rotate7Bits);
        rotateAmount.add(rotate16Bits);
    }
    
    /**
     * Lays out the child components in this view using GridBagConstraints.
     */
    private void initializeLayout() {
        GridBagConstraints c = new GridBagConstraints();
        // Add exampleInputLabel centered
        addc(prompt, 2, 0, 2, 1, 0.2, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
        addc(problem, 2, 1, 2, 1, 0.2, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
        // Add answerField to the layout, centered
        addc(answerField, 2, 2, 2, 1, 0.2, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        addc(checkButton, 2, 3, 2, 1, 0.2, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
        addc(hintButton, 2, 4, 2, 1, 0.2, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
        addc(nextButton, 1, 5, 2, 1, 0.0, 0.2, 
              GridBagConstraints.WEST, GridBagConstraints.NONE,
              5, 5, 5, 5);
        addc(shortProblem, 0, 6, 1, 1, 0.0, 0.0, 
              GridBagConstraints.WEST, GridBagConstraints.NONE, 
              5, 5, 5, 5);
        addc(longProblem, 0, 7, 1, 1, 0.0, 0.0, 
              GridBagConstraints.WEST, GridBagConstraints.NONE, 
              5, 5, 5, 5);
        addc(rightRotate, 2, 6, 1, 1, 0.0, 0.0, 
              GridBagConstraints.WEST, GridBagConstraints.NONE, 
              5, 5, 5, 5);
        addc(leftRotate, 2, 7, 1, 1, 0.0, 0.0, 
              GridBagConstraints.WEST, GridBagConstraints.NONE, 
              5, 5, 5, 5);
        addc(rotate7Bits, 3, 6, 1, 1, 0.0, 0.0, 
              GridBagConstraints.WEST, GridBagConstraints.NONE, 
              5, 5, 5, 5);
        addc(rotate16Bits, 3, 7, 1, 1, 0.0, 0.0, 
              GridBagConstraints.WEST, GridBagConstraints.NONE, 
              5, 5, 5, 5);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == checkButton) {
            if (this.answerField.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Please provide an answer");
            }
        } else if (event.getSource() == hintButton) {
            JOptionPane.showMessageDialog(this, "Hint");
        } else if (event.getSource() == nextButton) {
            checkHintEnabled = true;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && answerField.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Please provide an answer");
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            checkButton.doClick();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
    
    /**
     * Performs rotation (ROR or ROL) on the given input string for the 
     * specified number of positions.
     *
     * @param input     The input string to rotate.
     * @param positions The number of positions for the rotation.
     * @return The rotated string.
     */
    protected String rotateString(String input, int positions) { // why is this function used in ShaZeroView?
        this.currentStep.setDirection(RotateStep.Direction.RIGHT);
        if (input == null || input.isEmpty()) {
            return input;
        }

        int length = input.length();
        positions = positions % length; // Ensure positions is within the string length

        if (positions < 0) {
            positions = length + positions; // Handle negative positions
        }

        // Perform the rotation
        // The rotated string is formed by concatenating two substrings:
        // 1. The substring starting from (length - positions) to the end of the string.
        // 2. The substring from the beginning of the string to (length - positions).
        if(currentStep.getDirection() == RotateStep.Direction.RIGHT){
           answer = input.substring(length - positions) + input.substring(0, length - positions);
        }
        else {
           answer = input.substring(positions) + input.substring(0, positions);
        }
        return answer;
    }
    
    /**
     * Update the view with the contents of a new step sent by the tutor
     */
    @Override
    protected void updateView() {
        view = SplashFrame.instance().getView(); // Accessing view to use universal buttons
        hintButton = view.getHintButton();
        checkButton = view.getCheckButton();
        nextButton = view.getNewExampleButton();
        
        // If check and hint buttons are disabled, reset listenerers and apply those used by this view
        if(!checkHintEnabled) {
            view.resetButtonListeners(); // Clear any listeners applied from other views          
            hintButton.addActionListener(this);           
            checkButton.addActionListener(this);            
            nextButton.addActionListener(this);
        }
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Step step = model.currentTask().getCurrentStep();
        
        RotateStep example = gson.fromJson(step.getData(), RotateStep.class);

        this.currentStep = example;
        
        String problemData = currentStep.getData();

        if (rightRotate.isSelected()) {
            currentStep.setDirection(RotateStep.Direction.RIGHT);
        } else {
            currentStep.setDirection(RotateStep.Direction.LEFT);
        }

        if (currentStep.getDirection() == RotateStep.Direction.RIGHT) {
            prompt.setText("Perform ROR(" + currentStep.getAmount() + ") on the following String:");
        } else {
            prompt.setText("Perform ROL(" + currentStep.getAmount() + ") on the following String:");
        }

        if (problemData == null || problemData.isEmpty()) {
            prompt.setText("");
            problem.setText("Click 'New Example' when ready.");
        } else {
            problem.setText(problemData);
            this.problemString = problemData;
            this.numRotations = currentStep.getAmount();
        }
    }
    
    @Override
    public void setCurrentTask(Task task) {
        this.model.addCurrentTask(task);
        updateView();
    }
}
