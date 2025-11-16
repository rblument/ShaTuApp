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

import edu.regis.shatu.model.Account;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.regis.shatu.model.TutoringSession;
import java.awt.CardLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * The primary GUI window in the ShaTu application.
 *
 * Most of the display functionality is handled by child component views.
 *
 * @author rickb
 */
public class MainFrame extends JFrame implements WindowListener {

    /**
     * The singleton instance of this frame.
     */
    private final static MainFrame SINGLETON;

    // Invoked when this class is loaded
    static {
        SINGLETON = new MainFrame();
    }

    /**
     * Return the singleton instance of this frame.
     *
     * @return the MainFrame singleton
     */
    public static MainFrame instance() {
        return SINGLETON;
    }

    /**
     * The size of this frame will the size of the user's screen minus this
     * screen size inset.
     */
    private static final int SCREEN_SIZE_INSET = 50;
    
    /**
     * Allowed consecutive illegal passwords before the user is locked out.
     */
    public static final int MAX_SIGNIN_ATTEMPTS = 3;

    /**
     * The SHA tutoring session displayed in this frame.
     */
    private TutoringSession model;

    /**
     * The view currently displayed in the card panel.
     */
    private ViewName displayedView;

    /**
     * The card panel displayed in the content pane of this frame, which is
     * capable of displaying various views (see displayedView).
     */
    private JPanel cardPanel;

    /**
     * The tutoring session view.
     */
    private TutoringSessionView tutorSessionView;

    /**
     * The splash panel, which also allows signing in.
     */
    private SplashPanel splashPanel;

    private NewAccountPanel newAccountPanel;

    private ResetPasswordPanel resetPasswordPanel;

    private ForgotPasswordPanel forgotPasswordPanel;
    
    private DashboardPanel dashboardPanel;
    
    /**
     * The number of consecutive illegal passwords attempted by the current
     * user attempting to login (see MAX_SIGNIN_ATTEMPTS).
     */
    protected int signInAttempts = 0;

    private Timer inactivityTimer; // Timer for inactivity tracking

    /**
     * Initialize and layout the child components displayed in this frame.
     */
    private MainFrame() {
        super("ShaTut");

        // Get screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Calculate the initial size (80% of the splashFrame width)
        int width = (int) (screenSize.width * 0.5);
        int height = (int) (screenSize.height);
        setSize(width, height);

        setJMenuBar(new ShaTuMenuBar());

        initializeComponents();
        layoutComponents();

        addWindowListener(this);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // see windowClosing()

        displayView(ViewName.SPLASH);
        
        setVisible(true);
    }

    /**
     * Update the current model with changes made in this frame and return it.
     *
     * @return the Session model
     */
    public TutoringSession getModel() {
        updateModel();

        return model;
    }

    /**
     * Display the given model in the tutoring session view of this frame.
     *
     * @param model a TutoringSession model or null to logout.
     */
    public void setModel(TutoringSession model) {
        this.model = model;

        Account account;
   
        if (model == null) { // signing out
            account = new Account();
        } else {
            account = model.getStudent().getAccount();
        }
            
        tutorSessionView.setModel(model); 
        splashPanel.setModel(account);
        forgotPasswordPanel.setModel(account);
        resetPasswordPanel.setModel(account);
        dashboardPanel.setModel(model);
        
        if (model == null) { // Signed out
            displayView(ViewName.SPLASH);
        } else {
            displayView(ViewName.TUTOR);
        }
    }

    /**
     * Display the given view in this frame.
     *
     * @param name the name of the view to display.
     */
    public void displayView(ViewName name) {
        CardLayout cl = (CardLayout) cardPanel.getLayout();

        cl.show(cardPanel, name.toString());

        displayedView = name;
    }
    
    // assumes the tutoring session view is displayed.
    public void displayStep(StepSelection selection) {
        if (displayedView != ViewName.TUTOR)
            displayView(ViewName.TUTOR);
        
        tutorSessionView.displayStep(selection);
    }
    
    /**
     * Return the current account, which depends on the view currently being
     * displayed.
     * 
     * @return 
     */
    public Account getAccount() {
        switch (displayedView) {
            case DASHBOARD:
            case TUTOR:
                model.getStudent().getAccount();
                
            case SPLASH:
                return splashPanel.getModel();
                
            case FORGOT_PASSWORD:
                return forgotPasswordPanel.getModel();
                
            case RESET_PASSWORD:
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
        if (signInAttempts < MAX_SIGNIN_ATTEMPTS) {
           
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
     * Reset the text fields in the new account panel to the empty string.
     */
    public void clearNewAccountPanel() {
        newAccountPanel.clearFields();
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        // ToDo: Save etc.
        this.dispose();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    /**
     * Create the child components used in this frame.
     */
    private void initializeComponents() {
        cardPanel = new JPanel();
        cardPanel.setLayout(new CardLayout());

        dashboardPanel = new DashboardPanel();
        forgotPasswordPanel = new ForgotPasswordPanel();
        newAccountPanel = new NewAccountPanel();
        resetPasswordPanel = new ResetPasswordPanel();
        splashPanel = new SplashPanel();
        tutorSessionView = new TutoringSessionView();
    }

    /**
     * Adds the child component panels/views to the card layout, which is then
     * set as the content pane of this frame.
     */
    private void layoutComponents() {
        cardPanel.add(tutorSessionView, ViewName.TUTOR.toString());
        cardPanel.add(splashPanel, ViewName.SPLASH.toString());
        cardPanel.add(newAccountPanel, ViewName.NEW_ACCOUNT.toString());
        cardPanel.add(resetPasswordPanel, ViewName.RESET_PASSWORD.toString());
        cardPanel.add(forgotPasswordPanel, ViewName.FORGOT_PASSWORD.toString());
        cardPanel.add(dashboardPanel, ViewName.DASHBOARD.toString());


        setContentPane(cardPanel);
    }

    /**
     * Update the current model with any changes made in this frame's view.
     */
    private void updateModel() {

    }

    /**
     * Display the current model in this frame's view.
     */
    private void updateView() {
    }

    /**
     * The sub-views that can be displayed in the MainFrame;
     */
    public enum ViewName {
        DASHBOARD,
        FORGOT_PASSWORD,
        NEW_ACCOUNT,
        RESET_PASSWORD,
        SPLASH,
        TUTOR;
    }
}
