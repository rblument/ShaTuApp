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

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.User;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * The first window displayed to a student user, which contains a splash panel 
 * and associated panels for creating new users and signing-in exiting users.
 * 
 * @author rickb
 */
public class SplashFrame extends JFrame {
    /**
     * Name of the splash panel in this frame's primary card layout panel.
     */
    public static final String SPLASH = "SplashPanel";
    
    /**
     * Name of the new user panel in this frame's primary card layout panel.
     */
    public static final String NEW_USER = "NewUserPanel";
    
     /**
     * Dashboard Reference Name for CardLayout;
     * linked to splashPanel sign in.
     */
    public static final String DASHBOARD = "DashboardPanel";
    
    /**
     * Tutor View Reference Name for CardLayout;
     * linked to by dashboard's practice button.
     */
    public static final String TUTOR = "TutoringSessionView";
    /**
     * Allowed consecutive illegal passwords before the user is locked out.
     */
    public static final int MAX_SIGNIN_ATTEMPTS = 3;
    
    /**
     * The single instance of this frame.
     */
    private static final SplashFrame SINGLETON;
    
    /**
     * Create the singleton for this JFrame, which occurs when this class
     * is loaded by the Java class loaded, as a result of the class being 
     * referenced by executing WelcomeFrame.instance() in the main() method of
     * EthicsCourtTutor.
     */
    static {
        SINGLETON = new SplashFrame();
    }
    
    /**
     * Return the singleton instance of this JFrame.
     * 
     * @return the WelcomeFrame singleton 
     */
    public static SplashFrame instance() {
        return SINGLETON;
    }
    
    /**
     * A CardLayout containing the SPLASH and NEW_USER panels.
     */
    private JPanel cards;
    
    /**
     * The name of the currently selected panel, SPLASH, NEW_USERf, or DASHBOARD.
     */
    private String selectedPanel;
    
    /**
     * The splash panel, which displays splash information, sign-in fields,
     * and a link to the create new student account panel.
     */
    private SplashPanel splashPanel;
    
    /**
     * The panel that allows users to select a type of service
     * (teach, practice, quiz) upon sign in.
     */
    private DashboardPanel dashboardPanel;
    
    /**
     * The panel which displays the ShaTuApp tutoring view;
     * Used for practicing skills.
     */
    private TutoringSessionView tutoringSessionView;
    
    private TutoringSession tutoringSession;
    
    
    /**
     * A panel which allows the user to create a new student account with 
     * associated sign-in information.
     */
    private NewAccountPanel newAccountPanel;
    
    /**
     * The number of consecutive illegal passwords attempted by the current
     * user attempting to login (see MAX_SIGNIN_ATTEMPTS).
     */
    protected int signInAttempts = 1;
    
   /**
     * Create and layout the child components in this Splash JFrame.
     */
    private SplashFrame() {
        super("ShaTu");
        
        setMinimumSize(new Dimension(875,650));
        
        initializeComponents();
        
        this.setContentPane(cards);
        
        selectPanel(SPLASH);
        
        pack();
        
        splashPanel.setInitialFocus();
        
        setVisible(true);
    }
    
    /**
     * Return the student login information displayed in this frame.
     * 
     * See getAccount()
     * 
     * @return a User (userId and password)
     */
    public User getUser() {
       return splashPanel.getModel();
    }
    
    /**
     * Return the user account information 
     * 
     * See getUser()
     * 
     * @return an Account (userId, passwd, first and last name)
     */
    public Account getAccount() {
       return newAccountPanel.getModel();
    }
    
    /**
     * Display to the user the result of an invalid password in a sign in.
     * 
     * Handles an invalid password response from a SignInAction keeping track
     * of the number of user attempts thus far.
     */
    public void invalidPass() {
        if (signInAttempts <= MAX_SIGNIN_ATTEMPTS) {
           
            String msg = "Invalid Password attempt " + 
                         String.valueOf(signInAttempts) + " of " + 
                         MAX_SIGNIN_ATTEMPTS;
            
            signInAttempts++;
            
            JOptionPane.showMessageDialog(this, msg, "SignIn Error", JOptionPane.ERROR_MESSAGE);
            
        } else {
            String msg = "You exceeded the max number of sign in attempts\n" +
                         "Please contact the ShaTu administrator";
            
            JOptionPane.showMessageDialog(this, msg, "SignIn Error", JOptionPane.ERROR_MESSAGE);

            // ToDo: What?
            this.dispose();
            System.exit(1);
        }
    }
    
    /**
     * Convenience method that displays the Splash panel allowing sign in.
     */
    public void selectSplash() {
        selectPanel(SPLASH);
    }
    
    /**
     * Sets current TutoringSession instance for the SplashFrame.
     * @param session 
     */
    public void setSession(TutoringSession session) {
        this.tutoringSession = session;  // Store the session for later use
        if (this.tutoringSession == null) {
            System.err.println("Failed to store TutoringSession in SplashFrame");
        } else {
            System.out.println("TutoringSession successfully stored in SplashFrame");
        }
    }
    
    /**
     * Returns the current session for the SplashFrame.
     * @return The current TutoringSession instance.
     */
    public TutoringSession getSession() {
        return this.tutoringSession;
    }
    
    /**
     * Clears the tutoringSession instance.
     * Changes current user to an empty user instance.
     * Clears splashPanel fields.
     * Switches to splash screen for sign in.
     */
    public void logout() {        
        // Ensure that tutoring session is not null before attempting to clear
        if (this.tutoringSession != null && this.tutoringSession.getAccount() != null) {
            // Clear account information
            this.tutoringSession.getAccount().clear();
        }

        // Invalidate or set the tutoring session to null
        this.tutoringSession = null;

        // Clear the splash panel model by setting it to a new user and reset the fields
        this.splashPanel.setModel(new User());  // Reset the splash panel model
        this.splashPanel.clearFields();  // Clear the userId and password fields

        // Swap to splash screen for login
        this.selectSplash();
    }

    /**
     * Sets the current card panel to Dashboard.
     * @param session
     */
    public void selectDashboard(TutoringSession session) {
        if (session == null) {
            System.err.println("TutoringSession is null in selectDashboard");
            return;  // Exit early to prevent passing a null session
        }

        this.setSession(session);  // Store the session for later use
        this.dashboardPanel = new DashboardPanel(session);  // Pass session to DashboardPanel
        this.cards.add(dashboardPanel, DASHBOARD);
        this.selectPanel(DASHBOARD);  // Display the dashboard
        System.out.println("SplashFrame.java: selectDashboard: session = " + session.getAccount().getFirstName());
    }

     /**
     * Selects a personalized practice screen for each user upon selecting
     * the dashboard's practice button.
     * @param session
     */
        public void selectPracticeScreen() {
        TutoringSession session = getSession(); // Retrieve the session
        if (session == null) {
            System.err.println("Session is null when switching to practice screen.");
            return;
        }

        // Initialize the TutoringSessionView if it's not already initialized
        if (this.tutoringSessionView == null) {
            this.tutoringSessionView = new TutoringSessionView(); // Create the tutoring session view
            cards.add(tutoringSessionView, TUTOR);  // Add it to the CardLayout
        }

        // Set the model (session) for the TutoringSessionView
        this.tutoringSessionView.setModel(session);
        
        // Sets size of practice screen window.
        // Without this, the window opens too small.
        this.setPreferredSize(new Dimension(1000, 800));
        this.pack();
        
        // Switch to the tutoring session view
        selectPanel(TUTOR);
    }

    /**
     * Display the New User panel, which allows the user to create a new
     * student account with associated sign-in information.
     */
    public void selectNewUser() {
        this.selectPanel(NEW_USER);
    }
    
    /**
     * Reset the text fields in the new account panel to the empty string.
     */
    public void clearNewAccountPanel() {
        this.newAccountPanel.clearFields();
    }
    
    /**
     * Display to the user they entered an unknown user during a sign in.
     */
    public void unknownUser() {
        User user = splashPanel.getModel();
       
        JOptionPane.showMessageDialog(this, 
                user.getUserId() + " is not a known user.\n\nPerhaps, try creating a 'New User' first.",
                "Warning", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Display the card panel with the associated name.
     * 
     * @param name SPLASH, NEW_USER, or DASHBOARD
     */
    private void selectPanel(String name) {
        CardLayout cl = (CardLayout) (cards.getLayout());
        cl.show(cards, name);
        this.selectedPanel = name;
        
        if (name.equals(SPLASH)) {
            JButton but = splashPanel.getSigninButton();
        
            SwingUtilities.getRootPane(but).setDefaultButton(but);
        }
        else {
            this.newAccountPanel.updateFocus();
        }
    }
    
    /**
     * Initializes a personalized dashboard screen for each user after sign in.
     * @param sessin: a reference to the current SplashFrame session instance.
     */
    public void initializeDashboard(TutoringSession session) {
        if (session == null) {
            System.err.println("TutoringSession is null in initializeDashboard");
            return;
        }

        this.setSession(session);  // Store the session for future use
        this.dashboardPanel = new DashboardPanel(session);  // Pass session to DashboardPanel
        this.cards.add(dashboardPanel, DASHBOARD);
        this.selectPanel(DASHBOARD);  // Display the dashboard
    }

    /**
     * Create the child GUI components appearing in this frame.
     */
    private void initializeComponents() {
        this.cards = new JPanel(new CardLayout());
        
        this.splashPanel = new SplashPanel();
        this.newAccountPanel = new NewAccountPanel();
                        
        this.cards.add(splashPanel, SPLASH);
        this.cards.add(newAccountPanel, NEW_USER);
    }
}