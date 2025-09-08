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
 *
 * A step requesting the student user to perform a ShaOneView step.
 * 
 * @author George Mendenhall
 */
public class ShaOneViewStep {
        /**
     * The example that is associated with this step.
     */
    private NewExampleRequest example;
    
     /**
     * Whether the result in the example should be performed one bit at a time
     * versus all the bits at one time.
     */
    private boolean isMultiStep;
    
     /**
     * The user's response
     */
    private String userResponse;
    
    /**
     * Initialize with a default multiStep value.
     */
    public ShaOneViewStep() {
    }

    public NewExampleRequest getExample() {
        return example;
    }

    public void setExample(NewExampleRequest example) {
        this.example = example;
    }

    public boolean isMultiStep() {
        return isMultiStep;
    }

    public void setMultiStep(boolean isMultiStep) {
        this.isMultiStep = isMultiStep;
    }
    
        /**
     * Getter for the User's response to the rotation question
     *
     * @return The user's response to the question
     */
    public String getUserResponse(){
        return userResponse;
    }

    /**
     * Setter for the User's response to the question
     *
     * @param userResponse The user's response to the question
     */
    public void setUserResponse(String userResponse){
        this.userResponse = userResponse;
    }
}