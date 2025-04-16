package edu.regis.shatu.objectives;

import java.util.Random;

import edu.regis.shatu.err.NonRecoverableException;
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
import edu.regis.shatu.model.aol.BitOpExample;
import edu.regis.shatu.model.aol.BitOpStep;
import edu.regis.shatu.model.aol.PendingStep;
import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.aol.TaskKind;
import edu.regis.shatu.model.aol.Timeout;
import edu.regis.shatu.svc.ServiceFactory;
import edu.regis.shatu.svc.StudentModelSvc;
import edu.regis.shatu.svc.TutorReply;

public class XorBits extends Objective {

    private static final int MAX_ASCII_SIZE = 20;

    private static final int MAX_BITS_SIZE = 32;

    public XorBits(Student student) {
        super(student);
    }

    @Override
    public TutorReply completeStep(StepCompletion completion) {
        System.out.println("Tutor completeXorBitsStep");

        BitOpStep example = gson.fromJson(completion.getData(), BitOpStep.class);

        String operand1 = example.getExample().getOperand1();
        String operand2 = example.getExample().getOperand2();
        String result = example.getExample().getResult();

        String expectedResult = xorBitsFunction(operand1, operand2);

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
            int dbId = KnowledgeComponentKind.fromString("XOR Bits").dbId();
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
     * Handles client requests for a new XOR bits example.
     *
     * @return a TutorReply
     */
    @Override
    public TutorReply example(TutoringSession session, String jsonData) {
        Random rnd = new Random();

        BitOpExample example = gson.fromJson(jsonData, BitOpExample.class);

        int size = example.getPreSize();

        if (size == 0) {
            // ToDo: The tutor should generate the string length and timeout
            // based on the the current student model.
            size = rnd.nextInt(MAX_BITS_SIZE - 1) + 1;
            example.setTimeOut(600);

        } else if (size > MAX_BITS_SIZE) {
            // The student is requesting practice for a specific string length.
            size = MAX_ASCII_SIZE;
            example.setTimeOut(0);
        }

        example.generatedRandomOperands(size);

        int xor = (int) example.getOperand1Val() ^ (int) example.getOperand2Val();
        example.setResultVal(xor);

        BitOpStep subStep = new BitOpStep();
        subStep.setExample(example);
        // ToDo: multistep should be determined by the student model.
        subStep.setMultiStep(rnd.nextBoolean());

        Step step = new Step(1, 0, StepSubType.XOR_BITS);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        step.setData(gson.toJson(subStep));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.XOR_BITS);
        task.setDescription("Xor the bits in the two operands");
        task.addStep(step);

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("XOR Bits").dbId();
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
        System.out.println("Tutor hintXorBits");

        StepCompletionReply stepReply = new StepCompletionReply();

        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);

        Hint hintOne = new Hint();
        hintOne.setId(0);
        hintOne.setText("XOR operation results in 1 only when the bits are different");

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
        int dbId = KnowledgeComponentKind.fromString("XOR Bits").dbId();
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

    private String xorBitsFunction(String operand1, String operand2) {
        StringBuilder result = new StringBuilder();
        int length = Math.min(operand1.length(), operand2.length());

        for (int i = 0; i < length; i++) {
            char bit1 = operand1.charAt(i);
            char bit2 = operand2.charAt(i);
            char xorBit = (char) (((bit1 - '0') ^ (bit2 - '0')) + '0');
            result.append(xorBit);
        }

        return result.toString();
    }
}
