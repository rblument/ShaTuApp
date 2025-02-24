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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.view.act.NewExampleAction;
import edu.regis.shatu.view.act.StepCompletionAction;

/**
 * This class represents the Prepare Schedule operation step in a multiple-choice format.
 * Users must select the correct missing step from predefined choices.
 * 
 * @author rickb
 */
public class PrepareScheduleView extends UserRequestView implements ActionListener {

    private TutoringSessionView view;
    private JLabel titleLabel, stepLabel, feedbackLabel, previousStepsLabel;
    private JRadioButton[] answerOptions;
    private ButtonGroup answerGroup;
    private JButton checkAnswerButton, hintButton, newExampleButton;
    private int currentStep = 1;
    private int correctAnswerIndex; // Stores the shuffled position of the correct answer

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
        updatePreviousStepsDisplay();  // Ensure previous steps are cleared initially
        loadStep();
        revalidate();  // Forces UI refresh
        repaint();  // Ensures the correct question is displayed
    }

    /**
     * Handles button actions for checking answers, hints, and new examples.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == checkAnswerButton) {
            checkAnswer();
        } else if (event.getSource() == hintButton) {
            showHint();
        } else if (event.getSource() == newExampleButton) {
            loadNewExample();
        }
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

        checkAnswerButton = new JButton("Check Answer");
        checkAnswerButton.addActionListener(this);

        hintButton = new JButton("Hint");
        hintButton.addActionListener(this);

        newExampleButton = new JButton("New Example");
        newExampleButton.addActionListener(this);

        feedbackLabel = new JLabel(""); // To display hints and feedback
        feedbackLabel.setHorizontalAlignment(SwingConstants.CENTER);
        feedbackLabel.setForeground(Color.BLUE); // System messages in blue
    }

    /**
     * Sets up the layout for the view.
     */
    private void initializeLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridy++;
        add(previousStepsLabel, gbc);

        gbc.gridy++;
        add(stepLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        
        for (JRadioButton option : answerOptions) {
            gbc.gridx = 0;
            gbc.gridwidth = 2;  // Ensure buttons span properly
            add(option, gbc);
            gbc.gridy++;
        }

        gbc.gridwidth = 2;
        add(feedbackLabel, gbc);
        gbc.gridy++;

        gbc.gridwidth = 1;
        gbc.gridy++;
        add(checkAnswerButton, gbc);

        gbc.gridy++;
        add(hintButton, gbc);

        gbc.gridy++;
        add(newExampleButton, gbc);
    }


    /**
     * Loads the correct step question for the current step.
     */
    private void loadStep() {
        feedbackLabel.setText("");

        // More challenging step questions
        String[] stepQuestions = {
            "Which process ensures the input message conforms to the required block size in SHA-256?",
            "What transformation is applied to each 512-bit block before processing?",
            "What technique does SHA-256 use to extend a small set of words into a larger sequence?",
            "How is the expanded word sequence utilized in the hashing process?"
        };

        // Ensure the first question always appears correctly
        if (currentStep == 1) {
            stepLabel.setText("<html><u>Step 1: " + stepQuestions[0] + "</u></html>");
        } else {
            stepLabel.setText("<html><u>Step " + currentStep + ": " + stepQuestions[currentStep - 1] + "</u></html>");
        }

        // Answer choices with correct answers always in the first position before shuffling
        String[][] answerChoices = {
            { "Padding is added to ensure the message is a multiple of 512 bits.",   // Correct answer
            "SHA-256 automatically adjusts message length without padding.",
            "Each message is divided into arbitrary-sized chunks before hashing." },
            { "The block is split into 32-bit segments for further manipulation.",   // Correct answer
            "A checksum is computed and attached to each block.",
            "Each chunk remains unaltered until the final hashing step." },
            { "A series of bitwise operations and shifts increase the number of words.",  // Correct answer
            "Additional words are appended from external sources.",
            "Words are kept constant, ensuring stability in the process." },
            { "The transformed sequence helps derive intermediate hash values.",   // Correct answer
            "The expanded words are discarded after processing.",
            "Only a subset of the expanded words contributes to the hash output." }
        };

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
     * Checks if the selected answer is correct.
     */
    private void checkAnswer() {
        if (answerOptions[correctAnswerIndex].isSelected()) {
            feedbackLabel.setText("Correct! Please choose New Example.");
        } else {
            feedbackLabel.setText("Incorrect. Try again.");
        }
    }


    /**
     * Displays a tailored hint based on the selected answer.
     */
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


    /**
     * Loads the next example or resets the steps.
     */
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
        return new StepCompletion(currentStepObj, gson.toJson(correctSteps[this.currentStep - 1]));
    }

    /**
     * **Override the abstract updateView() method from UserRequestView.**
     */
    @Override
    protected void updateView() {
        view = SplashFrame.instance().getTutoringSessionView();
        view.resetButtonListeners();

        // Only update label if the step is greater than 1 to prevent overriding first question
        if (currentStep > 1) {
            stepLabel.setText("<html><u>Step " + currentStep + ": " + correctSteps[currentStep - 1] + "</u></html>");
        }

        feedbackLabel.setText("");
        updatePreviousStepsDisplay();
    }

    /**
     * **Override the abstract newRequest() method from UserRequestView.**
     */
    @Override
    public NewExampleRequest newRequest() {
        NewExampleRequest ex = new NewExampleRequest();
        ex.setExampleType(ProblemType.PREPARE_SCHEDULE);
        ex.setData(gson.toJson(correctSteps));
        return ex;
    }
}
