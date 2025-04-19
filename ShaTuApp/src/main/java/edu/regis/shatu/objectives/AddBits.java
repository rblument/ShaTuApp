package edu.regis.shatu.objectives;

import java.math.BigInteger;
import java.util.Random;

import edu.regis.shatu.model.KnowledgeComponentKind;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.BitOpExample;
import edu.regis.shatu.model.aol.BitOpStep;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.svc.TutorReply;

public class AddBits extends Objective {

    private static final int MAX_BITS_SIZE = 32;

    public AddBits(Student student) {
        super(student);
    }

    @Override
    public TutorReply hint(StepCompletion completion) {
        return genericHint(completion, KnowledgeComponentKind.ADD_BITS,
                "Add the bits as if they were unsigned integers, discarding any overflow beyond 32 bits");
    }

    /**
     * Handles client requests for a new add bits example.
     *
     * Generates two operands with pre-size n bits. These are added together
     * using modulo-preSize arithmetic. For example, with preSize 8 bits:</br>
     * Operand1: 11100111:231 </br>
     * Operand2: 10101111:175 </br>
     * Intermediate: 110010110:406 </br>
     * Result: 10010110:150 (e.g., 406 % 256)
     *
     * @param session  the current tutoring session.
     * @param jsonData a BitOpExample encoded object
     * @return a TutorReply
     */
    @Override
    public TutorReply example(TutoringSession session, String jsonData) {
        Random rnd = new Random();

        BitOpExample example = gson.fromJson(jsonData, BitOpExample.class);

        int size = example.getPreSize(); // number of bits

        if (size == 0) {
            // ToDo: The tutor should generate the string length and timeout
            // based on the the current student model.
            size = rnd.nextInt(MAX_BITS_SIZE - 1) + 1;
            example.setTimeOut(600);

        } else if (size > MAX_BITS_SIZE) {
            // The student is requesting practice for a specific string length.
            size = MAX_BITS_SIZE;
            example.setTimeOut(0);
        }

        example.setPreSize(size);

        int maxOperandVal = (int) Math.pow(2.0d, size) - 1; // e.g., 2^8 - 1 = 255

        long operand1 = rnd.nextLong((maxOperandVal - 1) + 1);
        example.setOperand1Val(operand1);

        long operand2 = rnd.nextLong((maxOperandVal - 1) + 1);
        example.setOperand2Val(operand2);

        // This is Mod size arithmetic
        example.setPostSize(size);
        long result = ((long) operand1) + ((long) operand2);

        if (result > maxOperandVal) {
            result = result % (maxOperandVal + 1);
        }

        example.setResultVal(result);

        BitOpStep subStep = new BitOpStep();
        subStep.setExample(example);
        // ToDo: multistep should be determined by the student model.
        subStep.setMultiStep(rnd.nextBoolean());

        return genericExample(subStep, StepSubType.ADD_BITS, ProblemType.ADD_BITS, KnowledgeComponentKind.ADD_BITS,
                "addition modulo 2^256 the bits in the two operands");
    }

    @Override
    public TutorReply completeStep(StepCompletion completion) {
        System.out.println("Tutor completeAddBitsStep");

        BitOpStep example = gson.fromJson(completion.getData(), BitOpStep.class);

        String operand1 = example.getExample().getOperand1();
        String operand2 = example.getExample().getOperand2();
        String result = example.getExample().getResult();

        int m = 8; // this will be changed

        String expectedResult = addBitsFunction(operand1, operand2, m);

        return genericComplete(expectedResult, result, KnowledgeComponentKind.ADD_BITS);
    }

    /**
     * Performs binary addition modulo 2^256
     *
     * @param operand1 The binary string for operand 1.
     * @param operand2 The binary string for operand 2.
     * @param m        The int for calculating the modulo
     * @return The binary string result after adding two bits
     */
    private String addBitsFunction(String operand1, String operand2, int m) {

        if (operand1 == null || operand1.isEmpty()) {
            return "";
        }
        if (operand2 == null || operand2.isEmpty()) {
            return "";
        }

        // Convert binary strings to BigIntegers
        BigInteger num1 = new BigInteger(operand1, 2);
        BigInteger num2 = new BigInteger(operand2, 2);

        // Perform addition
        BigInteger sum = num1.add(num2);

        // Calculate the result modulo 2^256
        BigInteger modulo = new BigInteger("2").pow(m);
        BigInteger result = sum.mod(modulo);

        // Convert the result back to a binary string
        String resultBinary = result.toString(2);

        // Ensure the binary string has 256 bits (pad with leading zeros if necessary)
        while (resultBinary.length() < m) {
            resultBinary = "0" + resultBinary;
        }

        System.out.println("Result : " + resultBinary);

        return resultBinary;
    }
}
