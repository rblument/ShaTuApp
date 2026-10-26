package edu.regis.shatu.objectives;

import java.util.Map;

import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.model.Hint;
import edu.regis.shatu.model.KnowledgeComponentKind;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.StudentModelFieldKind;
import edu.regis.shatu.model.Task;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.Assessment;
import edu.regis.shatu.model.aol.AssessmentLevel;
import edu.regis.shatu.model.aol.PendingStep;
import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.aol.TaskKind;
import edu.regis.shatu.model.aol.Timeout;
import edu.regis.shatu.model.steps.InitVarStep;
import edu.regis.shatu.model.steps.Step;
import edu.regis.shatu.model.steps.StepCompletionReply;
import edu.regis.shatu.svc.ServiceFactory;
import edu.regis.shatu.svc.StudentModelSvc;
import edu.regis.shatu.svc.TutorReply;

public class InitVars extends Objective {

    /**
     * SHA256 has eight initial H variables H0 ... H7.
     */
    private static final int NUM_VARS = 8;

    public InitVars(Student student) {
        super(student);
    }

    @Override
    public TutorReply hint(StepCompletion completion) {
        System.out.println("Tutor hintInitVars");

        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);

        Step step = completion.getStep();
        addHints(step);

        step.setSubType(StepSubType.REQUEST_HINT);

        // Add timeout
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        // Serialize the reply with hints
        step.setData(gson.toJson(stepReply));

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(pendingStep));

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Initialize Variables").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementHints();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.HINTS);

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }

        return reply;
    }

    /**
     * Adds the three levels of hints for H0 to H7 to the given step.
     *
     * The hints come from the tutor's own InitVarStep, not from the data sent
     * by the client.
     *
     * @param step the step the hints are added to
     */
    private void addHints(Step step) {
        InitVarStep initVarStep = new InitVarStep();

        for (int i = 0; i < NUM_VARS; i++) {
            String variable = "H" + i;

            for (int level = 1; level <= 3; level++) {
                Hint hint = new Hint();
                hint.setId(level);
                hint.setText(initVarStep.getHint(variable, level));
                step.addHint(hint);
            }
        }
    }

    /**
     * Handles client requests for a new initialize vars example.
     *
     * @return a TutorReply
     */
    @Override
    public TutorReply example(TutoringSession session, String jsonData) {

        InitVarStep subStep = gson.fromJson(jsonData, InitVarStep.class);

        return genericExample(subStep, StepSubType.INITIALIZE_VARS, ProblemType.INITIALIZE_VARS,
                KnowledgeComponentKind.INITIALIZE_VARS, "Variables are Initialized using preset values.");
    }

    /**
     * Handler for the Initialize Variables Step Completion TODO: Refactor so
     * that: 1.) Steps in the database are actually completed since as of now,
     * none exist 2.) Steps are completed for Tasks (Task table) in Units (Unit
     * Table) 3.) Steps are completed for each Unit (See One, Do One, Teach One)
     * As of now, this is only logging assessment data (Assessment table) to the
     * database based on the number of exposures, successes, and hints the user
     * has completed during the Do One section of the application and it is not
     * actually logging anything
     *
     * @param completion
     * @return
     */
    @Override
    public TutorReply completeStep(StepCompletion completion) {
        System.out.println("Tutor completeInitVarsStep");

        InitVarStep submitted = gson.fromJson(completion.getData(), InitVarStep.class);

        // Only the student's answers are taken from the client. They are
        // graded against the tutor's own copy of the correct answers.
        InitVarStep example = gradedStep(submitted);
        boolean allCorrect = example.allAnswersCorrect();

        // Prepare feedback, listing H0 to H7 in order
        StringBuilder feedback = new StringBuilder();
        StringBuilder correctList = new StringBuilder();
        StringBuilder studentList = new StringBuilder();

        for (int i = 0; i < NUM_VARS; i++) {
            String variable = "H" + i;
            String userAnswer = example.getUserAnswer(variable);
            String correctAnswer = example.getAnswer(variable);

            feedback.append(variable)
                    .append(": User Answer: ")
                    .append(userAnswer)
                    .append(", Correct Answer: ")
                    .append(correctAnswer)
                    .append("\n");

            correctList.append(correctAnswer).append(" ");
            studentList.append(userAnswer).append(" ");
        }

        System.out.println("Feedback:\n" + feedback);

        // Create StepCompletionReply
        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setIsCorrect(allCorrect);
        stepReply.setCorrectAnswer(correctList.toString().trim());
        stepReply.setStudentAnswer(studentList.toString().trim());

        // Update the student model
        int dbId = KnowledgeComponentKind.fromString("Initialize Variables").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);

        // Increment success or exposure based on correctness
        // Increment success or exposure based on correctness
        if (allCorrect) {
            assessment.incrementSuccessess();
        }
        assessment.incrementExposures();

        int successes = assessment.getSuccessess();
        int exposures = assessment.getExposures();

        if (successes >= 4) {
            assessment.setAssessment(AssessmentLevel.COMPLETED);
        } else if (successes == 3) {
            assessment.setAssessment(AssessmentLevel.VERY_HIGH);
        } else if (successes == 2) {
            assessment.setAssessment(AssessmentLevel.HIGH);
        } else if (successes == 1) {
            assessment.setAssessment(AssessmentLevel.MEDIUM);
        } else if (exposures > 0) {
            assessment.setAssessment(AssessmentLevel.VERY_LOW);
        } else {
            assessment.setAssessment(AssessmentLevel.NOT_STARTED);
        }

        if (assessment.getAssessment() == AssessmentLevel.COMPLETED) {
            stepReply.setIsNewTask(true);
            System.out.println("Next task is recommended.");
        } else {
            stepReply.setIsNewTask(false);
        }

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.SUCCESSES);
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ASSESSMENT_LEVEL);
        } catch (NonRecoverableException ex) {
            return createError("Failed to update assessment data in database", ex);
        }

        stepReply.setIsRepeatStep(!allCorrect);
        stepReply.setIsNewStep(allCorrect);
        stepReply.setIsNextStep(false);

        // Wrap the StepCompletionReply into a Task and TutorReply
        Step step = new Step(1, 0, StepSubType.STEP_COMPLETION_REPLY);

        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.STEP_COMPLETION_REPLY);
        task.setDescription("Review your results and choose the next action.");
        task.addStep(step);

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        PendingTask pendingTask = new PendingTask(task);
        pendingTask.setCurrentStep(pendingStep);

        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(pendingTask));

        return reply;
    }

    /**
     * Copies the student's answers for H0 to H7 into a new InitVarStep.
     *
     * The new step holds the tutor's own correct answers, so any correct
     * answers sent by the client are ignored. A missing answer becomes "".
     *
     * @param submitted the InitVarStep sent by the client
     * @return a step with the student's answers and the tutor's correct answers
     */
    private InitVarStep gradedStep(InitVarStep submitted) {
        InitVarStep graded = new InitVarStep();
        Map<String, String> userAnswers = submitted.getUserAnswers();

        for (int i = 0; i < NUM_VARS; i++) {
            String variable = "H" + i;
            String userAnswer = "";

            if (userAnswers != null && userAnswers.get(variable) != null) {
                userAnswer = userAnswers.get(variable);
            }

            graded.setUserAnswer(variable, userAnswer);
        }

        return graded;
    }

}
