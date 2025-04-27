package edu.regis.shatu.objectives;

import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.model.CompressRoundStep;
import edu.regis.shatu.model.Hint;
import edu.regis.shatu.model.KnowledgeComponentKind;
import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.StepCompletionReply;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.StudentModelFieldKind;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.Assessment;
import edu.regis.shatu.model.aol.PendingStep;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.aol.Timeout;
import edu.regis.shatu.svc.ServiceFactory;
import edu.regis.shatu.svc.StudentModelSvc;
import edu.regis.shatu.svc.TutorReply;

public class CompressRound extends Objective {
    public CompressRound(Student student) {
        super(student);
    }

    @Override
    public TutorReply hint(StepCompletion completion) {
        System.out.println("Tutor hintCompressRound");

        StepCompletionReply stepReply = new StepCompletionReply();

        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);

        Hint hintOne = new Hint();
        hintOne.setId(0);
        hintOne.setText("Perform the main compression function for this round");

        Hint hintTwo = new Hint();
        hintTwo.setId(1);
        hintTwo.setText("Use the schedule word and round constant for this round");

        Step step = completion.getStep();
        step.addHint(hintOne);
        step.addHint(hintTwo);

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
        int dbId = KnowledgeComponentKind.fromString("Compress Round").dbId();
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
     * Handles client requests for a new compress round example.
     *
     * @return a TutorReply
     */
    @Override
    public TutorReply example(TutoringSession session, String jsonData) {
        CompressRoundStep subStep = gson.fromJson(jsonData, CompressRoundStep.class);

        return genericExample(subStep, StepSubType.COMPRESS_ROUND, ProblemType.COMPRESS_ROUND,
                KnowledgeComponentKind.COMPRESS_ROUND, "Data chunks are compressed repeatedly");
    }

    @Override
    public TutorReply completeStep(StepCompletion completion) {
        return genericComplete("true", "true", KnowledgeComponentKind.COMPRESS_ROUND);
    }

}