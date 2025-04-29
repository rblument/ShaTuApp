package edu.regis.shatu.objectives;

import edu.regis.shatu.model.KnowledgeComponentKind;
import edu.regis.shatu.model.ShaZeroStep;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.svc.TutorReply;

public class ShaZero extends Objective {
    public ShaZero(Student student) {
        super(student);
    }

    /**
     * Handler for returning Hint information to the client for the ShaSigmaZero
     * View
     *
     * @param completion The StepCompletion that the user is on
     * @return Returns a response to the Tutor with the hint in the response body
     */
    @Override
    public TutorReply hint(StepCompletion completion) {
        return genericHint(completion, KnowledgeComponentKind.SHAR_ZERO,
                "The Σ₀ function involves three ROTR operations XOR'd together ");
    }

    /**
     * Handler that returns a new example problem to the ShaSigmaZero client view
     *
     * @param session  The active Tutoring Session
     * @param jsonData The JSON sent from the client which models the SigmaZero step
     * @return Returns a response which can be sent back to the client with a new
     *         example problem in the body
     */
    @Override
    public TutorReply example(TutoringSession session, String jsonData) {
        ShaZeroStep substep = gson.fromJson(jsonData, ShaZeroStep.class);

        substep.setOperandA(generateInputString(substep.getBitLength()));

        substep.setResult(calculateSigma(substep.getOperandA(), substep.getBitLength()));

        return genericExample(substep, StepSubType.SHA_ZERO, ProblemType.SHA_ZERO, KnowledgeComponentKind.SHAR_ZERO,
                "Compute the result of the Σ₀ function");
    }

    /**
     * Handler for completion of the problem in the SigmaZero client view
     * TODO: Refactor so that:
     *  1.) Steps in the database are actually completed since as of now, none exist
     *  2.) Steps are completed for Tasks (Task table) in Units (Unit Table)
     *  3.) Steps are completed for each Unit (See One, Do One, Teach One)
     *  As of now, this is only logging assessment data (Assessment table) to the database based on the number of
     *  exposures, successes, and hints the user has completed during the Do One section of the application and it is
     *  not actually logging anything
     *
     * @param completion The StepCompletion that has occurred
     * @return Returns a TutorReply which tells which tasks the user has left
     */
    @Override
    public TutorReply completeStep(StepCompletion completion) {
        ShaZeroStep example = gson.fromJson(completion.getData(), ShaZeroStep.class);
        String operand1 = example.getOperandA();
        int bitLength = example.getBitLength();
        String result = example.getResult();

        String expectedResult = calculateSigma(operand1, bitLength);
        System.out.println("Expected result: " + expectedResult);

        return genericComplete(expectedResult, result, KnowledgeComponentKind.SHAR_ZERO);
    }

    /**
     * Calculates the SHA Σ₀ function involving rotation and right shift operations.
     *
     * @param input The input binary number.
     * @return The result after performing the SHA Σ₀ function.
     */
    private String calculateSigma(String input, int bitLength) {
        input = input.replaceAll("\\s", "");
        long a = Long.parseLong(input, 2);

        // Perform rotations and shift operations
        return formatResult(rotateRight(a, 2) ^ rotateRight(a, 13) ^ rotateRight(a, 22), bitLength);
    }

    /**
     * Performs rotation (ROR) on the given input string for the
     * specified number of positions.
     *
     * @param input     The input value to rotate.
     * @param positions The number of positions for the rotation.
     * @return The rotated string.
     */

    private long rotateRight(long input, int positions) {
        return (input >>> positions) | (input << (32 - positions));
    }
}