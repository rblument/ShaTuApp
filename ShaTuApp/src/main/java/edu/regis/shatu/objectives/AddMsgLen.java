package edu.regis.shatu.objectives;

import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.model.KnowledgeComponentKind;
import edu.regis.shatu.model.MessageLenStep;
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

public class AddMsgLen extends Objective {

    public AddMsgLen(Student student) {
        super(student);
    }

    @Override
    public TutorReply hint(StepCompletion completion) {
        return simpleHint(completion, "Add Message Length",
                "Append the original message length as a 64-bit big-endian integer");
    }

    /**
     * Handles client requests for a new add message length example.
     *
     * @return a TutorReply
     */
    @Override
    public TutorReply example(TutoringSession session, String jsonData) {
        System.out.println("Start tutor newAddMsgLenExample"); // Error checking

        MessageLenStep subStep = gson.fromJson(jsonData, MessageLenStep.class);

        int messageLength = subStep.getMessageLength();

        String question = generateRandomString(messageLength);

        subStep.setQuestion(question);

        subStep.setResult(Integer.toBinaryString(messageLength * 8));

        System.out.println(subStep.getResult()); // Error checking

        Step step = new Step(1, 0, StepSubType.ADD_MSG_LENGTH);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        step.setData(gson.toJson(subStep));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.ADD_MSG_LENGTH);
        task.setDescription("Calculate the message length for the last 64 bits of the message length step");
        task.addStep(step);

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Add Message Length").dbId();
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

    /**
     * Function that is called from the overrided stepCompletion method from the
     * MessageLenView. Checks the users answer with the correct answer and will
     * provide the user with further guidance.
     *
     * @param completion
     * @return
     */
    @Override
    public TutorReply completeStep(StepCompletion completion) {
        MessageLenStep completedMessageLenStep = gson.fromJson(completion.getData(), MessageLenStep.class);

        String userAnswer = completedMessageLenStep.getUserAnswer();
        String correctAnswer = completedMessageLenStep.getResult();

        System.out.println("user answer: " + userAnswer); // Error checking
        System.out.println("Correct answer: " + correctAnswer); // Error checking

        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setCorrectAnswer(correctAnswer);
        stepReply.setResponse(userAnswer);

        if (userAnswer.equals(correctAnswer)) { // User was correct
            System.out.println("Answer was correct, correct if branch taken."); // Error checking
            stepReply.setIsCorrect(true);
            stepReply.setIsRepeatStep(false);
            stepReply.setIsNewStep(true);

            // ToDo: Use the student model to figure out whether we want
            // to give the student another practice problem of the same
            // type or move on to an entirely different problem.
            stepReply.setIsNewTask(true);

            // ToDo: currently only one step in a task, so there isn't a next one???
            stepReply.setIsNextStep(false);

            // Update the assessment data and save it to the database.
            int dbId = KnowledgeComponentKind.fromString("Add Message Length").dbId();
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

        } else { // User was wrong
            System.out.println("Answer was not correct, correct if branch taken."); // Error checking
            stepReply.setIsCorrect(false);
            stepReply.setIsRepeatStep(true);
            stepReply.setIsNewStep(false);
            stepReply.setIsNewTask(false);
            stepReply.setIsNextStep(false);
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
