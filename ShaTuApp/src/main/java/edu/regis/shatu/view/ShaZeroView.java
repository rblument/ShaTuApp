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

import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.aol.RotateStep;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * ShaZero class represents the GUI view for performing the SHA Σ₀ function, involving rotation and right shift operations.
 * It extends GPanel and implements ActionListener and KeyListener interfaces.
 * <p>
 * The class provides a user interface for performing rotations and right shifts on binary numbers and checking the results.
 * Inline comments have been added for better understanding of the code.
 *
 * @author rickb
 */
public class ShaZeroView extends UserRequestView implements ActionListener, KeyListener {
    private TutoringSessionView view;
    
    /**
     * The number of places for the right shift operation.
     */
    private final int X_PLACES = 10; // will be changed and dynamically updated
    private final int EXAMPLE_INPUT = 0b11011010101010101010101010101010;

    private String answer;
    private JLabel exampleInputLabel;
    private JTextField answerField;
    private JButton checkButton;
    private JButton hintButton;
    private JButton nextButton;
    private boolean checkHintEnabled = false;
    
    /**
     * Initialize this view including creating and laying out its child components.
     */
    public ShaZeroView() {
        initializeComponents();
        initializeLayout();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == checkButton) {
            onCheckButton();
        } else if (event.getSource() == hintButton) {
            onNextHint();
        } else if (event.getSource() == nextButton) {
            checkHintEnabled = true;
            onNextQuestion();
        }
    }

    /**
     * Create the child GUI components appearing in this frame.
     */
    private void initializeComponents() {

        exampleInputLabel = new JLabel("Given an 𝑛 bit binary number, output the value of the SHA 𝛴₀  function");

        answerField = new JTextField(10);
        answerField.addKeyListener(this);

        // Create and initialize the checkButton
        checkButton = new JButton("Check");
        checkButton.addActionListener(this); // Add an action listener for the check button
        
        hintButton = new JButton("Hint");
        hintButton.addActionListener(this); // Add an action listener for the check button
        
        nextButton = new JButton("Next Question");
        nextButton.addActionListener(this);
    }

    /**
     * Layout the child components in this view.
     */
    private void initializeLayout() {
        GridBagConstraints centerConstraints = new GridBagConstraints();
        centerConstraints.anchor = GridBagConstraints.CENTER;
        centerConstraints.insets = new Insets(5, 5, 5, 5);

        // Add exampleInputLabel centered
        addc(exampleInputLabel, 0, 0, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
        // Add answerField to the layout, centered
        addc(answerField, 0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        addc(checkButton, 0, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
        addc(hintButton, 0, 3, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
        addc(nextButton, 0, 7, 1,1,0.0,0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
    }
    /**
     * Performs rotation (ROR or ROL) on the given input string for the 
     * specified number of positions.
     *
     * @param input     The input string to rotate.
     * @param positions The number of positions for the rotation.
     * @return The rotated string.
     */
    
    protected String rotateString(String input, int positions, RotateStep.Direction direction) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        int length = input.length();
        positions = positions % length; // Ensure positions is within the string length

        if (positions < 0) {
            positions = length + positions; // Handle negative positions
        }

        // Perform the rotation based on the direction
        String result;
        if (direction == RotateStep.Direction.RIGHT) {
            result = input.substring(length - positions) + input.substring(0, length - positions);
        } else { // LEFT direction
            result = input.substring(positions) + input.substring(0, positions);
        }

        return result;
    }
    /**
     * Calculates the SHA Σ₀ function involving rotation and right shift operations.
     *
     * @param input The input binary number.
     * @return The result after performing the SHA Σ₀ function.
     */
    private String calculateSigma(int input) {
        ShiftRightView shiftRight = new ShiftRightView();
        String inputString = Integer.toBinaryString(input); // Convert input to binary string

        // Perform rotations and shift operations
        String answer = rotateString(inputString, X_PLACES, RotateStep.Direction.RIGHT);
        answer = rotateString(answer, X_PLACES + 10, RotateStep.Direction.RIGHT);
        answer = shiftRight.shiftRightString(Integer.parseInt(answer, 2), X_PLACES);
        return answer;
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
     * Verifies the user's answer by comparing it with the correct result of the SHA Σ₀ function.
     */
    private void verifyAnswer() {
        String correctAnswer = calculateSigma(EXAMPLE_INPUT);
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
        
        if (model != null) {
            // ****TO-DO*****
            // Update the view's information from the model
            // Debugging dynamic updates to the model can be done here.
            System.out.println("ShaZeroView");
        }
    }

    @Override
    public NewExampleRequest newRequest() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public StepCompletion stepCompletion() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
