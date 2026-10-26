package edu.regis.shatu.objectives;

import edu.regis.shatu.model.KnowledgeComponentKind;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.steps.MessageLenStep;
import edu.regis.shatu.svc.TutorReply;

public class AddMsgLen extends Objective {

    public AddMsgLen(Student student) {
        super(student);
    }

    @Override
    public TutorReply hint(StepCompletion completion) {
        return genericHint(completion, KnowledgeComponentKind.ADD_MSG_LENGTH,
                "Append the original message length as a 64-bit big-endian integer");
    }

    /**
     * Handles client requests for a new add message length example.
     *
     * @return a TutorReply
     */
    @Override
    public TutorReply example(TutoringSession session, String jsonData) {
        System.out.println("Start tutor newAddMsgLenExample"); // Error checking

        MessageLenStep subStep = gson.fromJson(jsonData, MessageLenStep.class);

        int messageLength = subStep.getMessageLength();

        String question = generateRandomString(messageLength);

        subStep.setQuestion(question);

        subStep.setResult(Integer.toBinaryString(messageLength * 8));

        System.out.println(subStep.getResult()); // Error checking

        return genericExample(subStep, StepSubType.ADD_MSG_LENGTH, ProblemType.ADD_MSG_LENGTH,
                KnowledgeComponentKind.ADD_MSG_LENGTH,
                "Calculate the message length for the last 64 bits of the message length step");
    }

    /**
     * Function that is called from the overrided stepCompletion method from the
     * MessageLenView. Checks the users answer with the correct answer and will
     * provide the user with further guidance.
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
        MessageLenStep completedMessageLenStep = gson.fromJson(completion.getData(), MessageLenStep.class);
        if (completedMessageLenStep == null || completedMessageLenStep.getQuestion() == null
                || completedMessageLenStep.getQuestion().isEmpty()
                || completedMessageLenStep.getQuestion().length() > MessageLenStep.MAX_MESSAGE_LENGTH) {
            return createError("There is no message to measure. Request a new example.", null);
        }

        String userAnswer = completedMessageLenStep.getUserAnswer();
        userAnswer = userAnswer == null ? "" : userAnswer.replaceAll("\\s", "");
        String correctAnswer = binaryForLength(completedMessageLenStep.getQuestion().length());

        return genericComplete(correctAnswer, userAnswer, KnowledgeComponentKind.ADD_MSG_LENGTH);
    }

    /** The practice screen asks for the binary length without leading zeros. */
    static String binaryForLength(int characters) {
        return Long.toBinaryString((long) characters * 8);
    }

}
