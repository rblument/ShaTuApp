package edu.regis.shatu.objectives;

import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.model.AddOneStep;
import edu.regis.shatu.model.Hint;
import edu.regis.shatu.model.KnowledgeComponentKind;
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

public class AddOne extends Objective {
    public AddOne(Student student) {
        super(student);
    }

    @Override
    public TutorReply hint(StepCompletion completion) {
        System.out.println("Tutor hintAddOne");

        StepCompletionReply stepReply = new StepCompletionReply();

        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);

        Hint hintOne = new Hint();
        hintOne.setId(0);
        hintOne.setText("Add a single '1' bit to the end of the message");

        Step step = completion.getStep();
        step.addHint(hintOne);

        step.setSubType(StepSubType.REQUEST_HINT);
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(pendingStep));

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Add One Bit").dbId();
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
     * Handles client requests for a new add one bit example.
     *
     * @return a TutorReply
     */
    @Override
    public TutorReply example(TutoringSession session, String jsonData) {

        System.out.println("Start tutor newaddonebitexample"); // Error checking

        AddOneStep newAddOneBit = gson.fromJson(jsonData, AddOneStep.class); // This is the AddOneStep created in the
                                                                             // newExample function from the AddOneView.

        int messageLength = newAddOneBit.getMessageLength(); // Set in the newExample function from the AddOneView,
                                                             // represents the String length that will be generated for
                                                             // the question.

        String question = generateRandomString(messageLength); // Generates a random string to convert to binary

        newAddOneBit.setQuestion(question);

        newAddOneBit.setResult(addOneFunction(question)); // Generates the binary version of the question, which is now
                                                          // the answer

        System.out.println(newAddOneBit.getResult()); // Error checking

        Step step = new Step(1, 0, StepSubType.ADD_ONE_BIT);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        step.setData(gson.toJson(newAddOneBit));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.ADD_ONE_BIT);
        task.setDescription("Add one bit to the given bit string");
        task.addStep(step);

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Add One Bit").dbId();
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
     * This method is called from the AddOneView when the check button is
     * clicked, will check the users answer to the correct answer generated by
     * the newAddOneStep method in this file.
     *
     * @param completion
     *
     * @return
     */
    @Override
    public TutorReply completeStep(StepCompletion completion) {

        AddOneStep completedAddOneStep = gson.fromJson(completion.getData(), AddOneStep.class);

        String userAnswer = completedAddOneStep.getUserAnswer(); // What the user submitted as the answer.
        String correctAnswer = completedAddOneStep.getResult();

        System.out.println("Correct Answer: " + correctAnswer); // Error checking
        System.out.println("User Answer: " + userAnswer); // Error checking

        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setCorrectAnswer(correctAnswer);
        stepReply.setResponse(userAnswer);

        if (userAnswer.equals(correctAnswer)) { // User was correct
            System.out.println("Answer was correct, correct if branch taken."); // Error checking.
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
            int dbId = KnowledgeComponentKind.fromString("Add One Bit").dbId();
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

    /**
     * This method takes the question from the newAddOneStep ad converts it to
     * binary and returns the binary answer.
     *
     * @param question
     *
     * @return
     */
    private String addOneFunction(String question) {
        String answer;

        char stringArray[] = question.toCharArray();

        StringBuilder binary = new StringBuilder();

        for (int i = 0; i < stringArray.length; i++) {
            String binaryChar = String.format("%8s", Integer.toBinaryString(stringArray[i])).replaceAll(" ", "0");

            binary.append(binaryChar).append(" ");
        }

        answer = binary + "1";

        return answer;
    }

}
