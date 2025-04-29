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
import edu.regis.shatu.model.ShaOneStep;
import edu.regis.shatu.model.MajorityStep;
import edu.regis.shatu.model.ShaZeroStep;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import edu.regis.shatu.model.StepCompletion;
import javax.swing.JRadioButton;
import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * ShaOne class represents the GUI view for performing the SHA Σ₁ function,
 * involving a right shift operation.
 * It extends GPanel and implements ActionListener and KeyListener interfaces.
 * <p>
 * The class provides a user interface for performing right shifts on binary
 * numbers and checking the results.
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
    public ShaOneView() {
        initializeComponents();
        initializeLayout();
    }

        /**
     * Create and return the server request this view makes when a user selects
     * that they want to practice a new Sha One View example.
     *
     * @return
     */
    @Override
    public NewExampleRequest newRequest() {
        NewExampleRequest ex = new NewExampleRequest();
        
        ex.setExampleType(ProblemType.SHA_ONE);
        
        ShaOneStep step = new ShaOneStep();
        
        step.setBitLength(problemSize);
      
        String shaStepJson = gson.toJson(step);
        ex.setData(shaStepJson);

        return ex;
    }
    
    @Override
    public StepCompletion stepCompletion() {
        Step currentStep = model.currentTask().currentStep().getStep();

        ShaOneStep example = gson.fromJson(currentStep.getData(), ShaOneStep.class);

        String userResponse = answerField.getText().replaceAll("\\s", "");

        example.setResult(userResponse);

        StepCompletion step = new StepCompletion(currentStep, gson.toJson(example));
        
        step.setStep(currentStep);
        
        return step;
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
    /**
     * Sets the main View title and description of the function
     */
    private void setupDescription()
    {
        viewNameLabel = new JLabel("The Σ₁ Function");
        viewNameLabel.setFont(new Font("", Font.BOLD, 20));

        descTextArea = new JTextArea();
        descTextArea.setEditable(false);
        descTextArea.setLineWrap(true);
        descTextArea.setWrapStyleWord(true);
        descTextArea.setOpaque(false);
        descTextArea.append("""
                            The Σ₁ function takes a single 32-bit word operand then:
                            
                            1) Shift  input value right with n=6
                            2) Shift  input value right with n=11
                            3) Shift  input value right with n=25
                            4) Modulo addition using 3 shift right values.
                            
                            Outputs a single 32-bit word.""");
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

        fourRadioButton.setSelected(true); //Set default radio button to true

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
        addc(buttonPanel, 0, 4, 1, 1, 0.0, 0.0,
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
        responseTextArea.setEnabled(false); 

        responsePane = new JScrollPane(responseTextArea);
        responsePane.setPreferredSize(new Dimension(800, 200));
        
    }
     /**
     * Creates a GPanel containing the response JScrollPanes and the button
     * panel.
     */
        private void setUpQRPanel() {
        qrPanel = new GPanel();
        responsePane.setPreferredSize(new Dimension(300, 20));
        qrPanel.addc(responsePane, 0, 4, 4, 4, 1.0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
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

    @Override
    /**
     * Updates the description, question, and hints from the model
     * 
     * TODO: THIS IS A PLACEHOLDER UNTIl WE HAVE HAVE THE MODEL CODE COMPLETED
     */
    protected void updateView() {
        view = SplashFrame.instance().getTutoringSessionView(); // Accessing view to use universal buttons

        switch(view.getCurrentViewType())
        {
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
        if (model != null) {

            Step step = model.currentTask().getCurrentStep().getStep();

            if (step != null) {
                ShaOneStep example = gson.fromJson(step.getData(), ShaOneStep.class);

                if (example != null) {
                    operandAValue = example.getOperandA();
                    operandALabel.setText("Operand A: " + operandAValue);
                }

            }
        }
    }

    /**
     * Defines each view classes' standard method for updating in the Practice View
     */
    @Override
    protected void updatePracticeView() {

        // If check and hint buttons are disabled, reset listenerers and apply those used by this view
        if(!checkHintEnabled) {
            resetButtonListeners(); // Clear any listeners applied from other views
        }
        
                Step step = model.currentTask().getCurrentStep().getStep();

        if (step.getSubType() == StepSubType.SHA_ZERO) {
            //Get the data from the model as a RotateStep object
            MajorityStep example = gson.fromJson(step.getData(), MajorityStep.class);

            if (example.getOperandA() == null || example.getOperandA().isEmpty()) {
                operandALabel.setText("x: Please");
                hintButton.setEnabled(false);
                checkButton.setEnabled(false);
                responseTextArea.setEnabled(false);
            } else {
                operandALabel.setText("x: " + example.getOperandA());
                hintButton.setEnabled(true);
                checkButton.setEnabled(true);
                responseTextArea.setEnabled(true);
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


    /**
     * Create and return the server request this view makes when a user selects
     * that they want to practice a new Sha One View example.
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
    
    public void keyTyped (KeyEvent e) {
    }
  
    public void keyPressed (KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && answerField.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Please provide an answer");
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            checkButton.doClick();
        }
    }
    
   public void keyReleased(KeyEvent e) {
   }

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
    
    @Override
    public void setCurrentTask(PendingTask task) {
        this.model.addCurrentTask(task);
        updateView();
    }
}
