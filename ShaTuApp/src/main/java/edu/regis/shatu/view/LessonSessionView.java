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

import java.awt.GridBagConstraints;

import javax.swing.JButton;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.LessonSession;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.TutoringSession;

/**
 *
 * @author mwemapowanga
 */
public class LessonSessionView extends GPanel {
    /**
     * Universal button for returning to the dashboard.
     */
    private JButton dashboardButton;
    
    /**
     * Universal button for logging out of the tutoring system
     */
    private JButton logOutButton;
    
    /**
     * The lesson session model displayed in this view.
     */
    private LessonSession model;
    
    /**
     * The tutoring session model for the dashboard button
     */
    private TutoringSession tutoringSessionModel;

    /**
     * The lesson step selector view displayed in the left panel of this view
     */
    private LessonStepSelectorView lessonStepSelectorView;

    /**
     * A card panel capable of displaying different views of various steps.
     */
    private LessonStepView lessonStepView;
    
    /**
     * Account for the dashboard button Welcome message
     */
    private Account account;

    /**
     * Initialize this view including creating and laying out its child components.
     * 
     * @param tutoringSession
     */
    public LessonSessionView(TutoringSession tutoringSession) {
       
        this.tutoringSessionModel = tutoringSession;
        

        if (tutoringSession == null) {
            System.err.println("TutoringSession is null in DashboardPanel constructor");
        } else if (tutoringSession.getStudent().getAccount() == null) {
            account = new Account(); // Create default account
            account.setFirstName(" "); //Set default first name for dashboard button Welcome message
            Student student = new Student(account);
            tutoringSession.setStudent(student); //Set default account for dashboard button Welcome message
            System.err.println("Account is null in DashboardPanel. Added");
        } else {
            System.out.println("DashboardPanel initialized for user: " + tutoringSession.getStudent().getAccount().getFirstName());
        }
    
        initializeComponents();
        layoutComponents();
    }

    /**
     * Return the model currently displayed in this view.
     *
     * @return a LessonSession
     */
    public LessonSession getLessonModel() {
        return model;
    }
    
    /**
     * Return the model currently displayed in this view.
     *
     * @return a LessonSession
     */
    
    public TutoringSession getModel() {
        return tutoringSessionModel;
    }
    
    
    /**
     * Display the given model in this view.
     *
     * @param model a LessonSession.
     */
    public void setLessonModel(LessonSession model) {
        this.model = model;

    }

    /**
     * Create the child GUI components appearing in this frame.
     */
    private void initializeComponents() {
        // LessonStepView must be created before LessonStepSelectorView since the later
        // references the EncodeView in LessonStepView by selecting it.
        lessonStepView = new LessonStepView(); 
        lessonStepSelectorView = new LessonStepSelectorView();
        
        //button for returning to dashboard
        dashboardButton = new JButton("Go to Dashboard");
        dashboardButton.addActionListener(e -> navigateToDashboard());
        
        //button log out of the tutoring system
        logOutButton = new JButton("Log Out");
        logOutButton.addActionListener(e -> LogOut());
        
    }
    
    /**
     * Layout the child components in this view
     */
    private void layoutComponents() {

        addc(lessonStepSelectorView, 0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                5, 5, 5, 5);
        addc(lessonStepView, 1, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                5, 5, 5, 5);
        addc(dashboardButton, 0, 2, 1, 1, 0.1, 0.1,
                GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE,
                5, 5, 5, 5);
        addc(logOutButton, 0, 2, 0, 1, 0.1, 0.1,
                GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
                5, 5, 5, 5);
    }
    
    /**
     * Log Out of Tutoring System
     */
    public void LogOut() {
        
        SplashFrame.instance().selectSplash();
    }
  
    /**
     * Dashboard Button to return to dashboard.
     */
    public void navigateToDashboard() {
        
        SplashFrame.instance().selectDashboard(this.getModel());
    }
}

