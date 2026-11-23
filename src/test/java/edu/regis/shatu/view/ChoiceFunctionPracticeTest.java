package edu.regis.shatu.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.Task;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.aol.TutoringMode;
import edu.regis.shatu.model.steps.ChoiceFunctionStep;
import edu.regis.shatu.model.steps.Step;

class ChoiceFunctionPracticeTest {
    private final Gson gson = new Gson();

    @Test
    void doOneShowsQuestionAndResetsAnswerForNextExample() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ChoiceFunctionView view = new ChoiceFunctionView();
            TutoringSession session = session(TutoringMode.DO_ONE,
                    example("1010", "1100", "0011", 4));
            view.setModel(session);

            assertEquals("x: 1010", field(view, "stringXLabel", JLabel.class).getText());
            assertEquals("y: 1100", field(view, "stringYLabel", JLabel.class).getText());
            assertEquals("z: 0011", field(view, "stringZLabel", JLabel.class).getText());
            JTextArea answer = field(view, "responseTextArea", JTextArea.class);
            assertTrue(answer.isEnabled());
            assertTrue(view.getCheckButton().isEnabled());
            assertTrue(view.getHintButton().isEnabled());
            assertTrue(field(view, "fourRadioButton", JRadioButton.class).isSelected());

            answer.setText("10 01");
            StepCompletion completion = view.stepCompletion();
            ChoiceFunctionStep submitted = gson.fromJson(completion.getData(), ChoiceFunctionStep.class);
            assertEquals("1001", submitted.getResult());
            assertEquals("1010", submitted.getOperand1());
            assertEquals(4, submitted.getBitLength());

            // Request creation should leave the current answer visible if the tutor rejects it.
            assertEquals(4, gson.fromJson(view.newRequest().getData(), ChoiceFunctionStep.class).getBitLength());
            assertEquals("10 01", answer.getText());

            view.setCurrentTask(task(example("10101010", "11110000", "00001111", 8)));
            assertEquals("x: 10101010", field(view, "stringXLabel", JLabel.class).getText());
            assertEquals("", answer.getText());
            assertTrue(field(view, "eightRadioButton", JRadioButton.class).isSelected());
            assertEquals(8, gson.fromJson(view.newRequest().getData(), ChoiceFunctionStep.class).getBitLength());
        });
    }

    @Test
    void teachOneAllowsAnswersAndSeeOneExplainsWithoutEnablingCheck() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ChoiceFunctionView practice = new ChoiceFunctionView();
            practice.setModel(session(TutoringMode.TEACH_ONE,
                    example("0101", "0000", "1111", 4)));
            assertTrue(field(practice, "responseTextArea", JTextArea.class).isEnabled());
            assertTrue(practice.getCheckButton().isEnabled());

            ChoiceFunctionView see = new ChoiceFunctionView();
            see.setModel(session(TutoringMode.SEE_ONE,
                    example("0101", "0000", "1111", 4)));
            JTextArea explanation = field(see, "responseTextArea", JTextArea.class);
            assertTrue(explanation.getText().contains("The full answer is 1010"));
            assertFalse(explanation.isEnabled());
            assertFalse(see.getCheckButton().isEnabled());
            assertFalse(see.getHintButton().isEnabled());
            assertFalse(field(see, "fourRadioButton", JRadioButton.class).isEnabled());
        });
    }

    @Test
    void missingExampleKeepsPracticeButtonsDisabledAndSizeSelectionRequestsNewExample() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ChoiceFunctionView empty = new ChoiceFunctionView();
            TutoringSession noTask = new TutoringSession(new Student(new Account("test@regis.edu")));
            noTask.setTutoringMode(TutoringMode.DO_ONE);
            empty.setModel(noTask);
            assertFalse(empty.getCheckButton().isEnabled());
            assertTrue(empty.getNewExampleButton().isEnabled());

            ChoiceFunctionView view = new ChoiceFunctionView();
            view.setModel(session(TutoringMode.DO_ONE, new ChoiceFunctionStep()));
            assertFalse(view.getCheckButton().isEnabled());
            assertFalse(view.getHintButton().isEnabled());
            assertFalse(field(view, "responseTextArea", JTextArea.class).isEnabled());
            assertTrue(view.getNewExampleButton().isEnabled());

            JButton newExample = view.getNewExampleButton();
            for (var listener : newExample.getActionListeners()) {
                newExample.removeActionListener(listener);
            }
            AtomicInteger requests = new AtomicInteger();
            newExample.addActionListener(event -> requests.incrementAndGet());
            field(view, "sixteenRadioButton", JRadioButton.class).doClick();
            assertEquals(1, requests.get());
            assertEquals(16, gson.fromJson(view.newRequest().getData(), ChoiceFunctionStep.class).getBitLength());
        });
    }

    private TutoringSession session(TutoringMode mode, ChoiceFunctionStep example) {
        TutoringSession session = new TutoringSession(new Student(new Account("test@regis.edu")));
        session.setTutoringMode(mode);
        session.addCurrentTask(task(example));
        return session;
    }

    private PendingTask task(ChoiceFunctionStep example) {
        Step step = new Step(3, 1, StepSubType.CHOICE_FUNCTION);
        step.setData(gson.toJson(example));
        PendingTask pending = new PendingTask(new Task());
        pending.setCurrentStep(step);
        return pending;
    }

    private static ChoiceFunctionStep example(String x, String y, String z, int width) {
        ChoiceFunctionStep step = new ChoiceFunctionStep();
        step.setOperand1(x);
        step.setOperand2(y);
        step.setOperand3(z);
        step.setBitLength(width);
        return step;
    }

    private static <T> T field(ChoiceFunctionView view, String name, Class<T> type) {
        try {
            Field field = ChoiceFunctionView.class.getDeclaredField(name);
            field.setAccessible(true);
            return type.cast(field.get(view));
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError(ex);
        }
    }
}
