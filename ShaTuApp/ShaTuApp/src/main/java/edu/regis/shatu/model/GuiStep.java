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

/**
 * A step in which the student must perform some GUI action, which is typically
 * performed outside of actual tutoring for purposes of learning the GUI, such 
 * as learning to request a hint.
 * 
 * @author rickb
 */
public class GuiStep {
    /**
     * The gesture (action) the student must perform in the GUI to complete
     * successfully this step.
     */
    private GuiGesture gesture;
    
    public GuiStep() {
        
    }

    public GuiGesture getGesture() {
        return gesture;
    }

    public void setGesture(GuiGesture gesture) {
        this.gesture = gesture;
    }
}
