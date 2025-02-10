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

import edu.regis.shatu.model.InitVarStep;
import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.view.act.NewExampleAction;
import edu.regis.shatu.view.act.StepCompletionAction;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Font;


/**
 *
 * @author rickb, Ryley MacLagan
 */
public class InitVarView extends UserRequestView implements ActionListener {
    
    private TutoringSessionView view;
    private InitVarStep initVarStep;
    private JButton showButton, hintButton, checkButton;
    private JTextField h0, h1, h2, h3, h4, h5, h6, h7;
    private JTextArea feedbackTextArea;
    private short hintCount;
    private boolean answersVisible, hintsVisible;
    private JLabel[] answerLabels = new JLabel[8];  // Holds correct answer labels

    public InitVarView() { 
        initializeComponents();
        initializeLayout();
        attachDocumentListeners();
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == checkButton) {
            NewExampleAction.instance().actionPerformed(null);
            boolean allCorrect = initVarStep.allAnswersCorrect();
            
            if(allCorrect){
                showAnswer();
                feedbackTextArea.setText("Correct!");
            }
            else{
                feedbackTextArea.setText("Incorrect. Please try again or use a hint.");
            }
        } else if (event.getSource() == hintButton) {
            showHint();
        }
    }

    private void initializeComponents() {
    initVarStep = new InitVarStep();
    hintCount = 1;
    answersVisible = false;
    hintsVisible = false;

    Dimension textFieldSize = new Dimension(200, 30); // Adjust width and height

    h0 = new JTextField(20); h0.setName("@H0"); h0.setPreferredSize(textFieldSize);
    h1 = new JTextField(20); h1.setName("@H1"); h1.setPreferredSize(textFieldSize);
    h2 = new JTextField(20); h2.setName("@H2"); h2.setPreferredSize(textFieldSize);
    h3 = new JTextField(20); h3.setName("@H3"); h3.setPreferredSize(textFieldSize);
    h4 = new JTextField(20); h4.setName("@H4"); h4.setPreferredSize(textFieldSize);
    h5 = new JTextField(20); h5.setName("@H5"); h5.setPreferredSize(textFieldSize);
    h6 = new JTextField(20); h6.setName("@H6"); h6.setPreferredSize(textFieldSize);
    h7 = new JTextField(20); h7.setName("@H7"); h7.setPreferredSize(textFieldSize);

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

    showButton = new JButton("Show Answer");
    showButton.addActionListener(e -> showAnswer());
    hintButton = new JButton("Hint");
    checkButton = new JButton(StepCompletionAction.instance());
    checkButton.setText("Check");
    }

    private void initializeLayout() {
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();

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

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 3; // Make it span multiple columns for better readability
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(10, 10, 10, 10);
    add(infoLabel, gbc);

    // Labels, input fields, and answer labels
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.WEST;

    JTextField[] inputFields = {h0, h1, h2, h3, h4, h5, h6, h7};
    String[] labels = {"H0:", "H1:", "H2:", "H3:", "H4:", "H5:", "H6:", "H7:"};

    for (int i = 0; i < 8; i++) {
        gbc.gridy = i + 1;

        // Column 1: Label (e.g., "H0:")
        gbc.gridx = 0;
        JLabel label = new JLabel(labels[i]);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        add(label, gbc);

        // Column 2: Input Field
        gbc.gridx = 1;
        inputFields[i].setPreferredSize(new Dimension(150, 25));
        add(inputFields[i], gbc);

        // Column 3: Answer Label (Initially Hidden)
        gbc.gridx = 2;
        answerLabels[i] = new JLabel(""); // Empty initially
        answerLabels[i].setFont(new Font("SansSerif", Font.BOLD, 12));
        answerLabels[i].setForeground(Color.BLUE);
        add(answerLabels[i], gbc);
    }

    // Show Answer Button
    gbc.gridx = 0;
    gbc.gridy = 10;
    gbc.gridwidth = 3;
    gbc.anchor = GridBagConstraints.CENTER;
    JButton showButton = new JButton("Show Answer");
    showButton.addActionListener(e -> showAnswer());
    add(showButton, gbc);
    }

    /**
     * Creates and adds a label for a text field to the GUI.
     * @param labelText references an initial variable.
     * @param textField holds the user's answer.
     * @param gridY The position on the y-axis to place the label and text field.
     */
    private void createLabelForField(String labelText, JTextField textField, int gridY) {
    JLabel label = new JLabel(labelText);  // Ensure label is separate from the text field
    label.setFont(new Font("SansSerif", Font.BOLD, 12));  // Make it bold for visibility

    textField.setPreferredSize(new Dimension(250, 30));  // Set proper size
    textField.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Ensure the font is readable
    textField.setHorizontalAlignment(JTextField.LEFT); // Align text to the left

    GridBagConstraints gbc = new GridBagConstraints();
    
    // Label on the left
    gbc.gridx = 0;
    gbc.gridy = gridY;
    gbc.anchor = GridBagConstraints.WEST; // Align left
    gbc.insets = new Insets(5, 5, 5, 10); // Add spacing
    add(label, gbc);

    // Text field on the right
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add(textField, gbc);
    }

    /**
     * Monitors all text fields for initial variable labels.
     * Sets text color to red until correct.
     * Correct answers are set to a dark green.
     */
    private void attachDocumentListeners() {
        DocumentListener docListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkInput(e);
                if (!checkButton.isEnabled() && !hintButton.isEnabled()){
                    view.toggleButton(checkButton);
                    view.toggleButton(hintButton);
                }
                else if (!checkButton.isEnabled()) {
                    view.toggleButton(checkButton);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkInput(e);
                if(allFieldsEmpty()){
                    checkButton.setEnabled(false);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkInput(e);
            }

            private void checkInput(DocumentEvent e) {
                JTextField sourceField = (JTextField) e.getDocument().getProperty("field");
                String variableName = sourceField.getName();  // Use a unique name or identifier for each JTextField
                String userAnswer = sourceField.getText();
                
                initVarStep.setUserAnswer(variableName, userAnswer);
                
                if (initVarStep.isUserCorrect(variableName)) {
                    sourceField.setForeground(Color.GREEN.darker().darker());
                } else {
                    sourceField.setForeground(Color.RED);
                }
            }
        };

        h0.getDocument().putProperty("field", h0);
        h1.getDocument().putProperty("field", h1);
        h2.getDocument().putProperty("field", h2);
        h3.getDocument().putProperty("field", h3);
        h4.getDocument().putProperty("field", h4);
        h5.getDocument().putProperty("field", h5);
        h6.getDocument().putProperty("field", h6);
        h7.getDocument().putProperty("field", h7);

        h0.getDocument().addDocumentListener(docListener);
        h1.getDocument().addDocumentListener(docListener);
        h2.getDocument().addDocumentListener(docListener);
        h3.getDocument().addDocumentListener(docListener);
        h4.getDocument().addDocumentListener(docListener);
        h5.getDocument().addDocumentListener(docListener);
        h6.getDocument().addDocumentListener(docListener);
        h7.getDocument().addDocumentListener(docListener);
    }
    
    /**
     * Adds a new row at the specified x and y values.
     * @param labelText first field in each row, the label for the text field
     * @param textField second field in each row, the text field accepting user input
     * @param correctAnswer third field in each row, the correct answer for this row
     * @param gridX the x coordinate for the row
     * @param gridY the y coordinate for the row
     */
    private void addRowWithAnswer(String labelText, JTextField textField, String correctAnswer, int gridX, int gridY) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); // 5px horizontal gap, no vertical gap
        JLabel label = new JLabel(labelText);
        JLabel answerLabel = new JLabel(correctAnswer);
        answerLabel.setForeground(Color.BLUE); // Set color for differentiation

        rowPanel.add(label);
        rowPanel.add(textField);
        rowPanel.add(answerLabel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(rowPanel, gbc);
    }
    
    /**
     * Adds a label containing an answer for its associated variable and text field.
     */
    private void addAnswerLabels() {
        addRowWithAnswer("H0: ", h0, "Correct: " + initVarStep.getAnswer("@H0"), 0, 1);
        addRowWithAnswer("H1: ", h1, "Correct: " + initVarStep.getAnswer("@H1"), 0, 2);
        addRowWithAnswer("H2: ", h2, "Correct: " + initVarStep.getAnswer("@H2"), 0, 3);
        addRowWithAnswer("H3: ", h3, "Correct: " + initVarStep.getAnswer("@H3"), 0, 4);
        addRowWithAnswer("H4: ", h4, "Correct: " + initVarStep.getAnswer("@H4"), 0, 5);
        addRowWithAnswer("H5: ", h5, "Correct: " + initVarStep.getAnswer("@H5"), 0, 6);
        addRowWithAnswer("H6: ", h6, "Correct: " + initVarStep.getAnswer("@H6"), 0, 7);
        addRowWithAnswer("H7: ", h7, "Correct: " + initVarStep.getAnswer("@H7"), 0, 8);
    }
    
    /**
     * Displays answers next to their corresponding variable and text field.
     */
    private void showAnswer() {
    removeHints(); // Ensure hints don't interfere

    if (answersVisible) {
        // Hide answers
        for (int i = 0; i < 8; i++) {
            answerLabels[i].setText(""); // Clear answer labels
        }
        answersVisible = false;
    } else {
        // Show correct answers next to input fields
        for (int i = 0; i < 8; i++) {
            answerLabels[i].setText("Correct: " + initVarStep.getAnswer("@H" + i));
        }
        answersVisible = true;
    }

    revalidate();
    repaint();
    }
    
    /**
     * Displays hints to the GUI for their corresponding variable's row.
     * @param initVar the targeted variable's text field
     * @param variableName the targeted variable's label
     * @param hintLevel the level of hint (1st, 2nd, or 3rd hint)
     * @param hintY the position on the y-axis to place the hint
     */
    private void addHints(JTextField initVar, String variableName, int hintLevel, int hintY) {
        // Retrieve the hint for the specific variable and level
        String hint = initVarStep.getHint(variableName, hintLevel);

        // Only add the hint if it’s not null or empty
        if (hint != null && !hint.isEmpty()) {
            addRowWithAnswer(variableName + ": ", initVar, "Hint: " + hint, 1, hintY);
        }
    }
    
    /**
     * Called when hint button is pressed.
     * Determines if hints should be displayed to the user.
     */
    private void showHint() {
        if (hintsVisible) {
            removeHints();
            hintsVisible = false;
        } else {
            removeExistingAnswerLabels();
            if (hintCount > 3) hintCount = 1;

            // Pass specific variable names to addHints for each text field
            addHints(h0, "H0", hintCount, 1);
            addHints(h1, "H1", hintCount, 2);
            addHints(h2, "H2", hintCount, 3);
            addHints(h3, "H3", hintCount, 4);
            addHints(h4, "H4", hintCount, 5);
            addHints(h5, "H5", hintCount, 6);
            addHints(h6, "H6", hintCount, 7);
            addHints(h7, "H7", hintCount, 8);

            hintCount++;
            hintsVisible = true;
        }
        revalidate();
        repaint();
    }
    
    /**
     * Removes all generated answer labels from the GUI
     */
    private void removeExistingAnswerLabels() {
        // Remove only labels that start with "Correct:" from each row JPanel
        for (int i = 0; i < getComponentCount(); i++) {
            if (getComponent(i) instanceof JPanel) {
                JPanel rowPanel = (JPanel) getComponent(i);
                for (int j = rowPanel.getComponentCount() - 1; j >= 0; j--) {
                    if (rowPanel.getComponent(j) instanceof JLabel) {
                        JLabel label = (JLabel) rowPanel.getComponent(j);
                        if (label.getText().startsWith("Correct:")) {
                            rowPanel.remove(label); // Remove only the correct answer label
                        }
                    }
                }
            }
        }
    }
    
    // Removes all generated hints from the GUI
    private void removeHints() {
        // Remove only labels that start with "Hint:" from each row JPanel
        for (int i = 0; i < getComponentCount(); i++) {
            if (getComponent(i) instanceof JPanel) {
                JPanel rowPanel = (JPanel) getComponent(i);
                for (int j = rowPanel.getComponentCount() - 1; j >= 0; j--) {
                    if (rowPanel.getComponent(j) instanceof JLabel) {
                        JLabel label = (JLabel) rowPanel.getComponent(j);
                        if (label.getText().startsWith("Hint:")) {
                            rowPanel.remove(label); // Remove only the correct hint label
                        }
                    }
                }
            }
        }
    }
    
    /**
    * Checks if all text fields are empty.
    * 
    * @return true if all fields are empty, false otherwise.
    */
    public boolean allFieldsEmpty() {
        return h0.getText().trim().isEmpty() &&
               h1.getText().trim().isEmpty() &&
               h2.getText().trim().isEmpty() &&
               h3.getText().trim().isEmpty() &&
               h4.getText().trim().isEmpty() &&
               h5.getText().trim().isEmpty() &&
               h6.getText().trim().isEmpty() &&
               h7.getText().trim().isEmpty();
    }
    
    /**
     * Initializes Buttons for this view
     * Attaches all unique listeners for this view
     */
    private void setupButtons() {
        // Initializes Buttons
        showButton = view.initializeButton("Show Answer");
        hintButton = view.getHintButton();
        hintButton.addActionListener(this);
        checkButton = view.getCheckButton();
        checkButton.addActionListener(this);
    }
    
    @Override
    protected void updateView() {
        view = SplashFrame.instance().getTutoringSessionView(); // Accessing view to use universal buttons
        view.resetButtonListeners(); // Clear any listeners applied from other views
        feedbackTextArea.setText(""); // Resets text feedback area      
        setupButtons();
        
        // New example is uniquely hidden for this view, as
        // There are only 8 initial values, 
        // all of which the user shall define.          
        view.getNewExampleButton().setEnabled(false);   
        
        if (model == null) {
            System.out.println("Error: The model is null when switching to Initialize Variables...");
        }
        else {
            // TODO: Debug statements. Task is not being set properly.
            // The model's tasks list holds only the first task. It must be populated with each task.
            // This should originate from a lack of data within the database. 
            // Populating it should aid in resolving this error.
            System.out.println("Initialize update view called.");
            System.out.println("----Init Var Task Title-----"+model.currentTask().getTask().getTitle());
            System.out.println("----Init Var Step Title-----"+model.currentTask().getCurrentStep().getStep().getTitle());        
        }
    }

    @Override
    public NewExampleRequest newRequest() {
        NewExampleRequest ex = new NewExampleRequest();
        
        //Set example type to the problem associated with the current view
        ex.setExampleType(ProblemType.INITIALIZE_VARS);
        
        return ex;
    }

    @Override
    public StepCompletion stepCompletion() {
        Step currentStep = model.currentTask().currentStep().getStep();

        // Populate InitVarStep with user input
        InitVarStep completedInitVarStep = new InitVarStep();
        completedInitVarStep.setUserAnswer("@H0", h0.getText().trim());
        completedInitVarStep.setUserAnswer("@H1", h1.getText().trim());
        completedInitVarStep.setUserAnswer("@H2", h2.getText().trim());
        completedInitVarStep.setUserAnswer("@H3", h3.getText().trim());
        completedInitVarStep.setUserAnswer("@H4", h4.getText().trim());
        completedInitVarStep.setUserAnswer("@H5", h5.getText().trim());
        completedInitVarStep.setUserAnswer("@H6", h6.getText().trim());
        completedInitVarStep.setUserAnswer("@H7", h7.getText().trim());

        // Create StepCompletion with serialized JSON of dataWrapper
        StepCompletion stepCompletion = new StepCompletion(currentStep, gson.toJson(completedInitVarStep));
        stepCompletion.setStep(currentStep);
        return stepCompletion;
    }
}