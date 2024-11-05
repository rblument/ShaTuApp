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
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.NewExampleRequest;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    
    private InitVarStep initVarStep;
    private JButton showButton, hintButton;
    private JTextField h0, h1, h2, h3, h4, h5, h6, h7;
    private short hintCount;
    private boolean answersVisible, hintsVisible;

    public InitVarView() {       
        initializeComponents();
        initializeLayout();
        attachDocumentListeners();
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
      // Placeholder for future use.
    }

    private void initializeComponents() {
        initVarStep = new InitVarStep();
        hintCount = 1;
        answersVisible = false;
        hintsVisible = false;

        h0 = new JTextField(13);
        h0.setName("H0");
        h1 = new JTextField(13);
        h1.setName("H1");
        h2 = new JTextField(13);
        h2.setName("H2");
        h3 = new JTextField(13);
        h3.setName("H3");
        h4 = new JTextField(13);
        h4.setName("H4");
        h5 = new JTextField(13);
        h5.setName("H5");
        h6 = new JTextField(13);
        h6.setName("H6");
        h7 = new JTextField(13);
        h7.setName("H7");

        showButton = new JButton("Show Answer");
        hintButton = new JButton("Hint");
    }

    private void initializeLayout() {
        String description = ("<html>In SHA-256, the algorithm begins with a set of "
                + "initial hash values, which are specifically chosen constants.<br>"
                + "These constants are derived from the first 32 bits of the "
                + "fractional parts of the square roots of the first 8 prime numbers.<br>"
                + "They serve as the starting points for the hash computation and "
                + "ensure that the algorithm starts from a random-like state.<br>"
                + "<br>Please enter the inital hash values in hexadecimal for H0 to H7 Below:<br>"
                + "(Incorrect answers are in red, and turn green when correct.)<br></html>");
        JLabel infoLabel = new JLabel(description);
        addc(infoLabel, 0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 5, 5, 5, 5);

        createLabelForField("H0: ", h0, 1);
        createLabelForField("H1: ", h1, 2);
        createLabelForField("H2: ", h2, 3);
        createLabelForField("H3: ", h3, 4);
        createLabelForField("H4: ", h4, 5);
        createLabelForField("H5: ", h5, 6);
        createLabelForField("H6: ", h6, 7);
        createLabelForField("H7: ", h7, 8);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        buttonPanel.add(showButton);
        buttonPanel.add(hintButton);

        showButton.addActionListener(e -> showAnswer());
        hintButton.addActionListener(e -> hint());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(buttonPanel, gbc);
    }

    /**
     * Creates and adds a label for a text field to the GUI.
     * @param labelText references an initial variable.
     * @param textField holds the user's answer.
     * @param gridY The position on the y-axis to place the label and text field.
     */
    private void createLabelForField(String labelText, JTextField textField, int gridY) {
        JLabel label = new JLabel(labelText);
        label.setLabelFor(textField);
        textField.setOpaque(true);

        JPanel rowPanel = new JPanel(new FlowLayout(5, 5, 0));
        rowPanel.add(label);
        rowPanel.add(textField);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = gridY;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(rowPanel, gbc);
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
     * Displays answers next to their corresponding variable and text field.
     */
    private void showAnswer() {
        removeHints();
        if (answersVisible) {
            removeExistingAnswerLabels();
            answersVisible = false;
        } else {
            addAnswerLabels();
            answersVisible = true;
        }
        revalidate();
        repaint();
    }
    
    /**
     * Adds a label containing an answer for its associated variable and text field.
     */
    private void addAnswerLabels() {
        addRowWithAnswer("H0: ", h0, "Correct: " + initVarStep.getAnswer("H0"), 0, 1);
        addRowWithAnswer("H1: ", h1, "Correct: " + initVarStep.getAnswer("H1"), 0, 2);
        addRowWithAnswer("H2: ", h2, "Correct: " + initVarStep.getAnswer("H2"), 0, 3);
        addRowWithAnswer("H3: ", h3, "Correct: " + initVarStep.getAnswer("H3"), 0, 4);
        addRowWithAnswer("H4: ", h4, "Correct: " + initVarStep.getAnswer("H4"), 0, 5);
        addRowWithAnswer("H5: ", h5, "Correct: " + initVarStep.getAnswer("H5"), 0, 6);
        addRowWithAnswer("H6: ", h6, "Correct: " + initVarStep.getAnswer("H6"), 0, 7);
        addRowWithAnswer("H7: ", h7, "Correct: " + initVarStep.getAnswer("H7"), 0, 8);
    }
    
    /**
     * Called when hint button is pressed.
     * Determines if hints should be displayed to the user.
     */
    private void hint() {
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
