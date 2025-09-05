/**
 * SHATU: SHA-256 Tutor
 * <p>
 * (C) Johanna & Richard Blumenthal, All rights reserved
 * <p>
<<<<<<< HEAD
 * Unauthorized use, duplication, or distribution without the authors' permission is strictly prohibited.
 * <p>
 * Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" basis
 * without warranties or conditions of any kind, either expressed or implied.
=======
 * Unauthorized use, duplication, or distribution without the authors'
 * permission is strictly prohibited.
 * <p>
 * Unless required by applicable law or agreed to in writing, this software is
 * distributed on an "AS IS" basis without warranties or conditions of any kind,
 * either expressed or implied.
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
 */
package edu.regis.shatu.view;

import java.awt.GridBagConstraints;
<<<<<<< HEAD
import java.awt.Insets;
=======
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
<<<<<<< HEAD
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * RotateView class represents the GUI view for rotating strings using ROTR (Right Rotate).
 * It extends GPanel and implements ActionListener and KeyListener interfaces.
 * <p>
 * The class provides a user interface for performing right rotations on strings and checking the results.
 * Inline comments have been added for better understanding of the code.
 *
 * @author rickb
 */
public class RotateView extends GPanel implements ActionListener, KeyListener {

    /**
     * The number of rotations used in the ROTR operation.
     */
    private final int NO_ROTATIONS = 7; // will be changed and dynamically updated

    /**
     * Example input string for ROTR operation.
     */
    private final String EXAMPLE_INPUT = "Example Input";

    private String answer;
    private JLabel exampleInputLabel;
    private JTextField answerField;
    private JButton checkButton; // Add the check button
    private JButton hintButton;
    private JButton nextQuestionButton;

    /**
     * Initializes the RotateView by creating and laying out its child components.
=======

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.RotateStep;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.steps.Step;

/**
 * RotateView class represents the GUI view for rotating strings using ROTR
 * (Right Rotate). It extends GPanel and implements ActionListener and
 * KeyListener interfaces.
 * <p>
 * The class provides a user interface for performing right rotations on strings
 * and checking the results. Inline comments have been added for better
 * understanding of the code.
 *
 * @author rickb
 */
public class RotateView extends UserRequestView implements KeyListener {

    TutoringSessionView view;
    private String problemString;
    private int numRotations;
    private JLabel prompt;
    private JLabel problem;
    private JTextField answerField;

    private JRadioButton shortProblem;
    private JRadioButton longProblem;
    private JRadioButton rightRotate;
    private JRadioButton leftRotate; // should be phased out as SHA256 does not left rotate, and the concept is
                                     // simple enough.
    private JRadioButton rotate7Bits;// -however, there is a lot of framework already in place that includes LEFT.
    private JRadioButton rotate16Bits;
    private ButtonGroup lengthType;
    private ButtonGroup rotationType;
    private ButtonGroup rotateAmount;
    private RotateStep currentStep;

    /**
     * Initializes the RotateView by creating and laying out its child
     * components.
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
     */
    public RotateView() {
        initializeComponents();
        initializeLayout();
    }

<<<<<<< HEAD
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == checkButton) {
            onCheckButton();
        } else if (event.getSource() == hintButton) {
            onNextHint();
        } else if (event.getSource() == nextQuestionButton) {
            onNextQuestion();
        }
=======
    /**
     * Generates a NewExampleRequest to be sent to the tutor based on the
     * conditions selected by the user when newRequest() is called.
     *
     * @return The NewExampleRequest object to be sent to the tutor
     */
    @Override
    public NewExampleRequest newRequest() {
        NewExampleRequest ex = new NewExampleRequest();

        ex.setExampleType(ProblemType.ROTATE_BITS);
        RotateStep newStep = new RotateStep();
        if (shortProblem.isSelected()) {
            newStep.setLength(16);
        } else {
            newStep.setLength(32);
        }

        newStep.setDirection(RotateStep.Direction.RIGHT);

        if (rotate7Bits.isSelected()) {
            newStep.setAmount(7);
        } else {
            newStep.setAmount(16);
        }
        String rotateStepJson = gson.toJson(newStep);

        ex.setData(rotateStepJson);

        return ex;
    }

    @Override
    public StepCompletion stepCompletion() {
        Step currentStep = model.currentTask().currentStep().getStep();

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
        prompt = new JLabel("Default Prompt Text");
        problem = new JLabel("Default Problem Text");

        answerField = new JTextField(10);
        answerField.addKeyListener(this);
        answerField.setHorizontalAlignment(JTextField.CENTER);

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
        // Logic for radio buttons (selected/enabled)
        rotate7Bits.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rotate7Bits.isSelected()) {
                    shortProblem.setEnabled(true);
                }
            }
        });
        rotate16Bits.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rotate16Bits.isSelected()) {
                    shortProblem.setEnabled(false);
                    longProblem.setSelected(true);
                } else {
                    shortProblem.setEnabled(true);
                }
            }
        });
        shortProblem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (shortProblem.isSelected()) {
                    rotate16Bits.setEnabled(false);
                    rotate16Bits.setSelected(false);
                } else {
                    rotate16Bits.setEnabled(true);
                }
            }
        });
        longProblem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (longProblem.isSelected()) {
                    shortProblem.setEnabled(true);
                    rotate16Bits.setEnabled(true);
                }
            }
        });
    }

    /**
     * Lays out the child components in this view using GridBagConstraints.
     */
    private void initializeLayout() {
        GridBagConstraints c = new GridBagConstraints();
        addc(prompt, 2, 0, 2, 1, 0.2, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
        addc(problem, 2, 1, 2, 1, 0.2, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
        addc(answerField, 2, 2, 2, 1, 0.2, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        addc(shortProblem, 0, 6, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                5, 5, 5, 5);
        addc(longProblem, 0, 7, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                5, 5, 5, 5);
        addc(rotate7Bits, 3, 6, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                5, 5, 5, 5);
        addc(rotate16Bits, 3, 7, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                5, 5, 5, 5);

        addc(buttonPanel, 0, 8, 4, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && answerField.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Please provide an answer");
<<<<<<< HEAD
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            verifyAnswer();
        }
    }

    /**
     * Creates and initializes the child GUI components appearing in this frame.
     */
    private void initializeComponents() {
        exampleInputLabel = new JLabel("Perform ROTR(" + NO_ROTATIONS + ") on: " + EXAMPLE_INPUT);

        answerField = new JTextField(10);
        answerField.addKeyListener(this);

        // Create and initialize the checkButton
        checkButton = new JButton("Check");
        checkButton.addActionListener(this); // Add an action listener for the check button

        hintButton = new JButton("Hint");
        hintButton.addActionListener(this);

        nextQuestionButton = new JButton("Next Question");
        nextQuestionButton.addActionListener(this);
    }

    /**
     * Lays out the child components in this view using GridBagConstraints.
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

        addc(nextQuestionButton, 0, 7, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
    }

    /**
     * Performs right rotation (ROTR) on the given input string for the specified number of positions.
     *
     * @param input     The input string to rotate.
     * @param positions The number of positions for the rotation.
     * @return The rotated string.
     */
    protected String rotateString(String input, int positions) {
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
        answer = input.substring(length - positions) + input.substring(0, length - positions);

        return answer;
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    /**
     * Verifies the user's answer by comparing it with the correct rotated string.
     */
    private void verifyAnswer() {
        String correctAnswer = rotateString(EXAMPLE_INPUT, NO_ROTATIONS);
        // Get the text from the answerField when the checkButton is clicked
        String userAnswer = answerField.getText();

        if (userAnswer.equals(correctAnswer)) {
            JOptionPane.showMessageDialog(this, "Correct");
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect. The correct answer is: " + correctAnswer);
=======
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Update the view with the contents of a new step sent by the tutor
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
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        }
    }

    /**
<<<<<<< HEAD
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
=======
     * Defines each view classes' standard method for updating in the Practice View
     */
    @Override
    protected void updatePracticeView() {
        // If check and hint buttons are disabled, reset listenerers and apply those
        // used by this view
        if (!checkHintEnabled) {
            resetButtonListeners(); // Clear any listeners applied from other views
        }

        Step step = model.currentTask().getCurrentStep().getStep();

        if (step.getSubType() == StepSubType.ROTATE_BITS) {
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
                checkButton.setEnabled(false);
                hintButton.setEnabled(false);
            } else {
                problem.setText(problemData);
                this.problemString = problemData;
                this.numRotations = currentStep.getAmount();
                checkButton.setEnabled(true);
                hintButton.setEnabled(true);
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

    @Override
    public void setCurrentTask(PendingTask task) {
        this.model.addCurrentTask(task);
        updateView();
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }
}
