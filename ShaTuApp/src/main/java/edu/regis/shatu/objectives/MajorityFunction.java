package edu.regis.shatu.objectives;

import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.model.Hint;
import edu.regis.shatu.model.KnowledgeComponentKind;
import edu.regis.shatu.model.MajorityStep;
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

public class MajorityFunction extends Objective {
    public MajorityFunction(Student student) {
        super(student);
    }

    @Override
    public TutorReply hint(StepCompletion completion) {
        System.out.println("Tutor hintMajorityFunction");

        StepCompletionReply stepReply = new StepCompletionReply();

        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);

        Hint hintOne = new Hint();
        hintOne.setId(0);
        hintOne.setText(
                "The majority function returns the bit value that appears most frequently among the three inputs");

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
        int dbId = KnowledgeComponentKind.fromString("Majority Function").dbId();
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
     * Handles client requests for a new majority function example.
     *
     * @return a TutorReply
     */
    @Override
    public TutorReply example(TutoringSession session, String jsonData) {
        System.out.println("newMajorityFunctionExample");
        MajorityStep substep = gson.fromJson(jsonData, MajorityStep.class);

        int bitLength = substep.getBitLength();

        String operand1 = generateInputString(bitLength);
        String operand2 = generateInputString(bitLength);
        String operand3 = generateInputString(bitLength);

        substep.setOperandA(operand1);
        substep.setOperandB(operand2);
        substep.setOperandC(operand3);

        substep.setResult(majorityFunction(operand1, operand2, operand3, bitLength));

        Step step = new Step(1, 0, StepSubType.MAJORITY_FUNCTION);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        step.setData(gson.toJson(substep));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.MAJORITY_FUNCTION);
        task.setDescription("Compute the result of the majority function on the three operands");
        task.addStep(step);

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Majority Function").dbId();
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
    public TutorReply completeStep(StepCompletion completion) {
        System.out.println("Tutor completeMajorityStep");

        MajorityStep example = gson.fromJson(completion.getData(), MajorityStep.class);
        String operand1 = example.getOperandA();
        String operand2 = example.getOperandB();
        String operand3 = example.getOperandC();
        int bitLength = example.getBitLength();
        String result = example.getResult();
        // System.out.println("Result: " + example.getResult());

        String expectedResult = majorityFunction(operand1, operand2, operand3, bitLength);
        System.out.println("Expected result: " + expectedResult);

        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setCorrectAnswer(expectedResult);
        stepReply.setResponse(result);

        if (expectedResult.equals(result)) {
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
            int dbId = KnowledgeComponentKind.fromString("Majority Function").dbId();
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

        } else {
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
     * Evaluates the maj function maj(x, y, z).
     *
     * @param x Binary string representation of x.
     * @param y Binary string representation of y.
     * @param z Binary string representation of z.
     * @return Binary string result of maj(x, y, z).
     */
    private String majorityFunction(String x, String y, String z, int bitLength) {
        // Convert the binary strings to integer values
        String tempX = x.replaceAll("\\s", "");
        String tempY = y.replaceAll("\\s", "");
        String tempZ = z.replaceAll("\\s", "");

        long intX = Long.parseLong(tempX, 2);
        long intY = Long.parseLong(tempY, 2);
        long intZ = Long.parseLong(tempZ, 2);

        long xy = intX & intY;

        long xz = intX & intZ;

        long yz = intY & intZ;

        long result = xy ^ xz ^ yz;

        // Convert the result back to binary string
        String binaryResult = formatResult(result, bitLength);

        return binaryResult;
    }
}
