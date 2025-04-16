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


import edu.regis.shatu.model.PrepScheduleStep;
import java.awt.*;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;


/**
 * This class represents the Prepare Schedule operation step in a multiple-choice format.
 * Users must select the correct missing step from predefined choices.
 * 
 * @author rickb
 */
public class PrepareScheduleView extends UserRequestView /*implements ActionListener*/ {

    private TutoringSessionView view;
    private JLabel titleLabel, stepLabel, feedbackLabel, previousStepsLabel;
    private JRadioButton[] answerOptions;
    private ButtonGroup answerGroup;
    private JButton checkButton, hintButton, nextButton;
    private int stepNumber = 0;
    private int correctAnswerIndex; // Stores the shuffled position of the correct answer
    private String question;

    // Correct Steps (displayed in order)
    private final String[] correctSteps = {
        "Padding is added to ensure the message is a multiple of 512 bits.",
        "The block is split into 32-bit segments for further manipulation.",
        "A series of bitwise operations and shifts increase the number of words.",
        "The transformed sequence helps derive intermediate hash values."
    };
    
    // More challenging step questions
    private final String[] stepQuestions = {
            "Which process ensures the input message conforms to the required block size in SHA-256?",
            "What transformation is applied to each 512-bit block before processing?",
            "What technique does SHA-256 use to extend a small set of words into a larger sequence?",
            "How is the expanded word sequence utilized in the hashing process?"
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
    }


    /**
     * Loads the correct step question for the current step.
     */
    private void loadStep() {
        feedbackLabel.setText("");

        
        // Ensure the first question always appears correctly
        if (stepNumber == 0) {
            stepLabel.setText("<html><u>Step 1: " + stepQuestions[0] + "</u></html>");
        }else{   
            stepLabel.setText("<html><u>Step " + stepNumber+1 + ": " + stepQuestions[stepNumber] + "</u></html>");
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
        String correctAnswer = answerChoices[stepNumber][0];

        // Shuffle answer choices to randomize positions
        List<String> shuffledAnswers = new ArrayList<>();
        for (String answer : answerChoices[stepNumber]) {
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
     * Updates the display of previous steps above the question.
     */
    private void updatePreviousStepsDisplay() {
        StringBuilder sb = new StringBuilder("<html>");
        for (int i = 0; i < stepNumber; i++) {
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
        Step currentStep = model.currentTask().currentStep().getStep();
        
        view = SplashFrame.instance().getTutoringSessionView();
        hintButton = view.getHintButton();
        checkButton = view.getCheckButton();
        nextButton = view.getNewExampleButton();
        
        PrepScheduleStep example = gson.fromJson(currentStep.getData(), PrepScheduleStep.class);
        String userAnswer = "";
        for(int i = 0; i < 3; i++){
            if (answerOptions[i].isSelected()){
                userAnswer = answerOptions[i].getText();
            }
        }
        
        example.setUserAnswer(userAnswer);
        example.setQuestion(stepQuestions[stepNumber]);
        example.setCorrectAnswer(correctSteps[stepNumber]);
        example.setStepNumber(stepNumber);
                
        StepCompletion step = new StepCompletion(currentStep, gson.toJson(example));
        step.setStep(currentStep);
        if(userAnswer.equals(correctSteps[stepNumber]) && stepNumber <=3){
            stepNumber++;
        }

        step.setStepNumber(stepNumber);       
        return step;
    }

    /**
     * **Override the abstract updateView() method from UserRequestView.**
     */
    @Override
    protected void updateView() {

        view = SplashFrame.instance().getTutoringSessionView();
        hintButton = view.getHintButton();
        checkButton = view.getCheckButton();
        nextButton = view.getNewExampleButton();
        
        StepSubType type = StepSubType.PREPARE_SCHEDULE;
        
        System.out.println("Prepare Schedule update view called"); // Error checking

        Step step = model.currentTask().getCurrentStep().getStep(); // Will be the last subtype a example was created for or empty

        if(step.getSubType() == StepSubType.PREPARE_SCHEDULE){
            System.out.println("BRANCH TAKEN!!!"); // Error checking
            PrepScheduleStep prepareScheduleStep = gson.fromJson(step.getData(), PrepScheduleStep.class);
            
            stepNumber = prepareScheduleStep.getStepNumber();
            
            for (int i = 0; i < 3; i++) { //reset radio button selections
                answerOptions[i].setSelected(false);
            }
            
            System.out.println("If branch was taken, subtype was a prepare schedule"); // Error checking.'
            this.question = prepareScheduleStep.getQuestion();
            System.out.println("THIS.QUESTION: " + this.question); // Error checking.'

            if (this.question == null) { // new example hasnt been created yet
                checkButton.setEnabled(false);
                hintButton.setEnabled(false);
                titleLabel.setText("<html><b>CONGRATULATIONS YOU FINISHED THE PREPARE SCHEDULE!<br>PLEASE CLICK NEW EXAMPLE TO START OR RESTART<b></html>");
                stepLabel.setText("");
                previousStepsLabel.setText("");
            } else{ // example has been created.
                titleLabel.setText("<html><b>Please choose the correct option for each step.<b></html>");
                checkButton.setEnabled(true);
                hintButton.setEnabled(true);
                nextButton.setEnabled(false);
                updatePreviousStepsDisplay();
                loadStep();
            }
        }else { // subtype didnt match, new example needs to be created.
            System.out.println("Else branch was taken, subtype not prepae schedule"); // Error checking.
            System.out.println("Please click new example button to get started");

            checkButton.setEnabled(false);
            hintButton.setEnabled(false);
        }
      
        
        // Only update label if the step is greater than 1 to prevent overriding first question
        if (stepNumber > 0 && stepNumber != 4) {
            stepLabel.setText("<html><u>Step " + (stepNumber+1) + ": " + stepQuestions[stepNumber] + "</u></html>");
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

        PrepScheduleStep newPrepareScheduleStep = new PrepScheduleStep();
        
        if(stepNumber > 3){
            ex.setData(gson.toJson(newPrepareScheduleStep));
            return ex;
        }
        newPrepareScheduleStep.setQuestion(stepQuestions[stepNumber]);
        newPrepareScheduleStep.setStepNumber(stepNumber);

        ex.setData(gson.toJson(newPrepareScheduleStep));

        return ex;
    }
}
