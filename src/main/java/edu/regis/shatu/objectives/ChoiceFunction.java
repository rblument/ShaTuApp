package edu.regis.shatu.objectives;

import edu.regis.shatu.dao.SessionDAO;
import edu.regis.shatu.model.KnowledgeComponentKind;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.steps.ChoiceFunctionStep;
import edu.regis.shatu.svc.ServiceFactory;
import edu.regis.shatu.svc.TutorReply;

public class ChoiceFunction extends Objective {
    public ChoiceFunction(Student student) {
        super(student);
    }

    /**
     * Handler for the Choice Function Step Completion
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
    public TutorReply hint(StepCompletion completion) {
        return genericHint(completion, KnowledgeComponentKind.CHOICE_FUNCTION,
                "The choice function selects bits from one input or another based on the value of the first input");
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

        int bitLength = substep.getBitLength();

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

        System.out.println("Expected result: " + expectedResult); // ToDo debuggin

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
        // Convert the binary strings to integer values
        String tempX = x.replaceAll("\\s", "");
        String tempY = y.replaceAll("\\s", "");
        String tempZ = z.replaceAll("\\s", "");

        long intX = Long.parseLong(tempX, 2);
        long intY = Long.parseLong(tempY, 2);
        long intZ = Long.parseLong(tempZ, 2);

        long xy = intX & intY;

        long notX = ~intX & intZ;

        long result = xy ^ notX;

        // Convert the result back to binary string
        String binaryResult = formatResult(result, bitLength);

        return binaryResult;
    }

}
