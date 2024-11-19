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
 *
 * @author austenj
 */
public class InactivityManager {
    private Timer inactivityTimer;
    // 10 minutes in milliseconds
    private static final int inactive = 600000;

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

    public void resetInactivityTimer() {
        if (inactivityTimer != null) {
            inactivityTimer.restart();
        }
    }

    public void stopTracking() {
        if (inactivityTimer != null) {
            inactivityTimer.stop();
        }
    }

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

     // Hide the main application window and log out
     MainFrame.instance().setVisible(false);
     BackToLogin.instance().actionPerformed(null); // Log out the user
    }
}
