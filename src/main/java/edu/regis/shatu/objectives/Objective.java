package edu.regis.shatu.objectives;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
import edu.regis.shatu.model.aol.StudentModel;
import edu.regis.shatu.model.aol.TaskKind;
import edu.regis.shatu.model.aol.Timeout;
import edu.regis.shatu.model.steps.Step;
import edu.regis.shatu.model.steps.StepCompletionReply;
import edu.regis.shatu.svc.ServiceFactory;
import edu.regis.shatu.svc.ShaTuTutor;
import edu.regis.shatu.svc.StudentModelSvc;
import edu.regis.shatu.svc.TutorReply;

abstract public class Objective {

    StudentModel studentModel;

    Objective(Student student) {
        this.studentModel = student.getStudentModel();
    }

    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Formats the result output by the choice function based on the size of the
     * problem.
     *
     * @param answer the output of the choice function
     *
     * @return the binary string representation of the answer
     */
    protected String formatResult(long answer, int bitLength) {
        String finalResult = "";

        switch (bitLength) {
            case 4:
                finalResult = String.format("%4s", Long.toBinaryString(answer)).replace(' ', '0');
                break;
            case 8:
                finalResult = String.format("%8s", Long.toBinaryString(answer)).replace(' ', '0');
                break;
            case 16:
                finalResult = String.format("%16s", Long.toBinaryString(answer)).replace(' ', '0');
                break;
            case 32:
                finalResult = String.format("%32s", Long.toBinaryString(answer)).replace(' ', '0');
                break;
            default:
                break;
        }
        return finalResult;
    }

    /**
     * Generates an n-bit binary string (length 4, 8, 16, or 32) to be used as
     * an input into the Ch function. Every four bits are separated by a space
     * to improve readability.
     *
     * @return A string to be used as an input into the function.
     */
    protected String generateInputString(int problemSize) {
        Random random = new Random();

        String inputString;
        String tempString;
        StringBuilder inputStringBuilder = new StringBuilder();
        int num;

        switch (problemSize) {
            case 4:
                num = random.nextInt();
                tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                inputStringBuilder.append(tempString);
                break;
            case 8:
                num = random.nextInt();
                tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                inputStringBuilder.append(tempString);

                inputStringBuilder.append(" ");
                num = random.nextInt();
                tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                inputStringBuilder.append(tempString);
                break;
            case 16:
                num = random.nextInt();
                tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                inputStringBuilder.append(tempString);

                for (int i = 0; i < 3; i++) {
                    inputStringBuilder.append(" ");
                    num = random.nextInt();
                    tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                    inputStringBuilder.append(tempString);
                }
                break;
            case 32:
                num = random.nextInt();
                tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                inputStringBuilder.append(tempString);

                for (int i = 0; i < 7; i++) {
                    inputStringBuilder.append(" ");
                    num = random.nextInt();
                    tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                    inputStringBuilder.append(tempString);
                }
                break;
            default:
                break;
        }

        inputString = inputStringBuilder.toString();

        return inputString;
    }

    /**
     * Utility for logging an error and an creating a tutoring reply error with
     * the given message, and optional originating exception.
     *
     * @param errMsg a displayable error message
     * @param ex     the original exception, if any, that caused the error,
     *               otherwise null.
     * @return a TutorReply with an ":ERR" status
     */
    public TutorReply createError(String errMsg, Exception ex) {
        if (ex == null) {
            Logger.getLogger(ShaTuTutor.class.getName()).log(Level.SEVERE, errMsg);
        } else {
            Logger.getLogger(ShaTuTutor.class.getName()).log(Level.SEVERE, errMsg, ex);
        }

        return new TutorReply(":ERR", errMsg);
    }

    /**
     * Method that generates and returns a random string.
     *
     * @param length
     *
     * @return
     */
    protected String generateRandomString(int length) {

        Random random = new Random();
        StringBuilder sb = new StringBuilder(length); // StringBuilder allows easier altering of a string.

        for (int i = 0; i < length; i++) {
            // Generates a random integer between 32 (inclusive) and 126 (inclusive)
            int randomChar = 32 + random.nextInt(95); // 126 - 32 + 1 = 95
            sb.append((char) randomChar);
        }

        return sb.toString();
    }

    abstract public TutorReply hint(StepCompletion completion);

    /**
     * Generic Hint function for objectives to leverage. Currently only supports 1
     * hint message.
     * 
     * @param completion
     * @param stepName
     * @param hintText
     * @return
     */
    public TutorReply genericHint(StepCompletion completion, KnowledgeComponentKind stepName, String hintText) {
        // System.out.println("Tutor hintAddBits");

        StepCompletionReply stepReply = new StepCompletionReply();

        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);

        Hint hintOne = new Hint();
        hintOne.setId(0);
        hintOne.setText(hintText);

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
        Assessment assessment = studentModel.findAssessment(stepName.dbId());
        assessment.incrementHints();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.HINTS);

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }

        return reply;
    }

    abstract public TutorReply example(TutoringSession session, String jsonData);

    /**
     * Generic example function covering the more mundane repetitive parts of hint
     * creation.
     * 
     * @param subStep
     * @param subType
     * @param probType
     * @param kind
     * @param description
     * @return
     */
    public TutorReply genericExample(Object subStep, StepSubType subType, ProblemType probType,
            KnowledgeComponentKind kind, String description) {
        Step step = new Step(1, 0, subType);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        step.setData(gson.toJson(subStep));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(probType);
        task.setDescription(description);
        task.addStep(step);

        // Update the assessment data and save it to the database.
        Assessment assessment = studentModel.findAssessment(kind.dbId());
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

    abstract public TutorReply completeStep(StepCompletion completion);

    /**
     * Generic Step completion function again covering the repetitive aspects of
     * marking a step as completed.
     * 
     * @param correctAnswer the correct answer to the problem in the task step
     * @param studentAnswer the answer given by the student
     * @param stepName
     * @return a TutorReply whose data is a PendingTask with a PendingSept with
     * an associated StepCompletionReply
     */
    public TutorReply genericComplete(String correctAnswer, String studentAnswer, KnowledgeComponentKind stepName) {
        // TODO: The id and the sequence are incorrect in this reply
        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setSubType(StepSubType.STEP_COMPLETION_REPLY);
        stepReply.setCorrectAnswer(correctAnswer);
        stepReply.setStudentAnswer(studentAnswer);

        if (studentAnswer.equals(correctAnswer)) { // User was correct
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
            Assessment assessment = studentModel.findAssessment(stepName.dbId());
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
            System.out.println("Answer was not correct, else branch taken."); // Error checking
            stepReply.setIsCorrect(false);
            stepReply.setIsRepeatStep(true);
            stepReply.setIsNewStep(false);
            stepReply.setIsNewTask(false);
            stepReply.setIsNextStep(false);
        }

        
        // To Do, how to handle id's and sequence for StempCompletion reply steps
        Step step = new Step(-1, -1, StepSubType.STEP_COMPLETION_REPLY);
        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        stepReply.setTimeout(timeout);
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