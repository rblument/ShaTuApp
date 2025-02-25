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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.regis.shatu.model.BitShiftStep;
import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.view.act.HintAction;
import edu.regis.shatu.view.act.NewExampleAction;
import edu.regis.shatu.view.act.StepCompletionAction;

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
    private TutoringSessionView view;
    private String operand;
    private int shiftLength;
    private final boolean shiftRight = true;
    private int bitLength;
    
    private JTextArea descTextArea, feedbackTextArea, responseTextArea;
    private JScrollPane responsePane;
    private GPanel questionPanel, descriptionPanel, feedbackPanel, qrPanel;
    private JPanel buttonPanel, radioButtonPanel;
    private JButton checkButton, nextButton, hintButton;
    private boolean checkHintEnabled = false;
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
        } else if (event.getSource() == nextButton) {
            checkHintEnabled = true;
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
    }

    /**
     * Updates the view when data changes
     */
    @Override
    protected void updateView() {
        view = SplashFrame.instance().getTutoringSessionView(); // Accessing view to use universal buttons
        hintButton = view.getHintButton();
        checkButton = view.getCheckButton();
        nextButton = view.getNewExampleButton();
        
        responseTextArea.setText("");
        feedbackTextArea.setText("");
        
        // If check and hint buttons are disabled, reset listenerers and apply those used by this view
        if(!checkHintEnabled) {
            view.resetButtonListeners(); // Clear any listeners applied from other views          
            hintButton.addActionListener(this);           
            checkButton.addActionListener(this);            
            nextButton.addActionListener(this);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Step step = model.currentTask().getCurrentStep().getStep();
        System.out.println("Current Step: " + step.getSubType());
        System.out.println(" Data: " + step.getData());

        // If we got here by the user selecting this step/view, the current
        // task could be anything. 
        if (step.getSubType() == StepSubType.SHIFT_BITS) {
            BitShiftStep example = gson.fromJson(step.getData(), BitShiftStep.class);
        
            System.out.println("Shift right update display called");
            operand = example.getOperand();
            if (operand == null || operand.isEmpty()) {
                operand = "Please click New Example";
                checkButton.setEnabled(false);
                responseTextArea.setEnabled(false);
                hintButton.setEnabled(false);
            } else {
                checkButton.setEnabled(true);
                responseTextArea.setEnabled(true);
                hintButton.setEnabled(true);
            }
            shiftLength = example.getShiftLength();
            operandLabel.setText(operand);
            instructionLabel.setText("Logical right shift the input given below by "
              + shiftLength + " bits:");
        }
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
        ex.setExampleType(ProblemType.SHIFT_BITS);
        
        BitShiftStep newStep = new BitShiftStep();
        
        newStep.setBitLength(bitLength);
        newStep.setShiftRight(shiftRight);
        
        ex.setData(gson.toJson(newStep));
        
        return ex;
    }

    @Override
    public StepCompletion stepCompletion() {
        Step currentStep = model.currentTask().currentStep().getStep();

        BitShiftStep example = gson.fromJson(currentStep.getData(), BitShiftStep.class);

        String userResponse = responseTextArea.getText().replaceAll("\\s", "");

        example.setResult(userResponse);

        StepCompletion step = new StepCompletion(currentStep, gson.toJson(example));
        
        step.setStep(currentStep);

        return step;
    }
}
