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
package edu.regis.shatu.view;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.BitOpStep;
import edu.regis.shatu.model.aol.ExampleType;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.view.act.NewExampleAction;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * ExclusiveOrView class represents a GUI view for performing Exclusive OR (XOR)
 * on two given binary numbers. Users can input their answers in a JTextField
 * and check correctness. Provides functionality for hints and moving to the
 * next question.
 *
 * @author rickb
 */
public class ExclusiveOrView extends UserRequestView implements ActionListener, KeyListener {   
    private String stringX, stringY;
    private int problemSize; 
    private JTextArea descTextArea, feedbackTextArea, responseTextArea;
    private JScrollPane feedbackPane, responsePane;
    private GPanel questionPanel, descriptionPanel, qrPanel;
    private JPanel buttonPanel, radioButtonPanel;
    private JButton checkButton, nextButton, hintButton, newExampleButton;
    private ButtonGroup problemSizeGroup;
    private JRadioButton fourRadioButton, eightRadioButton, sixteenRadioButton, 
                         thirtytwoRadioButton;
    private JLabel viewNameLabel, stringXLabel, stringYLabel, answerLabel, 
                   problemSizeLabel, instructionLabel;

    
    private static final Random random = new Random();
    
    /**
     * Initialize this view including creating and laying out its child
     * components.
     */
    public ExclusiveOrView() {
        initializeComponents();
        initializeLayout();
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
        viewNameLabel = new JLabel("The Exclusive OR");
        viewNameLabel.setFont(new Font("", Font.BOLD, 20));
        
        descTextArea = new JTextArea();
        descTextArea.setEditable(false);
        descTextArea.setLineWrap(true);
        descTextArea.setWrapStyleWord(true);
        descTextArea.setOpaque(false);
        descTextArea.append("The exclusive OR compares two n-length binary strings. When comparing the strings, if both bits are the same, the output is 0, else its 1."); 
        
        descTextArea.setPreferredSize(new Dimension(800, 50));
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

        descriptionPanel.addc(questionPanel, 0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
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
            generateNewQuestion();
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
    private void updateProblemSize(JRadioButton source){
        if (source == fourRadioButton) {
            problemSize = 4;
        } else if (source == eightRadioButton) {
            problemSize = 8;
        } else if (source == sixteenRadioButton) {
            problemSize = 16;
        } else if (source == thirtytwoRadioButton){
            problemSize = 32;
        }
    }
    
    /**
     * Initializes the question components and adds them to the question panel. 
     */
    private void setUpQuestionArea() {
        problemSize = 4;
        stringX = generateInputString();
        stringY = generateInputString();
        
        stringXLabel = new JLabel("x: " + stringX);
        stringYLabel = new JLabel("y: " + stringY);
        
        problemSizeLabel = new JLabel("Select Problem Size:");
        instructionLabel = new JLabel("Perform an XOR using the two "
                + "inputs given below:");
        
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
        questionPanel.addc(stringXLabel, 0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
        
        questionPanel.addc(stringYLabel, 0, 4, 1, 1, 0.0, 0.0,
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
     * Initialized the feedback area
     */
    private void setUpFeedbackArea() {
        feedbackTextArea = new JTextArea(3, 20);
        feedbackTextArea.setEditable(false);
        feedbackTextArea.setLineWrap(true);
        feedbackTextArea.setWrapStyleWord(true);
        feedbackTextArea.setBackground(null);
        
        feedbackPane = new JScrollPane(feedbackTextArea);
        feedbackPane.setPreferredSize(new Dimension(800, 200));
    }
    
     /**
     * Sets up the Check, Next, and Hint buttons and their action listeners
     */
    private void setUpButtons() {
        checkButton = new JButton("Check");
        checkButton.addActionListener(this);
        
        hintButton = new JButton("Hint");
        hintButton.addActionListener(this);
        
        newExampleButton = new JButton(NewExampleAction.instance());
        newExampleButton.addActionListener(this);
        
        nextButton = new JButton("Next");
        nextButton.addActionListener(this);
        nextButton.setEnabled(false);
        
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(checkButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(hintButton);
        buttonPanel.add(newExampleButton);
    }
    
     /**
     * Creates a GPanel containing the response and feedback JScrollPanes and 
     * the button panel. 
     */
    private void setUpQRPanel(){
        qrPanel = new GPanel();
        
        qrPanel.addc(responsePane, 0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                5, 5, 5, 5);
        
        qrPanel.addc(feedbackPane, 0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                5, 5, 5, 5);

        qrPanel.addc(buttonPanel, 0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
    }
    
    /**
     * Generates an n-bit binary string (length 4, 8, 16, or 32) to be used as an input into the 
     * Ch function. Every four bits are separated by a space to improve readability.
     * 
     * @return A string to be used as an input into the function.
     */
    private String generateInputString() {
        String inputString;
        String tempString;
        StringBuilder inputStringBuilder = new StringBuilder();
        int num;

        switch (problemSize) {
            case 4:
                num = random.nextInt();
                tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                inputStringBuilder.append(tempString);
                break;
            case 8:
                num = random.nextInt();
                tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                inputStringBuilder.append(tempString);
                
                inputStringBuilder.append(" ");
                num = random.nextInt();
                tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                inputStringBuilder.append(tempString);
                break;
            case 16:
                num = random.nextInt();
                tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                inputStringBuilder.append(tempString);
                
                for (int i = 0; i < 3; i++) {
                    inputStringBuilder.append(" ");
                    num = random.nextInt();
                    tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                    inputStringBuilder.append(tempString);
                }   break;
            case 32:
                num = random.nextInt();
                tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                inputStringBuilder.append(tempString);
                
                for (int i = 0; i < 7; i++) {
                    inputStringBuilder.append(" ");
                    num = random.nextInt();
                    tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                    inputStringBuilder.append(tempString);
                }   break;
            default:
                break;
        }
        
        inputString = inputStringBuilder.toString();
        
        return inputString;
    }
    
     /**
     * Formats the result output by the choice function based on the size of the 
     * problem.
     * @param answer the output of the choice function
     * 
     * @return the binary string representation of the answer
     */
    private String formatResult(long answer) {
        String finalResult = "";
        
        switch (problemSize) {
            case 4: 
                finalResult = String.format("%4s", Long.toBinaryString(answer)).replace(' ', '0');
                break;
            case 8:
                finalResult = String.format("%8s", Long.toBinaryString(answer)).replace(' ', '0');
                break;
            case 16:
                finalResult = String.format("%16s", Long.toBinaryString(answer)).replace(' ', '0');
                break;
            case 32:
                finalResult = String.format("%32s", Long.toBinaryString(answer)).replace(' ', '0');   
                break;
            default:
                break;
        }
        return finalResult;
    }
    
    /**
     * Generates and displays three new input strings.
     */
    private void generateNewQuestion() { 
        responseTextArea.setText("");
        feedbackTextArea.setText("");
        
        stringX = generateInputString();
        stringY = generateInputString();
        
        stringXLabel.setText("x: " + stringX);
        stringYLabel.setText("y: " + stringY);
    }

    /**
     * Performs the XOR operation on two binary strings.
     * 
     * The method compares
     * corresponding bits of the two binary strings and produces a new string
     * where a bit is set to '1' if the corresponding bits in the input strings
     * are different and '0' otherwise.
     * 
     * @param binary1 The first binary string.
     * @param binary2 The second binary string.
     * @return The result of XOR operation as a binary string.
     */
    private String performXOR(String x, String y) {
        String tempX = x.replaceAll("\\s", "");
        String tempY = y.replaceAll("\\s", "");

        long intX = Long.parseLong(tempX, 2);
        long intY = Long.parseLong(tempY, 2);

        long result = intX ^ intY;

        // Convert the result back to binary string
        String binaryResult = formatResult(result);

        return binaryResult;
    }

    /**
     * Pads the binary string with leading zeroes to make it of the specified
     * length.
     *
     * @param binary The binary string to pad.
     * @param length The desired length.
     * @return The padded binary string.
     */
    private static String padWithZeroes(String binary, int length) {
        StringBuilder paddedBinary = new StringBuilder(binary);
        while (paddedBinary.length() < length) {
            paddedBinary.insert(0, '0');
        }
        return paddedBinary.toString();
    }

    /**
     * Handles the actionPerformed event for buttons in the view.
     *
     * @param event The ActionEvent that occurred.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == checkButton) {
            onCheckButton();
        } else if (event.getSource() == hintButton) {
            onNextHint();
        } else if (event.getSource() == nextButton) {
            onNextQuestion();
        }
    }

    /**
     * Handles the keyTyped event for the view.
     *
     * @param e The KeyEvent that occurred.
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used in this context
    }

    /**
     * Handles the keyPressed event for the view.
     *
     * @param e The KeyEvent that occurred.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && responseTextArea.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Please provide an answer");
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            verifyAnswer();
        }
    }

    /**
     * Handles the keyReleased event for the view.
     *
     * @param e The KeyEvent that occurred.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        // Not used in this context
    }

    /**
     * Verifies the user's answer against the correct answer.
     */
    private void verifyAnswer() {
        String correctAnswer = performXOR(stringX, stringY);
        String userResponse = responseTextArea.getText();
        
        userResponse = userResponse.replaceAll("\\s", "");
        
        if (correctAnswer.equals(userResponse)) {
            feedbackTextArea.setText("Correct!");
            nextButton.setEnabled(true);
            checkButton.setEnabled(false);
        } else {
            feedbackTextArea.setText("Incorrect! Please check your entry and "
                    + "try again or use the hint feature for help. Correct answer: " + correctAnswer);
        }
    }

    /**
     * Handles the action for the Next Question button.
     */
    private void onNextQuestion() {
        responseTextArea.setText("");
        feedbackTextArea.setText("");
        
        generateNewQuestion();
        
        nextButton.setEnabled(false);
        checkButton.setEnabled(true);
    }

    /**
     * Handles the action for the Hint button.
     */
    private void onNextHint() {
        feedbackTextArea.setText("Hint: Check the truth table above for the "
                + "appropriate values."); //Add different hint
    }

    /**
     * Handles the action for the Check button.
     */
    private void onCheckButton() {
        if (responseTextArea.getText().equals("")) {
            feedbackTextArea.setText("Please provide an answer");
        } else {
            verifyAnswer();
        }
    }
/**
 * XOR Example Request Added
 * @return 
 */
    @Override
    public NewExampleRequest newRequest() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        NewExampleRequest ex = new NewExampleRequest();
        
        //Set example type to the problem associated with the current view
        
        ex.setExampleType(ExampleType.XOR_BITS);
        
        BitOpStep newStep = new BitOpStep();
        
        ex.setData(gson.toJson(newStep));
        
        responseTextArea.setText("");
        feedbackTextArea.setText("");
        
        generateNewQuestion();
        
        nextButton.setEnabled(false);
        checkButton.setEnabled(true);
        
        return ex;
    }

    @Override
    public StepCompletion stepCompletion() {
        Step currentStep = model.currentTask().currentStep();

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
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Step step = model.currentTask().getCurrentStep();

        BitOpStep example = gson.fromJson(step.getData(), BitOpStep.class);
        
        stringX = example.getExample().getOperand1();
        stringY = example.getExample().getOperand2();

        stringXLabel = new JLabel("x: " + stringX);
        stringYLabel = new JLabel("y: " + stringY);

    }
}
