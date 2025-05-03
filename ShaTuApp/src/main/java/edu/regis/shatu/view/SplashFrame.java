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
/*
*Modified by John Hennessey
*20 march 2025
* SHAT-179 added functionality to allow user to change information 
*/

package edu.regis.shatu.view;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.LessonSession;
import edu.regis.shatu.model.TutoringSession;

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
     * Name of the change user data panel in this frame's primary card layout panel.
     */
    public static final String CHANGE_DATA = "ChangeUserDataPanel";
            
     /**
     * Dashboard Reference Name for CardLayout;
     * linked to splashPanel sign in.
     */
    public static final String DASHBOARD = "DashboardPanel";
    
    /**
     * Dashboard Reference Name for CardLayout;
     * linked to splashPanel sign in.
     */
    public static final String LESSON = "LessonMenu";
    
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
     * Name of the forgot password panel in this frame's primary card layout panel.
     */
    public static final String FORGOT_PASSWORD = "ForgotPasswordPanel";
    
    /**
     * Name of the forgot password panel in this frame's primary card layout panel.
     */
    public static final String RESET_PASSWORD = "ResetPasswordPanel";
    
        
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
     * The panel that allows users to select a type of service
     * (teach, practice, quiz) upon sign in.
     */
    private LessonSessionView lessonSessionView;

    private LessonSession lessonSession;
    
    /**
     * The "Do One" tutoring view (see its documentation).
     */
    private TutoringSessionView tutoringSessionView;
    
    /**
     * This is the domain model for this 
     */
    private TutoringSession tutoringSession;
    
    /**
     * A panel which allows the user to create a new student account with 
     * associated sign-in information.
     */
    private NewAccountPanel newAccountPanel;
    
    /**
     * A panel which allows the user to verify themselves so they can reset their
     * password.
     */
    private ForgotPasswordPanel forgotPasswordPanel;
    
    /**
     * A panel which allows the user to reset their password.
     */
    private ResetPasswordPanel resetPasswordPanel;
    
   /**
     * A panel which allows the user to change their data.
     */
    private ChangeUserDataPanel changeUserDataPanel;
    
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

        // Get screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Calculate initial size (e.g., 80% of the screen)
        int width = (int) (screenSize.width * 0.5);
        int height = (int) (screenSize.height * 0.56);
        setSize(width, height);
        setMinimumSize(new Dimension(875, 650)); // Minimum size

        initializeComponents();

        this.setContentPane(cards);

        selectPanel(SPLASH);

        pack();

        splashPanel.setInitialFocus();

        setVisible(true);
    }
    
    /**
     * Return the user account information 
     * 
     * @return an Account (userId, passwd, first and last name)
     */
    public Account getAccount() {
        String panel = this.selectedPanel;
        
        // ToDo: This appears to be a mess
        //System.out.println("***** Selected Panel *********** " + panel);
        switch (panel) {
            case SPLASH:
            case TUTOR:
            case DASHBOARD:
                return splashPanel.getModel();
            case FORGOT_PASSWORD:
                return forgotPasswordPanel.getModel();
            
            case RESET_PASSWORD:
                System.out.println("RESET");
                return resetPasswordPanel.getModel();
               
            default:
                return newAccountPanel.getModel();
                
        }
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
        tutoringSession = session;
    }
    
     /**
     * Enables access to the current tutoring session view
     * @return an instance of the current view
     */
    public TutoringSessionView getTutoringSessionView(){
        return tutoringSessionView;
    }
    
    /**
     * Returns the current lesson session for the SplashFrame.
     * @return The current LessonSession instance.
     */
    
    public LessonSession getLessonSession() {
        return this.lessonSession;
    }
    /**
     * Clears the tutoringSession instance.
     * Changes current user to an empty user instance.
     * Clears splashPanel fields.
     * Switches to splash screen for sign in.
     */
    public void logout() {        
        // Ensure that tutoring session is not null before attempting to clear
        if (this.tutoringSession != null && this.tutoringSession.getStudent().getAccount() != null) {
            // Clear account information
            this.tutoringSession.getStudent().getAccount().clear();
        }

        // Invalidate or set the tutoring session to null
        this.tutoringSession = null;

        // Clear the splash panel model by setting it to a new user and reset the fields
        this.splashPanel.setModel(new Account());  // Reset the splash panel model
        this.splashPanel.clearFields();  // Clear the userId and password fields

        // Swap to splash screen for login
        this.selectSplash();
    }

    /**
     * Sets the current card panel to Dashboard.
     * @param session
     */
    public void selectDashboard(TutoringSession session) {   
        tutoringSession = session;

        if (dashboardPanel == null) {
            dashboardPanel = new DashboardPanel(session);
        } else {
            dashboardPanel.setModel(session);
        }
        
        cards.add(dashboardPanel, DASHBOARD);
        
        selectPanel(DASHBOARD);  // Display the dashboard
    }

     /**
     * Selects a personalized practice screen for each user upon selecting
     * the dashboard's practice button.
     */
     public void selectPracticeScreen() {
         if (tutoringSessionView == null) {
            tutoringSessionView = new TutoringSessionView(); 
            cards.add(tutoringSessionView, TUTOR);
        }

        // Dynamically resize the frame to fit the screen
        // ToDo:
        // code for somewhere else?
        
       // Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
       // int width = (int) (screenSize.width * 0.5); // 50% of the screen width
       // int height = (int) (screenSize.height * 0.56); // 56% of the screen height

       // setPreferredSize(new Dimension(width, height));
       // pack();
        
        // Set the model (session) for the TutoringSessionView
        tutoringSessionView.setModel(tutoringSession);
        
              // Switch to the tutoring session view
        selectPanel(TUTOR);
    }
        
    /**
     * Selects a personalized lesson screen for each user upon selecting
     * the dashboard's practice teach me.
     */
    public void selectLessonScreen() {
        LessonSession session = getLessonSession(); // Retrieve the lesson session

        //Initialize the LessonSessionView if it's not already initialized
        if (this.lessonSessionView == null) {
            this.lessonSessionView = new LessonSessionView(tutoringSession); // Create the lesson session view
            cards.add(lessonSessionView, LESSON);  // Add it to the CardLayout
        }
        
        // Set the model (session) for the LessonSessionView
        this.lessonSessionView.setLessonModel(session);

        // Sets size of lesson screen window.
        // Without this, the window opens too small.
        this.setPreferredSize(new Dimension(1000, 800));
        this.pack();
        
        // Switch to the lesson session view
        selectPanel(LESSON);
        
    }

    /**
     * Display the New User panel, which allows the user to create a new
     * student account with associated sign-in information.
     */
    public void selectNewUser() {
        this.selectPanel(NEW_USER);
    }
    
    public void selectChangeData() {
        this.selectPanel(CHANGE_DATA);
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
        Account account = splashPanel.getModel();
       
        JOptionPane.showMessageDialog(this, 
                account.getUserId() + " is not a known user.\n\nPerhaps, try creating a 'New User' first.",
                "Warning", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Display the Forgot Password panel, which allows the user to verify themselves
     * so that they can reset their password.
     */
    public void selectForgotPassword() {
        this.selectPanel(FORGOT_PASSWORD);
    }
    
    /**
     * Reset the text fields in the forgot password panel to the empty string.
     */
    public void clearForgotPassword() {
        this.forgotPasswordPanel.clearFields();
    }
    
    /**
     * Display the Reset Password panel, which allows the user to reset their
     * password.
     * @param userId
     */
    public void selectResetPassword(String userId) {
        this.selectPanel(RESET_PASSWORD);
    }
    
    /**
     * Reset the text fields in the reset password panel to the empty string.
     */
    public void clearResetPassword() {
        this.resetPasswordPanel.clearFields();
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
        else if (name.equals(NEW_USER)) {
            this.newAccountPanel.updateFocus();
        }
    }
    
    /**
     * Initializes a personalized dashboard screen for each user after sign in.
     * @param session: a reference to the current SplashFrame session instance.
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
     * 
     * @param email
     */
    public void initializeResetPassword(String email) {
        if (email == null) {
            System.err.println("Email is null in initializeResetPassword");
            return;
        }

        this.resetPasswordPanel = new ResetPasswordPanel(email);
        
        this.cards.add(resetPasswordPanel, RESET_PASSWORD);
    }

    /**
     * Create the child GUI components appearing in this frame.
     * 
     * Note, as the Tutoring Session View references this Splash Frame,
     * we delay its creation until it's actually required (as triggered
     * from the Dashboard Panel).
     */
    private void initializeComponents() {
        cards = new JPanel(new CardLayout());
        
        splashPanel = new SplashPanel();
        newAccountPanel = new NewAccountPanel();
        forgotPasswordPanel = new ForgotPasswordPanel();
        changeUserDataPanel = new ChangeUserDataPanel();
       
                      
        cards.add(splashPanel, SPLASH);
        cards.add(newAccountPanel, NEW_USER);
        cards.add(changeUserDataPanel, CHANGE_DATA);
        cards.add(forgotPasswordPanel, FORGOT_PASSWORD); 
        
    }
}
