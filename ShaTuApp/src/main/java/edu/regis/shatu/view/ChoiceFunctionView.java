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
import edu.regis.shatu.model.ChoiceFunctionStep;
import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.ExampleType;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.view.act.HintAction;
import edu.regis.shatu.view.act.NewExampleAction;
import edu.regis.shatu.view.act.StepCompletionAction;
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
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * ChoiceFunctionView class represents a GUI view for a choice function Ch(x, y,
 * z). Users can input their answers in a JTextField and check correctness.
 * Provides functionality for hints and moving to the next question.
 *
 * @author rickb
 */
public class ChoiceFunctionView extends UserRequestView implements ActionListener, KeyListener {

    private String stringX, stringY, stringZ;
    private int problemSize;
    private JTextArea descTextArea, feedbackTextArea, responseTextArea;
    private JScrollPane feedbackPane, responsePane, chTruthTablePane;
    private GPanel truthTablePanel, questionPanel, descriptionPanel, qrPanel;
    private JPanel buttonPanel, radioButtonPanel;
    private JTable chTruthTable;
    private JButton checkButton, nextButton, hintButton;
    private ButtonGroup problemSizeGroup;
    private JRadioButton fourRadioButton, eightRadioButton, sixteenRadioButton,
            thirtytwoRadioButton;
    private JLabel viewNameLabel, truthTableLabel, chFunctionLabel,
            stringXLabel, stringYLabel, stringZLabel, answerLabel,
            problemSizeLabel, instructionLabel;

    /**
     * Initializes the ChoiceFunctionView by creating and laying out its child
     * components.
     */
    public ChoiceFunctionView() {
        initializeComponents();
        initializeLayout();
    }

    /**
     * Create and return the server request this view makes when a user selects
     * that they want to practice a new choice function example.
     *
     * @return
     */
    @Override
    public NewExampleRequest newRequest() {
        NewExampleRequest ex = new NewExampleRequest();

        //Set example type to the problem associated with the current view
        ex.setExampleType(ExampleType.CHOICE_FUNCTION);

        ChoiceFunctionStep newStep = new ChoiceFunctionStep();

        newStep.setBitLength(problemSize);

        //Set the data of the NewExampleRequest to the new RotateStep containing
        //the desired conditions
        ex.setData(gson.toJson(newStep));

        return ex;
    }

    @Override
    public StepCompletion stepCompletion() {
        Step currentStep = model.currentTask().currentStep();

        ChoiceFunctionStep example = gson.fromJson(currentStep.getData(), ChoiceFunctionStep.class);

        String userResponse = responseTextArea.getText().replaceAll("\\s", "");

        example.setResult(userResponse);

        StepCompletion step = new StepCompletion(currentStep, gson.toJson(example));
        
        step.setStep(currentStep);

        return step;
    }

    /**
     * Creates child GUI components for the view.
     */
    private void initializeComponents() {
        setUpDescription();
        setUpRadioButtons();
        setUpQuestionArea();
        setUpResponseArea();
        setUpFeedbackArea();
        setUpButtons();
        setUpTruthTable();
        setUpDescriptionPanel();
        setUpQRPanel();
    }

    /**
     * Lays out the child components in the view.
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
        viewNameLabel = new JLabel("The Choice Function");
        viewNameLabel.setFont(new Font("", Font.BOLD, 20));

        descTextArea = new JTextArea();
        descTextArea.setEditable(false);
        descTextArea.setLineWrap(true);
        descTextArea.setWrapStyleWord(true);
        descTextArea.setOpaque(false);
        descTextArea.append("""
                            The Choice function takes three 32-bit words as input and outputs one 32-bit word. This output is necessary to complete the
                            second addition step in the SHA-256 algorithm.""");
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

        descriptionPanel.addc(truthTablePanel, 1, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
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
            updateProblemSi(source);
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
    private void updateProblemSi(JRadioButton source) {
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

    /**
     * Initializes the question components and adds them to the question panel.
     */
    private void setUpQuestionArea() {

        problemSize = 4;
        stringX = "foo"; // generateInputString();
        stringY = "var"; // generateInputString();
        stringZ = "baz"; // generateInputString();

        stringXLabel = new JLabel("x: " + stringX);
        stringYLabel = new JLabel("y: " + stringY);
        stringZLabel = new JLabel("z: " + stringZ);

        problemSizeLabel = new JLabel("Select Problem Size:");
        instructionLabel = new JLabel("Solve the choice function using the three "
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

        questionPanel.addc(stringZLabel, 0, 5, 1, 1, 0.0, 0.0,
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

        qrPanel.addc(feedbackPane, 0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                5, 5, 5, 5);

        qrPanel.addc(buttonPanel, 0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
    }

    /**
     * Sets up the truth table associated with the Choice Function.
     */
    private void setUpTruthTable() {

        truthTableLabel = new JLabel("Ch Function Truth Table");
        truthTableLabel.setFont(new Font("", Font.BOLD, 14));
        chFunctionLabel = new JLabel("Ch(𝑥,𝑦,𝑧)=(𝑥∧𝑦)⊕(¬𝑥∧𝑧)");

        Object[] columnNames = {"x", "y", "z", "(𝑥∧𝑦)", "(¬𝑥∧𝑧)", "(𝑥∧𝑦)⨁(¬𝑥∧𝑧)"};
        Object[][] data = {{0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 1, 1},
        {0, 1, 0, 0, 0, 0},
        {0, 1, 1, 0, 1, 1},
        {1, 0, 0, 0, 0, 0},
        {1, 0, 1, 0, 0, 0},
        {1, 1, 0, 1, 0, 1},
        {1, 1, 1, 1, 0, 1}};

        chTruthTable = new JTable(data, columnNames);
        configureChTruthTable();

        chTruthTablePane = new JScrollPane(chTruthTable);
        chTruthTablePane.setPreferredSize(new Dimension(400, 151));
        chTruthTablePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chTruthTablePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        truthTablePanel = new GPanel();

        truthTablePanel.addc(truthTableLabel, 0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);

        truthTablePanel.addc(chFunctionLabel, 0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);

        truthTablePanel.addc(chTruthTablePane, 0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                5, 5, 5, 5);
    }

    /**
     * Configures the appearance of the truth table.
     */
    private void configureChTruthTable() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int columnIndex = 0; columnIndex < chTruthTable.getColumnCount(); columnIndex++) {
            chTruthTable.getColumnModel().getColumn(columnIndex).setCellRenderer(centerRenderer);
        }
        chTruthTable.getColumnModel().getColumn(0).setPreferredWidth(25);
        chTruthTable.getColumnModel().getColumn(1).setPreferredWidth(25);
        chTruthTable.getColumnModel().getColumn(2).setPreferredWidth(25);
        chTruthTable.getColumnModel().getColumn(5).setPreferredWidth(100);
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
    }

    /**
     * Handles the keyPressed event for the view.
     *
     * @param e The KeyEvent that occurred.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && responseTextArea.getText().equals("")) {
            feedbackTextArea.setText("Please provide an answer");
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
    }

    /**
     * Verifies the user's answer against the correct answer.
     */
    private void verifyAnswer() {
        /*
        String correctAnswer = choiceFunction(stringX, stringY, stringZ);
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
         */
    }

    /**
     * Handles the action for the Next Question button.
     */
    private void onNextQuestion() {
        /*
        responseTextArea.setText("");
        feedbackTextArea.setText("");

        generateNewQuestion();

        nextButton.setEnabled(false);
        checkButton.setEnabled(true);
         */
    }

    /**
     * Handles the action for the Hint button.
     */
    private void onNextHint() {
        feedbackTextArea.setText("Hint: Check the truth table above for the "
                + "appropriate values.");
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

    @Override
    protected void updateView() {
        System.out.println("Choice function update view called."); // Error checking
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Step step = model.currentTask().getCurrentStep();
        
        System.out.println("Choice value View substep from current step: " + step.getSubType()); // Error checking

        ChoiceFunctionStep example = gson.fromJson(step.getData(), ChoiceFunctionStep.class);

        stringXLabel.setText("x: " + example.getOperand1());
        stringYLabel.setText("y: " + example.getOperand2());
        stringZLabel.setText("z: " + example.getOperand3());

    }

}
