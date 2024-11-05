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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.regis.shatu.model.Pad0Step;
import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.ExampleType;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.view.act.NewExampleAction;
import edu.regis.shatu.view.act.StepCompletionAction;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A view that requests the student to figure out the number of zeros needed to 
 * Pad a SHA256 message.
 * 
 * @author rickb
 */
public class Pad0View extends UserRequestView implements ActionListener {
    
    private JTextPane descriptionTextPane;
    private JLabel questionLabel, instructionsLabel, messageLengthLabel;
    private JTextField messageLengthField;
    private JTextArea responseArea;
    private JTextArea feedbackArea;
    private JButton checkButton, nextButton, hintButton;
    private JTable asciiTable;
    private JScrollPane responseScrollPane, asciiTableScrollPane, feedbackScrollPane;
    private String question;
    private boolean wasHintRequested = false;
    
    /**
     * Initialize this view including creating and laying out its child components.
     */
    public Pad0View() { 
        
        initializeComponents();
        initializeLayout();
    }
    
    /**
     * Responds to actions performed in the view, specifically button presses,
     * and delegates to appropriate methods for handling.
     * 
     * @param event the event that triggered the action listener
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == checkButton) {
            submitAnswer();           
        } else if (event.getSource() == nextButton) {
            prepareNextQuestion();
        } else if (event.getSource() == hintButton) {
            requestHint();
        }
    }
    

    /**
     * Initializes all GUI components, setting up their properties and configurations.
     */
    private void initializeComponents() {  
        setupDescriptionSection();
        setupQuestionLabel();
        setupInstructionLabel();
        setupMessageLengthInput();
        setupResponseArea();
        setupFeedbackArea();
        setupButtons();
        setupAsciiTable();
    }
    
    /**
     * Lays out the initialized components on the panel using GridBagLayout 
     * constraints.
     */
    private void initializeLayout() {
        
        JPanel buttonPanel = createButtonPanel();  
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
        addc(buttonPanel, 0, 6, 1, 1, 
                1.0, 1.0, GridBagConstraints.CENTER, 
                GridBagConstraints.NONE, 10, 0, 0, 0);
    }  
    
    /**
     * Submit the student's answer to the tutor.  Currently is suppose to just do
     * a error check, when the check button is clicked, its suppose to call
     * the stepCompletion method that calls to the tutor and lets the tutor
     * handle checking the answer.  This method may be removed later in development.
     */
    public void submitAnswer() {
        
        if (this.responseArea.getText().equals("")) {
            this.feedbackArea.setText("Please provide an answer");
        }
        else {
            // Do nothing, tutor should be handling everything, but will leave incase a use can be found in development.
        }
    }
    
    /**
     * This method use to be called when the new example button is clicked, 
     * the tutor is suppose to handle creating a new example/question so this
     * method may be outdated, leaving in-case a use can be found in development,
     * but may no longer be needed.
     */
    private void prepareNextQuestion() {
        // Do nothing, tutor should be handling things, will leave incase a use
        // could be found during development.
    }
    
    /**
     * Gives the student a hint and adds the ASCII table to the view, rest 
     * should be handles by the tutor, maybe all of it should?  Adjust as development
     * continues.
     */
    public void requestHint() {
        
        //Adjust the hint as needed
        this.feedbackArea.setText("Hint: Remember, the message must be padded to"
                + " ensure its length is a multiple of 512 bits.  The message needs "
                + "to be padded until its length is 448 (leaving room for the final "
                + "64 bits in the message length step), including the binary coversion "
                + "and the appended 1 from the previous step. "
                + "Check the ASCII Table to the right for guidance, but for this step, "
                + "its not needed, each character is a byte, so take each byte, "
                + "add 1 more bit to your total, then subtract that from 448, "
                + "that answer would be the number of zeros you need to pad with.");
        
        if (!this.isAncestorOf(asciiTableScrollPane)) { // If the ASCII table isnt in the view, add it.
            addc(asciiTableScrollPane, 3, 0, GridBagConstraints.REMAINDER,
                    7, 2.0, 1.0, GridBagConstraints.CENTER, 
                    GridBagConstraints.BOTH, 5, 5, 5, 5);
            
            this.wasHintRequested = true;
        }
        
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
                    "<html>" +
                    "<body>" +
                    "<h2>Padding with 0's (Zeros)</h2>" +
                    "<p>For this step, you need to pad the Sha256 message with zeros. <br> "
                            + "1: Remember, the Sha256 message is a multiple of 512 bits <br>"
                            + "2: The last 64 bits are for the message length that will be calculated in the next step. <br>"
                            + "3: 512 - 64 = 448 bits, that means your message for this step"
                            + " needs to be 448 bits long. <br>"
                            + "4: With that in mind, how many bits is the question "
                            + "(dont forget to add 1 bit like you learned in the previous step). <br>"
                            + "5: Maybe subtract that from 448 to get your answer?</p>" +
                    "</body>" +
                    "</html>"
            );
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
        responseArea = new JTextArea(3, 20);
        responseArea.setLineWrap(true); // Enable line wrapping
        responseArea.setWrapStyleWord(true); // Wrap lines at word boundaries
        responseScrollPane = new JScrollPane(responseArea);
        responseScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // Enable vertical scrolling
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
        feedbackScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // Enable vertical scrolling
    }
    
    /**
     * Initializes the submit, next, and hint buttons and sets up action listeners
     */
    private void setupButtons() {
        checkButton = new JButton(StepCompletionAction.instance());
        checkButton.addActionListener(this);

        hintButton = new JButton("Hint"); // Needs to be adjusted once the tutor can handle hints.
        hintButton.addActionListener(this);

        nextButton = new JButton(NewExampleAction.instance());
        nextButton.addActionListener(this);
    }
    
    /**
     * Creates and returns a JPanel containing the action buttons with a FlowLayout
     * @return JPanel containing the action buttons
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(checkButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(hintButton);
        return buttonPanel;
    }
    
    /**
     * Initializes the components for inputting the message length. This method creates and configures
     * a JLabel and a JTextField where users can specify the length of the message they want to encode.
     * The JTextField is initialized with a default value of "1" and is set to align text centrally.
     */
    private void setupMessageLengthInput() {
        messageLengthLabel = new JLabel("Message Length:");
        messageLengthField = new JTextField("1", 5);  // Default length 1, adjust size as needed
        messageLengthField.setHorizontalAlignment(JTextField.CENTER);
    }
    
    /**
     * Creates and returns a JPanel dedicated to setting the message length. 
     *
     * @return A JPanel containing components for message length input, arranged vertically.
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
        instructionsLabel = new JLabel("Please submit your answer here.");
        instructionsLabel.setHorizontalAlignment(JLabel.CENTER);
    }
    
    /**
     * Initializes the ASCII table and its scroll pane
     */
    private void setupAsciiTable() {
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Decimal", "Binary", "Symbol"}, 0);
        fillAsciiTable(tableModel); // Method to fill table data
        asciiTable = new JTable(tableModel);
        configureAsciiTable(); // Method to configure table appearance
        asciiTableScrollPane = new JScrollPane(asciiTable);
        asciiTableScrollPane.setPreferredSize(new Dimension(350, 400));
    }
    
    /**
     * Fills the ASCII table with the decimal, binary, hexadecimal and symbol representation
     * of printable ASCII characters
     * 
     * @param tableModel the filled ASCII table
     */
    private void fillAsciiTable(DefaultTableModel tableModel) {
        for (char i = 32; i < 127; i++) {
            tableModel.addRow(new Object[]{
                Integer.toString(i), // Decimal representation
                String.format("%8s", Integer.toBinaryString(i)).replaceAll(" ", "0"), // Binary representation
                i == 32 ? "<SPACE>" : String.valueOf(i) // Symbol representation, with special handling for the space character
            });
        }
    }
    
    /**
     * Configures the appearance of the ASCII table 
     */
    private void configureAsciiTable() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int columnIndex = 0; columnIndex < asciiTable.getColumnCount(); columnIndex++) {
            asciiTable.getColumnModel().getColumn(columnIndex).setCellRenderer(centerRenderer);
        }
    }
    
    /**
     * Display the current tutoring session model in this view.  Is called when
     * the step button is clicked automatically, and is called again from
     * the tutor when a new example is created or a step has been completed.
     */
    @Override
    protected void updateView() {
        
        /*
        When switching between steps, the current step will be the previous enum
        that a example was created for.  If that enums related stepobject has
        similar variables, their may be a conflict causing a error.
        */
        StepSubType type = StepSubType.PAD_ZEROS;
        
        System.out.println("Pad Zero update display called"); // Error checking
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create(); // May not be needed here.
        
        Step step = model.currentTask().getCurrentStep(); // will be the last step a example was created for.
        
        System.out.println("Pad 0 View substep from current step: " + step.getSubType()); // Error checking.
        
        Pad0Step newPadZeroObject = gson.fromJson(step.getData(), Pad0Step.class); // Issues can happen here if the class contains similar named variables
        
        // Clear any existing feedback and response from the previous question.
        feedbackArea.setText("");
        responseArea.setText("");
        
        if (type == step.getSubType()) { // prevents data assignment issues if subtype is for a different class.
            
            this.question = newPadZeroObject.getQuestion();
        
            if (this.question == null) { // A example hasnt been created yet
                questionLabel.setText("Please click new example button to get started");
                checkButton.setEnabled(false);
                hintButton.setEnabled(false);
            }
        
            else { // subtype matches and a example was already made
                questionLabel.setText(String.format("Calculate the number of zero's "
                        + "needed to pad the following string so it is "
                        + "the proper length (448 bits): %s", question));
                checkButton.setEnabled(true);
                hintButton.setEnabled(true);
            }
        }
        
        else { // Subtype differs, need to create a new example to correctly set it.
                questionLabel.setText("Please click new example button to get started");
                checkButton.setEnabled(false);
                hintButton.setEnabled(false);
        }
        
        if (this.wasHintRequested) { // Removes the ASCII table from the view if exists
            this.remove(this.asciiTableScrollPane);
            this.revalidate(); // Refreshes the view
            this.repaint(); // Refreshes the view
        }
    }

    /**
     * This method is suppose to be called when the new example button is clicked,
     * it will assign related data pertaining to this step to the related class,
     * then send that class to the tutor to handle generating a question and answer.
     * Once the example is created by the tutor, the update view for this step is
     * called by the tutor.
     * 
     * @return
     */
    @Override
    public NewExampleRequest newRequest() {
        
        NewExampleRequest ex = new NewExampleRequest(); // Will be sent to the tutor.
        
        ex.setExampleType(ExampleType.PAD_ZEROS);
        
        Pad0Step newPad0Step = new Pad0Step(); // New Pad0Step class object to use for the question and answer.
        
        newPad0Step.setMessageLength(Integer.parseInt(messageLengthField.getText().trim())); // Number of characters the question should be.
        
        System.out.println(newPad0Step); // Error checking
        
        ex.setData(gson.toJson(newPad0Step));
        
        return ex;
    }

    /**
     * This method is suppose to be called when the check button is clicked,
     * it should take the users answer, assign it to the related class, then
     * send it to the tutor to handle checking the answer and then will handle
     * a new GUI for the user to view.
     * 
     * @return 
     */
    @Override
    public StepCompletion stepCompletion() {
        
        Step currentStep = model.currentTask().currentStep();
        
        Pad0Step completedPadZeroStep = gson.fromJson(currentStep.getData(), Pad0Step.class); // Assigns the class with the data assigned while creating the example.
        
        String userResponse = this.responseArea.getText().replaceAll("\\s", ""); // Gets the users answer and removes spaces
        
        completedPadZeroStep.setUserAnswer(userResponse);
        
        StepCompletion step = new StepCompletion(currentStep, gson.toJson(completedPadZeroStep));
        
        step.setStep(currentStep); // Will be sent to the tutor.
        
        return step;
    }
}
