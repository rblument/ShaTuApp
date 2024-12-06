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
import edu.regis.shatu.model.BitShiftStep;
import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.ExampleType;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.view.act.HintAction;
import edu.regis.shatu.view.act.NewExampleAction;
import edu.regis.shatu.view.act.StepCompletionAction;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * ShiftRightView class represents the GUI view for performing the right shift operation on a binary number.
 * It extends GPanel and implements ActionListener and KeyListener interfaces.
 * <p>
 * The class provides a user interface for shifting a binary number to the right by a specified number of places
 * and checking the result. Inline comments have been added for better understanding of the code.
 *
 * @author rickb, Chandon Hamel
 */
public class ShiftRightView extends UserRequestView implements ActionListener, KeyListener {
    
    private String operand;
    private int shiftLength;
    private final boolean shiftRight = true;
    private int bitLength;
    
    private JTextArea descTextArea, feedbackTextArea, responseTextArea;
    private JScrollPane responsePane;
    private GPanel questionPanel, descriptionPanel, feedbackPanel, qrPanel;
    private JPanel buttonPanel, radioButtonPanel;
    private JButton checkButton, newExampleButton, hintButton;
    private ButtonGroup problemSizeGroup;
    private JRadioButton fourRadioButton, eightRadioButton, sixteenRadioButton,
            thirtytwoRadioButton;
    private JLabel viewNameLabel, operandLabel, answerLabel,
            problemSizeLabel, instructionLabel;

    /**
     * Initialize this view including creating and laying out its child components.
     */
    public ShiftRightView() {
        initializeComponents();
        initializeLayout();
    }

    /**
     * Handles the actionPerformed event for buttons in the view.
     *
     * @param event The ActionEvent that occurred.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == hintButton) {
            onNextHint();
        } else if (event.getSource() == checkButton) {
            onCheckButton();
        }
    }
    
    /**
     * Handles the action for the Hint button.
     */
    private void onNextHint() {
        feedbackTextArea.setText(String.format(
            "Hint: Put %d 0s on the left and remove %d bits from the right", 
            shiftLength, shiftLength));
    }
    
    /**
     * Handles the action for the Check button.
     */
    private void onCheckButton() {
        if (responseTextArea.getText().equals("")) {
            feedbackTextArea.setText("Please provide an answer");
        }
    }

    /**
     * Create the child GUI components appearing in this frame.
     */
    private void initializeComponents() {
        setUpDescription();
        setUpRadioButtons();
        setUpQuestionArea();
        setUpResponseArea();
        setUpFeedbackArea();
        setUpButtons();
        setUpDescriptionPanel();
        setUpQRPanel();
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
    
    /**
     * Sets up the description area
     */
    private void setUpDescription() {
        viewNameLabel = new JLabel("Right Shift");
        viewNameLabel.setFont(new Font("", Font.BOLD, 20));

        descTextArea = new JTextArea();
        descTextArea.setEditable(false);
        descTextArea.setLineWrap(true);
        descTextArea.setWrapStyleWord(true);
        descTextArea.setOpaque(false);
        descTextArea.append("""
                            The Right Shift operation moves bits to the right 
                            by a specified number of positions. Zeros fill the 
                            empty left positions. This operation effectively 
                            divides the number by powers of two.""");
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
            //generateNewQuestion();
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

        fourRadioButton.setSelected(true); //Set default radio button to true

        radioButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        radioButtonPanel.add(fourRadioButton);
        radioButtonPanel.add(eightRadioButton);
        radioButtonPanel.add(sixteenRadioButton);
        radioButtonPanel.add(thirtytwoRadioButton);
    }
    
    /**
     * Updates the size of the problem to display.
     *
     * @param source The radio button that triggered the even.
     */
    private void updateProblemSize(JRadioButton source) {
        if (source == fourRadioButton) {
            bitLength = 4;
        } else if (source == eightRadioButton) {
            bitLength = 8;
        } else if (source == sixteenRadioButton) {
            bitLength = 16;
        } else if (source == thirtytwoRadioButton) {
            bitLength = 32;
        }
    }
    
    /**
     * Initializes the question components and adds them to the question panel.
     */
    private void setUpQuestionArea() {

        bitLength = 4;
        operand = "foo"; // generateInputString();

        operandLabel = new JLabel(operand);

        problemSizeLabel = new JLabel("Select Problem Size:");
        instructionLabel = new JLabel("Logical right shift the input given below by "
              + shiftLength + " bits:");

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
        questionPanel.addc(operandLabel, 0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
    }
    
    /**
     * Initializes the response area
     */
    private void setUpResponseArea() {
        answerLabel = new JLabel("Enter your answer below:");
        responseTextArea = new JTextArea();
        responseTextArea.setLineWrap(true);
        responseTextArea.setWrapStyleWord(true);

        responsePane = new JScrollPane(responseTextArea);
        responsePane.setPreferredSize(new Dimension(250, 50));
    }
    
    /**
     * Initialized the feedback area
     */
    private void setUpFeedbackArea() {
        feedbackTextArea = new JTextArea(3, 20);
        feedbackTextArea.setEditable(false);
        feedbackTextArea.setLineWrap(true);
        feedbackTextArea.setWrapStyleWord(true);
        feedbackTextArea.setBackground(null);
        feedbackTextArea.setText("Feedback");

        feedbackPanel = new GPanel();
        feedbackPanel.addc(feedbackTextArea, 0, 1, 3, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                5, 5, 5, 5);
    }
    
    /**
     * Sets up the Check, Next, and Hint buttons and their action listeners
     */
    private void setUpButtons() {
        checkButton = new JButton(StepCompletionAction.instance());
        checkButton.addActionListener(this);

        hintButton = new JButton(HintAction.instance());
        hintButton.addActionListener(this);

        newExampleButton = new JButton(NewExampleAction.instance());
        newExampleButton.addActionListener(this);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(checkButton);
        buttonPanel.add(newExampleButton);
        buttonPanel.add(hintButton);
    }
    
    /**
     * Creates a GPanel containing the response and feedback JScrollPanes and
     * the button panel.
     */
    private void setUpQRPanel() {
        qrPanel = new GPanel();

        qrPanel.addc(responsePane, 0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                5, 5, 5, 5);

        qrPanel.addc(feedbackPanel, 0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                5, 5, 5, 5);

        qrPanel.addc(buttonPanel, 0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
    }

    /**
     * Updates the view when data changes
     */
    @Override
    protected void updateView() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Step step = model.currentTask().getCurrentStep();

        BitShiftStep example = gson.fromJson(step.getData(), BitShiftStep.class);
        
        System.out.println("Shift right update display called");
        operand = example.getOperand();
        if (operand == null || operand.isEmpty()) {
            operand = "Please click New Example";
            checkButton.setEnabled(false);
            hintButton.setEnabled(false);
        }
        else {
            checkButton.setEnabled(true);
            hintButton.setEnabled(true);
        }
        shiftLength = example.getShiftLength();
        operandLabel.setText(operand);
        instructionLabel.setText("Logical right shift the input given below by "
              + shiftLength + " bits:");
    }


    /**
     * Performs the right shift operation on the given input binary number for the specified number of places.
     *
     * @param x      The input binary number.
     * @param places The number of places for the right shift.
     * @return The result after performing the right shift operation.
     */
    public String shiftRightString(int x, int places) {
        // Perform the right shift operation
        int result = x >>> places;
        // Print the original and shifted binary numbers
        System.out.println("Original Binary: " + Integer.toBinaryString(x));
        System.out.println("Shifted Binary:  " + Integer.toBinaryString(result));

        return Integer.toBinaryString(result);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && responseTextArea.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Please provide an answer");
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            checkButton.doClick();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Create and return the server request this view makes when a user selects
     * that they want to practice a new bit shift example.
     *
     * @return
     */
    @Override
    public NewExampleRequest newRequest() {
        NewExampleRequest ex = new NewExampleRequest();
        //Set example type to the problem associated with the current view
        ex.setExampleType(ExampleType.SHIFT_BITS);
        
        BitShiftStep newStep = new BitShiftStep();
        
        newStep.setBitLength(bitLength);
        newStep.setShiftRight(shiftRight);
        
        ex.setData(gson.toJson(newStep));
        
        return ex;
    }

    @Override
    public StepCompletion stepCompletion() {
        Step currentStep = model.currentTask().currentStep();

        BitShiftStep example = gson.fromJson(currentStep.getData(), BitShiftStep.class);

        String userResponse = responseTextArea.getText().replaceAll("\\s", "");

        example.setResult(userResponse);

        StepCompletion step = new StepCompletion(currentStep, gson.toJson(example));
        
        step.setStep(currentStep);

        return step;
    }
}
