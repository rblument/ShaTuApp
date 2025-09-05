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
<<<<<<< HEAD
package edu.regis.shatu.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
=======

/*
* Set responseTextArea to disabled at initialization per SHAT-225 John hennessey 23 Feb 2025
*/
package edu.regis.shatu.view;

import java.awt.GridBagConstraints;
import java.awt.Insets;
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigInteger;

<<<<<<< HEAD
/**
 * AddTwoBitView class represents a GUI view for adding two binary numbers modulo 2^m.
 * Users can input their answers in a JTextField and check correctness.
 * Provides functionality for hints and moving to the next question.
 *
 * @author rickb
 */
public class AddTwoBitView extends GPanel implements ActionListener, KeyListener {
=======
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.BitOpStep;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.steps.Step;

/**
 * AddTwoBitView class represents a GUI view for adding two binary numbers
 * modulo 2^m. Users can input their answers in a JTextField and check
 * correctness. Provides functionality for hints and moving to the next
 * question.
 *
 * @author rickb
 * @author Amanda Roskelley
 */
public class AddTwoBitView extends UserRequestView implements KeyListener {

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * The modulo value for addition of binary numbers.
     */
    private final int m = 8; // will be changed and dynamically updated

<<<<<<< HEAD
    private final String binary1 = "101100";
    private final String binary2 = "011011";
    
    private JTextField answerField;
    private JLabel instructionLabel;
    private JLabel stringLabel1;
    private JLabel stringLabel2;
    private JButton checkButton; // Add the check button
    private JButton hintButton;
    private JButton nextQuestionButton;

    /**
     * Initializes the AddTwoBitView by creating and laying out its child components.
=======
    private String binary1 = "";
    private String binary2 = "";
    private String result = "";

    private TutoringSessionView view;
    private JTextField responseTextArea;
    private JLabel instructionLabel;
    private JLabel stringLabel1;
    private JLabel stringLabel2;
    private JLabel stringLabel3;
    private JLabel stringLabel4; // Only for testing that view is communicating with server

    /**
     * Initializes the AddTwoBitView by creating and laying out its child
     * components.
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
     */
    public AddTwoBitView() {
        initializeComponents();
        initializeLayout();
    }

    /**
<<<<<<< HEAD
     * Handles action events for components.
     *
     * @param event The ActionEvent to be handled.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == checkButton) {
            onCheckButton();
        } else if (event.getSource() == hintButton) {
            onNextHint();
        } else if (event.getSource() == nextQuestionButton) {
            onNextQuestion();
        }
    }

    /**
     * Creates child GUI components for the view.
     */
    private void initializeComponents() {
        instructionLabel = new JLabel("Add two binary numbers using modulo 2^"+ m + " addition");
        
        stringLabel1 = new JLabel("binary number1 : " + binary1);
        stringLabel2 = new JLabel("binary number2 : " + binary2);
        
        answerField = new JTextField(10);
        answerField.addKeyListener(this);

        // Create and initialize the checkButton
        checkButton = new JButton("Check");
        checkButton.addActionListener(this); // Add an action listener for the check button
        
        hintButton = new JButton("Hint");
        hintButton.addActionListener(this);
        
        nextQuestionButton = new JButton("Next Question");
        nextQuestionButton.addActionListener(this);
=======
     * Creates child GUI components for the view.
     */
    private void initializeComponents() {
        instructionLabel = new JLabel("Add two binary numbers using modulo 2^" + m + " addition");

        stringLabel1 = new JLabel("binary number1 : ");
        stringLabel2 = new JLabel("binary number2 : ");
        stringLabel3 = new JLabel("Hit New Example to get set of binary numbers");

        stringLabel4 = new JLabel(); // only for testing that view is communicating with server

        responseTextArea = new JTextField(10);
        responseTextArea.addKeyListener(this);

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }

    /**
     * Lays out the child components in the view.
     */
    private void initializeLayout() {
        GridBagConstraints centerConstraints = new GridBagConstraints();
        centerConstraints.anchor = GridBagConstraints.CENTER;
        centerConstraints.insets = new Insets(5, 5, 5, 5);
<<<<<<< HEAD
        
=======
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3

        // Add instructionLabel centered
        addc(instructionLabel, 0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);

        // Add binaryNumberOneLabel centered below instructionLabel
        addc(stringLabel1, 0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);

        // Add binaryNumberTwoLabel centered below binaryNumberOneLabel
        addc(stringLabel2, 0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);

<<<<<<< HEAD
        // Add answerField centered below binaryNumberTwoLabel
        addc(answerField, 0, 4, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);

        // Add checkButton centered below answerField
        addc(checkButton, 0, 5, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
        
        addc(hintButton, 0, 6, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
        
        addc(nextQuestionButton, 0, 7, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
=======
        addc(stringLabel3, 0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);

        // To provide answer for easier testing during build of application
        addc(stringLabel4, 0, 9, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);

        // Add responseTextArea centered below binaryNumberTwoLabel
        addc(responseTextArea, 0, 4, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);

        responseTextArea.setEnabled(false); // Text area disabled at initialization

        addc(buttonPanel, 0, 5, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }

    /**
     * Calculates the modulo addition of two binary numbers.
     *
     * @param binary1 The first binary number.
     * @param binary2 The second binary number.
     * @return The result of the modulo addition in binary form.
     */
    private String calculateModulo(String binary1, String binary2) {
        if (binary1 == null || binary1.isEmpty()) {
            return "";
        }
        if (binary2 == null || binary2.isEmpty()) {
            return "";
        }

        // Convert binary strings to BigIntegers
        BigInteger num1 = new BigInteger(binary1, 2);
        BigInteger num2 = new BigInteger(binary2, 2);

        // Perform addition
        BigInteger sum = num1.add(num2);

        // Calculate the result modulo 2^256
        BigInteger modulo = new BigInteger("2").pow(m);
        BigInteger result = sum.mod(modulo);

        // Convert the result back to a binary string
        String resultBinary = result.toString(2);

        // Ensure the binary string has 256 bits (pad with leading zeros if necessary)
        while (resultBinary.length() < m) {
            resultBinary = "0" + resultBinary;
        }

        System.out.println("Result : " + resultBinary);

        return resultBinary;
    }

    /**
     * Handles the keyTyped event for the view.
     *
     * @param e The KeyEvent that occurred.
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Handles the keyPressed event for the view.
     *
     * @param e The KeyEvent that occurred.
     */
    @Override
    public void keyPressed(KeyEvent e) {
<<<<<<< HEAD
        if (e.getKeyCode() == KeyEvent.VK_ENTER && answerField.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Please provide an answer");
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            verifyAnswer();
=======
        if (e.getKeyCode() == KeyEvent.VK_ENTER && responseTextArea.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Please provide an answer");
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            checkButton.doClick();
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        }
    }

    /**
     * Handles the keyReleased event for the view.
     *
     * @param e The KeyEvent that occurred.
     */
    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Verifies the user's answer against the correct answer.
     */
    private void verifyAnswer() {
        String correctAnswer = calculateModulo(binary1, binary2);
<<<<<<< HEAD
        // Get the text from the answerField when the checkButton is clicked
        String userAnswer = answerField.getText();
=======
        // Get the text from the responseTextArea when the checkButton is clicked
        String userAnswer = responseTextArea.getText();
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3

        if (userAnswer.equals(correctAnswer)) {
            JOptionPane.showMessageDialog(this, "Correct");
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect. The correct answer is: " + correctAnswer);
        }
    }

    /**
     * Handles the action for the Next Question button.
     */
    private void onNextQuestion() {
<<<<<<< HEAD
        JOptionPane.showMessageDialog(this, "Next Question");
=======
        // JOptionPane.showMessageDialog(this, "Next Question");
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }

    /**
     * Handles the action for the Hint button.
     */
    private void onNextHint() {
        JOptionPane.showMessageDialog(this, "Hint");
    }

    /**
     * Handles the action for the Check button.
     */
    private void onCheckButton() {
<<<<<<< HEAD
        if (answerField.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Please provide an answer");
        } else {
            verifyAnswer();
        }
    }
}

=======
        if (responseTextArea.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Please provide an answer");
        }
    }

    /**
     * Create and return the server request this view makes when a user selects
     * that they want to practice a new add two n bits example.
     *
     * @return
     */
    @Override
    public NewExampleRequest newRequest() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        NewExampleRequest ex = new NewExampleRequest();

        ex.setExampleType(ProblemType.ADD_BITS);

        BitOpStep newStep = new BitOpStep();

        ex.setData(gson.toJson(newStep));

        return ex;
    }

    /**
     * Gets the current step, then gets the next example, completes the current
     * step and then gets the next step.
     *
     * @return
     */
    @Override
    public StepCompletion stepCompletion() {
        Step currentStep = model.currentTask().currentStep().getStep();

        BitOpStep example = gson.fromJson(currentStep.getData(), BitOpStep.class);

        String userResponse = responseTextArea.getText().replaceAll("\\s", "");

        example.getExample().setResult(userResponse);

        StepCompletion step = new StepCompletion(currentStep, gson.toJson(example));

        step.setStep(currentStep);

        return step;
    }

    /**
     * Update the view with the new operands.
     *
     */
    @Override
    protected void updateView() {
        view = SplashFrame.instance().getTutoringSessionView(); // Accessing view to use universal buttons

        switch (view.getCurrentViewType()) {
            case DO_ONE:
                updatePracticeView();
                break;

            case SEE_ONE:
                updateTeachView();
                break;

            case TEACH_ONE:
                updateQuizView();
                break;

            default:
                throw new UnsupportedOperationException("Unknown Update Operation for view type: "
                        + view.getCurrentViewType());
        }
    }

    /**
     * Defines each view classes' standard method for updating in the Practice View
     */
    @Override
    protected void updatePracticeView() {
        responseTextArea.setText("");

        // If check and hint buttons are disabled, reset listenerers and apply those
        // used by this view
        if (!checkHintEnabled) {
            resetButtonListeners(); // Clear any listeners applied from other views
        }

        if (model != null) {
            // ****TO-DO*****
            // Update the view's information from the model
            // Debugging dynamic updates to the model can be done here.
            System.out.println("AddTwoBitView");

            Step step = model.currentTask().getCurrentStep().getStep();

            if (step.getSubType() == StepSubType.ADD_BITS) {
                BitOpStep example = gson.fromJson(step.getData(), BitOpStep.class);

                try {
                    binary1 = example.getExample().getOperand1();
                    binary2 = example.getExample().getOperand2();
                    result = calculateModulo(binary1, binary2);
                    checkButton.setEnabled(true);
                    responseTextArea.setEnabled(true);
                    hintButton.setEnabled(true);
                } catch (NullPointerException e) {
                    System.out.println("Example is empty.");
                    checkButton.setEnabled(false);
                    responseTextArea.setEnabled(false);
                    hintButton.setEnabled(false);
                }

                stringLabel1.setText("binary number1: " + binary1);
                stringLabel2.setText("binary number2: " + binary2);
            }
        }
    }

    /**
     * Defines each view classes' standard method for updating in the Teach Me View
     */
    @Override
    protected void updateTeachView() {

    }

    /**
     * Defines each view classes' standard method for updating in the Teach Me View
     */
    @Override
    protected void updateQuizView() {

    }
}
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
