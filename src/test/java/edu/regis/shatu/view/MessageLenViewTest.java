package edu.regis.shatu.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JTextArea;
import javax.swing.JTextField;
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
import edu.regis.shatu.model.steps.MessageLenStep;
import edu.regis.shatu.model.steps.Step;

class MessageLenViewTest {
    private final Gson gson = new Gson();

    @Test
    void stepCompletionKeepsTheQuestionLengthAndSendsAnswerSeparately() throws Exception {
        AtomicReference<MessageLenView> ref = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            MessageLenView view = new MessageLenView();
            MessageLenStep problem = new MessageLenStep();
            problem.setQuestion("Hello");
            problem.setMessageLength(5);
            problem.setResult("101000");
            Step step = new Step(1, 0, StepSubType.ADD_MSG_LENGTH);
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
            MessageLenView view = ref.get();
            field(view, "responseTextArea", JTextArea.class).setText("10 10\n00");
            MessageLenStep submitted = gson.fromJson(view.stepCompletion().getData(), MessageLenStep.class);
            assertEquals(5, submitted.getMessageLength());
            assertEquals("Hello", submitted.getQuestion());
            assertEquals("101000", submitted.getUserAnswer());
        });
    }

    private static <T> T field(MessageLenView view, String name, Class<T> kind) {
        try {
            Field field = MessageLenView.class.getDeclaredField(name);
            field.setAccessible(true);
            return kind.cast(field.get(view));
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError(ex);
        }
    }
}
