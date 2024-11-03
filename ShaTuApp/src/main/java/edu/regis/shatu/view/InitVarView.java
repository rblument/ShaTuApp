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

import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.NewExampleRequest;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author rickb, Ryley MacLagan
 */
public class InitVarView extends UserRequestView implements ActionListener {
    
    // Defines the show answer and hint buttons
    private JButton showButton, hintButton;
    
    // Defines the text fields associated with the inital variable labels
    private JTextField h0, h1, h2, h3, h4, h5, h6, h7;
    
    // Tracks the number of times the hintButton has been pressed
    private short hintCount;
    
    private boolean answersVisible, hintsVisible;
    
    // Defines a map to associate text fields with their correct answers
    final private Map<JTextField, String> fieldValues = new HashMap<>();
    
    // Defines maps to associate text fields with their correct hints
    final private Map<JTextField, String> hint1 = new HashMap<>();
    final private Map<JTextField, String> hint2 = new HashMap<>();
    final private Map<JTextField, String> hint3 = new HashMap<>();
    
    
    /**
     * Initialize this view including creating and laying out its child components.
     */
    public InitVarView() {       
        initializeComponents();
        initializeLayout();
        attachDocumentListeners();
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
      // Do not know what this method is for. 
    }
 
    /**
     * Create the child GUI components appearing in this frame.
     */
    private void initializeComponents() {
        hintCount = 1;
        answersVisible = false;
        hintsVisible = false;
        
     
        h0 = new JTextField(13);
        h1 = new JTextField(13);
        h2 = new JTextField(13);
        h3 = new JTextField(13);
        h4 = new JTextField(13);
        h5 = new JTextField(13);
        h6 = new JTextField(13);
        h7 = new JTextField(13);
        
        // Map text fields with their correct answers to enable automated checking
        fieldValues.put(h0, "0x6a09e667");
        fieldValues.put(h1, "0xbb67ae85");
        fieldValues.put(h2, "0x3c6ef372");
        fieldValues.put(h3, "0xa54ff53a");
        fieldValues.put(h4, "0x510e527f");
        fieldValues.put(h5, "0x9b05688c");
        fieldValues.put(h6, "0x1f83d9ab");
        fieldValues.put(h7, "0x5be0cd19");
        
        hint1.put(h0, "First prime number is 2");
        hint1.put(h1, "Second prime number is 3");
        hint1.put(h2, "Third prime number 5");
        hint1.put(h3, "Fourth prime number 7");
        hint1.put(h4, "Fifth prime number 11");
        hint1.put(h5, "Sixth prime number 13");
        hint1.put(h6, "Seventh prime number 17");
        hint1.put(h7, "Eighth prime number 19");
        
        hint2.put(h0, "Fractional part is 0.414213562");
        hint2.put(h1, "Fractional part is 0.732050807");
        hint2.put(h2, "Fractional part is 0.236067977");
        hint2.put(h3, "Fractional part is 0.645751311");
        hint2.put(h4, "Fractional part is 0.316624790");
        hint2.put(h5, "Fractional part is 0.605551275");
        hint2.put(h6, "Fractional part is 0.123105625");
        hint2.put(h7, "Fractional part is 0.358898944");
        
        hint3.put(h0, "Convert 01101010000010011110011001100110 to Hexadecimal");
        hint3.put(h1, "Convert 10111011011001111010111010000011 to Hexadecimal");
        hint3.put(h2, "Convert 00111100011011101111001101110000 to Hexadecimal");
        hint3.put(h3, "Convert 10100101010011111111010100111010 to Hexadecimal");
        hint3.put(h4, "Convert 01010001000011100101001001111110 to Hexadecimal");
        hint3.put(h5, "Convert 10011011000001010110100010001010 to Hexadecimal");
        hint3.put(h6, "Convert 00011111100000111101100110101001 to Hexadecimal");
        hint3.put(h7, "Convert 01011011111000001100110100011011 ​to Hexadecimal");
        
        showButton = new JButton("Show Answer");
        hintButton = new JButton("Hint");
    }
    
    /**
     * Layout the child components in this view.
     */
    private void initializeLayout() {
        String description = "<html>In SHA-256, the algorithm begins with a set of "
                + "initial hash values, which are specifically chosen constants.<br>"
                + "These constants are derived from the first 32 bits of the "
                + "fractional parts of the square roots of the first 8 prime numbers.<br>"
                + "They serve as the starting points for the hash computation and "
                + "ensure that the algorithm starts from a random-like state.<br>"
                + "<br>Please enter the inital hash values in hexadecimal for H0 to H7 Below:<br>"
                + "(Incorrect answers are in red, and turn green when correct.)<br></html>";
        
        // Label for description
        JLabel infoLabel = new JLabel(description);
        addc(infoLabel, 0, 0, 2, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                5, 5, 5, 5);
        
        // Setup labels and associate them with their corresponding text fields
        createLabelForField("H0: ", h0, 1);
        createLabelForField("H1: ", h1, 2);
        createLabelForField("H2: ", h2, 3);
        createLabelForField("H3: ", h3, 4);
        createLabelForField("H4: ", h4, 5);
        createLabelForField("H5: ", h5, 6);
        createLabelForField("H6: ", h6, 7);
        createLabelForField("H7: ", h7, 8);
        
        // Creates a panel with FlowLayout to hold the Show Answer and Hint buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        buttonPanel.add(showButton);
        buttonPanel.add(hintButton);

        // Sets action listeners for the buttons
        showButton.addActionListener((ActionEvent e) -> showAnswer());
        hintButton.addActionListener(e -> {
         hint();  
        }
         );

        // Adds the button panel to the main layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2; // Span both columns to center the panel
        gbc.anchor = GridBagConstraints.WEST;
        add(buttonPanel, gbc); // Add button panel instead of individual buttons
    }
    
     /**
     * Generates a panel with a label and an adjacent text field, arranged using FlowLayout.
     * The panel is then added to the main layout.
     *
     * @param labelText the label to be displayed
     * @param textField text field associated with the label
     * @param gridY the y position in the main layout
     */
    private void createLabelForField(String labelText, JTextField textField, int gridY) {
        // Creates a label and associate it with the text field
        JLabel label = new JLabel(labelText);
        label.setLabelFor(textField);
        textField.setOpaque(true); // Allows text field background color to be set

        // Associates label with text field for later use in checkInput
        textField.putClientProperty("label", label);

        // Creates a panel with FlowLayout to contain label and text field
        JPanel rowPanel = new JPanel(new FlowLayout(5, 5, 0));
        rowPanel.add(label);
        rowPanel.add(textField);

        // Adds the row panel to the main layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = gridY;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(rowPanel, gbc); // Adding the entire row panel to the main layout
    }

    /**
     * Initializes a document listener, which enables the application to set
     * the text color of all associated text fields based on if they hold a
     * correct or incorrect answer.
     * 
     * Incorrect: text is set to red
     * Correct: text is set to dark green
     */
    private void attachDocumentListeners() {
        DocumentListener docListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkInput(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkInput(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkInput(e);
            }

            private void checkInput(DocumentEvent e) {
                JTextField sourceField = (JTextField) e.getDocument().getProperty("field");
                String expectedValue = fieldValues.get(sourceField);
                JLabel associatedLabel = (JLabel) sourceField.getClientProperty("label");

                if (sourceField.getText().equals(expectedValue)) {
                    sourceField.setForeground(Color.GREEN.darker().darker()); // Green text for correct input
                } else {
                    sourceField.setForeground(Color.RED); // Red text for incorrect input
                }

                associatedLabel.repaint(); // Ensures the label repaints to show the color change
            }

        };

        // Associates the DocumentListener and the "field" property with each text field
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
    
    private void showAnswer() {
        removeHints();
            if (answersVisible) {
            // If answers are already visible, remove them
            removeExistingAnswerLabels();
            answersVisible = false;
        } else {
            // Otherwise, add them
            addAnswerLabels();
            answersVisible = true;
        }

        revalidate();
        repaint();
    }
    
    private void addAnswerLabels() {
        // Add rows for each label, text field, and correct answer
        addRowWithAnswer("H0: ", h0, "Correct: " + fieldValues.get(h0), 0, 1);
        addRowWithAnswer("H1: ", h1, "Correct: " + fieldValues.get(h1), 0, 2);
        addRowWithAnswer("H2: ", h2, "Correct: " + fieldValues.get(h2), 0, 3);
        addRowWithAnswer("H3: ", h3, "Correct: " + fieldValues.get(h3), 0, 4);
        addRowWithAnswer("H4: ", h4, "Correct: " + fieldValues.get(h4), 0, 5);
        addRowWithAnswer("H5: ", h5, "Correct: " + fieldValues.get(h5), 0, 6);
        addRowWithAnswer("H6: ", h6, "Correct: " + fieldValues.get(h6), 0, 7);
        addRowWithAnswer("H7: ", h7, "Correct: " + fieldValues.get(h7), 0, 8);
    }
    
    private void addHints(JTextField initVar) {
        String first = hint1.get(initVar);
        String second = hint2.get(initVar);
        String third = hint3.get(initVar);
        if (hintCount == 1) {
            if (first.equals(hint1.get(h0)))
                addRowWithAnswer("H0: ", initVar, "Hint: " + hint1.get(initVar), 1, 1);
            else if (first.equals(hint1.get(h1)))
                addRowWithAnswer("H1: ", initVar, "Hint: " + hint1.get(initVar), 1, 2);
            else if (first.equals(hint1.get(h2)))
                addRowWithAnswer("H2: ", initVar, "Hint: " + hint1.get(initVar), 1, 3);
            else if (first.equals(hint1.get(h3)))
                addRowWithAnswer("H3: ", initVar, "Hint: " + hint1.get(initVar), 1, 4);
            else if (first.equals(hint1.get(h4)))
                addRowWithAnswer("H4: ", initVar, "Hint: " + hint1.get(initVar), 1, 5);
            else if (first.equals(hint1.get(h5)))
                addRowWithAnswer("H5: ", initVar, "Hint: " + hint1.get(initVar), 1, 6);
            else if (first.equals(hint1.get(h6)))
                addRowWithAnswer("H6: ", initVar, "Hint: " + hint1.get(initVar), 1, 7);
            else if (first.equals(hint1.get(h7)))
                addRowWithAnswer("H7: ", initVar, "Hint: " + hint1.get(initVar), 1, 8);
        }
        else if (hintCount == 2) {
            if (second.equals(hint2.get(h0)))
                addRowWithAnswer("H0: ", initVar, "Hint: " + hint2.get(initVar), 1, 1);
            else if (second.equals(hint2.get(h1)))
                addRowWithAnswer("H1: ", initVar, "Hint: " + hint2.get(initVar), 1, 2);
            else if (second.equals(hint2.get(h2)))
                addRowWithAnswer("H2: ", initVar, "Hint: " + hint2.get(initVar), 1, 3);
            else if (second.equals(hint2.get(h3)))
                addRowWithAnswer("H3: ", initVar, "Hint: " + hint2.get(initVar), 1, 4);
            else if (second.equals(hint2.get(h4)))
                addRowWithAnswer("H4: ", initVar, "Hint: " + hint2.get(initVar), 1, 5);
            else if (second.equals(hint2.get(h5)))
                addRowWithAnswer("H5: ", initVar, "Hint: " + hint2.get(initVar), 1, 6);
            else if (second.equals(hint2.get(h6)))
                addRowWithAnswer("H6: ", initVar, "Hint: " + hint2.get(initVar), 1, 7);
            else if (second.equals(hint2.get(h7)))
                addRowWithAnswer("H7: ", initVar, "Hint: " + hint2.get(initVar), 1, 8);
        }
         else if (hintCount == 3) {
            if (third.equals(hint3.get(h0)))
                addRowWithAnswer("H0: ", initVar, "Hint: " + hint3.get(initVar), 1, 1);
            else if (third.equals(hint3.get(h1)))
                addRowWithAnswer("H1: ", initVar, "Hint: " + hint3.get(initVar), 1, 2);
            else if (third.equals(hint3.get(h2)))
                addRowWithAnswer("H2: ", initVar, "Hint: " + hint3.get(initVar), 1, 3);
            else if (third.equals(hint3.get(h3)))
                addRowWithAnswer("H3: ", initVar, "Hint: " + hint3.get(initVar), 1, 4);
            else if (third.equals(hint3.get(h4)))
                addRowWithAnswer("H4: ", initVar, "Hint: " + hint3.get(initVar), 1, 5);
            else if (third.equals(hint3.get(h5)))
                addRowWithAnswer("H5: ", initVar, "Hint: " + hint3.get(initVar), 1, 6);
            else if (third.equals(hint3.get(h6)))
                addRowWithAnswer("H6: ", initVar, "Hint: " + hint3.get(initVar), 1, 7);
            else if (third.equals(hint3.get(h7)))
                addRowWithAnswer("H7: ", initVar, "Hint: " + hint3.get(initVar), 1, 8);
        }
        else hintCount = 1;
    }
    
    private void hint() {
        if (hintsVisible) {
            removeHints();  // Remove all previous hints before showing new ones
            hintsVisible = false;
        } else {
            removeExistingAnswerLabels();
            
            // Reset hint count if it exceeds the number of available hints
            if (hintCount > 3) {
                hintCount = 1;
            }

            // Add hints for all fields based on current hint count
            addHints(h0);
            addHints(h1);
            addHints(h2);
            addHints(h3);
            addHints(h4);
            addHints(h5);
            addHints(h6);
            addHints(h7);

            // Increment hint count for next press
            hintCount++;
            hintsVisible = true;
        }

        revalidate();
        repaint();
    }


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


    
    @Override
    /**
     * Updates the description, question, and hints from the model
     * 
     * TODO: THIS IS A PLACEHOLDER UNTIl WE HAVE HAVE THE MODEL CODE COMPLETED
     */
    protected void updateView() {
        if (model != null) {
            // ****TO-DO*****
            // Update the view's information from the model
            // Debugging dynamic updates to the model can be done here.
            System.out.println("InitVarView");
        }
    }

    @Override
    public NewExampleRequest newRequest() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public StepCompletion stepCompletion() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
