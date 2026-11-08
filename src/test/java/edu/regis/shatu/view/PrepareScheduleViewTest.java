package edu.regis.shatu.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.Task;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.aol.TutoringMode;
import edu.regis.shatu.model.steps.Step;

class PrepareScheduleViewTest {

    @Test
    void stepCompletionCarriesTheCurrentStep() throws Exception {
        AtomicReference<PrepareScheduleView> ref = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            PrepareScheduleView view = new PrepareScheduleView();
            Step step = new Step(5, 2, StepSubType.PREPARE_SCHEDULE);
            PendingTask pending = new PendingTask(new Task());
            pending.setCurrentStep(step);
            TutoringSession session = new TutoringSession(new Student(new Account("test@regis.edu")));
            session.setTutoringMode(TutoringMode.DO_ONE);
            session.addCurrentTask(pending);
            view.setModel(session);
            ref.set(view);
        });

        SwingUtilities.invokeAndWait(() -> {
            StepCompletion completion = ref.get().stepCompletion();
            assertNotNull(completion.getStep(), "the tutor routes hints and checks by this step");
            assertEquals(5, completion.getStep().getId());
            assertEquals(StepSubType.PREPARE_SCHEDULE, completion.getStep().getSubType());
        });
    }
}
