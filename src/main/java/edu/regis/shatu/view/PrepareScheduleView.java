/*
 * SHATU: SHA-256 Tutor
 * 
 * (C) Johanna & Richard Blumenthal, All rights reserved
 * 
 * Unauthorized use, duplication, or distribution without the authors'
 * permission is strictly prohibited.
 * 
 * Unless required by applicable law or agreed to in writing, this
 * software is distributed on an "AS IS" basis without warranties
 * or conditions of any kind, either expressed or implied.
 */
package edu.regis.shatu.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import com.google.gson.JsonSyntaxException;

import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.aol.TutoringMode;
import edu.regis.shatu.model.steps.PrepScheduleStep;
import edu.regis.shatu.model.steps.Step;

/**
 * This class represents the Prepare Schedule operation step in a
 * multiple-choice format.
 * Users must select the correct missing step from predefined choices.
 * 
 * @author rickb
 */
public class PrepareScheduleView extends UserRequestView { // implements ActionListener {
    private TutoringSessionView view;
    private JLabel titleLabel, stepLabel, feedbackLabel, previousStepsLabel;
    private JRadioButton[] answerOptions;
    private ButtonGroup answerGroup;

    private int currentStep = 1;
    private int correctAnswerIndex; // Stores the shuffled position of the correct answer

    // Whether the question on screen came from the tutor. The first one is
    // shown before the tutor has sent any.
    private boolean questionFromTutor = false;

    // The tutor's step whose question is on screen, so a new one is loaded
    // even when it asks the same question.
    private Step shownStep;

    // Correct Steps (displayed in order)
    private final String[] correctSteps = {
            "The input message has been padded and divided into 512-bit chunks.",
            "Each chunk is divided into 32-bit words (initially 16 words).",
            "The words are expanded to 64 using shifts, rotations, and bitwise operations.",
            "The expanded words are used in the compression phase."
    };

    /**
     * Generates the prepare schedule view.
     */
    public PrepareScheduleView() {
        initializeComponents();
        initializeLayout();
        updatePreviousStepsDisplay(); // Ensure previous steps are cleared initially
        loadStep();
        revalidate(); // Forces UI refresh
        repaint(); // Ensures the correct question is displayed
    }

    /**
     * Initializes UI components.
     */
    private void initializeComponents() {
        titleLabel = new JLabel("Please choose the correct option for each step.", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        previousStepsLabel = new JLabel("<html></html>"); // Displays previous steps
        previousStepsLabel.setFont(new Font("Arial", Font.BOLD, 14));

        stepLabel = new JLabel("");
        stepLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        answerOptions = new JRadioButton[3];
        answerGroup = new ButtonGroup();
        for (int i = 0; i < 3; i++) {
            answerOptions[i] = new JRadioButton();
            answerOptions[i].setFont(new Font("Arial", Font.PLAIN, 12));
            answerGroup.add(answerOptions[i]);
        }

        feedbackLabel = new JLabel(""); // To display hints and feedback
        feedbackLabel.setHorizontalAlignment(SwingConstants.CENTER);
        feedbackLabel.setForeground(Color.BLUE); // System messages in blue
    }

    /**
     * Sets up the layout for the view.
     */
    private void initializeLayout() {
        addc(titleLabel, 0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);

        addc(stepLabel, 0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);

        int gridX = 2;
        for (JRadioButton option : answerOptions) {
            addc(option, 0, gridX++, 1, 1, 1.0, 0.0,
                    GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                    5, 5, 5, 5);
        }

        addc(buttonPanel, 0, ++gridX, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);

        /*
         * setLayout(new GridBagLayout());
         * GridBagConstraints gbc = new GridBagConstraints();
         * gbc.insets = new Insets(10, 10, 10, 10);
         * gbc.fill = GridBagConstraints.HORIZONTAL;
         * gbc.anchor = GridBagConstraints.CENTER;
         * 
         * gbc.gridx = 0;
         * gbc.gridy = 0;
         * gbc.gridwidth = 2;
         * add(titleLabel, gbc);
         * 
         * gbc.gridy++;
         * add(previousStepsLabel, gbc);
         * 
         * gbc.gridy++;
         * add(stepLabel, gbc);
         * 
         * gbc.gridwidth = 1;
         * gbc.gridy++;
         * 
         * for (JRadioButton option : answerOptions) {
         * gbc.gridx = 0;
         * gbc.gridwidth = 2; // Ensure buttons span properly
         * add(option, gbc);
         * gbc.gridy++;
         * }
         * 
         * gbc.gridwidth = 2;
         * add(feedbackLabel, gbc);
         * gbc.gridy++;
         * 
         * gbc.gridwidth = 1;
         * gbc.gridy++;
         * add(checkAnswerButton, gbc);
         * 
         * gbc.gridy++;
         * add(hintButton, gbc);
         * 
         * gbc.gridy++;
         * add(newExampleButton, gbc);
         */
    }

    /**
     * Loads the correct step question for the current step.
     */
    private void loadStep() {
        feedbackLabel.setText("");
        answerGroup.clearSelection();

        String[] stepQuestions = PrepScheduleStep.QUESTIONS;

        // Ensure the first question always appears correctly
        if (currentStep == 1) {
            stepLabel.setText("<html><u>Step 1: " + stepQuestions[0] + "</u></html>");
        } else {
            stepLabel.setText("<html><u>Step " + currentStep + ": " + stepQuestions[currentStep - 1] + "</u></html>");
        }

        // The correct answer is always the first choice before shuffling.
        String[][] answerChoices = PrepScheduleStep.CHOICES;

        // Store correct answer before shuffling
        String correctAnswer = answerChoices[currentStep - 1][0];

        // Shuffle answer choices to randomize positions
        List<String> shuffledAnswers = new ArrayList<>();
        for (String answer : answerChoices[currentStep - 1]) {
            shuffledAnswers.add(answer);
        }
        Collections.shuffle(shuffledAnswers, new Random()); // Shuffle the answers

        // Find the new position of the correct answer
        correctAnswerIndex = shuffledAnswers.indexOf(correctAnswer);

        // Assign shuffled answer choices to the radio buttons
        for (int i = 0; i < answerOptions.length; i++) {
            answerOptions[i].setText(shuffledAnswers.get(i));
        }

        // Ensure UI refresh
        revalidate();
        repaint();
    }

    /**
     * NO LONGER NEEDED KEPT FOR REFRENCE
     * 
     * Checks if the selected answer is correct.
     * private void checkAnswer() {
        if (answerOptions[correctAnswerIndex].isSelected()) {
            feedbackLabel.setText("Correct! Please choose New Example.");
        } else {
            feedbackLabel.setText("Incorrect. Try again.");
        }
    }
     */

    /**
     * NO LONGER NEEDED KEPT FOR REFRENCE
     * 
     * Displays a tailored hint based on the selected answer.
    private void showHint() {
        int selectedIndex = -1; // Default: No selection

        // Find the index of the selected radio button
        for (int i = 0; i < answerOptions.length; i++) {
            if (answerOptions[i].isSelected()) {
                selectedIndex = i;
                break;
            }
        }

        if (selectedIndex == -1) {
            feedbackLabel.setText("Please select an answer before requesting a hint.");
            return;
        }

        if (selectedIndex == correctAnswerIndex) {
            feedbackLabel.setText("You should check your answer.");
            return;
        }

        // Tailored hints based on incorrect selection
        String[][] hintMessages = {
                { "Consider how SHA-256 processes blocks before hashing.",
                        "Think about why padding is necessary in a cryptographic hash function.",
                        "Does SHA-256 allow arbitrary block sizes, or does it standardize them?" },

                { "What is the purpose of dividing blocks into smaller segments?",
                        "Does adding a checksum affect the internal structure of SHA-256?",
                        "Why do cryptographic algorithms transform data before applying functions?" },

                { "Consider how SHA-256 generates new words during expansion.",
                        "Does SHA-256 add external words, or does it derive them from existing ones?",
                        "Why would an algorithm need to keep word length constant?" },

                { "How does SHA-256 use transformed words in its compression phase?",
                        "Does discarding words make sense in a secure hashing process?",
                        "Would limiting the words used affect the final hash?" }
        };

        // Assign a specific hint for the wrong choice selected
        feedbackLabel.setText(hintMessages[currentStep - 1][selectedIndex]);
    }
     */

    /**
     * NO LONGER NEEDED KEPT FOR REFRENCE
     * 
     * Loads the next example or resets the steps.
    private void loadNewExample() {
        if (answerOptions[0].isSelected() || feedbackLabel.getText().contains("Correct")) {
            if (currentStep < 4) {
                currentStep++;
                updatePreviousStepsDisplay();
                loadStep();
            } else {
                currentStep = 1;
                previousStepsLabel.setText("<html></html>");
                loadStep();
            }
            feedbackLabel.setText(""); // Clear feedback message
        } else {
            feedbackLabel.setText("You must select the correct answer before proceeding.");
        }
    }
     */

    /**
     * Updates the display of previous steps above the question.
     */
    private void updatePreviousStepsDisplay() {
        StringBuilder sb = new StringBuilder("<html>");
        for (int i = 0; i < currentStep - 1; i++) {
            sb.append("<b>Step ").append(i + 1).append(":</b> ").append(correctSteps[i]).append("<br>");
        }
        sb.append("</html>");
        previousStepsLabel.setText(sb.toString());
    }

    /**
     * **Override the abstract stepCompletion() method from UserRequestView.**
     */
    @Override
    public StepCompletion stepCompletion() {
        Step currentStepObj = model.currentTask().currentStep().getStep();

        // Send the question and the student's pick so the tutor can grade it.
        PrepScheduleStep answered = new PrepScheduleStep();
        answered.setQuestionNumber(currentStep);
        answered.setAnswer(selectedAnswer());

        return new StepCompletion(currentStepObj, gson.toJson(answered));
    }

    /**
     * The text of the selected choice.
     *
     * @return the selected choice, or "" if none is selected
     */
    private String selectedAnswer() {
        for (JRadioButton option : answerOptions) {
            if (option.isSelected()) {
                return option.getText();
            }
        }
        return "";
    }

    /**
     * **Override the abstract updateView() method from UserRequestView.**
     */
    @Override
    protected void updateView() {
                resetButtonListeners();

        // A new example from the tutor says which question to show.
        // A step from another screen leaves the question on screen as it is.
        int questionNumber = questionInCurrentStep();
        if (questionNumber > 0) {
            questionFromTutor = true;
            Step step = model.currentTask().currentStep().getStep();
            if (step != shownStep) {
                shownStep = step;
                currentStep = questionNumber;
                loadStep();
            }
        }

        feedbackLabel.setText("");
        updatePreviousStepsDisplay();
    }

    /**
     * The question number in the current step's data.
     *
     * @return the question the tutor sent, or 0 if the current step isn't a
     *         Prepare Schedule question
     */
    private int questionInCurrentStep() {
        if (model == null || model.currentTask() == null || model.currentTask().currentStep() == null) {
            return 0;
        }

        Step step = model.currentTask().currentStep().getStep();
        if (step == null || step.getSubType() != StepSubType.PREPARE_SCHEDULE) {
            return 0;
        }

        try {
            PrepScheduleStep data = gson.fromJson(step.getData(), PrepScheduleStep.class);
            if (data != null && PrepScheduleStep.isQuestion(data.getQuestionNumber())) {
                return data.getQuestionNumber();
            }
        } catch (JsonSyntaxException e) {
            // Not a Prepare Schedule question, keep the one on screen.
        }
        return 0;
    }

    
    
    /**
     * Configure UI components based on the current tutoring mode
     * Disables fields not available for SEE_ONE mode for passive viewing.
     * 
     */
    
    @Override
    protected void configureModeSpecificUI() {
        super.configureModeSpecificUI();//call parent to handle buttons
        
        if(model == null) {
            return;
        }
        
        TutoringMode mode = model.getTutoringMode();
        
        if (mode == TutoringMode.SEE_ONE) {
            answerOptions[0].setEnabled(false);
            answerOptions[1].setEnabled(false);
            answerOptions[2].setEnabled(false);
            
            //disable feedback label
            feedbackLabel.setEnabled(false);
        }
        // DO_ONE & TEACH_ONE
        else {
            answerOptions[0].setEnabled(true);
            answerOptions[1].setEnabled(true);
            answerOptions[2].setEnabled(true);
            
            feedbackLabel.setEnabled(true);
        }
    } 
    
    /**
     * I think this could be done by ShaTuTutor but removing it breaks the class
     * 
     * **Override the abstract newRequest() method from UserRequestView.**
     */
    @Override
    public NewExampleRequest newRequest() {
        NewExampleRequest ex = new NewExampleRequest();
        ex.setExampleType(ProblemType.PREPARE_SCHEDULE);

        // Ask for the question on screen if the tutor hasn't sent one yet,
        // otherwise the next one, going back to the first after the last.
        PrepScheduleStep next = new PrepScheduleStep();
        next.setQuestionNumber(questionFromTutor
                ? currentStep % PrepScheduleStep.QUESTIONS.length + 1
                : currentStep);
        ex.setData(gson.toJson(next));
        return ex;
    }
}