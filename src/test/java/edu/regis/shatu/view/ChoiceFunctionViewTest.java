package edu.regis.shatu.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;

class ChoiceFunctionViewTest {
    @Test
    void explanationUsesChoiceRuleAndKeepsFullWidth() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ChoiceFunctionView view = new ChoiceFunctionView();
            assertEquals("1010", choice(view, "0101", "0000", "1111"));
            assertEquals("11110000111100001111000011110000",
                    choice(view, "00000000000000000000000000000000",
                            "00000000000000000000000000000000",
                            "11110000111100001111000011110000"));
            assertEquals("01", choice(view, "1 0", "0 0", "1 1"));
            assertThrows(IllegalArgumentException.class, () -> choice(view, "10", "1", "00"));
        });
    }

    @Test
    void enterSubmitsFilledAnswerOnceAndDoesNotInsertNewline() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ChoiceFunctionView view = new ChoiceFunctionView();
            JTextArea response = response(view);
            JButton check = view.getCheckButton();
            for (ActionListener listener : check.getActionListeners()) {
                check.removeActionListener(listener);
            }
            AtomicInteger submissions = new AtomicInteger();
            check.addActionListener(event -> submissions.incrementAndGet());
            check.setEnabled(true);
            response.setText("0101");
            KeyEvent enter = new KeyEvent(response, KeyEvent.KEY_PRESSED, 1, 0,
                    KeyEvent.VK_ENTER, '\n');
            view.keyPressed(enter);
            assertEquals(1, submissions.get());
            assertTrue(enter.isConsumed());
            assertEquals("0101", response.getText());

            response.setText("   ");
            view.keyPressed(new KeyEvent(response, KeyEvent.KEY_PRESSED, 2, 0,
                    KeyEvent.VK_ENTER, '\n'));
            assertEquals(1, submissions.get());
            check.setEnabled(false);
            response.setText("1010");
            view.keyPressed(new KeyEvent(response, KeyEvent.KEY_PRESSED, 3, 0,
                    KeyEvent.VK_ENTER, '\n'));
            assertEquals(1, submissions.get());
            assertTrue(java.util.Arrays.asList(response.getKeyListeners()).contains(view));
        });
    }

    private static JTextArea response(ChoiceFunctionView view) {
        try {
            Field field = ChoiceFunctionView.class.getDeclaredField("responseTextArea");
            field.setAccessible(true);
            return (JTextArea) field.get(view);
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError(ex);
        }
    }

    private static String choice(ChoiceFunctionView view, String x, String y, String z) {
        try {
            Method method = ChoiceFunctionView.class.getDeclaredMethod(
                    "choiceFunction", String.class, String.class, String.class);
            method.setAccessible(true);
            return (String) method.invoke(view, x, y, z);
        } catch (InvocationTargetException ex) {
            if (ex.getCause() instanceof RuntimeException runtime) {
                throw runtime;
            }
            throw new AssertionError(ex.getCause());
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError(ex);
        }
    }
}
