/*
 * SHATU: SHA-256 Tutor
 * 
 *  (C) Johanna & Richard Blumenthal, All rights reserved
 * 
 *  Unauthorized use, duplication or distribution without the authors'
 *  permission is strictly prohibited.
 * 
 *  Unless required by applicable law or agreed to in writing, this
 *  software is distributed on an "AS IS" basis without warranties
 *  or conditions of any kind, either expressed or implied.
 */
package edu.regis.shatu.view;

<<<<<<< HEAD
import edu.regis.shatu.model.User;
import edu.regis.shatu.svc.SHA_256;
import edu.regis.shatu.view.act.NewUserAction;
import edu.regis.shatu.view.act.SignInAction;
=======
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
<<<<<<< HEAD
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;
=======
import java.util.logging.Logger;

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

<<<<<<< HEAD
=======
import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.svc.SHA_256;
import edu.regis.shatu.view.act.ForgotPasswordAction;
import edu.regis.shatu.view.act.NewUserAction;
import edu.regis.shatu.view.act.SignInAction;

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
/**
 * A splash panel introducing the ShaTut tutor, which also allows the user to 
 * sign-in or navigate to another screen to create a new student account.
 * 
 * This panel also provides a simple view of a User model.
 * 
 * @author rickb
 */
public class SplashPanel extends GPanel {
    /**
     * Events of interest occurring in this class are logged to this logger.
     */
    private static final Logger LOGGER = Logger.getLogger(SplashPanel.class.getName());
    
     /**
     * The user model displayed in this view.
     */
<<<<<<< HEAD
    private User model;
=======
    private Account model;
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    
    /**
     * The user's id of the form "user@university.edu".
     */
    protected JTextField userId;
    
    /**
     * Hidden display of the user's password: *******.
     */
    protected JPasswordField password;
    
    /**
     * Allows the user to attempt to sign-in using their id and password.
     */
    protected JButton signInBut;
    
    /**
     * Displays a panel allowing the user to create a new student account.
     */
    protected JButton createAcctBut;
    
    /**
<<<<<<< HEAD
=======
     * Displays a panel allowing the user to reset their password.
     */
    protected JButton forgotPasswordBut;
    
    /**
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
     * Create and layout the child GUI components in this panel.
     */
    public SplashPanel() {
	super();
        
<<<<<<< HEAD
        model = new User();
=======
        model = new Account();
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3

	initializeComponents();
	layoutComponents();
        
        // ToDo: TMP DEBUGGING (saves me typing), remove this for real
<<<<<<< HEAD
        userId.setText("");
=======
        userId.setText("test@regis.edu");
        password.setText("TestP@ss");
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }
    
    /**
     * Set the default focus to the user id field.
     */
    public void setInitialFocus() {
        userId.requestFocusInWindow();
    }
    
    /**
     * Update and return the model with the user login information in this view.
     * 
     * @return a User model with the user id and password fields set (the 
     *         password is encrypted)
     */
<<<<<<< HEAD
    public User getModel() {
	model.setUserId(userId.getText());
        
        String encryptedPass = SHA_256.instance().sha256(new String(password.getPassword()));
        
        model.setPassword(encryptedPass);
=======
    public Account getModel() {
	updateModel();
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3

	return model;
    }

    /**
     * Display the given user (id) in this view, but the encrypted password
     * is not displayed. 
     * 
     * @param model the user (id) to display
     */
<<<<<<< HEAD
    public void setModel(User model) {
=======
    public void setModel(Account model) {
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
	this.model = model;

	userId.setText(model.getUserId());
    }
    
    /**
     * Set the input focus to the user id field.
     */
    public void updateFocus() {
    	userId.requestFocusInWindow();
    }
    
    /**
     * Return a reference to the sign-in button (used by the WelcomeFrame
     * to make it the default button, when this panel is displayed).
     * 
     * @return 
     */
    public JButton getSigninButton() {
        return signInBut;
    }
    
<<<<<<< HEAD
=======
    public void clearFields(){
        userId.setText("");  // Clear user ID field
        password.setText("");  // Clear password field
    }
    
    /**
     * Creates an account instance for the user upon successful sign-in.
     * @param userId
     * @param encryptedPass
     * @return A TutoringSession instance set with the signed-in user's account instance.
     * Null otherwise.
     */
    private TutoringSession authenticateUser(String userId, String encryptedPass) {
        // Simulate server-side authentication and session creation
        if (userId != null && encryptedPass.equals("validEncryptedPassword")) {
            
            Account account = new Account();
            account.setUserId(userId);
            account.setPassword(encryptedPass);
            
            Student student = new Student(account);
            
            TutoringSession session = new TutoringSession(student);
            
            return session;
        }
        return null;
    }
    
    /**
     * Update our domain model with the information currently in this view.
     */
    private void updateModel() {
        model.setUserId(userId.getText());
        
        String encryptedPass = SHA_256.instance().sha256(new String(password.getPassword()));
        
        model.setPassword(encryptedPass);
    }
    
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * Create the primary child components used in this view
     */
    private void initializeComponents() {
        LoginDocumentListener docListener = new LoginDocumentListener();
        
        userId = new JTextField(20);
	userId.getDocument().addDocumentListener(docListener);

	password = new JPasswordField(20);
	password.getDocument().addDocumentListener(docListener);

	signInBut = new JButton(SignInAction.instance());
	signInBut.setEnabled(false);
<<<<<<< HEAD
        
        createAcctBut = new JButton(NewUserAction.instance());
=======
        /*
        signInBut.addActionListener(e -> {
        // Fetch the userId and encrypted password
        String userIdInput = userId.getText();

        
        if (!userIdInput.isEmpty()) {
                // Encrypt the password for validation (if needed)
                String encryptedPass = SHA_256.instance().sha256(new String(password.getPassword()));

                // Pass the userId to SplashFrame and initialize the Dashboard
                SplashFrame.instance().initializeDashboard(authenticateUser(userId.getText(), encryptedPass));
            } else {
                JOptionPane.showMessageDialog(this, "Please enter your User ID", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        );

        */
        createAcctBut = new JButton(NewUserAction.instance());
        
        forgotPasswordBut = new JButton(ForgotPasswordAction.instance());
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }
    
    /**
     * Layout the child components used in this view.
     */
    private void layoutComponents() {
<<<<<<< HEAD
        setBackground(Color.WHITE);

	addc(createHeader(), 0,0, 2,1, 1.0,0.0,
	     GridBagConstraints.NORTHWEST,  GridBagConstraints.HORIZONTAL,
	     5,5,5,5);	

	addc(createOverview(), 0,1, 1,1, 1.0,1.0,
	     GridBagConstraints.NORTHWEST,  GridBagConstraints.BOTH,
	     5,5,5,5);	

	addc(createLogin(), 1,1, 1,1, 0.0,0.0,
	    GridBagConstraints.NORTHWEST,  GridBagConstraints.NONE,
	    10,5,5,5);	

=======
        setBackground(new Color(0, 43, 73));
	addc(createHeader(), 0,0, 2,1, 1.0,0.0,
	     GridBagConstraints.NORTHWEST,  GridBagConstraints.HORIZONTAL,
	     5,5,5,5);	
	addc(createOverview(), 0,1, 1,1, 1.0,1.0,
	     GridBagConstraints.NORTHWEST,  GridBagConstraints.BOTH,
	     5,5,5,5);	
	addc(createLogin(), 1,1, 1,1, 0.0,0.0,
	    GridBagConstraints.NORTHWEST,  GridBagConstraints.NONE,
	    10,5,5,5);	
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        addc(new JLabel(" "), 0,2, 2,1, 1.0,1.0,
            GridBagConstraints.NORTHWEST,  GridBagConstraints.BOTH,
            5,5,5,5);
        
<<<<<<< HEAD
        JLabel copyright = new JLabel("(C) 2019-2024 Johanna and Richard Blumenthal. All Rights Reserved");
=======
        JLabel copyright = new JLabel("(C) 2019-2025 Johanna and Richard Blumenthal. All Rights Reserved");
        copyright.setForeground(new Color(241,196,0));
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        copyright.setFont(new Font("Dialog", Font.PLAIN, 10));
        addc(copyright, 0,3, 2,1, 1.0,1.0,
		GridBagConstraints.NORTH,  GridBagConstraints.CENTER,
		5,5,5,5);
        
	setSize(490, 400);
    }
    
    private GPanel createHeader() {
	GPanel panel = new GPanel();
<<<<<<< HEAD
	panel.setBackground(new Color(223,242,245));

	JLabel ccis = new JLabel("Regis University Department of Computer and Cyber Sciences");
	ccis.setFont(new Font("Dialog", Font.PLAIN, 20));
	ccis.setForeground(Color.BLUE);
=======
	panel.setBackground(new Color(241,196,0));

	JLabel ccis = new JLabel("Regis University Department of Computer and Cyber Sciences Product");
	ccis.setFont(new Font("Dialog", Font.PLAIN, 20));
	ccis.setForeground(new Color(0, 43, 73));
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
	
	panel.addc(ccis , 0,0, 1,1, 1.0,1.0,
	     GridBagConstraints.NORTHWEST,  GridBagConstraints.HORIZONTAL,
	     5,5,5,5);

	JLabel newLabel = new JLabel("New to ShaTu?");
	newLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
	newLabel.setForeground(Color.GRAY);

	panel.addc(newLabel , 1,0, 1,1, 0.0,0.0,
	     GridBagConstraints.EAST,  GridBagConstraints.NONE,
	     5,5,5,5);	

	panel.addc(createAcctBut, 2,0, 1,1, 0.0,0.0,
		   GridBagConstraints.EAST, GridBagConstraints.NONE,
		   5,5,5,5);	
<<<<<<< HEAD

=======
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
	return panel;
    }
    
     private GPanel createLogin() {
	GPanel panel = new GPanel();
<<<<<<< HEAD
	panel.setBackground(new Color(223,242,245));

=======
	panel.setBackground(new Color(241,196,0));
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));

        JLabel label = new JLabel("Sign in");
	panel.addc(label, 0,0, 1,1, 0.0,0.0,
		   GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		   5,5,5,5);	

        label = new JLabel("User Id");
	label.setLabelFor(userId);

	panel.addc(label, 0,1, 1,1, 1.0,0.0,
		   GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		   5,5,5,5);
<<<<<<< HEAD

=======
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
 	panel.addc(userId, 0,2, 2,1, 1.0,0.0,
		   GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
		   0,5,5,5);

        label = new JLabel("Password:");
	label.setLabelFor(password);

	panel.addc(label, 0,3, 1,1, 0.0,0.0,
		   GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		   15,5,5,5);
<<<<<<< HEAD

	panel.addc(password, 0,4, 2,1, 1.0,0.0,
		   GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
		   0,5,5,5);

	panel.addc(signInBut, 0,5, 1,1, 0.0,0.0,
		   GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		   15,5,5,5);

	return panel;
    }

    
    private GPanel createOverview() {
	GPanel panel = new GPanel();
	panel.setBackground(Color.WHITE);
=======
	panel.addc(password, 0,4, 2,1, 1.0,0.0,
		   GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
		   0,5,5,5);
	panel.addc(signInBut, 0,5, 1,1, 0.0,0.0,
		   GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		   15,5,5,5);
        panel.addc(forgotPasswordBut,1,5,1,1, 0.0,0.0,
		   GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		   15,5,5,5);
	return panel;
    }

    private GPanel createOverview() {
	GPanel panel = new GPanel();
	panel.setBackground(new Color(241,196,0));
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3

	panel.setSize(300, 400);
	panel.setPreferredSize(new Dimension(300,400));

	JLabel logo = new JLabel("ShaTu: SHA-256 Tutor");
	logo.setFont(new Font("Dialog", Font.PLAIN, 20));
<<<<<<< HEAD
	logo.setForeground(Color.MAGENTA);
=======
	logo.setForeground(new Color(0, 43, 73));
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3

	panel.addc(logo, 0,0, 1,1, 0.0,0.0,
		   GridBagConstraints.NORTHWEST,  GridBagConstraints.NONE,
		   5,5,5,5);	

	JLabel name = new JLabel("A See_1, Do_1, Teach_1 Intelligent Tutoring System.");
	name.setFont(new Font("Dialog", Font.PLAIN, 14));
	panel.addc(name, 0,1, 1,1, 0.0,0.0,
		   GridBagConstraints.NORTHWEST,  GridBagConstraints.NONE,
		   0,5,5,5);	

<<<<<<< HEAD
	
=======
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
	JTextArea descr = new JTextArea();
	descr.setEditable(false);
	descr.setLineWrap(true);
	descr.setWrapStyleWord(true);
	descr.setFont(new Font("Dialog", Font.PLAIN, 12));
	descr.append("ShaTu provides individualized tutoring practice focused ");
	descr.append("on understanding the SHA-256 digest algorithm and the");
        descr.append("underlying computer science concepts upon which it is ");
        descr.append("based.\n\n");
        descr.append("Please sign in or use 'New User' to create a student account.");
        descr.append("\n\n");
<<<<<<< HEAD
=======
        descr.setBackground(new Color(241,196,0));
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        
	panel.addc(descr, 0,2, 1,1, 1.0,1.0,
		   GridBagConstraints.NORTHWEST,  GridBagConstraints.BOTH,
		   15,5,5,5);

	JLabel loginMsg = new JLabel("To use the tutor, you must sign in.");
	panel.addc(loginMsg, 0,3, 1,1, 0.0,0.0,
		   GridBagConstraints.NORTHWEST,  GridBagConstraints.NONE,
		   5,5,5,5);	
	
	panel.addc(new JLabel(" "), 0,4, 1,1, 1.0,1.0,
		   GridBagConstraints.NORTHWEST,  GridBagConstraints.BOTH,
		   5,5,5,5);
<<<<<<< HEAD
        
        return panel;
    }
    
       /**
=======
        return panel;
    }
    
     /**
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
     * Listens to changes made to the LoginDialog's userId and password fields
     * in order to appropriate enable the buttons in the dialog.
     */
    public class LoginDocumentListener implements DocumentListener {
	/**
	 * As text was insert into the userId or password field, check whether
	 * we need to enable or disable the LoginDialog's buttons.
	 */
        @Override
	public void insertUpdate(DocumentEvent e) {
	    enableButtons(e);
	}

	/**
	 * As text was removed from the userId or password field, check whether
	 * we need to enable or disable the LoginDialog's buttons.
	 */
        @Override
	public void removeUpdate(DocumentEvent e) {
	    enableButtons(e);
	}

	/**
	 * As text was changed in the userId or password field, check whether
	 * we need to enable or disable the LoginDialog's buttons.
	 */
        @Override
	public void changedUpdate(DocumentEvent e) {
	    enableButtons(e);
	}

	/**
	 * If the userId or password fields are empty, disable the OK 'Login'
	 * button.
	 */
	private void enableButtons(DocumentEvent e) {
            Document document = (Document) e.getDocument();

	    if ((userId.getDocument().getLength() == 0) ||
		(password.getDocument().getLength() == 0)) {
		signInBut.setEnabled(false);
<<<<<<< HEAD

=======
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
	    } else {
		signInBut.setEnabled(true);
	    }
	}
    }
<<<<<<< HEAD
    
     /**
     * Encrypt the given password using SHA-256
     * @param base
     * @return 
     */
    /*
    public static String encryptSHA256(String base) {
        // ToDo: use the EncryptionMgr ???
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            
            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
            
                if(hex.length() == 1) 
                    hexString.append('0');
            
                hexString.append(hex);
            }

            return hexString.toString();
            
        } catch(UnsupportedEncodingException | NoSuchAlgorithmException ex){
            throw new RuntimeException(ex);
        }
    }
    */

    /**
     * Encrypt the given password using MD5
     */
    /*
    private String encrypt(String password) {
        // ToDo: use the EncryptionMgr ???
	try {
	    MessageDigest m = MessageDigest.getInstance("MD5");
	    byte[] data = password.getBytes();

	    m.update(data,0,data.length);

	    BigInteger i = new BigInteger(1,m.digest());

	    return String.format("%1$032X", i).toLowerCase();

	} catch (NoSuchAlgorithmException e) {
            LOGGER.severe(e.getMessage());
	}

	return "";
    }
    */
    
=======
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
}