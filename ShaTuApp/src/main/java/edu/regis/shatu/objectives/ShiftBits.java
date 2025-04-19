package edu.regis.shatu.objectives;

import java.util.Random;

import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.model.BitShiftStep;
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

public class ShiftBits extends Objective {

    public ShiftBits(Student student) {
        super(student);
    }

    @Override
    public TutorReply hint(StepCompletion completion) {
        System.out.println("Tutor hintShiftBits");

        BitShiftStep example = gson.fromJson(completion.getData(), BitShiftStep.class);
        String operand = example.getOperand();
        int shiftLength = example.getShiftLength();
        boolean shiftRight = example.isShiftRight();
        int bitLength = example.getBitLength();
        String result = example.getResult();

        String expectedResult = bitShiftFunction(operand,
                shiftLength,
                shiftRight,
                bitLength);

        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setCorrectAnswer(expectedResult);
        stepReply.setResponse(result);

        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);

        Hint hintOne = new Hint();
        hintOne.setId(0);
        String hintText = "There will be " + shiftLength + " zeros on the left";
        hintOne.setText(hintText);

        Hint hintTwo = new Hint();
        hintTwo.setId(1);
        hintText = "Remove " + shiftLength + " bits from the right";
        hintTwo.setText(hintText);

        Step step = completion.getStep();
        step.addHint(hintOne);
        step.addHint(hintTwo);

        step.setSubType(StepSubType.REQUEST_HINT);
        // ToDo: fix timeouts
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
        int dbId = KnowledgeComponentKind.fromString("Shift Bits").dbId();
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
     * Handles client requests for a new shift bits zeros example.
     *
     * @return a TutorReply
     */
    @Override
    public TutorReply example(TutoringSession session, String jsonData) {
        System.out.println("newShiftBitsExample");
        BitShiftStep substep = gson.fromJson(jsonData, BitShiftStep.class);

        int bitLength = substep.getBitLength();

        String operand = generateInputString(bitLength);
        int shiftLength = new Random().nextInt(bitLength);
        boolean shiftRight = substep.isShiftRight();

        substep.setOperand(operand);
        substep.setShiftLength(shiftLength);
        substep.setShiftRight(shiftRight);

        substep.setResult(bitShiftFunction(operand, shiftLength, shiftRight, bitLength));

        return genericExample(substep, StepSubType.SHIFT_BITS, ProblemType.SHIFT_BITS,
                KnowledgeComponentKind.SHIFT_BITS, "Compute the result of the bitshift on the operand");
    }

    @Override
    public TutorReply completeStep(StepCompletion completion) {
        System.out.println("Tutor completeShiftBitsStep");

        BitShiftStep example = gson.fromJson(completion.getData(), BitShiftStep.class);
        String operand = example.getOperand();
        int shiftLength = example.getShiftLength();
        boolean shiftRight = example.isShiftRight();
        int bitLength = example.getBitLength();
        String result = example.getResult();

        String expectedResult = bitShiftFunction(operand,
                shiftLength,
                shiftRight,
                bitLength);

        return genericComplete(expectedResult, result, KnowledgeComponentKind.SHIFT_BITS);
    }

    /**
     * Performs a bit shift (left or right) on a binary string operand.
     *
     * @param operand     The binary string to be shifted.
     * @param shiftLength The number of positions to shift the bits.
     * @param bitLength   The length of the resulting binary string.
     * @param shiftRight  If true, performs a right shift; if false, performs a
     *                    left shift.
     * @return The binary string result after shifting.
     */
    private String bitShiftFunction(String operand, int shiftLength, boolean shiftRight, int bitLength) {
        // Convert the binary string to a long integer
        String tempOperand = operand.replaceAll("\\s", "");
        long intOperand = Long.parseLong(tempOperand, 2);

        // Perform the shift
        long shiftedOperand;
        if (shiftRight) {
            shiftedOperand = intOperand >>> shiftLength;
        } else {
            shiftedOperand = intOperand << shiftLength;
        }

        // Convert the result back to binary string
        String binaryResult = formatResult(shiftedOperand, bitLength);

        return binaryResult;
    }
}
