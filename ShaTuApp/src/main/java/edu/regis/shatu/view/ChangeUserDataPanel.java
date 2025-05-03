

package edu.regis.shatu.view;

import java.awt.*;


import javax.swing.event.*;
import javax.swing.text.*;


import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.view.act.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;




public class ChangeUserDataPanel extends GPanel {

 
    private static final Logger LOGGER = Logger.getLogger(ChangeUserDataPanel.class.getName());

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX
            = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    
    private Account account;

   
    protected HintTextField fName;
    protected HintTextField lName;
    protected HintTextField userId;
    
    protected JComboBox secQuestions;
    protected JPasswordField secAnswer;

    protected JLabel strength;
    protected JLabel msg;

    
    protected JButton updateAcctBut;
    protected JButton backBut;

    public ChangeUserDataPanel() {
        super();
       
        account = new Account();
        Student student = new Student(account);
       
        initComponents();
        layoutPanel();

        enableButtons(fName.getDocument());
    }

    
    public Account getAccount() {
        updateAccount();

        return account;
    }

   
    public void setAccount(Account account) {
        this.account = account;

        updateDisplay();
    }

    public HintTextField getUserIdComp() {
        return userId;
    }

    public void updateFocus() {
        fName.requestFocusInWindow();
    }
    
   
    public void clearFields() {
        fName.setText("");
        lName.setText("");
        userId.setText(account.getUserId());
        //userId.setText("");
        
        secQuestions.setSelectedIndex(0);
        secAnswer.setText("");
    }

   
    private void updateAccount() {
        account.setUserId(userId.getText());
        account.setFirstName(fName.getText());
        account.setLastName(lName.getText());
          System.out.println("INFO  ...... at line 105");
        account.setSecurityQuestion(secQuestions.getSelectedIndex());
        
    }

    
    private void updateDisplay() {
        userId.setText(account.getUserId());
         String IdCheck = account.getUserId();
         System.out.println("First name : " + IdCheck);
        fName.setText(account.getFirstName());
        System.out.println("First name : " + fName);
        lName.setText(account.getLastName());
        secQuestions.setSelectedIndex(0);
        secAnswer.setText("");
    }

    
    private void initComponents() {
           System.out.println("INFO  ...... at line 124");
        LoginDocumentListener docListener = new LoginDocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); 
            }

           
            @Override
            public void enableButtons(Document doc) {
               throw new UnsupportedOperationException("Not supported yet."); 
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); 
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); 
            }
        };
         System.out.println("First name : " + fName);
        fName = new HintTextField("First", 15);
        fName.getDocument().addDocumentListener(docListener);

        lName = new HintTextField("Last", 30);
        lName.getDocument().addDocumentListener(docListener);

        
        userId = new HintTextField("JOHN@university.edu", 10);
    
        userId.setIsEmailAddr(true);
        userId.getDocument().addDocumentListener(docListener);
        System.out.println("INFO     line 159 userID: " + account.getUserId());

                
        String s1[] = {"What city were you born in?", "What is your mother's maiden name?"};
        secQuestions = new JComboBox(s1);
        
        secAnswer = new JPasswordField(20);
        secAnswer.getDocument().addDocumentListener(docListener);


        updateAcctBut = new JButton(UpdateAcctAction.instance());

        updateAcctBut.setEnabled(false);
        MainFrame.instance().getRootPane().setDefaultButton(updateAcctBut);

        backBut = new JButton(BackToLogin.instance());
        backBut.setEnabled(true);
        
        
    }

    private void layoutPanel() {
    // Assuming you're using a GridBagLayout
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);

    // Add components to layout with appropriate constraints
    gbc.gridx = 0;
    gbc.gridy = 0;
    add(new JLabel("First Name:"), gbc);
    gbc.gridx = 1;
    add(fName, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    add(new JLabel("Last Name:"), gbc);
    gbc.gridx = 1;
    add(lName, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    add(new JLabel("User ID:"), gbc);
    gbc.gridx = 1;
    add(userId, gbc);

    gbc.gridx = 0;
    gbc.gridy = 3;
    add(new JLabel("Security Question:"), gbc);
    gbc.gridx = 1;
    add(secQuestions, gbc);

    gbc.gridx = 0;
    gbc.gridy = 4;
    add(new JLabel("Security Answer:"), gbc);
    gbc.gridx = 1;
    add(secAnswer, gbc);

    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.gridx = 1;
    add(updateAcctBut, gbc);
    gbc.gridx = 2;
    add(backBut, gbc);
}
    private GPanel createOverview() {
    GPanel panel = new GPanel();
    panel.setBackground(new Color(241, 196, 0));
    panel.setPreferredSize(new Dimension(300, 400));

    // Define constants for reuse
    final Color BACKGROUND_COLOR = new Color(241, 196, 0);
    final Font LOGO_FONT = new Font("Dialog", Font.PLAIN, 20);
    final Font NAME_FONT = new Font("Dialog", Font.PLAIN, 14);
    final Font DESC_FONT = new Font("Dialog", Font.PLAIN, 12);
    final Insets INSETS = new Insets(5, 5, 5, 5);

    // Common constraints
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = INSETS;
    gbc.anchor = GridBagConstraints.NORTHWEST;

    JLabel logo = new JLabel("ShaTu");
    logo.setFont(LOGO_FONT);
    logo.setForeground(new Color(0, 43, 73));
    addComponent(panel, logo, gbc, 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NONE);

    JLabel name = new JLabel("A See_1, Do_1, Teach_1 Intelligent Tutoring System.");
    name.setFont(NAME_FONT);
    addComponent(panel, name, gbc, 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NONE);

    JTextArea descr = new JTextArea("This is the change user data panel.");
    descr.setEditable(false);
    descr.setLineWrap(true);
    descr.setWrapStyleWord(true);
    descr.setBackground(BACKGROUND_COLOR);
    descr.setFont(DESC_FONT);
    addComponent(panel, descr, gbc, 0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH);

   
    // Adding spacer
    addComponent(panel, new JLabel(" "), gbc, 0, 4, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH);

    return panel;
}

private void addComponent(GPanel panel, Component comp, GridBagConstraints gbc,
                          int gridx, int gridy, int gridwidth, int gridheight,
                          double weightx, double weighty, int fill) {
    gbc.gridx = gridx;
    gbc.gridy = gridy;
    gbc.gridwidth = gridwidth;
    gbc.gridheight = gridheight;
    gbc.weightx = weightx;
    gbc.weighty = weighty;
    gbc.fill = fill;
    panel.add(comp, gbc);
}

private GPanel updateDATA() {
    GPanel panel = new GPanel();
    panel.setBackground(new Color(241, 196, 0));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));

    // Define constants for reuse
    final Color BACKGROUND_COLOR = new Color(241, 196, 0);
    final Color LABEL_FOREGROUND_COLOR = new Color(75, 66, 66);
    final Color MSG_FOREGROUND_COLOR = new Color(173, 7, 1);
    final Font LABEL_FONT = new Font("Dialog", Font.PLAIN, 10);
    final Font MSG_FONT = new Font("Dialog", Font.PLAIN, 10);
    final Insets INSETS = new Insets(5, 5, 5, 5);

    // Common constraints
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = INSETS;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    
    addLabel(panel, "Name", fName, 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NONE);
    addComponent(panel, fName, gbc, 0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL);
    addComponent(panel, lName, gbc, 1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.HORIZONTAL);

    addLabel(panel, "User Id:", userId, 0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.NONE);
    addComponent(panel, userId, gbc, 0, 3, 2, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL);

    JLabel label = new JLabel(" ");
    label.setFont(LABEL_FONT);
    label.setForeground(LABEL_FOREGROUND_COLOR);
    addComponent(panel, label, gbc, 0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NONE);
    addComponent(panel, label, gbc, 1, 4, 2, 1, 0.0, 0.0, GridBagConstraints.SOUTHEAST);
    addComponent(panel, label, gbc, 0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.NONE);

    addLabel(panel, "Choose Security Question:", secQuestions, 0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.NONE);
    addComponent(panel, secQuestions, gbc, 0, 10, 2, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL);
    addLabel(panel, "Answer:", secAnswer, 0, 11, 2, 1, 0.0, 0.0, GridBagConstraints.NONE);
    addComponent(panel, secAnswer, gbc, 0, 12, 2, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL);

    msg = new JLabel("");
    msg.setFont(MSG_FONT);
    msg.setForeground(MSG_FOREGROUND_COLOR);
    addComponent(panel, msg, gbc, 0, 13, 2, 1, 0.0, 0.0, GridBagConstraints.NONE);

    addComponent(panel, backBut, gbc, 0, 14, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL);
    addComponent(panel, updateAcctBut, gbc, 1, 14, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL);

    return panel;
}

private void addLabel(GPanel panel, String text, Component labelFor, int gridx, int gridy, int gridwidth, int gridheight,
                      double weightx, double weighty, int fill) {
    JLabel label = new JLabel(text);
    label.setLabelFor(labelFor);
    addComponent(panel, label, new GridBagConstraints(), gridx, gridy, gridwidth, gridheight, weightx, weighty, fill);
}


private void enableButtons(Document e) {
    boolean isValidFName = validateField(fName);
    boolean isValidLName = validateField(lName);
    boolean isValidUserId = validateUserId(userId);
    boolean isValidAnswer = secAnswer.getDocument().getLength() > 0;

    if (isValidFName && isValidLName && isValidUserId && isValidAnswer) {
        updateAcctBut.setEnabled(true);
        msg.setText("");
    } else {
        updateAcctBut.setEnabled(false);
        if (msg != null) {
        msg.setText("(* Please fix problems highlighted in red.)");
        } else {
        System.err.println("Error: msg is null!");
        }
        
    }
}

private boolean validateField(HintTextField field) {
    boolean isValid = !field.isDefaultValue();
    field.setBorder(BorderFactory.createLineBorder(isValid ? Color.BLACK : new Color(173, 7, 1)));
    return isValid;
}

private boolean validateUserId(HintTextField field) {
    boolean isValid = !field.isDefaultValue();
    if (isValid) {
        Document userIdDoc = field.getDocument();
        try {
            String email = userIdDoc.getText(0, userIdDoc.getLength());
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
            isValid = matcher.find();
        } catch (BadLocationException ex) {
            System.err.println("Error reading user ID: " + ex.getMessage());
            isValid = false;
        }
    }
    field.setBorder(BorderFactory.createLineBorder(isValid ? Color.BLACK : new Color(173, 7, 1)));
    return isValid;
}
      
    public abstract class LoginDocumentListener implements DocumentListener {

    @Override
    public void changedUpdate(DocumentEvent e) {
        handleDocumentEvent(e);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        handleDocumentEvent(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        handleDocumentEvent(e);
    }

    private void handleDocumentEvent(DocumentEvent e) {
    Component comp = MainFrame.instance().getFocusOwner();
    if (comp != null && (comp instanceof JTextField || comp instanceof JPasswordField)) {
        enableButtons(e.getDocument());
    }
}

    protected abstract void enableButtons(Document doc);
}

}