package edu.regis.shatu.objectives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.Hint;
import edu.regis.shatu.model.KnowledgeComponentKind;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.aol.PendingStep;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.steps.ChoiceFunctionStep;
import edu.regis.shatu.model.steps.Step;
import edu.regis.shatu.svc.TutorReply;

class ChoiceFunctionExampleHintTest {
    private final Gson gson = new Gson();
    private ChoiceFunctionStep generated;

    private final ChoiceFunction objective =
            new ChoiceFunction(new Student(new Account("test@regis.edu"))) {
                @Override
                public TutorReply genericExample(Object subStep, StepSubType subType,
                        ProblemType probType, KnowledgeComponentKind kind, String description) {
                    generated = (ChoiceFunctionStep) subStep;
                    return new TutorReply(":Success");
                }

                @Override
                public TutorReply genericHint(StepCompletion completion,
                        KnowledgeComponentKind kind, String text) {
                    Step step = completion.getStep();
                    Hint first = new Hint();
                    first.setId(0);
                    first.setText(text);
                    step.addHint(first);
                    // The real genericHint also replaces the question data with reply data.
                    step.setData("{}");
                    TutorReply reply = new TutorReply(":Success");
                    reply.setData(gson.toJson(new PendingStep(step)));
                    return reply;
                }
            };

    @Test
    void newExampleKeepsRequestedWidthAndCalculatesTheAnswer() {
        for (int width : new int[] {4, 8, 16, 32}) {
            ChoiceFunctionStep request = new ChoiceFunctionStep();
            request.setBitLength(width);
            generated = null;

            assertEquals(":Success", objective.example(null, gson.toJson(request)).getStatus());
            assertNotNull(generated);
            assertEquals(width, generated.getBitLength());
            String x = generated.getOperand1().replaceAll("\\s", "");
            String y = generated.getOperand2().replaceAll("\\s", "");
            String z = generated.getOperand3().replaceAll("\\s", "");
            assertEquals(width, x.length());
            assertEquals(width, y.length());
            assertEquals(width, z.length());
            StringBuilder expected = new StringBuilder(width);
            for (int i = 0; i < width; i++) {
                expected.append(x.charAt(i) == '1' ? y.charAt(i) : z.charAt(i));
            }
            assertEquals(expected.toString(), generated.getResult());
        }
    }

    @Test
    void missingOrUnsupportedWidthReturnsAUsefulErrorWithoutCreatingAnExample() {
        for (String data : new String[] {null, "{}", request(0), request(-1), request(5), request(33)}) {
            generated = null;
            TutorReply reply = objective.example(null, data);
            assertEquals(":ERR", reply.getStatus());
            assertEquals("Select a Choice Function problem size of 4, 8, 16, or 32 bits.",
                    reply.getErrorMessage());
            assertNull(generated);
        }
    }

    @Test
    void hintsExplainTheRuleThenWorkTheFirstBitOfTheCurrentQuestion() {
        for (String x : new String[] {"0 101", "1 010"}) {
            ChoiceFunctionStep problem = new ChoiceFunctionStep();
            problem.setBitLength(4);
            problem.setOperand1(x);
            problem.setOperand2("0000");
            problem.setOperand3("1111");
            Step step = new Step(1, 0, StepSubType.CHOICE_FUNCTION);
            step.setData(gson.toJson(problem));

            TutorReply reply = objective.hint(new StepCompletion(step, gson.toJson(problem)));
            PendingStep pending = gson.fromJson(reply.getData(), PendingStep.class);
            assertEquals(2, pending.getStep().getHints().size());
            assertEquals(0, pending.getStep().getHints().get(0).getId());
            assertEquals(1, pending.getStep().getHints().get(1).getId());
            String workedHint = pending.getStep().getHints().get(1).getText();
            assertEquals(x.charAt(0) == '1', workedHint.contains("first bit of y"));
            assertEquals(x.charAt(0) == '0', workedHint.contains("first bit of z"));
            assertFalse(workedHint.contains("full answer"));
        }
    }

    @Test
    void malformedQuestionStillReturnsTheGeneralHint() {
        ChoiceFunctionStep problem = new ChoiceFunctionStep();
        problem.setBitLength(4);
        problem.setOperand1("01x1");
        problem.setOperand2("0000");
        problem.setOperand3("1111");
        Step step = new Step(1, 0, StepSubType.CHOICE_FUNCTION);
        step.setData(gson.toJson(problem));

        TutorReply reply = objective.hint(new StepCompletion(step, gson.toJson(problem)));
        PendingStep pending = gson.fromJson(reply.getData(), PendingStep.class);
        assertEquals(1, pending.getStep().getHints().size());
        assertEquals("For each position, use the bit from y when x is 1, or z when x is 0.",
                pending.getStep().getHints().get(0).getText());
    }

    private String request(int width) {
        ChoiceFunctionStep step = new ChoiceFunctionStep();
        step.setBitLength(width);
        return gson.toJson(step);
    }
}
