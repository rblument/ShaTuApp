package edu.regis.shatu.objectives;

import edu.regis.shatu.model.KnowledgeComponentKind;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.steps.ShaOneStep;
import edu.regis.shatu.svc.TutorReply;

public class ShaOne extends Objective {
    public ShaOne(Student student) {
        super(student);
    }

    /**
     * Handler for returning Hint information to the client for the ShaOne
     * View
     *
     * @param completion The StepCompletion that the user is on
     * @return Returns a response to the Tutor with the hint in the response body
     */
    @Override
    public TutorReply hint(StepCompletion completion) {
        return genericHint(completion, KnowledgeComponentKind.SHA_ONE,
                "The Σ₁ function involves three ROTR operations XOR'd together ");
    }

    /**
     * Handler that returns a new example problem to the ShaOne client view
     *
     * @param session  The active Tutoring Session
     * @param jsonData The JSON sent from the client which models the ShaOne step
     * @return Returns a response which can be sent back to the client with a new
     *         example problem in the body
     */
    @Override
    public TutorReply example(TutoringSession session, String jsonData) {
        ShaOneStep substep = gson.fromJson(jsonData, ShaOneStep.class);

        substep.setOperandA(generateInputString(substep.getBitLength()));

        substep.setResult(calculateSigma(substep.getOperandA(), substep.getBitLength()));

        return genericExample(substep, StepSubType.SHA_ONE, ProblemType.SHA_ONE, KnowledgeComponentKind.SHA_ONE,
                "Compute the result of the Σ₁ function");
    }

    /**
     * Handler for completion of the problem in the SigmaZero client view
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
     * @param completion The StepCompletion that has occurred
     * @return Returns a TutorReply which tells which tasks the user has left
     */
    @Override
    public TutorReply completeStep(StepCompletion completion) {
        ShaOneStep example = gson.fromJson(completion.getData(), ShaOneStep.class);
        String operand1 = example.getOperandA();
        int bitLength = example.getBitLength();
        String result = example.getResult();

        String expectedResult = calculateSigma(operand1, bitLength);
        System.out.println("Expected result: " + expectedResult);

        return genericComplete(expectedResult, result, KnowledgeComponentKind.SHA_ONE);
    }

    /**
     * Calculates the SHA Σ₁ function, the exclusive-or of three right rotations
     * of the input. Σ₁ uses no shift; that belongs to the lowercase σ functions.
     *
     * @param input The input binary number.
     * @return The result after performing the SHA Σ₁ function.
     */
    private String calculateSigma(String input, int bitLength) {
        input = input.replaceAll("\\s", "");
        long a = Long.parseLong(input, 2);

        // FIPS 180-4 section 4.1.2: Sigma 1 is ROTR^6 XOR ROTR^11 XOR ROTR^25.
        // (Sigma 0 uses 2, 13 and 22 - see ShaZero.)
        return formatResult(rotateRight(a, 6, bitLength)
                ^ rotateRight(a, 11, bitLength)
                ^ rotateRight(a, 25, bitLength), bitLength);
    }

    /**
     * Performs rotation (ROR) on the given input string for the
     * specified number of positions.
     *
     * The rotation is performed within the width of the current problem, so a
     * 32-bit problem rotates within 32 bits. Bits shifted off the right re-enter
     * on the left and the result is masked back to {@code bitLength} bits, which
     * keeps the value from growing wider than the problem the student was given.
     *
     * @param input     The input value to rotate.
     * @param positions The number of positions for the rotation.
     * @param bitLength The width of the problem, in bits.
     * @return The rotated value, masked to bitLength bits.
     */

    private long rotateRight(long input, int positions, int bitLength) {
        if (bitLength < 1 || bitLength > 63) {
            throw new IllegalArgumentException(
                    "bitLength must be between 1 and 63, was: " + bitLength);
        }
        long mask = (1L << bitLength) - 1;
        long value = input & mask;
        // floorMod keeps a negative rotation meaningful instead of
        // silently collapsing the result to zero.
        int places = Math.floorMod(positions, bitLength);
        if (places == 0) {
            return value;
        }
        return ((value >>> places) | (value << (bitLength - places))) & mask;
    }
}
