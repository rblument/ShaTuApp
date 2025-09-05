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
 * A step requesting the student user to perform a bit operation example.
 * 
 * @author rickb
 */
public class BitOpStep {
    /**
     * The example that is associated with this step.
     */
    private BitOpExample example;
    
     /**
     * Whether the result in the example should be performed one bit at a time
     * versus all the bits at one time.
     */
    private boolean isMultiStep;
    
    /**
     * Initialize with a default multiStep value.
     */
    public BitOpStep() {
    }

    public BitOpExample getExample() {
        return example;
    }

    public void setExample(BitOpExample example) {
        this.example = example;
    }

    public boolean isMultiStep() {
        return isMultiStep;
    }

    public void setMultiStep(boolean isMultiStep) {
        this.isMultiStep = isMultiStep;
    }
}
