package edu.regis.shatu.objectives;

import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.model.KnowledgeComponentKind;
import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.StudentModelFieldKind;
import edu.regis.shatu.model.Task;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.Assessment;
import edu.regis.shatu.model.aol.PendingStep;
import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.RotateStep;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.aol.TaskKind;
import edu.regis.shatu.svc.ServiceFactory;
import edu.regis.shatu.svc.StudentModelSvc;
import edu.regis.shatu.svc.TutorReply;

public class RotateBits extends Objective {
    public RotateBits(Student student) {
        super(student);
    }

    @Override
    public TutorReply hint(StepCompletion completion) {
        return simpleHint(completion, KnowledgeComponentKind.ROTATE_BITS,
                "The bits that 'fall off' one end should be added to the other end");
    }

    /**
     * Handles client requests for a new rotate bits example.
     *
     * @return a TutorReply
     */
    @Override
    public TutorReply example(TutoringSession session, String jsonData) {
        RotateStep example = gson.fromJson(jsonData, RotateStep.class);

        // Check if the data (bit string) is provided, if not, generate it
        if (example.getData() == null || example.getData().isEmpty()) {
            String generatedData = generateInputString(example.getLength());
            example.setData(generatedData);
        }

        Step step = new Step(1, 0, StepSubType.ROTATE_BITS);
        step.setData(gson.toJson(example));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.ROTATE_BITS);
        task.addStep(step);

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Rotate n BITS").dbId();
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
        System.out.println("Tutor completeRotateStep");
        RotateStep example = gson.fromJson(completion.getData(), RotateStep.class);
        int amount = example.getAmount();
        String data = example.getData();

        String expectedResult = performBitRotation(data, amount);

        String result = example.getUserResponse();

        return genericComplete(expectedResult, result, KnowledgeComponentKind.ROTATE_BITS);
    }

    /**
     * Performs bit rotation on the example string to get correct answer for
     * comparison to user's answer.
     *
     * @param data
     * @param amount
     * @return result
     */
    private String performBitRotation(String data, int amount) {
        String fdata = data.replaceAll("\\s+", "");
        int length = fdata.length();
        amount = amount % length;

        if (amount < 0) {
            amount = length + amount;
        }
        String result = fdata.substring(length - amount) + fdata.substring(0, length - amount);
        return result;
    }

}
