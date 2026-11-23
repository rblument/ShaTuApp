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

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import edu.regis.shatu.view.MainFrame;

/**
 * An inactivity tracker that tracks the mouse movement and keyboard
 * strokes of users. If the user is inactive for 30 minutes they will
 * be returned to the login screen.
 * 
 * Tracking only runs while a user is signed in: it starts at sign-in and must
 * be stopped whenever the user is logged out (see SignOutAction and
 * BackToLogin), otherwise a signed-out user would be "logged out due to
 * inactivity" 30 minutes later.
 * 
 * @author austenj
 */
public class InactivityManager {
    /** The time a signed-in user may be idle: 30 minutes in milliseconds. */
    private static final int inactive = 30 * 60 * 1000;

    /** The single tracker shared by the sign-in and sign-out actions. */
    private static final InactivityManager SINGLETON = new InactivityManager();

    /**
     * Return the single inactivity manager.
     *
     * @return the shared InactivityManager
     */
    public static InactivityManager instance() {
        return SINGLETON;
    }

    /** How long, in milliseconds, the user may be idle. */
    private final int timeout;

    private Timer inactivityTimer;

    /** True from startTracking() until stopTracking() or the timeout. */
    private boolean tracking = false;

    /** True once the global activity listener has been registered. */
    private boolean listening = false;

    /**
     * Create a manager that logs the user out after 30 minutes of inactivity.
     */
    public InactivityManager() {
        this(inactive);
    }

    /**
     * Create a manager with the given idle time (for tests).
     *
     * @param timeoutMillis how long, in milliseconds, the user may be idle
     */
    InactivityManager(int timeoutMillis) {
        this.timeout = timeoutMillis;
    }

    /**
     * Starts the Inactivity Tracker.
     * 
     * Also starts tracking mouse movement/mouse clicks and keyboard strokes.
     */
    public void startTracking() {
        // Initializing timer
        if (inactivityTimer == null) {
            inactivityTimer = new Timer(timeout, e -> timedOut());
            inactivityTimer.setRepeats(false);
        }

        // Add the global event listener for activity tracking (once)
        if (!listening) {
            listening = true;
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

        tracking = true;
        inactivityTimer.restart();
    }
    
    /**
     * Resets the inactivity tracker, but only while a user is being tracked.
     * (Timer.restart() would also start a stopped timer, so the mouse and
     * keyboard activity of a signed-out user must be ignored.)
     */
    public void resetInactivityTimer() {
        if (tracking && inactivityTimer != null) {
            inactivityTimer.restart();
        }
    }

    /**
     * Stops the inactivity tracker.
     */
    public void stopTracking() {
        tracking = false;

        if (inactivityTimer != null) {
            inactivityTimer.stop();
        }
    }

    /**
     * Returns whether a user is currently being tracked for inactivity.
     *
     * @return true, if tracking has been started and not stopped
     */
    public boolean isTracking() {
        return tracking;
    }

    /**
     * The inactivity timer expired.
     */
    private void timedOut() {
        // Stop first, so the user's activity while the dialog is displayed
        // doesn't start the timer again.
        stopTracking();

        inactiveLogout();
    }

    /**
     * calls BackToLogin() and logs the user out when they go inactive.
     * 
     * Also provides a dialog box explaining why they were logged out.
     */
    void inactiveLogout() {
        // Logout warning
        JOptionPane.showMessageDialog(
            MainFrame.instance(),
            "You have been logged out due to inactivity.",
            "Session Expired",
            JOptionPane.WARNING_MESSAGE
        );

        // Log out and display the sign-in screen. The frame must stay visible,
        // nothing displays it again once it is hidden.
        BackToLogin.instance().actionPerformed(null);
    }
}
