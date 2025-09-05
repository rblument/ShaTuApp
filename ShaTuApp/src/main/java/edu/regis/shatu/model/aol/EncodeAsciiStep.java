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
package edu.regis.shatu.model.aol;

/**
 * A tutoring step that requires the student to encode a string as a sequence
 * of ASCII values in one or more steps.
 * 
 * @author rickb
 */
public class EncodeAsciiStep  {
    /**
     * The example that is associated with this encoding step.
     */
    private EncodeAsciiExample example;
<<<<<<< HEAD
    
=======
    private int currentIndex = 0;
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * Whether the encoding of the string in the example should be performed
     * as one or multiple steps.
     * 
     * If true, the coding of each character in the example should be performed
     * as its own step. Otherwise, the entire string should be encoded in one
     * step.
     */
    private boolean multiStep;
    
    public EncodeAsciiStep() {
<<<<<<< HEAD
       
=======
    this.multiStep = false;  // Set default to single-step encoding
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }

    public EncodeAsciiExample getExample() {
        return example;
    }

    public void setExample(EncodeAsciiExample example) {
        this.example = example;
<<<<<<< HEAD
=======
        this.currentIndex = 0;
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }

    public boolean isMultiStep() {
        return multiStep;
    }

    public void setMultiStep(boolean multiStep) {
        this.multiStep = multiStep;
    }
<<<<<<< HEAD
}
=======
    
    public interface OutputListener {
    void appendText(String text);
    }
    
    public String encode() {
        StringBuilder output = new StringBuilder();
        String inputString = example.getExampleString();
        int[] asciiValues = new int[inputString.length()];

        for (int i = 0; i < inputString.length(); i++) {
            asciiValues[i] = (int) inputString.charAt(i);
        }
        example.setAsciiEncoding(asciiValues);

        if (multiStep) {
            if (currentIndex < asciiValues.length) {
                output.append("Encoding each character one by one:\n");
                output.append(inputString.charAt(currentIndex) + " -> " 
                        + asciiValues[currentIndex] + "\n");
                currentIndex++;
                output.append("Current index: " + currentIndex + "\n");
            } else {
                output.append("Completed stepping through all characters.\n");
                currentIndex = 0;
            }
        } else {
            output.append("Encoding the entire string at once:\n");
            output.append(inputString + " -> " + 
                    java.util.Arrays.toString(asciiValues) + "\n");
        }
        return output.toString();
    }
}

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
