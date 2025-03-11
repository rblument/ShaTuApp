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
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JRadioButton;

import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.NewExampleRequest;
import javax.swing.JRadioButton;
import edu.regis.shatu.model.aol.ShaOneViewStep;
import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.aol.ProblemType;

/**
 * ShaOne class represents the GUI view for performing the SHA Σ₁ function, involving a right shift operation.
 * It extends GPanel and implements ActionListener and KeyListener interfaces.
 * <p>
 * The class provides a user interface for performing right shifts on binary numbers and checking the results.
 * Inline comments have been added for better understanding of the code.
 *
 * @author rickb
 */
public class ShaOneView extends UserRequestView { //implements KeyListener 
    private TutoringSessionView view;
    
    /**
     * The number of places to perform the right shift operation.
     */
    private final int X_PLACES = 10; // will be changed and dynamically updated
    private final int EXAMPLE_INPUT = 0b11011010101010101010101010101010;

    private String answer;
    private JLabel exampleInputLabel;
    private JLabel problem;
    private JTextField answerField;

    private JButton nextQuestionButton;

    private boolean checkHintEnabled = false;

    /**
     * Initialize this view including creating and laying out its child components.
     */
    public ShaOneView() {
        initializeComponents();
        initializeLayout();
    }
    
    /**
     * Create the child GUI components appearing in this frame.
     */
    private void initializeComponents() {
        exampleInputLabel = new JLabel("Given an 𝑛 bit binary number, output the value of the SHA Σ₁ function");
        problem = new JLabel("Default Problem Text");
        answerField = new JTextField(10);
        //answerField.addKeyListener(this);
        
        nextQuestionButton = new JButton("Next Question");
        //nextQuestionButton.addActionListener(this);
        
      //  shortProblem = new JRadioButton("16-bit");
     //   shortProblem.setSelected(true);

       // longProblem = new JRadioButton("32-bit");

    }

    /**
     * Layout the child components in this view.
     */
    private void initializeLayout() {
        GridBagConstraints centerConstraints = new GridBagConstraints();
        centerConstraints.anchor = GridBagConstraints.CENTER;
        centerConstraints.insets = new Insets(5, 5, 5, 5);
        // Add exampleInputLabel centered
        addc(exampleInputLabel, 0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
        addc(problem, 0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
        // Add answerField to the layout, centered
        addc(answerField, 0, 4, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
/*
        addc(checkButton, 0, 5, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
        addc(hintButton, 0, 6, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
        addc(nextQuestionButton, 0, 7, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
                addc(shortProblem, 0, 6, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                5, 5, 5, 5);
        addc(longProblem, 0, 7, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                5, 5, 5, 5);
*/
    }

    /**
     * Performs a right shift operation on a binary number for the specified number of places.
     *
     * @param x      The input binary number.
     * @param places The number of places for the right shift.
     * @return The binary result after the right shift operation.
     */
    public String shiftRightString(int x, int places) {
        // Perform the right shift operation
        int result = x >> places;
        // Print the original and shifted binary numbers
        System.out.println("Original Binary: " + Integer.toBinaryString(x));
        System.out.println("Shifted Binary:  " + Integer.toBinaryString(result));

        return Integer.toBinaryString(result);
    }
    


    /*
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && answerField.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Please provide an answer");
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            view.checkButton.doClick();
        }
    }
    */

   // @Override
   // public void keyReleased(KeyEvent e) {
   // }

    /**
     * Verifies the user's answer by comparing it with the correct result of the right shift operation.
     */
    private void verifyAnswer() {
        String correctAnswer = shiftRightString(EXAMPLE_INPUT, X_PLACES);
        // Get the text from the answerField when the checkButton is clicked
        String userAnswer = answerField.getText();

        if (userAnswer.equals(correctAnswer)) {
            JOptionPane.showMessageDialog(this, "Correct");
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect. The correct answer is: " + correctAnswer);
        }
    }
    
    /**
     * Displays a message dialog indicating the start of the next question.
     */
    private void onNextQuestion() {
        JOptionPane.showMessageDialog(this, "Next Question");
    }

    /**
     * Displays a message dialog indicating the provision of a hint.
     */
    private void onNextHint() {
        JOptionPane.showMessageDialog(this, "Hint");
    }

    /**
     * Handles the click event of the check button, verifying the user's answer.
     */
    private void onCheckButton() {
        if (answerField.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Please provide an answer");
        } else {
            verifyAnswer();
        }
    }
    
    @Override
    /**
     * Updates the description, question, and hints from the model
     * 
     * TODO: THIS IS A PLACEHOLDER UNTIl WE HAVE HAVE THE MODEL CODE COMPLETED
     */
    protected void updateView() {
        view = SplashFrame.instance().getTutoringSessionView(); // Accessing view to use universal buttons
        //hintButton = view.getHintButton();
        //checkButton = view.getCheckButton();
        nextQuestionButton = view.getNewExampleButton();
        
        // If check and hint buttons are disabled, reset listenerers and apply those used by this view
        if(!checkHintEnabled) {
            view.resetButtonListeners(); // Clear any listeners applied from other views          
        }
        
        if (model != null) {
            // ****TO-DO*****
            // Update the view's information from the model
            // Debugging dynamic updates to the model can be done here.
            System.out.println("ShaOneView");
        }
    }

       /**
     * Create and return the server request this view makes when a user selects
     * that they want to practice a new Sha One View example.
     *
     * @return
     */
    @Override
    public NewExampleRequest newRequest() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        NewExampleRequest ex = new NewExampleRequest();

        ex.setExampleType(ProblemType.SHA_ONE);

        ShaOneViewStep newStep = new ShaOneViewStep();

        ex.setData(gson.toJson(newStep));

        return ex;
    }


    @Override
    public StepCompletion stepCompletion() {
        Step currentStep = model.currentTask().currentStep().getStep();

        ShaOneViewStep example = gson.fromJson(currentStep.getData(), ShaOneViewStep.class);

        String userResponse = answerField.getText().replaceAll("\\s", "");

        example.setUserResponse(userResponse);

        StepCompletion step = new StepCompletion(currentStep, gson.toJson(example));
        step.setStep(currentStep);
        return step;
    }
}
