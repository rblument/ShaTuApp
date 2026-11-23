package edu.regis.shatu.objectives;

import edu.regis.shatu.model.Hint;
import edu.regis.shatu.model.KnowledgeComponentKind;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.PendingStep;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.steps.ChoiceFunctionStep;
import edu.regis.shatu.svc.TutorReply;

public class ChoiceFunction extends Objective {
    public ChoiceFunction(Student student) {
        super(student);
    }

    /**
     * Handler for the Choice Function Step Completion
     * Grades the answer and updates the Assessment table (exposures, successes, hints).
     * The completed Step is persisted for the student, in the Unit matching their
     * tutoring mode (See One, Do One, Teach One), by ShaTuTutor.completedStep() using
     * CompletedStepSvc.
     *
     * @param completion
     * @return
     */
    @Override
    public TutorReply hint(StepCompletion completion) {
        // Read the question before genericHint changes the step data into a hint reply.
        String workedFirstBit = firstBitHint(completion);
        TutorReply reply = genericHint(completion, KnowledgeComponentKind.CHOICE_FUNCTION,
                "For each position, use the bit from y when x is 1, or z when x is 0.");
        if (!":Success".equals(reply.getStatus()) || workedFirstBit == null) {
            return reply;
        }

        PendingStep pendingStep = gson.fromJson(reply.getData(), PendingStep.class);
        Hint nextHint = new Hint();
        nextHint.setId(1);
        nextHint.setText(workedFirstBit);
        pendingStep.getStep().addHint(nextHint);
        reply.setData(gson.toJson(pendingStep));
        return reply;
    }

    private String firstBitHint(StepCompletion completion) {
        if (completion == null || completion.getStep() == null
                || completion.getStep().getData() == null) {
            return null;
        }
        ChoiceFunctionStep problem = gson.fromJson(completion.getStep().getData(), ChoiceFunctionStep.class);
        if (problem == null) {
            return null;
        }
        try {
            int width = problem.getBitLength();
            if (!isSupportedWidth(width)) {
                return null;
            }
            String x = validateOperand(problem.getOperand1(), width);
            validateOperand(problem.getOperand2(), width);
            validateOperand(problem.getOperand3(), width);
            String source = x.charAt(0) == '1' ? "y" : "z";
            String selected = validateOperand(x.charAt(0) == '1'
                    ? problem.getOperand2() : problem.getOperand3(), width);
            return "The first bit of x is " + x.charAt(0) + ", so take the first bit of "
                    + source + ": " + selected.charAt(0) + ". Repeat for the remaining positions.";
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    /**
     * Handles client requests for a new choice function example.
     *
     * @return a TutorReply
     */
    @Override
    public TutorReply example(TutoringSession session, String jsonData) {
        System.out.println("newChoiceFunctionExample");
        ChoiceFunctionStep substep = gson.fromJson(jsonData, ChoiceFunctionStep.class);
        int bitLength = substep == null ? 0 : substep.getBitLength();
        if (!isSupportedWidth(bitLength)) {
            return createError("Select a Choice Function problem size of 4, 8, 16, or 32 bits.", null);
        }

        String operand1 = generateInputString(bitLength);
        String operand2 = generateInputString(bitLength);
        String operand3 = generateInputString(bitLength);

        substep.setOperand1(operand1);
        substep.setOperand2(operand2);
        substep.setOperand3(operand3);

        substep.setResult(choiceFunction(operand1, operand2, operand3, bitLength));
        
        TutorReply reply = genericExample(substep, StepSubType.CHOICE_FUNCTION, ProblemType.CHOICE_FUNCTION,
                KnowledgeComponentKind.CHOICE_FUNCTION,
                "Compute the result of the choice function on the three operands");
        
        return reply;
    }

    @Override
    public TutorReply completeStep(StepCompletion completion) {
        System.out.println("Tutor completeChoiceStep");

        ChoiceFunctionStep example = gson.fromJson(completion.getData(), ChoiceFunctionStep.class);
        String operand1 = example.getOperand1();
        String operand2 = example.getOperand2();

        String operand3 = example.getOperand3();
        int bitLength = example.getBitLength();
        String result = example.getResult();

        String expectedResult = choiceFunction(operand1, operand2, operand3, bitLength);


        return genericComplete(expectedResult, result, KnowledgeComponentKind.CHOICE_FUNCTION);
    }

    /**
     * Evaluates the choice function Ch(x, y, z).
     *
     * @param x Binary string representation of x.
     * @param y Binary string representation of y.
     * @param z Binary string representation of z.
     * @return Binary string result of Ch(x, y, z).
     */
    private String choiceFunction(String x, String y, String z, int bitLength) {
        if (!isSupportedWidth(bitLength)) {
            throw new IllegalArgumentException("Choice problems must be 4, 8, 16, or 32 bits");
        }
        String a = validateOperand(x, bitLength);
        String b = validateOperand(y, bitLength);
        String c = validateOperand(z, bitLength);
        StringBuilder result = new StringBuilder(bitLength);
        for (int i = 0; i < bitLength; i++) {
            result.append(a.charAt(i) == '1' ? b.charAt(i) : c.charAt(i));
        }
        return result.toString();
    }

    private boolean isSupportedWidth(int width) {
        return width == 4 || width == 8 || width == 16 || width == 32;
    }

    private String validateOperand(String operand, int bitLength) {
        String bits = operand == null ? "" : operand.replaceAll("\\s", "");
        if (bits.length() != bitLength || !bits.matches("[01]+")) {
            throw new IllegalArgumentException("Each choice operand must contain exactly "
                    + bitLength + " binary digits");
        }
        return bits;
    }

}
