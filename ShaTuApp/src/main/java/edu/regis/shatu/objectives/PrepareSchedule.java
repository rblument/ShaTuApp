package edu.regis.shatu.objectives;

import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.model.Hint;
import edu.regis.shatu.model.KnowledgeComponentKind;
import edu.regis.shatu.model.PrepScheduleStep;
import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.StepCompletionReply;
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
import edu.regis.shatu.svc.ServiceFactory;
import edu.regis.shatu.svc.StudentModelSvc;
import edu.regis.shatu.svc.TutorReply;

public class PrepareSchedule extends Objective {
    public PrepareSchedule(Student student) {
        super(student);
    }

    /**
     * Handles client requests for a new prepare schedule example.
     *
     * @return a TutorReply
     */
    @Override
    public TutorReply example(TutoringSession session, String jsonData) {
        PrepScheduleStep subStep = gson.fromJson(jsonData, PrepScheduleStep.class);

        Step step = new Step(1, 0, StepSubType.PREPARE_SCHEDULE);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(subStep));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.PREPARE_SCHEDULE);
        task.addStep(step);

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Prepare Schedule").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementExposures();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ATTEMPTS);

            PendingStep pendingStep = new PendingStep(step);
            pendingStep.setCurrentHintIndex(0);
            pendingStep.setNotifyTutor(true);
            pendingStep.setIsCompleted(false);

            PendingTask pendingTask = new PendingTask(task);
            pendingTask.setCurrentStep(pendingStep);

            TutorReply reply = new TutorReply(":Success");
            reply.setData(gson.toJson(pendingTask));

            return reply;

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }
    }

    @Override
    public TutorReply hint(StepCompletion completion) {
        
        System.out.println("Tutor hintPrepareSchedule");

        int stepNumber = completion.getStepNumber();
        
        System.out.print("\n Hint Prep Sched " + stepNumber + "\n\n");
        
        StepCompletionReply stepReply = new StepCompletionReply();

        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);
        
        

        Hint hintOne = new Hint();
        hintOne.setId(1);

        switch (stepNumber){
            case 0 -> hintOne.setText("Does SHA-256 allow arbitrary block sizes, or does it standardize them?");
            case 1 -> hintOne.setText("What is the purpose of dividing blocks into smaller segments?");
            case 2 -> hintOne.setText( "Does SHA-256 add external words, or does it derive them from existing ones?");
            case 3 -> hintOne.setText("How does SHA-256 use transformed words in its compression phase?");
        }
        
        Step step = completion.getStep();
        step.addHint(hintOne);
  
        step.setSubType(StepSubType.REQUEST_HINT);
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(1);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(pendingStep));
        
        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Prepare Schedule").dbId();
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

    @Override
    public TutorReply completeStep(StepCompletion completion) {
        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setResponse(" ");

        stepReply.setIsCorrect(true);
        stepReply.setIsRepeatStep(false);
        stepReply.setIsNewStep(true);
        
        PrepScheduleStep completedPrepareScheduleStep = gson.fromJson(completion.getData(), PrepScheduleStep.class);
        
        String userAnswer = completedPrepareScheduleStep.getUserAnswer();
        String correctAnswer = completedPrepareScheduleStep.getCorrectAnswer();

        if (userAnswer.equals(correctAnswer)) { // User was correct
            if(completedPrepareScheduleStep.getStepNumber() == 3){
                stepReply.setIsCorrect(true);
                stepReply.setIsNextStep(true);
                stepReply.setResponse("Please click new example to move on!");
            }else{
                stepReply.setIsCorrect(true);
                stepReply.setIsRepeatStep(false);
                stepReply.setIsNewStep(true);
                stepReply.setIsNextStep(true);
                stepReply.setResponse("Please click new example to move on!");
            }
            System.out.println("Answer was correct, correct if branch taken."); // Error checking            

        }else{ //User was wrong
            System.out.println("Answer was not correct, correct if branch taken."); // Error checking
            stepReply.setIsCorrect(false);
            stepReply.setIsRepeatStep(true);
            stepReply.setIsNewStep(false);
            stepReply.setIsNewTask(false);
            stepReply.setIsNextStep(true);
        }

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Prepare Schedule").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementSuccessess();

        int exposures = assessment.getExposures();
        int successes = assessment.getSuccessess();

        if (exposures > 0 && (double) successes / exposures > 0.6) {
            stepReply.setIsNewTask(true);
            System.out.println("%%%%%%%%%%% Next Task Recommended");
            assessment.setAssessment(AssessmentLevel.COMPLETED);
        } else {
            stepReply.setIsNewTask(false);
        }

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.SUCCESSES);
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ASSESSMENT_LEVEL);

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }

        Step step = new Step(1, 0, StepSubType.STEP_COMPLETION_REPLY);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.STEP_COMPLETION_REPLY);
        task.setDescription("Choose your next action");
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
