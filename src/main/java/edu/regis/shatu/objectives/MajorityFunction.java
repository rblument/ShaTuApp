package edu.regis.shatu.objectives;

import edu.regis.shatu.model.KnowledgeComponentKind;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.steps.MajorityStep;
import edu.regis.shatu.svc.TutorReply;

public class MajorityFunction extends Objective {
    public MajorityFunction(Student student) {
        super(student);
    }

    @Override
    public TutorReply hint(StepCompletion completion) {
        return genericHint(completion, KnowledgeComponentKind.MAJORITY_FUNCTION,
                "The majority function returns the bit value that appears most frequently among the three inputs");
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

        return genericExample(substep, StepSubType.MAJORITY_FUNCTION, ProblemType.MAJORITY_FUNCTION,
                KnowledgeComponentKind.MAJORITY_FUNCTION,
                "Compute the result of the majority function on the three operands");
    }

    /**
     * Handler for the Majority Function Step Conmpletion
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

        return genericComplete(expectedResult, result, KnowledgeComponentKind.MAJORITY_FUNCTION);
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
        if (bitLength != 4 && bitLength != 8 && bitLength != 16 && bitLength != 32) {
            throw new IllegalArgumentException("Majority problems must be 4, 8, 16, or 32 bits");
        }
        String a = validateOperand(x, bitLength);
        String b = validateOperand(y, bitLength);
        String c = validateOperand(z, bitLength);
        StringBuilder result = new StringBuilder(bitLength);
        for (int i = 0; i < bitLength; i++) {
            int ones = (a.charAt(i) - '0') + (b.charAt(i) - '0') + (c.charAt(i) - '0');
            result.append(ones >= 2 ? '1' : '0');
        }
        return result.toString();
    }

    private String validateOperand(String operand, int bitLength) {
        String bits = operand == null ? "" : operand.replaceAll("\\s", "");
        if (bits.length() != bitLength || !bits.matches("[01]+")) {
            throw new IllegalArgumentException("Each majority operand must contain exactly "
                    + bitLength + " binary digits");
        }
        return bits;
    }
}
