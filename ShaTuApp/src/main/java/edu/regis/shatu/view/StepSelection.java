/*
 * SHATU: SHA-256 Tutor
 * 
 *  (C) Johanna & Richard Blumenthal, All rights reserved
 * 
 *  Unauthorized use, duplication or distribution without the authors'
 *  permission is strictly prohibted.
 * 
 *  Unless required by applicable law or agreed to in writing, this
 *  software is distributed on an "AS IS" basis without warranties
 *  or conditions of any kind, either expressed or implied.
 */
package edu.regis.shatu.view;

/**
 * The primary steps in the SHA-256 algorithm, which correspond to views that
 * can be displayed by selecting the associated labels.
 * 
 * @author rickb
 */
public enum StepSelection {
    /**
     * Encode the input message as ASCII bytes step.
     */
    ENCODE(new HighlightLabel("Encode as ASCII")), 

    ADD1(new HighlightLabel("Add '1' bit")), 
    
    PAD(new HighlightLabel("Pad with '0's")), 
    
    LENGTH(new HighlightLabel("Add Msg Length")), 
    
    XOR (new HighlightLabel("Exclusive OR (XOR)")),
    
    CHOICE_FUNCTION(new HighlightLabel("Value of the Choice (Ch) function")),
  
    PREPARE(new HighlightLabel("Prepare Schedule")),
   
    INIT_VARS(new HighlightLabel("Initialize Variables")), 
    
    COMPRESS(new HighlightLabel("Compress Round")),

    ROTATE_BITS(new HighlightLabel("Rotate n bits")),

    SHIFT_RIGHT(new HighlightLabel("Shift right")),

    ADD_TWO_BIT(new HighlightLabel("Add two 𝑛 bit")),
    
    MAJ_FUNCTION(new HighlightLabel("Maj function value")),
    
    SHA_ZERO(new HighlightLabel("SHA Sum 0 value")),
    
    SHA_ONE(new HighlightLabel("SHA Sum 1 value")),
    
    /**
     * Lesson Labels for LessonStepSelectionView
     */   
    OVERVIEW(new HighlightLabel("Overview")),
    
    MESSAGE_PREPROCESSING(new HighlightLabel("Message Preprocessing")),
    
    HASH_COMPUTATION(new HighlightLabel("Hash Computation")),
    
    CONCLUSION(new HighlightLabel("Conclusion")),
        
    // Isn't displayed in StepSelectionView
    STEP_REPLY(new HighlightLabel("Step Reply"));

    private HighlightLabel label;
    
    StepSelection(HighlightLabel label) {
        this.label = label;
        label.setStepSelection(this);
    }
    
    public HighlightLabel getLabel() {
        return label;
    }
}

