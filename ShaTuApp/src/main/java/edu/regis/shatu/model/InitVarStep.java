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
package edu.regis.shatu.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This class holds the core logic for the initialize variables step.
 * All answers and hints are initialized and stored here.
 * User's answers may be checked for correctness as well.
 * @author Ryley MacLagan
 */
public class InitVarStep {
    // Defines a map to associate user answer with correct answer
    final private Map<String, String> answers = new HashMap<>();
    final private Map<String, String> userAnswers = new HashMap<>();

    // Defines maps to associate user answer with correct hint
    final private Map<String, String> hint1 = new HashMap<>();
    final private Map<String, String> hint2 = new HashMap<>();
    final private Map<String, String> hint3 = new HashMap<>();
    
    public InitVarStep(){        
        // Map text fields with their correct answers to enable automated checking
        answers.put("H0", "0x6a09e667");
        answers.put("H1", "0xbb67ae85");
        answers.put("H2", "0x3c6ef372");
        answers.put("H3", "0xa54ff53a");
        answers.put("H4", "0x510e527f");
        answers.put("H5", "0x9b05688c");
        answers.put("H6", "0x1f83d9ab");
        answers.put("H7", "0x5be0cd19");
        
        hint1.put("H0", "First prime number is 2");
        hint1.put("H1", "Second prime number is 3");
        hint1.put("H2", "Third prime number 5");
        hint1.put("H3", "Fourth prime number 7");
        hint1.put("H4", "Fifth prime number 11");
        hint1.put("H5", "Sixth prime number 13");
        hint1.put("H6", "Seventh prime number 17");
        hint1.put("H7", "Eighth prime number 19");
        
        hint2.put("H0", "Fractional part is 0.414213562");
        hint2.put("H1", "Fractional part is 0.732050807");
        hint2.put("H2", "Fractional part is 0.236067977");
        hint2.put("H3", "Fractional part is 0.645751311");
        hint2.put("H4", "Fractional part is 0.316624790");
        hint2.put("H5", "Fractional part is 0.605551275");
        hint2.put("H6", "Fractional part is 0.123105625");
        hint2.put("H7", "Fractional part is 0.358898944");
        
        hint3.put("H0", "Convert 01101010000010011110011001100110 to Hexadecimal");
        hint3.put("H1", "Convert 10111011011001111010111010000011 to Hexadecimal");
        hint3.put("H2", "Convert 00111100011011101111001101110000 to Hexadecimal");
        hint3.put("H3", "Convert 10100101010011111111010100111010 to Hexadecimal");
        hint3.put("H4", "Convert 01010001000011100101001001111110 to Hexadecimal");
        hint3.put("H5", "Convert 10011011000001010110100010001010 to Hexadecimal");
        hint3.put("H6", "Convert 00011111100000111101100110101001 to Hexadecimal");
        hint3.put("H7", "Convert 01011011111000001100110100011011 ​to Hexadecimal");
    }
    
    public void setUserAnswer(String variable, String answer){
        userAnswers.put(variable, answer);
    }
    
    public String getUserAnswer(String variable) {
        return userAnswers.get(variable);
    }
    
     public String getAnswer(String variable) {
        return answers.get(variable);
    }
    
    public String getHint(String variable, int level) {
        switch(level){
            case 1: return hint1.get(variable);
            case 2: return hint2.get(variable);
            case 3: return hint3.get(variable);
            default: return "";
        }
    }
    
    /**
     * Checks if a specific user answer is correct.
     * @param variable the variable to check user's answer for
     * @return true if user's answer for variable is correct. False otherwise.
     */
     public boolean isUserCorrect(String variable) {
        String correctAnswer = answers.get(variable);
        String userAnswer = userAnswers.get(variable);
        return correctAnswer != null && correctAnswer.equals(userAnswer);
    }
    
    /**
     * Checks if all user answers are correct
     * @return true if all user answers are correct. False otherwise.
     */
    public boolean allAnswersCorrect() {
        for (String key : answers.keySet()) {
            if (!isUserCorrect(key)) {
                return false;
            }
        }
        return true;
    }
}
