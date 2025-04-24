package edu.regis.shatu.objectives;

import edu.regis.shatu.model.KnowledgeComponentKind;
import edu.regis.shatu.model.MajorityStep;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;
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
