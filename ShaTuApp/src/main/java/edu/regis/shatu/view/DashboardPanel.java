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

//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Cursor;
//import java.awt.Dimension;
//import java.awt.GridBagConstraints;
import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;

import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.svc.ServiceFactory;
import edu.regis.shatu.svc.StudentModelSvc;
import edu.regis.shatu.util.CustomProgressBar;
import java.util.List;

/**
 * The dashboard screen to be displayed upon user sign in. Enables user to
 * select a mode from the tutor (teach me, practice, quiz me) Tracks user's
 * progress for each mode.
 *
 * @author Ryley MacLagan, Rickb
 */
public class DashboardPanel extends javax.swing.JPanel {

    private TutoringSession model; // Reference to current tutoringSession
    private static boolean welcome = false;
 
    private JButton logOutButton;
    private JButton practiceButton1;
    private JProgressBar practiceProgressBar1;
    private JProgressBar quizMeProgressBar1;
    private JButton quizeMeButton1;
    private JButton settingsButton;
    private JButton changeUserDataButton;
    private JButton teachMeButton1;
    private JProgressBar teachMeProgressBar1;
    private JLabel welcomeLabel;

    public DashboardPanel(TutoringSession tutoringSession) {
        model = tutoringSession;

        if (welcome == false) {
            welcome = true;
            System.out.println("DashboardPanel initialized for user: "
                    + tutoringSession.getStudent().getAccount().getFirstName());
            String welcomeMessage = "Welcome, "
                    + tutoringSession.getStudent().getAccount().getFirstName() + "! "
                    + "Your session has successfully started.";
            JOptionPane.showMessageDialog(
                    null,
                    welcomeMessage,
                    "Welcome",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }

        initializeComponents();
        layoutComponents();
    }
    
    public void setModel(TutoringSession model) {
        this.model = model;
    }

    private void initializeComponents() {
        GridBagConstraints gridBagConstraints;

        logOutButton = new JButton();
        settingsButton = new JButton();
        changeUserDataButton = new JButton();
        welcomeLabel = new JLabel();
        teachMeButton1 = new JButton();
        practiceButton1 = new JButton();
        quizeMeButton1 = new JButton();
        practiceProgressBar1 = new CustomProgressBar();
        teachMeProgressBar1 = new CustomProgressBar();
        quizMeProgressBar1 = new CustomProgressBar();
        
        // Attach tooltips to show incomplete lessons on hover
        teachMeProgressBar1.setToolTipText(getIncompleteLessons("Teach Me"));
        teachMeButton1.setToolTipText(getIncompleteLessons("Teach Me"));

        practiceProgressBar1.setToolTipText(getIncompleteLessons("Practice"));
        practiceButton1.setToolTipText(getIncompleteLessons("Practice"));

        quizMeProgressBar1.setToolTipText(getIncompleteLessons("Quiz Me"));
        quizeMeButton1.setToolTipText(getIncompleteLessons("Quiz Me"));


        logOutButton.setText("Log Out");
        logOutButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        logOutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logOutButtonMouseClicked(evt);
            }
        });
        logOutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logOutButtonActionPerformed(evt);
            }
        });   

        settingsButton.setText("Settings");
        //settingsButton.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        
        changeUserDataButton.setText("Change My Data");
        changeUserDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeUserDataButtonActionPerformed(evt);
            }
        });
                
                
        //changeUserDataButton.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        //changeUserDataButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        welcomeLabel.setBackground(new java.awt.Color(0, 43, 73));
        welcomeLabel.setOpaque(true);
        welcomeLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        welcomeLabel.setForeground(new java.awt.Color(241, 196, 0));
        welcomeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        welcomeLabel.setLabelFor(this);
        welcomeLabel.setText("Welcome!");
        welcomeLabel.setToolTipText("");
        welcomeLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        welcomeLabel.setText("Welcome, " + model.getStudent().getAccount().getFirstName() + "!");

        teachMeButton1.setText("Teach Me");
        teachMeButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teachMeButton1ActionPerformed(evt);
            }
        });
      
        practiceButton1.setText("Practice");
        practiceButton1.setActionCommand("practice");
        practiceButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                practiceButton1ActionPerformed(evt);
            }
        });
       
        quizeMeButton1.setText("Quiz Me");
      
        practiceProgressBar1.setBackground(new java.awt.Color(0, 43, 73));
        practiceProgressBar1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        practiceProgressBar1.setForeground(new java.awt.Color(241, 196, 0, 235));
        practiceProgressBar1.setOrientation(1);
        practiceProgressBar1.setValue(50);
        practiceProgressBar1.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.white, Color.white, Color.white));
        practiceProgressBar1.setString("");
        practiceProgressBar1.setStringPainted(true);
       
        teachMeProgressBar1.setBackground(new java.awt.Color(0, 43, 73));
        teachMeProgressBar1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        teachMeProgressBar1.setForeground(new java.awt.Color(241, 196, 0, 235));
        teachMeProgressBar1.setOrientation(1);
        teachMeProgressBar1.setValue(100);
        teachMeProgressBar1.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.white, Color.white, Color.white));
       
        quizMeProgressBar1.setBackground(new java.awt.Color(0, 43, 73));
        quizMeProgressBar1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        quizMeProgressBar1.setForeground(new java.awt.Color(241, 196, 0, 235));
        quizMeProgressBar1.setOrientation(1);
        quizMeProgressBar1.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.white, Color.white, Color.white));
        quizMeProgressBar1.setString("");
        quizMeProgressBar1.setStringPainted(true);
        
    }
    
    private void layoutComponents() {
    setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    setMaximumSize(new Dimension(32767, 32767));
    setMinimumSize(new Dimension(0, 0));
    setPreferredSize(new Dimension(986, 480));
    setLayout(new BorderLayout());
    
    JPanel headerPanel = new JPanel();
    headerPanel.setLayout(new BorderLayout());
    headerPanel.add(logOutButton, BorderLayout.LINE_END);

    // Create a sub-panel for settings and changeUserData buttons
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Align buttons to the left
    buttonPanel.add(settingsButton);
    buttonPanel.add(changeUserDataButton);

    headerPanel.add(buttonPanel, BorderLayout.LINE_START); // Add the button panel to the start position
    headerPanel.add(welcomeLabel, BorderLayout.CENTER);

    add(headerPanel, BorderLayout.NORTH);

    GPanel contentPanel = new GPanel();
    contentPanel.setBackground(new java.awt.Color(0, 43, 73));
    contentPanel.setAlignmentX(1.0F);
    contentPanel.setCursor(new java.awt.Cursor(Cursor.DEFAULT_CURSOR));

   
              //gridBagConstraints.ipadx = 121;
        //gridBagConstraints.ipady = 65; 
       contentPanel.addc(teachMeProgressBar1, 0,0, 1,1, 1.0,1.0, 
                         GridBagConstraints.NORTH,GridBagConstraints.BOTH,
	                  20,5,0,0);
       
        contentPanel.addc(practiceProgressBar1, 1,0, 1,1, 1.0,1.0, 
                          GridBagConstraints.NORTH,GridBagConstraints.BOTH,
	                  20,20,0,0);
        
        contentPanel.addc(quizMeProgressBar1, 2,0, 1,1, 1.0,1.0, 
                          GridBagConstraints.NORTH,GridBagConstraints.BOTH,
	                  20,20,0,5);
        
        contentPanel.addc(teachMeButton1, 0,1, 1,1, 0.0,0.0,
	                  GridBagConstraints.CENTER,  GridBagConstraints.HORIZONTAL,
	                  10,5,0,0);	

        contentPanel.addc(practiceButton1, 1,1, 1,1, 0.0, 0.0,
                          GridBagConstraints.SOUTH,GridBagConstraints.HORIZONTAL,
	                  10,20,0,0);	
        
        contentPanel.addc(quizeMeButton1, 2,1, 1,1, 0.0,0.0, 
                          GridBagConstraints.SOUTH,GridBagConstraints.HORIZONTAL,
	                  10,20,0,5);        
        
        add(contentPanel, java.awt.BorderLayout.CENTER);
       
    }

    
    private void changeUserDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_
        SplashFrame.instance().selectChangeData();
    }
    
    private void practiceButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_practiceButton1ActionPerformed
        SplashFrame.instance().selectPracticeScreen();
    }

    private void logOutButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logOutButtonMouseClicked
        SplashFrame.instance().logout();
    }

    private void teachMeButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teachMeButton1ActionPerformed
        SplashFrame.instance().selectLessonScreen();
    }

    private void logOutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logOutButtonActionPerformed
        // TODO add your handling code here:
    }

    /**
    * Retrieves the list of incomplete lessons for the given category.
    *
    * @param category The category (Teach Me, Practice, Quiz Me).
    * @return A formatted string of incomplete lessons.
    */
   private String getIncompleteLessons(String category) {
        try {
            String userId = model.getStudent().getAccount().getUserId();
            StudentModelSvc studentModelService = ServiceFactory.findStudentModelSvc();

            List<String> incompleteLessons = studentModelService.retrieveIncompleteLessons(userId, category);

            if (incompleteLessons.isEmpty()) {
                return "No lessons remaining in " + category + "!";
            } else {
                return "<html>" + category + " To Do:<br>" + String.join("<br>", incompleteLessons) + "</html>";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error loading lessons: " + e.getMessage();
        }
    }




}
