/*
 * SHATU: SHA-256 Tutor
 * 
 *  (C) Johanna & Richard Blumenthal, All rights reserved
 * 
 *  Unauthorized use, duplication or distribution without the authors'
 *  permission is strictly prohibited.
 * 
 *  Unless required by applicable law or agreed to in writing, this
 *  software is distributed on an "AS IS" basis without warranties
 *  or conditions of any kind, either expressed or implied.
 */
package edu.regis.shatu.model.steps;

import edu.regis.shatu.model.aol.StepSubType;

/**
 *
 * @author chand
 */
public class PrepScheduleStep extends Step {
    /**
     * The Prepare Schedule questions, in order.
     */
    public static final String[] QUESTIONS = {
            "Which process ensures the input message conforms to the required block size in SHA-256?",
            "What transformation is applied to each 512-bit block before processing?",
            "What technique does SHA-256 use to extend a small set of words into a larger sequence?",
            "How is the expanded word sequence utilized in the hashing process?"
    };

    /**
     * The answer choices for each question. The first choice is the correct
     * one. The view shuffles them before showing them.
     */
    public static final String[][] CHOICES = {
            { "Padding is added to ensure the message is a multiple of 512 bits.",
                    "SHA-256 automatically adjusts message length without padding.",
                    "Each message is divided into arbitrary-sized chunks before hashing." },
            { "The block is split into 32-bit segments for further manipulation.",
                    "A checksum is computed and attached to each block.",
                    "Each chunk remains unaltered until the final hashing step." },
            { "A series of bitwise operations and shifts increase the number of words.",
                    "Additional words are appended from external sources.",
                    "Words are kept constant, ensuring stability in the process." },
            { "The transformed sequence helps derive intermediate hash values.",
                    "The expanded words are discarded after processing.",
                    "Only a subset of the expanded words contributes to the hash output." }
    };

    /**
     * Which question this step is about, from 1 to QUESTIONS.length.
     */
    private int questionNumber = 1;

    /**
     * The choice the student picked, or "" if none was picked.
     */
    private String answer = "";

    public PrepScheduleStep() {
        super(1, 0, StepSubType.PREPARE_SCHEDULE);
    }

    /**
     * Whether the given number is one of the questions.
     *
     * @param questionNumber a question number
     * @return true if it is from 1 to QUESTIONS.length
     */
    public static boolean isQuestion(int questionNumber) {
        return questionNumber >= 1 && questionNumber <= QUESTIONS.length;
    }

    /**
     * The correct choice for the given question.
     *
     * @param questionNumber a question number from 1 to QUESTIONS.length
     * @return the text of the correct choice
     */
    public static String correctAnswer(int questionNumber) {
        return CHOICES[questionNumber - 1][0];
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
