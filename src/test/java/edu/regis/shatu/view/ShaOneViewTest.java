package edu.regis.shatu.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Field;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.google.gson.Gson;

import org.junit.jupiter.api.Test;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.Task;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.aol.TutoringMode;
import edu.regis.shatu.model.steps.ShaOneStep;
import edu.regis.shatu.model.steps.Step;

/**
 * Hint and Check both send ShaOneView.stepCompletion(), so it has to work
 * with an empty answer box and must not grade the answer itself. If it
 * showed a dialog these tests would block or fail on a headless build.
 */
class ShaOneViewTest {

    private final Gson gson = new Gson();

    private static ShaOneView viewWithExample() {
        ShaOneStep example = new ShaOneStep();
        example.setOperandA("1010");
        example.setBitLength(4);

        Step step = new Step(3, 1, StepSubType.SHA_ONE);
        step.setData(new Gson().toJson(example));
        PendingTask pending = new PendingTask(new Task());
        pending.setCurrentStep(step);
        TutoringSession session = new TutoringSession(new Student(new Account("test@regis.edu")));
        session.setTutoringMode(TutoringMode.DO_ONE);
        session.addCurrentTask(pending);

        ShaOneView view = new ShaOneView();
        view.setModel(session);
        return view;
    }

    private static JTextArea response(ShaOneView view) {
        try {
            Field field = ShaOneView.class.getDeclaredField("responseTextArea");
            field.setAccessible(true);
            return (JTextArea) field.get(view);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    @Test
    void emptyAnswerStillMakesACompletionForHint() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            StepCompletion completion = viewWithExample().stepCompletion();

            assertNotNull(completion, "Hint needs a completion even with no answer typed");
            assertEquals(StepSubType.SHA_ONE, completion.getStep().getSubType());
            assertEquals("", gson.fromJson(completion.getData(), ShaOneStep.class).getResult());
        });
    }

    @Test
    void typedAnswerIsSentForTheTutorToGrade() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ShaOneView view = viewWithExample();
            response(view).setText("01 01");

            StepCompletion completion = view.stepCompletion();

            ShaOneStep sent = gson.fromJson(completion.getData(), ShaOneStep.class);
            assertEquals("0101", sent.getResult());
            assertEquals("1010", sent.getOperandA());
            assertEquals(4, sent.getBitLength());
        });
    }
}
