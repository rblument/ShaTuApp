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
package edu.regis.shatu.view.act;

import edu.regis.shatu.view.MainFrame;
import edu.regis.shatu.view.act.BackToLogin;
import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 * An inactivity tracker that tracks the mouse movement and keyboard
 * strokes of users. If the user is inactive for 30 minutes they will
 * be returned to the login screen.
 * 
 * @author austenj
 */
public class InactivityManager {
    private Timer inactivityTimer;
    // 30 minutes in milliseconds
    private static final int inactive = 30 * 60 * 1000;

    /**
     * Starts the Inactivity Tracker.
     * 
     * Also starts tracking mouse movement/mouse clicks and keyboard strokes.
     */
    public void startTracking() {
        // Initializing timer
        if (inactivityTimer == null) {
            inactivityTimer = new Timer(inactive, e -> inactiveLogout());
            inactivityTimer.setRepeats(false);

            // Add global event listeners for activity tracking
            Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
                @Override
                public void eventDispatched(AWTEvent event) {
                    if (event instanceof MouseEvent || event instanceof KeyEvent) {
                        resetInactivityTimer();
                    }
                }
            }, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK 
                    | AWTEvent.KEY_EVENT_MASK);
        }

        inactivityTimer.start();
    }
    
    /**
     * Resets the inactivity tracker.
     */
    public void resetInactivityTimer() {
        if (inactivityTimer != null) {
            inactivityTimer.restart();
        }
    }

    /**
     * Stops the inactivity tracker.
     */
    public void stopTracking() {
        if (inactivityTimer != null) {
            inactivityTimer.stop();
        }
    }

    /**
     * calls BackToLogin() and logs the user out when they go inactive.
     * 
     * Also provides a dialog box explaining why they were logged out.
     */
    private void inactiveLogout() {
        if (inactivityTimer != null) {
        inactivityTimer.stop(); //stopping timer
        }

        // Logout warning
        JOptionPane.showMessageDialog(
            MainFrame.instance(),
            "You have been logged out due to inactivity.",
            "Session Expired",
            JOptionPane.WARNING_MESSAGE
        );

     // Hide mainframe and logout
     MainFrame.instance().setVisible(false);
     BackToLogin.instance().actionPerformed(null);
    }
}
