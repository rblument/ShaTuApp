package edu.regis.shatu.view.act;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Checks that inactivity tracking only runs while a user is signed in.
 */
class InactivityManagerTest {

    /** Idle time used by the tests. */
    private static final int TIMEOUT = 300;

    /**
     * Long enough for a timer to have fired if it was running. The margins
     * are generous so a slow, busy build machine doesn't make the tests flaky.
     */
    private static final int WAIT = 1500;

    @BeforeAll
    static void setupHeadlessMode() {
        System.setProperty("java.awt.headless", "true");
    }

    /** A manager that counts logouts instead of displaying the dialog. */
    private static class CountingManager extends InactivityManager {
        final AtomicInteger logouts = new AtomicInteger();

        CountingManager() {
            super(TIMEOUT);
        }

        @Override
        void inactiveLogout() {
            logouts.incrementAndGet();
        }
    }

    /** Run the given action on the event thread, then let time pass. */
    private static void onEventThread(Runnable action) throws Exception {
        SwingUtilities.invokeAndWait(action);
    }

    private static void idle() throws Exception {
        Thread.sleep(WAIT);
        SwingUtilities.invokeAndWait(() -> { }); // flush pending timer events
    }

    @Test
    void anIdleSignedInUserIsLoggedOutOnce() throws Exception {
        CountingManager manager = new CountingManager();

        onEventThread(manager::startTracking);
        idle();

        assertEquals(1, manager.logouts.get());
        assertFalse(manager.isTracking());
    }

    @Test
    void aSignedOutUserIsNeverLoggedOutForInactivity() throws Exception {
        CountingManager manager = new CountingManager();

        onEventThread(() -> {
            manager.startTracking();
            manager.stopTracking(); // the user signed out
        });
        idle();

        assertEquals(0, manager.logouts.get());
    }

    @Test
    void activityAfterSigningOutDoesNotRestartTheTimer() throws Exception {
        CountingManager manager = new CountingManager();

        onEventThread(() -> {
            manager.startTracking();
            manager.stopTracking();
            manager.resetInactivityTimer(); // e.g., a mouse move on the sign-in screen
        });
        idle();

        assertEquals(0, manager.logouts.get());
        assertFalse(manager.isTracking());
    }

    @Test
    void activityKeepsASignedInUserFromTimingOut() throws Exception {
        CountingManager manager = new CountingManager();

        onEventThread(manager::startTracking);
        // Activity every 100 ms for 800 ms, well past the 300 ms timeout
        for (int i = 0; i < 8; i++) {
            Thread.sleep(100);
            onEventThread(manager::resetInactivityTimer);
        }

        assertEquals(0, manager.logouts.get());
        assertTrue(manager.isTracking());

        onEventThread(manager::stopTracking);
    }

    @Test
    void signingInAgainAfterSigningOutTracksAgain() throws Exception {
        CountingManager manager = new CountingManager();

        onEventThread(() -> {
            manager.startTracking();
            manager.stopTracking();
            manager.startTracking();
        });
        idle();

        assertEquals(1, manager.logouts.get());
    }
}
