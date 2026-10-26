package edu.regis.shatu.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.Task;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.aol.TutoringMode;
import edu.regis.shatu.model.steps.EncodeAsciiStep;
import edu.regis.shatu.model.steps.Step;

class EncodeViewTest {
    private final Gson gson = new Gson();

    @Test
    void acceptsPrintableCustomQuestionWithoutTrimmingSpaces() throws Exception {
        AtomicReference<EncodeView> ref = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> ref.set(new EncodeView()));

        SwingUtilities.invokeAndWait(() -> {
            EncodeView view = ref.get();
            field(view, "setQuestionField").setText(" A ");
            EncodeAsciiStep request = gson.fromJson(view.newRequest().getData(), EncodeAsciiStep.class);
            assertEquals(" A ", request.getQuestion());

            field(view, "setQuestionField").setText("caf\u00e9");
            assertFalse(view.newExampleButton.isEnabled());
            assertTrue(instructions(view).getText().contains("printable ASCII"));
            assertThrows(IllegalArgumentException.class, view::newRequest);

            field(view, "setQuestionField").setText(" A ");
            assertTrue(view.newExampleButton.isEnabled());
        });
    }

    @Test
    void randomQuestionLengthMustBeAPositiveBoundedNumber() throws Exception {
        AtomicReference<EncodeView> ref = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> ref.set(new EncodeView()));

        SwingUtilities.invokeAndWait(() -> {
            EncodeView view = ref.get();
            JTextField length = field(view, "messageLengthField");
            length.setText("56");
            EncodeAsciiStep request = gson.fromJson(view.newRequest().getData(), EncodeAsciiStep.class);
            assertEquals(56, request.getMessageLength());

            for (String invalid : new String[] { "", "abc", "-1", "0", "1025" }) {
                length.setText(invalid);
                assertFalse(view.newExampleButton.isEnabled());
                assertTrue(instructions(view).getText().contains("message length"));
                assertThrows(IllegalArgumentException.class, view::newRequest);
            }
            length.setText("1");
            assertTrue(view.newExampleButton.isEnabled());
        });
    }

    @Test
    void answerCanBeGroupedAcrossSpacesAndLines() throws Exception {
        AtomicReference<EncodeView> ref = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            EncodeView view = new EncodeView();
            EncodeAsciiStep problem = new EncodeAsciiStep();
            problem.setQuestion("A ");
            problem.setResult("0100000100100000");
            Step step = new Step(1, 0, StepSubType.ENCODE_ASCII);
            step.setData(gson.toJson(problem));
            PendingTask pending = new PendingTask(new Task());
            pending.setCurrentStep(step);
            TutoringSession session = new TutoringSession(new Student(new Account("test@regis.edu")));
            session.setTutoringMode(TutoringMode.DO_ONE);
            session.addCurrentTask(pending);
            view.setModel(session);
            ref.set(view);
        });

        SwingUtilities.invokeAndWait(() -> {
            EncodeView view = ref.get();
            textArea(view, "responseTextArea").setText("0100 0001\n0010 0000");
            EncodeAsciiStep submitted = gson.fromJson(view.stepCompletion().getData(), EncodeAsciiStep.class);
            assertEquals("A ", submitted.getQuestion());
            assertEquals("0100000100100000", submitted.getAscii());
        });
    }

    private static JTextField field(EncodeView view, String name) {
        try {
            Field field = EncodeView.class.getDeclaredField(name);
            field.setAccessible(true);
            return (JTextField) field.get(view);
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError(ex);
        }
    }

    private static JTextArea textArea(EncodeView view, String name) {
        try {
            Field field = EncodeView.class.getDeclaredField(name);
            field.setAccessible(true);
            return (JTextArea) field.get(view);
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError(ex);
        }
    }

    private static JLabel instructions(EncodeView view) {
        try {
            Field field = EncodeView.class.getDeclaredField("instructionsLabel");
            field.setAccessible(true);
            return (JLabel) field.get(view);
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError(ex);
        }
    }
}
