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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

import edu.regis.shatu.model.ShaZeroStep;
import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.RotateStep;
import edu.regis.shatu.view.act.HintAction;
import edu.regis.shatu.view.act.NewExampleAction;
import edu.regis.shatu.view.act.StepCompletionAction;

/**
 * ShaZero class represents the GUI view for performing the SHA Σ₀ function,
 * involving rotation and right shift operations.
 * It extends GPanel and implements ActionListener and KeyListener interfaces.
 * <p>
 * The class provides a user interface for performing rotations and right shifts
 * on binary numbers and checking the results.
 * Inline comments have been added for better understanding of the code.
 *
 * @author rickb
 */
public class ShaZeroView extends UserRequestView implements KeyListener {
    /**
     * View for the Sigma0 tutoring session
     */
    private TutoringSessionView view;
    /**
     * Label describing the A operand for the function
     */
    private JLabel operandALabel;
    /**
     * The name of the current Tutoring Session View, i.e: Sigma0
     */
    private JLabel viewNameLabel;
    /**
     * Label describing the size of the problem to select
     */
    private JLabel problemSizeLabel;
    /**
     * Label describing instructions to the user
     */
    private JLabel instructionLabel;
    /**
     * The actual value of operand A
     */
    private String operandAValue;
    /**
     * Deprecated
     */
    private JTextField answerField;

    private boolean checkHintEnabled = false;
    /**
     * The area in which the Sigma0 function is described
     */
    private JTextArea descTextArea;
    /**
     * The panel where the description lies in
     */
    private GPanel descriptionPanel;
    /**
     * Radio Button selectors which the user uses to select the size of the problem
     */
    private JRadioButton fourRadioButton, eightRadioButton, sixteenRadioButton, thirtytwoRadioButton;
    /**
     * The button group in which the radio buttons are grouped in
     */
    private ButtonGroup problemSizeGroup;
    /**
     * The panel in which the radio buttons lie
     */
    private JPanel radioButtonPanel;
    /**
     * The actual size of the problem that the user selects
     */
    private int problemSize;
    /**
     * The panel in which the question lies in
     */
    private GPanel questionPanel;
    /**
     * The label that describes to the user where they can enter their answer
     */
    private JLabel answerLabel;
    /**
     * The text area where the user can enter their answer
     */
    private JTextArea responseTextArea;
    /**
     * The scrollable pane in which the response text area lies in
     */
    private JScrollPane responsePane;
    /**
     * The panel that contains the question and answer components
     */
    private GPanel qrPanel;

    /**
     * Initialize this view including creating and laying out its child components.
     */
    public ShaZeroView() {
        initializeComponents();
        initializeLayout();
    }

    /**
     * Sends a request to the server for a new example problem
     *
     * @return The new example problem
     */
    @Override
    public NewExampleRequest newRequest() {
        NewExampleRequest ex = new NewExampleRequest();

        ex.setExampleType(ProblemType.SHA_ZERO);

        answerField = new JTextField(10);
        answerField.addKeyListener(this);

        return ex;
    }

    /**
     * Handles Step completion of the current Tutoring Step
     *
     * @return The new current step
     */
    @Override
    public StepCompletion stepCompletion() {
        Step currentStep = model.currentTask().currentStep().getStep();

        ShaZeroStep example = gson.fromJson(currentStep.getData(), ShaZeroStep.class);

        if(example == null) {
            JOptionPane.showMessageDialog(this, "Please press New Example to generate a question");
            return null;

        }

        String userResponse = responseTextArea.getText().replaceAll("\\s", "");

        example.setResult(userResponse);

        StepCompletion step = new StepCompletion(currentStep, gson.toJson(example));

        step.setStep(currentStep);
        
        // Add exampleInputLabel centered


      //  addc(exampleInputLabel, 0, 0, 2, 1, 0.0, 0.0,
       //         GridBagConstraints.CENTER, GridBagConstraints.NONE,
      //          5, 5, 5, 5);
        // Add answerField to the layout, centered
       // addc(answerField, 0, 1, 1, 1, 1.0, 0.0,
       //         GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
       //         5, 5, 5, 5);
       
       return step;

    }
    /**
     * Performs rotation (ROR or ROL) on the given input string for the
     * specified number of positions.
     *
     * @param input     The input string to rotate.
     * @param positions The number of positions for the rotation.
     * @return The rotated string.
     */
    /*
     * protected String rotateString(String input, int positions,
     * RotateStep.Direction direction) {
     * if (input == null || input.isEmpty()) {
     * return input;
     * 
     * 
     * ShaZeroStep example = gson.fromJson(currentStep.getData(),
     * ShaZeroStep.class);
     * 
     * if(example == null {
     * JOptionPane.showMessageDialog(this,
     * "Please press New Example to generate a question");
     * return null;
     * 
     * }
     * 
     * String userResponse = responseTextArea.getText().replaceAll("\\s", "");
     * 
     * example.setResult(userResponse);
     * 
     * StepCompletion step = new StepCompletion(currentStep, gson.toJson(example));
     * 
     * step.setStep(currentStep);
     * 
     * return step;
     * }
     */



    /**
     * Handles events that the user preforms on the UI
     *
     * @param event the event to be processed
     */
  //  @Override
  //  public void actionPerformed(ActionEvent event) {
       // if (event.getSource() == checkButton) {
          //  onCheckButton();
       // } else if (event.getSource() == hintButton) {
       //     onNextHint();
       // } else if (event.getSource() == nextButton) {
       //     checkHintEnabled = true;
       //     onNextQuestion();
       // }
   // }


    /**
     * Handles key type events
     *
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Handles key pressed events
     *
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && answerField.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Please provide an answer");
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {

            //checkButton.doClick();

        }
    }

    /**
     * Handles key released events
     *
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {
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

            // verifyAnswer();
        }
    }

    /**
     * Sets the main View title and description of the function
     */
    private void setupDescription()   {

        viewNameLabel = new JLabel("The 𝛴₀ Function");
        viewNameLabel.setFont(new Font("", Font.BOLD, 20));

        descTextArea = new JTextArea();
        descTextArea.setEditable(false);
        descTextArea.setLineWrap(true);
        descTextArea.setWrapStyleWord(true);
        descTextArea.setOpaque(false);
        descTextArea.append("""
                The 𝛴₀ function takes a single 32-bit word operand, A, and outputs a single 32-bit word.""");
    }

    /**
     * Creates the description panel
     */
    private void setUpDescriptionPanel() {
        descriptionPanel = new GPanel();

        descriptionPanel.addc(viewNameLabel, 0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                5, 5, 5, 5);

        descriptionPanel.addc(descTextArea, 0, 1, 3, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                5, 5, 5, 5);

        descriptionPanel.addc(questionPanel, 0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE,
                5, 5, 5, 5);
    }

    /**
     * Sets up the radio buttons and action listener
     */
    private void setUpRadioButtons() {
        fourRadioButton = new JRadioButton("4 bits");
        eightRadioButton = new JRadioButton("8 bits");
        sixteenRadioButton = new JRadioButton("16 bits");
        thirtytwoRadioButton = new JRadioButton("32 bits");

        ActionListener selection = e -> {
            JRadioButton source = (JRadioButton) e.getSource();
            updateProblemSize(source);
        };

        fourRadioButton.addActionListener(selection);
        eightRadioButton.addActionListener(selection);
        sixteenRadioButton.addActionListener(selection);
        thirtytwoRadioButton.addActionListener(selection);

        problemSizeGroup = new ButtonGroup();
        problemSizeGroup.add(fourRadioButton);
        problemSizeGroup.add(eightRadioButton);
        problemSizeGroup.add(sixteenRadioButton);
        problemSizeGroup.add(thirtytwoRadioButton);

        fourRadioButton.setSelected(true); // Set default radio button to true


        radioButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        radioButtonPanel.add(fourRadioButton);
        radioButtonPanel.add(eightRadioButton);
        radioButtonPanel.add(sixteenRadioButton);
        radioButtonPanel.add(thirtytwoRadioButton);
    }

    /**
     * Initializes the question components and adds them to the question panel.
     */
    private void setUpQuestionArea() {
        problemSize = 4;
        operandAValue = "";

        operandALabel = new JLabel("Operand A: " + operandAValue);

        problemSizeLabel = new JLabel("Select Problem Size:");
        instructionLabel = new JLabel("Solve the Σ₀ function using operand A given below:");

        questionPanel = new GPanel();

        questionPanel.addc(problemSizeLabel, 0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                5, 5, 5, 5);
        questionPanel.addc(radioButtonPanel, 0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
        questionPanel.addc(instructionLabel, 0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                5, 5, 5, 5);
        questionPanel.addc(operandALabel, 0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
    }

    /**
     * Initializes the response area
     */
    private void setUpResponseArea() {
        answerLabel = new JLabel("Enter your answer below:");
        responseTextArea = new JTextArea(3, 20);
        responseTextArea.setLineWrap(true);
        responseTextArea.setWrapStyleWord(true);

        responsePane = new JScrollPane(responseTextArea);
        responsePane.setPreferredSize(new Dimension(800, 200));
    }

    /**
     * Creates a GPanel containing the response JScrollPanes and the button
     * panel.
     */
    private void setUpQRPanel() {
        qrPanel = new GPanel();

        qrPanel.addc(responsePane, 0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                5, 5, 5, 5);
    }

    /**
     * Create the child GUI components appearing in this frame.
     */
    private void initializeComponents() {

        setupDescription();
        setUpRadioButtons();
        setUpQuestionArea();
        setUpResponseArea();
        setUpButtons();
        setUpDescriptionPanel();
        setUpQRPanel();
    }

    /**
     * Sets up the Check, New Example, and Hint buttons and their action
     * listeners
     */
    private void setUpButtons() {

/*
        checkButton = new JButton(StepCompletionAction.instance());
        checkButton.addActionListener(this);

        hintButton = new JButton(HintAction.instance());
        hintButton.addActionListener(this);

        nextButton = new JButton(NewExampleAction.instance());
        nextButton.addActionListener(this);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(checkButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(hintButton);
        */

    }

    /**
     * Layout the child components in this view.
     */
    private void initializeLayout() {

        addc(descriptionPanel, 0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                5, 5, 5, 5);

        addc(answerLabel, 0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                5, 5, 5, 5);
        addc(qrPanel, 0, 2, 3, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                5, 5, 5, 5);
    }


    @Override
    /**
     * Updates the description, question, and hints from the model
     */
    protected void updateView() {
        view = SplashFrame.instance().getTutoringSessionView(); // Accessing view to use universal buttons

        
        // If check and hint buttons are disabled, reset listenerers and apply those used by this view
        if(!checkHintEnabled) {
            view.resetButtonListeners(); // Clear any listeners applied from other views          

        }

        if (model != null) {

            Step step = model.currentTask().getCurrentStep().getStep();

            if (step != null) {
                ShaZeroStep example = gson.fromJson(step.getData(), ShaZeroStep.class);

                if (example != null) {
                    operandAValue = example.getOperandA();
                    operandALabel.setText("Operand A: " + operandAValue);
                }

            }

        }
    }

    /**
     * Updates the size of the problem to display.
     *
     * @param source The radio button that triggered the even.
     */
    private void updateProblemSize(JRadioButton source) {
        if (source == fourRadioButton) {
            problemSize = 4;
        } else if (source == eightRadioButton) {
            problemSize = 8;
        } else if (source == sixteenRadioButton) {
            problemSize = 16;
        } else if (source == thirtytwoRadioButton) {
            problemSize = 32;
        }
    }
}
