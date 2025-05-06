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
    public InitVars(Student student) {
        super(student);
    }

    @Override
    public TutorReply hint(StepCompletion completion) {
        System.out.println("Tutor hintInitVars");

        // Deserialize the StepCompletion object to access the InitVarStep
        InitVarStep initVarStep = gson.fromJson(completion.getData(), InitVarStep.class);

        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);

        // Dynamically fetch hints for each variable
        Step step = completion.getStep();
        for (String variable : initVarStep.getCorrectAnswers().keySet()) {
            // Fetch hints for the variable at different levels
            Hint hintLevel1 = new Hint();
            hintLevel1.setId(1);
            hintLevel1.setText(initVarStep.getHint(variable, 1));
            step.addHint(hintLevel1);

            Hint hintLevel2 = new Hint();
            hintLevel2.setId(2);
            hintLevel2.setText(initVarStep.getHint(variable, 2));
            step.addHint(hintLevel2);

            Hint hintLevel3 = new Hint();
            hintLevel3.setId(3);
            hintLevel3.setText(initVarStep.getHint(variable, 3));
            step.addHint(hintLevel3);
        }

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
     * Handler for the Initialize Variables Step Completion
     * TODO: Refactor so that:
     * 1.) Steps in the database are actually completed since as of now, none exist
     * 2.) Steps are completed for Tasks (Task table) in Units (Unit Table)
     * 3.) Steps are completed for each Unit (See One, Do One, Teach One)
     * As of now, this is only logging assessment data (Assessment table) to the
     * database based on the number of
     * exposures, successes, and hints the user has completed during the Do One
     * section of the application and it is
     * not actually logging anything
     *
     * @param completion
     * @return
     */
    @Override
    public TutorReply completeStep(StepCompletion completion) {
        System.out.println("Tutor completeInitVarsStep");

        InitVarStep example = gson.fromJson(completion.getData(), InitVarStep.class);

        // Extract user-provided answers and expected results
        Map<String, String> userAnswers = example.getUserAnswers();
        Map<String, String> correctAnswers = example.getCorrectAnswers();

        // Track correctness and prepare feedback
        boolean allCorrect = true;
        StringBuilder feedback = new StringBuilder();

        for (String variable : correctAnswers.keySet()) {
            String userAnswer = userAnswers.getOrDefault(variable, "");
            String correctAnswer = correctAnswers.get(variable);

            feedback.append(variable)
                    .append(": User Answer: ")
                    .append(userAnswer)
                    .append(", Correct Answer: ")
                    .append(correctAnswer)
                    .append("\n");

            if (!userAnswer.equals(correctAnswer)) {
                allCorrect = false;
            }
        }

        System.out.println("Feedback:\n" + feedback);

        // Create StepCompletionReply
        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setIsCorrect(allCorrect);
        stepReply
                .setCorrectAnswer(correctAnswers.toString().replaceAll("[{}]", "").replaceAll("@\\w{2}=|,", "").trim());

        stepReply.setResponse(userAnswers.toString().replaceAll("[{}]", "").replaceAll("@\\w{2}=|,", "").trim());

        // Update the student model
        int dbId = KnowledgeComponentKind.fromString("Initialize Variables").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);

        // Increment success or exposure based on correctness
        if (allCorrect) {
            assessment.incrementSuccessess();
        }
        assessment.incrementExposures();

        // Determine whether to recommend a new task
        int successes = assessment.getSuccessess();
        if (successes > 0) {
            stepReply.setIsNewTask(true);
            System.out.println("Next task is recommended.");
            assessment.setAssessment(AssessmentLevel.COMPLETED);
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

}