package edu.regis.shatu.objectives;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.StudentModel;
import edu.regis.shatu.svc.ShaTuTutor;
import edu.regis.shatu.svc.TutorReply;

abstract public class Objective {

    StudentModel studentModel;

    Objective(Student student) {
        this.studentModel = student.getStudentModel();
    }

    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Formats the result output by the choice function based on the size of the
     * problem.
     *
     * @param answer the output of the choice function
     *
     * @return the binary string representation of the answer
     */
    protected String formatResult(long answer, int bitLength) {
        String finalResult = "";

        switch (bitLength) {
            case 4:
                finalResult = String.format("%4s", Long.toBinaryString(answer)).replace(' ', '0');
                break;
            case 8:
                finalResult = String.format("%8s", Long.toBinaryString(answer)).replace(' ', '0');
                break;
            case 16:
                finalResult = String.format("%16s", Long.toBinaryString(answer)).replace(' ', '0');
                break;
            case 32:
                finalResult = String.format("%32s", Long.toBinaryString(answer)).replace(' ', '0');
                break;
            default:
                break;
        }
        return finalResult;
    }

    /**
     * Generates an n-bit binary string (length 4, 8, 16, or 32) to be used as
     * an input into the Ch function. Every four bits are separated by a space
     * to improve readability.
     *
     * @return A string to be used as an input into the function.
     */
    protected String generateInputString(int problemSize) {
        Random random = new Random();

        String inputString;
        String tempString;
        StringBuilder inputStringBuilder = new StringBuilder();
        int num;

        switch (problemSize) {
            case 4:
                num = random.nextInt();
                tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                inputStringBuilder.append(tempString);
                break;
            case 8:
                num = random.nextInt();
                tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                inputStringBuilder.append(tempString);

                inputStringBuilder.append(" ");
                num = random.nextInt();
                tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                inputStringBuilder.append(tempString);
                break;
            case 16:
                num = random.nextInt();
                tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                inputStringBuilder.append(tempString);

                for (int i = 0; i < 3; i++) {
                    inputStringBuilder.append(" ");
                    num = random.nextInt();
                    tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                    inputStringBuilder.append(tempString);
                }
                break;
            case 32:
                num = random.nextInt();
                tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                inputStringBuilder.append(tempString);

                for (int i = 0; i < 7; i++) {
                    inputStringBuilder.append(" ");
                    num = random.nextInt();
                    tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                    inputStringBuilder.append(tempString);
                }
                break;
            default:
                break;
        }

        inputString = inputStringBuilder.toString();

        return inputString;
    }

    /**
     * Utility for logging an error and an creating a tutoring reply error with
     * the given message, and optional originating exception.
     *
     * @param errMsg a displayable error message
     * @param ex     the original exception, if any, that caused the error,
     *               otherwise null.
     * @return a TutorReply with an ":ERR" status
     */
    public TutorReply createError(String errMsg, Exception ex) {
        if (ex == null) {
            Logger.getLogger(ShaTuTutor.class.getName()).log(Level.SEVERE, errMsg);
        } else {
            Logger.getLogger(ShaTuTutor.class.getName()).log(Level.SEVERE, errMsg, ex);
        }

        return new TutorReply(":ERR", errMsg);
    }

    /**
     * Method that generates and returns a random string.
     *
     * @param length
     *
     * @return
     */
    protected String generateRandomString(int length) {

        Random random = new Random();
        StringBuilder sb = new StringBuilder(length); // StringBuilder allows easier altering of a string.

        for (int i = 0; i < length; i++) {
            // Generates a random integer between 32 (inclusive) and 126 (inclusive)
            int randomChar = 32 + random.nextInt(95); // 126 - 32 + 1 = 95
            sb.append((char) randomChar);
        }

        return sb.toString();
    }

    abstract public TutorReply hint(StepCompletion completion);

    abstract public TutorReply completeStep(StepCompletion completion);

    abstract public TutorReply example(TutoringSession session, String jsonData);

}
