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

import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Task;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.EncodeAsciiExample;
import edu.regis.shatu.model.aol.EncodeAsciiStep;
import edu.regis.shatu.model.aol.EncodeAsciiStep.OutputListener;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.ExampleType;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.svc.ShaTuTutor;
import edu.regis.shatu.svc.TutorReply;
import java.awt.Component;
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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.regis.shatu.view.act.NewExampleAction;
import edu.regis.shatu.view.act.StepCompletionAction;
import javax.swing.SwingUtilities;


/**
 * This class represents a view for encoding messages into ASCII bytes.
 * It provides a user interface for inputting text, submitting it for encoding,
 * receiving feedback, and hints on encoding, along with displaying an ASCII 
 * table for reference.
 * 
 * @author rickb
 */
public class EncodeView extends UserRequestView implements ActionListener, KeyListener, EncodeAsciiStep.OutputListener {
    private TutoringSessionView view;
    private TutoringSession model;    
    private JTextPane descriptionTextPane;
    private JLabel questionLabel, instructionsLabel, messageLengthLabel;
    private JTextField messageLengthField;
    private JTextArea responseArea;
    private JTextArea feedbackArea;
    private JButton submitButton, nextButton, hintButton, showHideAsciiTableButton;
    private JTable asciiTable;
    private JScrollPane responseScrollPane, asciiTableScrollPane, feedbackScrollPane;
    private String question;
    private JRadioButton fromDecimalRadioButton, fromBinaryRadioButton, fromHexRadioButton, fromSymbolRadioButton;
    private JRadioButton toDecimalRadioButton, toBinaryRadioButton, toHexRadioButton, toSymbolRadioButton;
    private ButtonGroup fromFormatButtonGroup, toFormatButtonGroup;
    private List<Integer> questionData;
    private JTextField exampleInputField;
    private JButton stepThroughButton;
    private JButton completeOutputButton;
    private JButton submitAsciiButton;
    private JButton newExampleButton;
    private EncodeAsciiStep asciiStep;
    private String lastInput = "";
    private int currentIndex = 0;

    
    // For random character generation
    private static final Random random = new Random();
    
    // Conversion From and To types initialized
    private ConversionType conversionFrom = ConversionType.SYMBOL;
    private ConversionType conversionTo = ConversionType.BINARY; 
    
    /**
     * Constructor initializes the view by setting up components and layout.
     */
    public EncodeView() {
        System.out.println("EncodeView constructor called");
        gson = new GsonBuilder().setPrettyPrinting().create();
        questionData = new ArrayList<>();
        initializeComponents();
        initializeLayout();
        updateToRadioButtonsEnabledState();
        prepareNextQuestion();
        
        // TEMPORARY: Set a model for testing purposes
        setModel(new TutoringSession()); // Replace with an actual session instance if available
    }

    
    /**
     * Enumeration to hold the various conversion types
     */
    public enum ConversionType {
        DECIMAL, BINARY, HEXADECIMAL, SYMBOL;
    }
    
    /**
     * Responds to actions performed in the view, specifically button presses,
     * and delegates to appropriate methods for handling.
     * 
     * @param event the event that triggered the action listener
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == submitButton) {
            handleSubmission();
        } else if (event.getSource() == nextButton) {
            prepareNextQuestion();
        } else if (event.getSource() == hintButton) {
            showHint();
        } else if  (event.getSource() == stepThroughButton) {
            handleStepThroughAscii();
        } else if (event.getSource() == completeOutputButton){
            handleCompleteAsciiConversion();
        } else if (event.getSource() == submitAsciiButton) {
            asciiStepQuestion();
        } else if (event.getSource() == newExampleButton) {
            handleNewAsciiExampleRequest();
    }
    }
    
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
        if (e.getKeyCode() == KeyEvent.VK_ENTER && responseArea.getText().equals("")) {
            feedbackArea.setText("Please provide an answer");
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
    
    private void verifyAnswer() {
      
    }
      
    /**
     * Initializes all GUI components, setting up their properties and configurations.
     */
    private void initializeComponents() {  
        setupDescriptionSection();
        setupRadios();
        setupQuestionLabel();
        setupInstructionLabel();
        setupMessageLengthInput();
        setupResponseArea();
        setupFeedbackArea();
        setupButtons();
        setupAsciiTable();
        setupExampleInputField();
    }

    /**
     * Lays out the initialized components on the panel using GridBagLayout 
     * constraints.
     */
    private void initializeLayout() {
    JPanel buttonPanel = createButtonPanel();
    JPanel asciiButtonPanel = createAsciiButtonPanel();  
    JPanel messageLengthPanel = createMessageLengthPanel();
    JPanel convertFromPanel = createConvertFromRadioPanel();
    JPanel convertToPanel = createConvertToRadioPanel();

    // Add components to the layout
    addc(descriptionTextPane, 0, 0, 3, 1, 
            1.0, 0.0, GridBagConstraints.CENTER, 
            GridBagConstraints.HORIZONTAL, 5, 5, 5, 5);

    // Create and configure examplePanel
    JPanel examplePanel = new JPanel();
    examplePanel.add(new JLabel("Select Simple text to ASCII Example: "));
    examplePanel.add(exampleInputField);  // Use the exampleDropdown component

    // example panel and buttons
    addc(examplePanel, 0, 1, 3, 1,  
         1.0, 0.0, GridBagConstraints.CENTER, 
         GridBagConstraints.HORIZONTAL, 5, 5, 5, 20);
    addc(messageLengthPanel, 0, 2, 1, 1, 
            1.0, 0.0, GridBagConstraints.CENTER, 
            GridBagConstraints.NONE, 5, 5, 5, 5);
    addc(convertFromPanel, 1, 2, 1, 1, 
            1.0, 0.0, GridBagConstraints.CENTER, 
            GridBagConstraints.NONE, 5, 5, 5, 5);
    addc(convertToPanel, 2, 2, 1, 1, 
            1.0, 0.0, GridBagConstraints.CENTER, 
            GridBagConstraints.NONE, 5, 5, 5, 5); 
    addc(questionLabel, 0, 3, 3, 1, 
            1.0, 0.0, GridBagConstraints.CENTER, 
            GridBagConstraints.HORIZONTAL, 5, 5, 5, 5);
    addc(instructionsLabel, 0, 4, 3, 1, 
            1.0, 0.0, GridBagConstraints.CENTER, 
            GridBagConstraints.HORIZONTAL, 5, 5, 5, 5);
    addc(responseScrollPane, 0, 5, 3, 1, 
            1.0, 1.0, GridBagConstraints.CENTER, 
            GridBagConstraints.BOTH, 5, 5, 5, 5);
    addc(feedbackScrollPane, 0, 6, 3, 1, 
            1.0, 1.0, GridBagConstraints.CENTER, 
            GridBagConstraints.BOTH, 5, 5, 5, 5);
    addc(buttonPanel, 0, 7, 3, 1, 
            1.0, 1.0, GridBagConstraints.CENTER, 
            GridBagConstraints.NONE, 10, 0, 0, 0);
    addc(asciiButtonPanel, 0, 8, 3, 1, 
            1.0, 1.0, GridBagConstraints.CENTER, 
            GridBagConstraints.NONE, 10, 0, 0, 0);
    addc(showHideAsciiTableButton, 3, 0, 1, 1, 
            1.0, 0.0, GridBagConstraints.NORTH, 
            GridBagConstraints.HORIZONTAL, 5, 5, 5, 5);
    addc(asciiTableScrollPane, 3, 0, GridBagConstraints.REMAINDER,
            8, 3.0, 1.0, GridBagConstraints.CENTER, 
            GridBagConstraints.BOTH, 5, 5, 5, 5);
}
    /**
    * Sets up the input field for entering the example string
    */
    private void setupExampleInputField() {
    exampleInputField = new JTextField(9);  // Creates a text field with a preferred width
}
    /**
    * Appends the given text to the response area.
    * 
    * @param text The text to append
    */
    public void appendText(String text) {
        responseArea.append(text + "\n");  // Append new text with a newline for readability
    }
    
    /**
    * Handles the logic for checking to see if the user's input 
    * matches the ASCII values for each character in the example string
    */
    private void asciiStepQuestion() {
    String userInput = responseArea.getText().trim();

    //Reset index if the string changed
    if (!exampleInputField.getText().equals(lastInput)) {
        currentIndex = 0;
        lastInput = exampleInputField.getText();
        char firstChar = lastInput.charAt(currentIndex);
        feedbackArea.setText("Please enter the ASCII value for the first"
                + " character: '" + firstChar + "'.");
    }

    //Get the example string from the input field
    String exampleString = exampleInputField.getText();

    //Ensure the current index is within bounds of the string
    if (currentIndex >= exampleString.length()) {
        feedbackArea.setText("All characters have been encoded correctly!");
        return;
    }
    
    //Get the current character based on the current index
    char currentChar = exampleString.charAt(currentIndex);
    String expectedAscii = String.valueOf((int) currentChar);

    //Compare the user's input to the expected ASCII value
    if (userInput.equals(expectedAscii)) {
        feedbackArea.setText("Correct! The "
                + "ASCII value for '" + currentChar + 
                "' is " + expectedAscii + ".");

        //When correct, move to next character
        currentIndex++;

        //Check if there are more characters to encode
        if (currentIndex < exampleString.length()) {
            char nextChar = exampleString.charAt(currentIndex);
            feedbackArea.append("\nPlease enter the ASCII value "
                    + "for the next character: '" + nextChar + "'.");
            responseArea.setText("");
        } else {
            feedbackArea.append("\nAll characters have been encoded correctly!");
        }
    } else {
        feedbackArea.setText("Incorrect. The ASCII value for"
                + " '" + currentChar + "' is not " + userInput + ". Try again.");
    }
}


    /**
    * Handles the creation of a new ASCII example request and updates the
    * UI with the received example.
    */
    private void handleNewAsciiExampleRequest() {
        //Create a new ShaTuTutor object
        System.out.println("In handle new example request."); //debugging
        ShaTuTutor tutor = new ShaTuTutor();
        int length = random.nextInt(6) + 3;
    
        //Prepare the JSON request with EncodeAsciiExample data
        String userInput = exampleInputField.getText();
        EncodeAsciiExample example = new EncodeAsciiExample(userInput);
        example.setStringLength(length);
        String exampleJson = gson.toJson(example);
        NewExampleRequest request = new NewExampleRequest();
        request.setExampleType(ExampleType.ASCII_ENCODE);
        request.setData(exampleJson);
        String jsonRequest = gson.toJson(request);
    
        //Call the backend method using the tutor object
        TutorReply reply = tutor.newExample(jsonRequest);
        Task task = gson.fromJson(reply.getData(), Task.class);
        if (model != null) {
            model.addCurrentTask(task);
            System.out.println("Model updated with new task.");
            } else {
            System.out.println("Warning: model is null when trying to add current task.");
            }
        Step step = task.getSteps().get(0);
        EncodeAsciiStep asciiStep = gson.fromJson(step.getData(), EncodeAsciiStep.class);
        EncodeAsciiExample receivedExample = asciiStep.getExample();

        //Display the example string in the UI
        SwingUtilities.invokeLater(() -> {
        exampleInputField.setText(receivedExample.getExampleString());
        feedbackArea.setText("Please enter the ASCII value for the first "
            + "character: '" + receivedExample.getExampleString().charAt(0) + "'.");
            responseArea.setText("");
        });

        currentIndex = 0;
    }
    
    /**
    * Handles the step-through ASCII conversion, allowing the user/tutor to
    * walk-through the ASCII conversion process with the example provided.
    */
    private void handleStepThroughAscii() {
        String userInput = exampleInputField.getText();
    if (asciiStep == null || !userInput.equals(lastInput)) {
        lastInput = userInput;
        EncodeAsciiExample example = new EncodeAsciiExample(userInput);
        asciiStep = new EncodeAsciiStep();
        asciiStep.setExample(example);
        asciiStep.setMultiStep(true);
    }
      String result = asciiStep.encode();
      feedbackArea.setText(result);  // Set the returned output to the response area
}
    /**
    * Handles the complete ASCII conversion, allowing the user/tutor to
    * show the entire ASCII conversion conveniently. 
    */
    private void handleCompleteAsciiConversion() {
        String userInput = exampleInputField.getText();  // Get user input from text field
        EncodeAsciiExample example = new EncodeAsciiExample(userInput);
        EncodeAsciiStep asciiStep = new EncodeAsciiStep();
        asciiStep.setExample(example);
        asciiStep.setMultiStep(false);  // Set to complete mode
        String result = asciiStep.encode();  // This will complete the ASCII conversion process at once
        feedbackArea.setText(result);
}

    /**
     * Handles the submission of the user's input, comparing it to the expected 
     * result and providing appropriate feedback.
     * 
     * THIS NEEDS UPDATED ONCE MODEL IS CONFIGURED TO HANDLE REQUESTS
     */
    private void handleSubmission() {
        // Trim user input and split it into an array based on spaces.
        String userInput = responseArea.getText().trim();
        String[] userEntries = userInput.split("\\s+");  
        
        // Generate the expected answers from questionData
        List<String> expectedAnswers = questionData.stream()
            .map(value -> convertBasedOnType(value, conversionTo))
            .collect(Collectors.toList());
        
        // Check if the number of user entries matches the number of expected answers.
        if (userEntries.length != expectedAnswers.size()) {
            feedbackArea.setText("Incorrect number of entries. Please ensure your answer matches the expected format.");
        } else {
            // Assume all answers are correct initially.
            boolean allCorrect = true;
            
             // Iterate through each user entry to compare it against the expected answer.
            for (int i = 0; i < userEntries.length; i++) {
                // If a mismatch is found, set allCorrect to false and exit the loop.
                if (!userEntries[i].trim().equalsIgnoreCase(expectedAnswers.get(i).trim())) {
                    allCorrect = false;
                    break;
                }
            }
               
            // If all user entries matched the expected answers, provide positive feedback.
            if (allCorrect) {
                feedbackArea.setText("Correct!");
            } else {
                // List of hints to provide
                List<String> hints = expectedAnswers.stream()
                      .map(answer -> answer.substring(0, 3) 
                            + "...")
                      .collect(Collectors.toList());
                      
                // Otherwise, inform the user that their entries were incorrect, and display hints                
                feedbackArea.setText(String.format("Incorrect. Expected answers start with: \n%s\n\nTry again", 
                String.join(" ", hints)));
                // Disable the submit button to prevent re-submission, and enable the next question button.
                submitButton.setEnabled(true);
                hintButton.setEnabled(false);
                nextButton.setEnabled(true);
            }
        }
    }


    /**
     * Prepares the view for the next question by clearing previous inputs
     * and feedback and generating a new question.
     * 
     * THIS NEEDS UPDATED ONCE MODEL IS CONFIGURED TO HANDLE REQUESTS
     */
    private void prepareNextQuestion() {
        // Clear any existing feedback and response from the previous question.
        feedbackArea.setText("");
        responseArea.setText("");
        // Clear the previous question data.
        questionData.clear();
        
        try {      
            // Parse the desired message length from the input field.
            int messageLength = Integer.parseInt(messageLengthField.getText().trim());
            StringBuilder questionBuilder = new StringBuilder();

            for (int i = 0; i < messageLength; i++) {
                // Generate a random value representing an ASCII character.
                int value = getRandomCharacter();
                // Store the value for later verification.
                questionData.add(value);
                // Convert the value to the format specified by conversionFrom.
                String convertedChar = convertBasedOnType(value, conversionFrom);
                
                // Add a space between elements for readability, except before the first element.
                if (i > 0) {
                    questionBuilder.append(" ");  // Space delimited
                }
                // Append the converted character or value to the question.
                questionBuilder.append(convertedChar);
            }
            
            question = questionBuilder.toString();
            
            // Determine the format of the question based on the conversionFrom type.
            String questionFormat = (conversionFrom == ConversionType.SYMBOL) ? 
                    "character(s)" : conversionFrom.toString().toLowerCase() + " value(s)";
            
            // Update the question label with the new question.
            questionLabel.setText(String.format("Convert the following "
                    + "%s to %s: %s", questionFormat, conversionTo.toString()
                            .toLowerCase(), question));
            
            // Enable the Submit and Hint buttons and disable the Next button, ready for the user's response.
            submitButton.setEnabled(true);
            hintButton.setEnabled(true);
            nextButton.setEnabled(false);
        } catch (NumberFormatException e) {
            // If the message length input is not a valid number, inform the user.
            feedbackArea.setText("Please enter a valid message length.");
        }
    }

    /**
    * Converts an integer input into its representation according to the specified conversion type.
    * This method acts as a central dispatcher that calls specific conversion methods based on the
    * type required, facilitating conversions between different numeral systems or formats.
    *
    * @param input The integer value to be converted.
    * @param type  The type of conversion to perform, as specified by the {@link ConversionType} enum.
    *              This can be one of DECIMAL, BINARY, HEXADECIMAL, or SYMBOL.
    * @return A string representing the converted value of the input. If the conversion type is DECIMAL,
    *         it returns the string representation of the input integer. For BINARY and HEXADECIMAL, it
    *         returns a string of the binary or hexadecimal representation, respectively. For SYMBOL, it
    *         returns the ASCII character corresponding to the integer value. In case of an unrecognized
    *         conversion type, it returns an empty string, which ideally should never happen.
    */
    private String convertBasedOnType(int input, ConversionType type) {
        switch (type) {
            case DECIMAL:
                return "" + input;
            case BINARY:
                return intToBinaryByte(input);
            case HEXADECIMAL:
                return intToHexString(input);
            case SYMBOL:
                return intToSymbol(input);
            default:
                return ""; // Default case, should not happen
        }
    }
    
    /**
     * Converts a integer to its binary representation, padding each character's 
     * binary value to 8 bits
     * 
     * @param value the integer to be converted.
     * @return String the binary representation of the input.
     */
    public String intToBinaryByte(int value) {
        // Convert the integer to a binary string and ensure it's 8 characters long, padding with 0s if necessary.
        return String.format("%8s", Integer.toBinaryString(value & 0xFF)).replace(' ', '0');
    }
    
    /**
    * Converts an integer value to its hexadecimal string representation.
    *
    * @param value the integer value to be converted to a hexadecimal string.
    * @return A string representing the hexadecimal value of the input integer,
    *         formatted to be at least two characters long and in uppercase.
    */
    public String intToHexString(int value) {
        // Convert the integer to a hexadecimal string
        String hexString = Integer.toHexString(value);

        // Ensure the string is at least two characters long, for byte representation
        // Also, convert to uppercase for standard hexadecimal representation
        return String.format("%02X", Integer.parseInt(hexString, 16));
    }
    
    /**
    * Converts an integer value representing an ASCII code to its corresponding
    * character representation. Special handling is implemented for the space
    * character to return a descriptive string "<SPACE>" instead of a blank space.
    *
    * @param value the ASCII code as an integer, which is to be converted to its character symbol.
    * @return A string containing the character symbol corresponding to the ASCII code.
    *         If the character is a space (' '), the string "<SPACE>" is returned.
    */
    public String intToSymbol(int value) {
        // Convert the integer to its corresponding ASCII character
        char symbol = (char) value;

        // Special handling for the space character
        if (symbol == ' ') {
            return "<SPACE>";
        } else {
            return String.valueOf(symbol);
        }
    }

    /**
     * Displays a hint in the feedback area to assist the user.
     */
    private void showHint() {
        // TEMPORARY UNTIL WE LOAD THE HINT FROM THE MODEL
        feedbackArea.setText("Hint: Check the ASCII table to the right for "
            + "the appropriate representation.");
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
                    "<h2>ASCII Encoding</h2>" +
                    "<p>The first step in SHA-256 preprocessing involves " + 
                    "converting the message to its ASCII equivalent. " +
                    "Each character in the message is represented by its " + 
                    "corresponding binary ASCII value.</p>" +
                    "</body>" +
                    "</html>"
            );
        descriptionTextPane.setEditable(false);
        descriptionTextPane.setBackground(null);
        descriptionTextPane.setBorder(null);
    }
    
    /**
     * Initializes the radio buttons and sets up action listeners
     * 
     */
    private void setupRadios() {
        // Radio buttons for convert from selection
        fromDecimalRadioButton = new JRadioButton("Decimal");
        fromBinaryRadioButton = new JRadioButton("Binary");
        fromHexRadioButton = new JRadioButton("Hex");
        fromSymbolRadioButton = new JRadioButton("Symbol", true);
        
        // Radio buttons for convert to selection
        toDecimalRadioButton = new JRadioButton("Decimal");
        toBinaryRadioButton = new JRadioButton("Binary", true);
        toHexRadioButton = new JRadioButton("Hex");
        toSymbolRadioButton = new JRadioButton("Symbol");
        
        // Action listeners for "Convert From" radio buttons
        ActionListener fromListener = e -> {
            JRadioButton source = (JRadioButton) e.getSource();
            updateConversionFrom(source);
            updateToRadioButtonsEnabledState();
            prepareNextQuestion();
        };
        fromDecimalRadioButton.addActionListener(fromListener);
        fromBinaryRadioButton.addActionListener(fromListener);
        fromHexRadioButton.addActionListener(fromListener);
        fromSymbolRadioButton.addActionListener(fromListener);

        // Action listeners for "Convert To" radio buttons
        ActionListener toListener = e -> {
            JRadioButton source = (JRadioButton) e.getSource();
            updateConversionTo(source);
            prepareNextQuestion();
        };
        toDecimalRadioButton.addActionListener(toListener);
        toBinaryRadioButton.addActionListener(toListener);
        toHexRadioButton.addActionListener(toListener);
        toSymbolRadioButton.addActionListener(toListener);

        // Grouping the radio buttons
        fromFormatButtonGroup = new ButtonGroup();
        fromFormatButtonGroup.add(fromDecimalRadioButton);
        fromFormatButtonGroup.add(fromBinaryRadioButton);
        fromFormatButtonGroup.add(fromHexRadioButton);
        fromFormatButtonGroup.add(fromSymbolRadioButton);

        toFormatButtonGroup = new ButtonGroup();
        toFormatButtonGroup.add(toDecimalRadioButton);
        toFormatButtonGroup.add(toBinaryRadioButton);
        toFormatButtonGroup.add(toHexRadioButton);
        toFormatButtonGroup.add(toSymbolRadioButton);
    }
    
    /**
     * Updates the source conversion type based on the selected "Convert From" radio button.
     * This method sets the {@code conversionFrom} variable to the corresponding {@code ConversionType}
     * enum value based on the radio button selected by the user. It is intended to be called by an
     * action listener associated with each "Convert From" radio button.
     *
     * @param source The "Convert From" radio button that triggered the action event, used to
     *               determine the selected source conversion type.
     */
    private void updateConversionFrom(JRadioButton source) {
        if (source == fromDecimalRadioButton) conversionFrom = ConversionType.DECIMAL;
        else if (source == fromBinaryRadioButton) conversionFrom = ConversionType.BINARY;
        else if (source == fromHexRadioButton) conversionFrom = ConversionType.HEXADECIMAL;
        else if (source == fromSymbolRadioButton) conversionFrom = ConversionType.SYMBOL;
    }
    
    /**
     * Updates the target conversion type based on the selected "Convert To" radio button.
     * This method sets the {@code conversionTo} variable to the appropriate {@code ConversionType}
     * enum value corresponding to the selected radio button. It is triggered by the action listener
     * attached to the "Convert To" radio buttons.
     *
     * @param source The radio button that triggered the action event. This parameter is used to
     *               determine which conversion type has been selected by the user.
     */
    private void updateConversionTo(JRadioButton source) {
        if (source == toDecimalRadioButton) conversionTo = ConversionType.DECIMAL;
        else if (source == toBinaryRadioButton) conversionTo = ConversionType.BINARY;
        else if (source == toHexRadioButton) conversionTo = ConversionType.HEXADECIMAL;
        else if (source == toSymbolRadioButton) conversionTo = ConversionType.SYMBOL;
    }
    
    /**
    * Updates the enabled state of the "Convert To" radio buttons based on the 
    * current selection of the "Convert From" radio buttons.
    */
    private void updateToRadioButtonsEnabledState() {
        toDecimalRadioButton.setEnabled(conversionFrom != ConversionType.DECIMAL);
        toBinaryRadioButton.setEnabled(conversionFrom != ConversionType.BINARY);
        toHexRadioButton.setEnabled(conversionFrom != ConversionType.HEXADECIMAL);
        toSymbolRadioButton.setEnabled(conversionFrom != ConversionType.SYMBOL);
        
         // If the currently selected "To" button is now disabled, select a different one
        if (!toDecimalRadioButton.isEnabled() && toDecimalRadioButton.isSelected()) {
            toBinaryRadioButton.setSelected(true);
            conversionTo = ConversionType.BINARY;
        } else if (!toBinaryRadioButton.isEnabled() && toBinaryRadioButton.isSelected()) {
            toDecimalRadioButton.setSelected(true);
            conversionTo = ConversionType.DECIMAL;
        } else if (!toHexRadioButton.isEnabled() && toHexRadioButton.isSelected()) {
            toDecimalRadioButton.setSelected(true);
            conversionTo = ConversionType.DECIMAL;
        } else if (!toSymbolRadioButton.isEnabled() && toSymbolRadioButton.isSelected()) {
            toDecimalRadioButton.setSelected(true);
            conversionTo = ConversionType.DECIMAL;
        }
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
        submitButton = new JButton("Submit");
        nextButton = new JButton("Next");
        hintButton = new JButton("Hint");
        submitButton.addActionListener(this);
        nextButton.addActionListener(this);
        hintButton.addActionListener(this);
        nextButton.setEnabled(false);
        
        submitAsciiButton = new JButton(StepCompletionAction.instance());
        newExampleButton = new JButton(NewExampleAction.instance());
        stepThroughButton = new JButton("Step Through ASCII Conversion");
        completeOutputButton = new JButton("Complete ASCII Conversion");
        newExampleButton.addActionListener(this);
        submitAsciiButton.addActionListener(this); 
        stepThroughButton.addActionListener(this);
        completeOutputButton.addActionListener(this);
        setupAsciiTableToggleButton();
    }    

    /**
     * Creates and returns a JPanel containing the action buttons with a FlowLayout
     * @return JPanel containing the action buttons
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(submitButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(hintButton);
        return buttonPanel;
    }
    
    /**
     * A second JPanel containing action buttons with a FlowLayout
     * @return asciiButtonPanel containing action buttons
     */
    private JPanel createAsciiButtonPanel() {
        JPanel asciiButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        asciiButtonPanel.add(submitAsciiButton);
        asciiButtonPanel.add(newExampleButton);
        asciiButtonPanel.add(stepThroughButton);
        asciiButtonPanel.add(completeOutputButton);
        return asciiButtonPanel;
    }
    
    /**
     * Creates and returns a JPanel containing the "convert from" radio buttons 
     * with a FlowLayout
     * @return JPanel containing the radio buttons
     */
    private JPanel createConvertFromRadioPanel() {
        // Create a panel for the covert from radio buttons
        JPanel formatPanel = new JPanel();
        
        // BoxLayout for vertical alignment
        formatPanel.setLayout(new BoxLayout(formatPanel, BoxLayout.Y_AXIS)); 
        
        // Convert from Label centered
        JLabel fromLabel = new JLabel("Convert From:");
        fromLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 
        
        // Panel for radio buttons
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        radioPanel.add(fromDecimalRadioButton);
        radioPanel.add(fromBinaryRadioButton);
        radioPanel.add(fromHexRadioButton);
        radioPanel.add(fromSymbolRadioButton);
        
        formatPanel.add(fromLabel);  // Add the label to the main panel
        formatPanel.add(radioPanel); // Add the radio buttons panel below the label
        return formatPanel;
    }
    
    /**
     * Creates and returns a JPanel containing the "convert to" radio buttons with 
     * a FlowLayout
     * @return JPanel containing the radio buttons
     */
    private JPanel createConvertToRadioPanel() {
        // Create a panel for the covert to radio buttons
        JPanel formatPanel = new JPanel();
        
        // BoxLayout for vertical alignment
        formatPanel.setLayout(new BoxLayout(formatPanel, BoxLayout.Y_AXIS)); 
        
        // Convert to Label centered
        JLabel toLabel = new JLabel("Convert To:");
        toLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 
        
        // Panel for radio buttons
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        radioPanel.add(toDecimalRadioButton);
        radioPanel.add(toBinaryRadioButton);
        radioPanel.add(toHexRadioButton);
        radioPanel.add(toSymbolRadioButton);
        
        formatPanel.add(toLabel);  // Add the label to the main panel
        formatPanel.add(radioPanel); // Add the radio buttons panel below the label
        return formatPanel;
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
    
    private void setupInstructionLabel() {
        instructionsLabel = new JLabel("Please separate your entries with spaces. "
                + "Note: Represent the space symbol as <SPACE> "
                + "in your answers.");
        instructionsLabel.setHorizontalAlignment(JLabel.CENTER);
    }

    /**
     * Generates and returns a random character from ASCII table values 
     * 
     * @return int the random integer to be returned
     */
    private int getRandomCharacter() {
        // Generates a random integer between 32 (inclusive) and 127 (exclusive)
        int randomInt = 32 + random.nextInt(127 - 32);
        return randomInt; 
    }
    
    /**
     * On button press will show/hide ASCII Table
     */
    private void setupAsciiTableToggleButton() {
    //If button is pressed setup ascii table
    setupAsciiTable();
    
    //Create the Show/Hide button
    showHideAsciiTableButton = new JButton("Hide ASCII Table");
    showHideAsciiTableButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean isCurrentlyVisible = asciiTableScrollPane.isVisible();
            asciiTableScrollPane.setVisible(!isCurrentlyVisible);
            
            //Update button text based on show/hide press
            if (isCurrentlyVisible) {
                showHideAsciiTableButton.setText("Show ASCII Table");
            } else {
                showHideAsciiTableButton.setText("Hide ASCII Table");
            }
        }
    });
}
    
    /**
     * Initializes the ASCII table and its scroll pane
     */
    private void setupAsciiTable() {
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Decimal", "Binary", "Hex", "Symbol"}, 0);
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
                Integer.toHexString(i).toUpperCase(), // Hexadecimal representation
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
     * Sets the TutoringSession model for this view and updates the view based 
     * on the model's data.
     * 
     * @param model the TutoringSession model to set
     */
    public void setModel(TutoringSession model) {
        System.out.println("setModel() called with model: " + model);

        this.model = model;
        
        updateView();
    }
    
        /**
    * Creates a new request for an ASCII encoding example.
    *
    * @return ex containing the encoded ASCII step data.
    */
    public NewExampleRequest newRequest() {
        System.out.println("EncodeView.newRequest() called.");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        NewExampleRequest ex = new NewExampleRequest();
        ex.setExampleType(ExampleType.ASCII_ENCODE);
        EncodeAsciiStep asciiStep = new EncodeAsciiStep();
        ex.setData(gson.toJson(asciiStep));
        
        System.out.println("EncodeView.newRequest() returning: " + ex);
        return ex;
    }
    
    /**
    * Handles the completion of a step by retrieving the current step data
    *
    * @return step object containing the updated step information.
     */
    public StepCompletion stepCompletion() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        Step currentStep = model.currentTask().currentStep();
        EncodeAsciiStep example = gson.fromJson(currentStep.getData(), EncodeAsciiStep.class);
        String userResponse = feedbackArea.getText().replaceAll("\\s", "");
        EncodeAsciiExample newExample = new EncodeAsciiExample(userResponse);
        example.setExample(newExample);
        String encodedResult = example.encode();
   
        feedbackArea.setText(encodedResult);

        StepCompletion step = new StepCompletion(currentStep, gson.toJson(example));
    
        return step;
    }
    
    /**
     * Updates the description, question, and hints from the model
     * 
     * TODO: THIS IS A PLACEHOLDER UNTIl WE HAVE HAVE THE MODEL CODE COMPLETED
     */
    @Override
    protected void updateView() {
        // Ensure 'view' is only initialized when SplashFrame.instance() is non-null
        if (view == null) {
            MainFrame splashFrame = MainFrame.instance();
            if (splashFrame != null) {
                view = SplashFrame.instance().getView(); // Initialize view once SplashFrame is ready
            } else {
                System.err.println("SplashFrame.instance() is null. Cannot initialize 'view'.");
                return; // Exit updateView if the view cannot be initialized
            }
        }

        // Reset button listeners using the initialized view
        if (view != null) {
            view.resetButtonListeners(); // Clear any listeners applied from other views
        }

        // Other update logic here
        System.out.println("UpdateView logic continues...");
    }
} 
