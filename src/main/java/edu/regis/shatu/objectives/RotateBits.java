package edu.regis.shatu.objectives;

import edu.regis.shatu.model.KnowledgeComponentKind;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.RotateStep;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.svc.TutorReply;

public class RotateBits extends Objective {
    public RotateBits(Student student) {
        super(student);
    }

    @Override
    public TutorReply hint(StepCompletion completion) {
        return genericHint(completion, KnowledgeComponentKind.ROTATE_BITS,
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
        if (example.getLength() != 16 && example.getLength() != 32) {
            throw new IllegalArgumentException("Rotation problems must be 16 or 32 bits long");
        }

        // Check if the data (bit string) is provided, if not, generate it
        if (example.getData() == null || example.getData().isBlank()) {
            String generatedData = generateInputString(example.getLength());
            example.setData(generatedData);
        }
        validateBits(example.getData(), example.getLength());

        return genericExample(example, StepSubType.ROTATE_BITS, ProblemType.ROTATE_BITS,
                KnowledgeComponentKind.ROTATE_BITS, "Bits are rotated");
    }

    /**
     * Handler for the Rotate Bits Step Completion
     * Grades the answer and updates the Assessment table (exposures, successes, hints).
     * The completed Step is persisted for the student, in the Unit matching their
     * tutoring mode (See One, Do One, Teach One), by ShaTuTutor.completedStep() using
     * CompletedStepSvc.
     *
     * @param completion
     * @return
     */
    @Override
    public TutorReply completeStep(StepCompletion completion) {
        System.out.println("Tutor completeRotateStep");
        RotateStep example = gson.fromJson(completion.getData(), RotateStep.class);
        int amount = example.getAmount();
        String data = example.getData();

        String expectedResult = performBitRotation(data, amount, example.getLength());

        String result = example.getUserResponse() == null ? ""
                : example.getUserResponse().replaceAll("\\s+", "");

        return genericComplete(expectedResult, result, KnowledgeComponentKind.ROTATE_BITS);
    }

    /**
     * Performs bit rotation on the example string to get correct answer for
     * comparison to user's answer.
     *
     * @param data
     * @param amount Number of positions to rotate right; negative values wrap
     *               around the width as well.
     * @param length Expected number of bits in the problem.
     * @return result
     */
    static String performBitRotation(String data, int amount, int length) {
        String bits = validateBits(data, length);
        int places = Math.floorMod(amount, length);
        return bits.substring(length - places) + bits.substring(0, length - places);
    }

    private static String validateBits(String data, int length) {
        if (length <= 0 || data == null) {
            throw new IllegalArgumentException("Rotation requires a nonempty binary string of the selected length");
        }
        String bits = data.replaceAll("\\s+", "");
        if (bits.length() != length || !bits.matches("[01]+")) {
            throw new IllegalArgumentException("Rotation requires a binary string of " + length + " bits");
        }
        return bits;
    }
}
