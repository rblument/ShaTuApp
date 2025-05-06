/*
 * SHATU: SHA-256 Tutor
 * 
 *  (C) Johanna & Richard Blumenthal, All rights reserved
 * 
 *  Unauthorized use, duplication or distribution without the authors'
 *  permission is strictly prohibted.
 * 
 *  Unless required by applicable law or agreed to in writing, this
 *  software is distributed on an "AS IS" basis without warranties
 *  or conditions of any kind, either expressed or implied.
 */
package edu.regis.shatu.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.steps.InitVarStep;
import edu.regis.shatu.model.steps.Step;

/**
 *
 * @author rickb, Ryley MacLagan
 */
public class InitVarView extends UserRequestView { // implements ActionListener

    /**
     * SHA256 has eight initial H variables H0 ... H7.
     */
    private static final int NUM_VARS = 8;

    private TutoringSessionView view;
    private InitVarStep initVarStep;

    private JTextField[] hVars;
    private JTextArea feedbackTextArea;
    private short hintCount;
    private boolean answersVisible, hintsVisible;
    private JLabel[] answerLabels = new JLabel[8]; // Holds correct answer labels

    public InitVarView() {
        initializeComponents();
        initializeLayout();
        attachDocumentListeners();
    }

    /*
     * @Override
     * public void actionPerformed(ActionEvent event) {
     * if (event.getSource() == checkButton) {
     * NewExampleAction.instance().actionPerformed(null);
     * boolean allCorrect = initVarStep.allAnswersCorrect();
     * 
     * if (allCorrect) {
     * showAnswer();
     * feedbackTextArea.setText("Correct!");
     * } else {
     * feedbackTextArea.setText("Incorrect. Please try again or use a hint.");
     * }
     * } else if (event.getSource() == hintButton) {
     * showHint();
     * }
     * }
     */
    private void initializeComponents() {
        hVars = new JTextField[NUM_VARS];

        for (int i = 0; i < NUM_VARS; i++) {
            hVars[i] = new JTextField(20);
            hVars[i].setName("H" + i);
        }

        // Initialize answer labels
        for (int i = 0; i < 8; i++) {
            answerLabels[i] = new JLabel(""); // Empty initially
            answerLabels[i].setFont(new Font("SansSerif", Font.BOLD, 12));
            answerLabels[i].setForeground(Color.BLUE);
        }

        feedbackTextArea = new JTextArea(3, 20);
        feedbackTextArea.setEditable(false);
        feedbackTextArea.setLineWrap(true);
        feedbackTextArea.setWrapStyleWord(true);
        feedbackTextArea.setBackground(null);
    }

    private void initializeLayout() {
        // Use JLabel with HTML to ensure proper paragraph formatting
        JLabel infoLabel = new JLabel("<html><div style='width: 500px;'>"
                + "<p>In SHA-256, the algorithm begins with a set of initial hash values, "
                + "which are specifically chosen constants. These constants are derived from the "
                + "first 32 bits of the fractional parts of the square roots of the first 8 prime numbers. "
                + "They serve as the starting points for the hash computation and ensure that the "
                + "algorithm starts from a random-like state.</p><p>"
                + "Please enter the initial hash values in hexadecimal for H0 to H7 below:<br>"
                + "(Incorrect answers are in red, and turn green when correct.)</p></div></html>");

        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        addc(infoLabel, 0, 0, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);

        int gridX = 1;
        for (int i = 0; i < NUM_VARS; i++, gridX++) {
            JLabel label = new JLabel(hVars[i].getName());
            label.setLabelFor(hVars[i]);
            addc(label, 0, gridX, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    5, 5, 5, 5);

            addc(hVars[i], 1, gridX, 1, 1, 1.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    5, 5, 5, 5);
        }

        addc(buttonPanel, 0, gridX, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
    }

    /**
     * Monitors all text fields for initial variable labels. Sets text color to
     * red until correct. Correct answers are set to a dark green.
     */
    private void attachDocumentListeners() {
        DocumentListener docListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkInput(e);
                // if (!checkButton.isEnabled() && !hintButton.isEnabled()) {
                // view.toggleButton(checkButton);
                // view.toggleButton(hintButton);
                // } else if (!checkButton.isEnabled()) {
                // view.toggleButton(checkButton);
                // }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkInput(e);
                // if (allFieldsEmpty()) {
                // checkButton.setEnabled(false);
                // }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkInput(e);
            }

            private void checkInput(DocumentEvent e) {
                JTextField sourceField = (JTextField) e.getDocument().getProperty("field");
                String variableName = sourceField.getName(); // Use a unique name or identifier for each JTextField
                String userAnswer = sourceField.getText();

                initVarStep.setUserAnswer(variableName, userAnswer);

                if (initVarStep.isUserCorrect(variableName)) {
                    sourceField.setForeground(Color.GREEN.darker().darker());
                } else {
                    sourceField.setForeground(Color.RED);
                }
            }
        };

        for (int i = 0; i < NUM_VARS; i++) {
            // hVars[i].getDocument().putProperty("field", hVars[i]); // property is itself?
            hVars[i].getDocument().addDocumentListener(docListener);
        }
    }

    /**
     * Checks if all text fields are empty.
     *
     * @return true if all fields are empty, false otherwise.
     */
    public boolean allFieldsEmpty() {
        for (int i = 0; i < NUM_VARS; i++) {
            if (!hVars[i].getText().trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }

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
     * Defines each view classes' standard method for updating in the Practice
     * View
     */
    @Override
    protected void updatePracticeView() {
        resetButtonListeners(); // Clear any listeners applied from other views
        feedbackTextArea.setText(""); // Resets text feedback area
        // setupButtons();

        // New example is uniquely hidden for this view, as
        // There are only 8 initial values,
        // all of which the user shall define.
        newExampleButton.setEnabled(false);

        if (model == null) {
            System.out.println("Error: The model is null when switching to Initialize Variables...");
        } else {
            // TODO: Debug statements. Task is not being set properly.
            // The model's tasks list holds only the first task. It must be populated with
            // each task.
            // This should originate from a lack of data within the database.
            // Populating it should aid in resolving this error.
            System.out.println("Initialize update view called.");
            System.out.println("----Init Var Task Title-----" + model.currentTask().getTask().getTitle());
            System.out.println(
                    "----Init Var Step Title-----" + model.currentTask().getCurrentStep().getStep().getTitle());
        }
    }

    /**
     * Defines each view classes' standard method for updating in the Teach Me
     * View
     */
    @Override
    protected void updateTeachView() {

    }

    /**
     * Defines each view classes' standard method for updating in the Teach Me
     * View
     */
    @Override
    protected void updateQuizView() {

    }

    @Override
    public NewExampleRequest newRequest() {
        NewExampleRequest ex = new NewExampleRequest();

        // Set example type to the problem associated with the current view
        ex.setExampleType(ProblemType.INITIALIZE_VARS);

        return ex;
    }

    @Override
    public StepCompletion stepCompletion() {
        Step currentStep = model.currentTask().currentStep().getStep();

        // Populate InitVarStep with user input
        InitVarStep completedInitVarStep = new InitVarStep();

        for (int i = 0; i < NUM_VARS; i++) {
            completedInitVarStep.setUserAnswer(hVars[i].getName(), hVars[i].getText().trim());
        }

        // Create StepCompletion with serialized JSON of dataWrapper
        StepCompletion stepCompletion = new StepCompletion(currentStep, gson.toJson(completedInitVarStep));
        stepCompletion.setStep(currentStep);
        return stepCompletion;
    }
}
