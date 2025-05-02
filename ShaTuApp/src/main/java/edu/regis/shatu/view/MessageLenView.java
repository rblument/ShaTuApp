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
/*
 * Set responseTextArea to disabled at initialization per SHAT-225 John hennessey 23 Feb 2025
 */
package edu.regis.shatu.view;

import java.awt.GridBagConstraints;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.steps.MessageLenStep;
import edu.regis.shatu.model.steps.Step;

/**
 * A view that requests the student to figure out the number of bits their
 * message is then convert that number to binary to represent the message length
 * step
 *
 * @author rickb
 */
public class MessageLenView extends UserRequestView {

    private TutoringSessionView view;
    private JTextPane descriptionTextPane;
    private JLabel questionLabel, instructionsLabel, messageLengthLabel;
    private JTextField messageLengthField;
    private JTextArea responseTextArea;
    private JTextArea feedbackArea;
    private JScrollPane responseScrollPane, feedbackScrollPane;
    private String question;

    /**
     * Initialize this view including creating and laying out its child
     * components.
     */
    public MessageLenView() {

        initializeComponents();
        initializeLayout();
    }

    /**
     * Initializes all GUI components, setting up their properties and
     * configurations.
     */
    private void initializeComponents() {
        setupDescriptionSection();
        setupQuestionLabel();
        setupInstructionLabel();
        setupMessageLengthInput();
        setupResponseArea();
        setupFeedbackArea();
    }

    /**
     * Lays out the initialized components on the panel using GridBagLayout
     * constraints.
     */
    private void initializeLayout() {

        JPanel messageLengthPanel = createMessageLengthPanel();

        // Add components to the layout
        addc(descriptionTextPane, 0, 0, 1, 1,
                1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, 5, 5, 5, 5);
        addc(messageLengthPanel, 0, 1, 1, 1,
                1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, 5, 5, 5, 5);
        addc(questionLabel, 0, 2, 1, 1,
                1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, 5, 5, 5, 5);
        addc(instructionsLabel, 0, 3, 1, 1,
                1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, 5, 5, 5, 5);
        addc(responseScrollPane, 0, 4, 1, 1,
                1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, 5, 5, 5, 5);
        addc(feedbackScrollPane, 0, 5, 1, 1,
                1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, 5, 5, 5, 5);

        addc(buttonPanel, 0, 6, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
    }

    /**
     * Submit the student's answer to the tutor. Currently is suppose to just do
     * a error check, when the check button is clicked, its suppose to call the
     * stepCompletion method that calls to the tutor and lets the tutor handle
     * checking the answer. This method may be removed later in development.
     */
    public void submitAnswer() {

        if (this.responseTextArea.getText().equals("")) {
            this.feedbackArea.setText("Please provide an answer");
        } else {
            // Do nothing, tutor should be handling everything, but will leave incase a use
            // can be found in development.
        }
    }

    /**
     * This method use to be called when the new example button is clicked, the
     * tutor is suppose to handle creating a new example/question so this method
     * may be outdated, leaving in-case a use can be found in development, but
     * may no longer be needed.
     */
    private void prepareNextQuestion() {
        // Do nothing, tutor should be handling things, will leave incase a use
        // could be found during development.
    }

    /**
     * Gives the student a hint and adds the ASCII table to the view, rest
     * should be handles by the tutor, maybe all of it should? Adjust as
     * development continues.
     */
    public void requestHint() {

        // Adjust the hint as needed
        this.feedbackArea.setText("Hint: Lets say you have a message with 1 character, "
                + "1 character is 8 bits.  Whats 8 in Binary form?  Its 1000, "
                + "please review binary if you ar unfimiliar or need a review before continuing. "
                + "1000 is the answer, typically you need to pad it with zeros, but for this step DONT.");

        this.revalidate(); // refreshes the view
        this.repaint(); // refreshes the view
    }

    /**
     * Sets up the description section of the view, explaining the purpose of
     * the encoding exercise.
     */
    private void setupDescriptionSection() {
        descriptionTextPane = new JTextPane();
        descriptionTextPane.setContentType("text/html");

        // TEMPORARY UNTIL WE LOAD THE MODEL DATA DESCRIPTION
        descriptionTextPane.setText(
                "<html>"
                        + "<body>"
                        + "<h2>Add Message Length</h2>"
                        + "<p>A sha256 message needs to be 512 bits.  We know that we added '1' bit during the add-one-bit step,"
                        + " and we ensured the message was padded with zeros until it contained 448 bits during the pad-zeros step.  "
                        + "We need to account for the last 64 bits, which will be the message length.<br>"
                        + "You need to calculate the message length:<br>"
                        + "1: What is the length of your message character wise?<br>"
                        + "2: How many bits is a character? Add them together to get the total bits of your message.<br>"
                        + "3: Convert that total to binary form. That will be your answer.<br>"
                        + "EXTRA: When submitting your answer, the program will remove spaces from your answer.  "
                        + "You will only need to submit the binary form of your integer value, "
                        + "but keep in mind the last 64 bits is allocated for this step, "
                        + "the real answer will be padded with zeros until it contains 64 bits, "
                        + "but you will not be expected to do that here. "
                        + "Feel free to click the hint button for the answer to a message with 1 character for reference, "
                        + "then try different lengths yourself.</p>"
                        + "</body>"
                        + "</html>");
        descriptionTextPane.setEditable(false);
        descriptionTextPane.setBackground(null);
        descriptionTextPane.setBorder(null);
    }

    /**
     * Initializes the question label
     */
    private void setupQuestionLabel() {
        question = "";
        questionLabel = new JLabel("");
    }

    /**
     * Initializes the response area and its scroll pane
     */
    private void setupResponseArea() {
        responseTextArea = new JTextArea(3, 20);
        responseTextArea.setLineWrap(true); // Enable line wrapping
        responseTextArea.setWrapStyleWord(true); // Wrap lines at word boundaries
        responseTextArea.setEnabled(false); // Text area disabled at initialization

        responseScrollPane = new JScrollPane(responseTextArea);
        responseScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // Enable vertical
                                                                                                 // scrolling
    }

    /**
     * Initializes the feedback area and its scroll pane
     */
    private void setupFeedbackArea() {
        feedbackArea = new JTextArea(3, 20);
        feedbackArea.setEditable(false);
        feedbackArea.setBackground(null);
        feedbackArea.setLineWrap(true); // Enable line wrapping
        feedbackArea.setWrapStyleWord(true); // Wrap lines at word boundaries
        feedbackScrollPane = new JScrollPane(feedbackArea);
        feedbackScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // Enable vertical
                                                                                                 // scrolling
    }

    /**
     * Initializes the components for inputting the message length. This method
     * creates and configures a JLabel and a JTextField where users can specify
     * the length of the message they want to encode. The JTextField is
     * initialized with a default value of "1" and is set to align text
     * centrally.
     */
    private void setupMessageLengthInput() {
        messageLengthLabel = new JLabel("Message Length:");
        messageLengthField = new JTextField("1", 5); // Default length 1, adjust size as needed
        messageLengthField.setHorizontalAlignment(JTextField.CENTER);
    }

    /**
     * Creates and returns a JPanel dedicated to setting the message length.
     *
     * @return A JPanel containing components for message length input, arranged
     *         vertically.
     */
    private JPanel createMessageLengthPanel() {
        JPanel messageLengthPanel = new JPanel();
        messageLengthPanel.add(messageLengthLabel);
        messageLengthPanel.add(messageLengthField);
        messageLengthPanel.setLayout(new BoxLayout(messageLengthPanel, BoxLayout.Y_AXIS));
        return messageLengthPanel;
    }

    /**
     * Creates the label for the instructions that the user may need.
     */
    private void setupInstructionLabel() {
        instructionsLabel = new JLabel("Please submit your answer here. Do NOT pad your answer with zero's.");
        instructionsLabel.setHorizontalAlignment(JLabel.CENTER);
    }

    /**
     * Display the current tutoring session model in this view. Is called when
     * the step button is clicked automatically, and is called again from the
     * tutor when a new example is created or a step has been completed.
     */
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
     * Defines each view classes' standard method for updating in the Practice View
     */
    @Override
    protected void updatePracticeView() {

        // If check and hint buttons are disabled, reset listenerers and apply those
        // used by this view
        if (!checkHintEnabled) {
            resetButtonListeners(); // Clear any listeners applied from other views
        }

        /*
         * When switching between steps, the current step will be the previous enum
         * that a example was created for. If that enums related stepobject has
         * similar variables, their may be a conflict causing a error.
         */
        StepSubType type = StepSubType.ADD_MSG_LENGTH;

        System.out.println("Message Length update display called"); // Error checking

        Step step = model.currentTask().getCurrentStep().getStep(); // will be the last step a example was created for.

        System.out.println("Message Length substep from current step: " + step.getSubType()); // Error checking.
        if (step.getSubType() == StepSubType.ADD_MSG_LENGTH) {
            MessageLenStep newMessageLenObject = gson.fromJson(step.getData(), MessageLenStep.class); // Issues can
                                                                                                      // happen here if
                                                                                                      // the class
                                                                                                      // contains
                                                                                                      // similar named
                                                                                                      // variables

            // Clear any existing feedback and response from the previous question.
            feedbackArea.setText("");
            responseTextArea.setText("");

            if (type == step.getSubType()) { // prevents data assignment issues if subtype is for a different class.

                this.question = newMessageLenObject.getQuestion();

                if (this.question == null) { // A example hasnt been created yet
                    questionLabel.setText("Please click new example button to get started");
                    checkButton.setEnabled(false);
                    responseTextArea.setEnabled(false);
                    hintButton.setEnabled(false);
                } else { // subtype matches and a example was already made
                    questionLabel.setText(String.format("Calculate the total number of bits your message contains "
                            + "(every character is 8 bits) then convert that total to binary: %s", question));
                    checkButton.setEnabled(true);
                    responseTextArea.setEnabled(true);
                    hintButton.setEnabled(true);
                }
            } else { // Subtype differs, need to create a new example to correctly set it.
                questionLabel.setText("Please click new example button to get started");
                checkButton.setEnabled(false);
                responseTextArea.setEnabled(false);
                hintButton.setEnabled(false);
            }
        }

        // Hide feedback panel if not used (fixes grey box visibility issue)
        if (feedbackArea.getText().trim().isEmpty()) {
            feedbackScrollPane.setVisible(false);
        } else {
            feedbackScrollPane.setVisible(true);
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
     * This method is suppose to be called when the new example button is
     * clicked, it will assign related data pertaining to this step to the
     * related class, then send that class to the tutor to handle generating a
     * question and answer. Once the example is created by the tutor, the update
     * view for this step is called by the tutor.
     *
     * @return
     */
    @Override
    public NewExampleRequest newRequest() {
        System.out.println("new Mess Len request called"); // Error checking

        NewExampleRequest ex = new NewExampleRequest(); // Will be sent to the tutor.

        ex.setExampleType(ProblemType.ADD_MSG_LENGTH);

        MessageLenStep newMessageLenStep = new MessageLenStep(); // New MessageLenStep class object to use for the
                                                                 // question and answer.

        newMessageLenStep.setMessageLength(Integer.parseInt(messageLengthField.getText().trim())); // Number of
                                                                                                   // characters the
                                                                                                   // question should
                                                                                                   // be.

        System.out.println(newMessageLenStep); // Error checking

        ex.setData(gson.toJson(newMessageLenStep));

        return ex;
    }

    /**
     * This method is suppose to be called when the check button is clicked, it
     * should take the users answer, assign it to the related class, then send
     * it to the tutor to handle checking the answer and then will handle a new
     * GUI for the user to view.
     *
     * @return
     */
    @Override
    public StepCompletion stepCompletion() {
        System.out.println("Message Len step completion called"); // Error checking

        Step currentStep = model.currentTask().currentStep().getStep();

        MessageLenStep completedMessageLenStep = gson.fromJson(currentStep.getData(), MessageLenStep.class); // Assigns
                                                                                                             // the
                                                                                                             // class
                                                                                                             // with the
                                                                                                             // data
                                                                                                             // assigned
                                                                                                             // while
                                                                                                             // creating
                                                                                                             // the
                                                                                                             // example.

        String userResponse = this.responseTextArea.getText().replaceAll("\\s", ""); // Gets the users answer and
                                                                                     // removes spaces

        completedMessageLenStep.setUserAnswer(userResponse);

        StepCompletion step = new StepCompletion(currentStep, gson.toJson(completedMessageLenStep));

        step.setStep(currentStep); // Will be sent to the tutor.

        return step;
    }
}
