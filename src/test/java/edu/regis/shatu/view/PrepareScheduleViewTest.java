package edu.regis.shatu.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JLabel;
import javax.swing.JRadioButton;
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
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.aol.TutoringMode;
import edu.regis.shatu.model.steps.PrepScheduleStep;
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

    private final Gson gson = new Gson();

    /** A view in DO_ONE with a Prepare Schedule step whose data is the given JSON. */
    private static PrepareScheduleView viewWithStepData(String data) {
        PrepareScheduleView view = new PrepareScheduleView();
        Step step = new Step(5, 2, StepSubType.PREPARE_SCHEDULE);
        step.setData(data);
        PendingTask pending = new PendingTask(new Task());
        pending.setCurrentStep(step);
        TutoringSession session = new TutoringSession(new Student(new Account("test@regis.edu")));
        session.setTutoringMode(TutoringMode.DO_ONE);
        session.addCurrentTask(pending);
        view.setModel(session);
        return view;
    }

    private static Object field(PrepareScheduleView view, String name) {
        try {
            Field f = PrepareScheduleView.class.getDeclaredField(name);
            f.setAccessible(true);
            return f.get(view);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static JRadioButton optionWithText(PrepareScheduleView view, String text) {
        for (JRadioButton option : (JRadioButton[]) field(view, "answerOptions")) {
            if (option.getText().equals(text)) {
                return option;
            }
        }
        throw new AssertionError("No option shows: " + text);
    }

    private PrepScheduleStep sent(StepCompletion completion) {
        return gson.fromJson(completion.getData(), PrepScheduleStep.class);
    }

    @Test
    void stepCompletionSendsTheQuestionAndThePick() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            PrepareScheduleView view = viewWithStepData(null);
            String wrong = PrepScheduleStep.CHOICES[0][2];
            optionWithText(view, wrong).setSelected(true);

            PrepScheduleStep data = sent(view.stepCompletion());

            assertEquals(1, data.getQuestionNumber());
            assertEquals(wrong, data.getAnswer());
        });
    }

    @Test
    void stepCompletionWithNoPickSendsAnEmptyAnswer() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            PrepScheduleStep data = sent(viewWithStepData(null).stepCompletion());

            assertEquals("", data.getAnswer());
        });
    }

    @Test
    void firstNewExampleAsksForTheQuestionOnScreen() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // The task from the database isn't a Prepare Schedule question.
            PrepareScheduleView view = new PrepareScheduleView();
            Step step = new Step(1, 0, StepSubType.ADD_ONE_BIT);
            PendingTask pending = new PendingTask(new Task());
            pending.setCurrentStep(step);
            TutoringSession session = new TutoringSession(new Student(new Account("test@regis.edu")));
            session.setTutoringMode(TutoringMode.DO_ONE);
            session.addCurrentTask(pending);
            view.setModel(session);

            NewExampleRequest request = view.newRequest();

            assertEquals(1, gson.fromJson(request.getData(), PrepScheduleStep.class).getQuestionNumber());
        });
    }

    @Test
    void tutorSendingTheSameQuestionStillClearsThePick() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            PrepareScheduleView view = viewWithStepData(null);
            optionWithText(view, PrepScheduleStep.CHOICES[0][1]).setSelected(true);

            // The first New Example asks for question 1, the one on screen.
            PrepScheduleStep first = new PrepScheduleStep();
            first.setQuestionNumber(1);
            Step step = new Step(1, 0, StepSubType.PREPARE_SCHEDULE);
            step.setData(gson.toJson(first));
            PendingTask pending = new PendingTask(new Task());
            pending.setCurrentStep(step);
            view.setCurrentTask(pending);

            for (JRadioButton option : (JRadioButton[]) field(view, "answerOptions")) {
                assertTrue(!option.isSelected(), "the old pick should be cleared");
            }
        });
    }

    @Test
    void newExampleAfterATutorQuestionAsksForTheNextOne() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            PrepScheduleStep first = new PrepScheduleStep();
            first.setQuestionNumber(1);
            NewExampleRequest request = viewWithStepData(gson.toJson(first)).newRequest();

            assertEquals(2, gson.fromJson(request.getData(), PrepScheduleStep.class).getQuestionNumber());
        });
    }

    @Test
    void stepFromAnotherScreenDoesNotResetTheQuestion() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            PrepScheduleStep first = new PrepScheduleStep();
            first.setQuestionNumber(1);
            PrepareScheduleView view = viewWithStepData(gson.toJson(first));

            // A New Example on another screen makes that screen's step current.
            PendingTask other = new PendingTask(new Task());
            other.setCurrentStep(new Step(1, 0, StepSubType.ENCODE_ASCII));
            view.setCurrentTask(other);

            NewExampleRequest request = view.newRequest();

            assertEquals(2, gson.fromJson(request.getData(), PrepScheduleStep.class).getQuestionNumber());
        });
    }

    @Test
    void lastQuestionWrapsBackToTheFirst() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            PrepScheduleStep last = new PrepScheduleStep();
            last.setQuestionNumber(PrepScheduleStep.QUESTIONS.length);
            NewExampleRequest request = viewWithStepData(gson.toJson(last)).newRequest();

            assertEquals(1, gson.fromJson(request.getData(), PrepScheduleStep.class).getQuestionNumber());
        });
    }

    @Test
    void newExampleFromTheTutorShowsItsQuestion() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            PrepareScheduleView view = viewWithStepData(null);
            optionWithText(view, PrepScheduleStep.CHOICES[0][0]).setSelected(true);

            // The tutor's reply to New Example names question 3.
            PrepScheduleStep third = new PrepScheduleStep();
            third.setQuestionNumber(3);
            Step step = new Step(1, 0, StepSubType.PREPARE_SCHEDULE);
            step.setData(gson.toJson(third));
            PendingTask pending = new PendingTask(new Task());
            pending.setCurrentStep(step);
            view.setCurrentTask(pending);

            String label = ((JLabel) field(view, "stepLabel")).getText();
            assertTrue(label.contains(PrepScheduleStep.QUESTIONS[2]), label);
            for (JRadioButton option : (JRadioButton[]) field(view, "answerOptions")) {
                assertTrue(!option.isSelected(), "the old pick should be cleared");
            }
            assertEquals(3, sent(view.stepCompletion()).getQuestionNumber());
        });
    }
}
