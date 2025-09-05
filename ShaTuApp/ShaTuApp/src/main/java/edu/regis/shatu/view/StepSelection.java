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
    ENCODE("Encode as ASCII"), 

    ADD1("Add '1' bit"), 
    
    PAD("Pad with '0's"), 
    
    LENGTH("Add Msg Length"), 
    
    XOR("Exclusive OR (XOR)"),
    
    CHOICE_FUNCTION("Choice Function (Ch)"),
  
    PREPARE("Prepare Schedule"),
   
    INIT_VARS("Initialize Variables"), 
    
    COMPRESS("Compress Round"),

    ROTATE_BITS("Rotate n bits"),

    SHIFT_RIGHT("Shift right"),

    ADD_TWO_BIT("Add two 𝑛 bit"),
    
    MAJ_FUNCTION("Maj function value"),
    
    SHA_ZERO("SHA Sum 0 value"),
    
    SHA_ONE("SHA Sum 1 value"),
    
    /**
     * Lesson Labels for LessonStepSelectionView
     */   
    OVERVIEW("Overview"),
    
    MESSAGE_PREPROCESSING("Message Preprocessing"),
    
    HASH_COMPUTATION("Hash Computation"),
    
    CONCLUSION("Conclusion"),
        
    // Isn't displayed in StepSelectionView
    STEP_REPLY("Step Reply");

    /**
     * A Highlight Label associated with this enum, which can be displayed
     */
    private final HighlightLabel label;
    
    /**
     * 
     * @param text pretty print text displayed for the associated label
     */
    StepSelection(String text) {
        this.label = new HighlightLabel(text);
        label.setViewName(name()); // Note, the enum name, not the text.
    }
    
    public HighlightLabel getLabel() {
        return label;
    }
}

