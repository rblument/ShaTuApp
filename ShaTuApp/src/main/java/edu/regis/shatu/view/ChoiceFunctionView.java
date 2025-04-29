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
 /*
 * set responseTextArea enabled/disabled per SHAT-225 John Hennessey 23 Feb 2025
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
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.regis.shatu.model.ChoiceFunctionStep;
import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;

/**
 * ChoiceFunctionView class represents a GUI view for a choice function Ch(x, y,
 * z). 
 * Given three inputs (variables e, f, g) perform a bitwise if/then/else.
 * If the 𝑥-bit is set (1), the 𝑦 bit is output,otherwise, the 𝑧 bit is output.
 * Users can input their answers in a JTextField and check correctness.
 * Provides functionality for hints and moving to the next question.
 * Returns a single 32 bit binary value.
 * 
 * @author rickb
 */
public class ChoiceFunctionView extends UserRequestView implements KeyListener {

    private TutoringSessionView view;
    private String stringX, stringY, stringZ;
    private int problemSize;
    private JTextArea descTextArea, responseTextArea;
    private JScrollPane responsePane, chTruthTablePane;
    private GPanel truthTablePanel, questionPanel, descriptionPanel, qrPanel;
    private JPanel radioButtonPanel;
    private JTable chTruthTable;
    private JButton truthTableToggleButton;
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
        ex.setExampleType(ProblemType.CHOICE_FUNCTION);

        ChoiceFunctionStep newStep = new ChoiceFunctionStep();

        newStep.setBitLength(problemSize);

        //Set the data of the NewExampleRequest to the new RotateStep containing
        //the desired conditions
        ex.setData(gson.toJson(newStep));

        return ex;
    }
    /*
    Sends notification to the tutor that this step in the tutoring has been
    successfully completed and student is ready to move on to the next step.
    */
    @Override
    public StepCompletion stepCompletion() {
        Step currentStep = model.currentTask().currentStep().getStep();

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
        
        addc(buttonPanel, 0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
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
                            The Choice function takes three 32-bit words as input and outputs one 32-bit word. This output is necessary to complete the second addition step in the SHA-256 algorithm.""");
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
        responseTextArea.setEnabled(false);  // Text area disabled at initialization 

        responsePane = new JScrollPane(responseTextArea);
        responsePane.setPreferredSize(new Dimension(800, 200));
    }


    /**
     * Creates a GPanel containing the response and feedback JScrollPanes and
     * the button panel.
     */
    private void setUpQRPanel() {
        qrPanel = new GPanel();
        qrPanel.setMinimumSize(new Dimension(500, 100));

        qrPanel.addc(responsePane, 0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
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

        setupTruthTableToggleButton();

        truthTablePanel.addc(truthTableLabel, 0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);

        truthTablePanel.addc(chFunctionLabel, 0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);

        truthTablePanel.addc(chTruthTablePane, 0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                5, 5, 5, 5);

        truthTablePanel.addc(truthTableToggleButton, 1, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                5, 5, 5, 5);
    }

    /**
     * On button press will show/hide Truth Table
     */
    private void setupTruthTableToggleButton() {
        truthTableToggleButton = new JButton("Show Truth Table");
        truthTableToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isCurrentlyVisible = chTruthTablePane.isVisible();
                chTruthTablePane.setVisible(!isCurrentlyVisible);
                truthTableLabel.setVisible(!isCurrentlyVisible);
                chFunctionLabel.setVisible(!isCurrentlyVisible);

                // Update button text based on show/hide press
                if (isCurrentlyVisible) {
                    truthTableToggleButton.setText("Show Truth Table");
                } else {
                    truthTableToggleButton.setText("Hide Truth Table");
                }
            }
        });
        //start by hiding truth table for practice mode
        chTruthTablePane.setVisible(false);
        truthTableLabel.setVisible(false);
        chFunctionLabel.setVisible(false);
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
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            checkButton.doClick();
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

    @Override
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
    }

    /**
     * Defines each view classes' standard method for updating in the Practice View
     */
    @Override
    protected void updatePracticeView() {


        // If check and hint buttons are disabled, reset listenerers and apply those used by this view
        if (!checkHintEnabled) {
           resetButtonListeners(); // Clear any listeners applied from other views
        }

        System.out.println("Choice function update view called."); // Error checking

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Step step = model.currentTask().getCurrentStep().getStep();

        System.out.println("Choice value View substep from current step: " + step.getSubType()); // Error checking

        if (step.getSubType() == StepSubType.CHOICE_FUNCTION) {
            ChoiceFunctionStep example = gson.fromJson(step.getData(), ChoiceFunctionStep.class);

            if (example.getOperand1() == null || example.getOperand1().isEmpty()) {
                stringXLabel.setText("x: Please");
                stringYLabel.setText("y: click");
                stringZLabel.setText("z: New Example");
                hintButton.setEnabled(false);
                responseTextArea.setEnabled(false);
                checkButton.setEnabled(false);
            } else {
                stringXLabel.setText("x: " + example.getOperand1());
                stringYLabel.setText("y: " + example.getOperand2());
                stringZLabel.setText("z: " + example.getOperand3());
                hintButton.setEnabled(true);
                responseTextArea.setEnabled(true);
                checkButton.setEnabled(true);
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
