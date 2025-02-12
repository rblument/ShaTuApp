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

import edu.regis.shatu.view.StepSelection;

/**
 * The legal step types
 * 
 * This class is essentially kludge since I couldn't get subclassing of
 * Gson/Json subtypes to work. So, I now manually handle it. 
 * 
 * @author rickb
 */
public enum StepSubType {
    /**
     * The user must acknowledge a message (i.e., perhaps via a pop-up dialog)
     */
    INFO_MESSAGE("Information Message", null),
    
    /**
    * A step in which the student must perform some GUI action, which is 
    * typically performed outside of actual tutoring for purposes of learning 
    * the GUI, such as learning to request a hint.
     */
    GUI_ACTION("GUI Action", null),
    
    /**
     * A request to encode a number as binary digits
     */
    ENCODE_BINARY("Encode Binary",StepSelection.ENCODE),
    
    /**
     * A request to encode a number as hexadecimal digits.
     */
    ENCODE_HEX("Encode Hex",StepSelection.ENCODE),
    
    /**
     * Represents a request to 
     * 
     * Data: An EncodeAsciiExample specifying the length of the string to be
     *       encoded.
     */
    ENCODE_ASCII("ASCII Encode",StepSelection.ENCODE),
    
    ADD_ONE_BIT("Add One Bit",StepSelection.ADD1),
    
    PAD_ZEROS("Pad with Zeros", StepSelection.PAD),
    
    ADD_MSG_LENGTH("Add Message Length",StepSelection.LENGTH),
    
    PREPARE_SCHEDULE("Prepare Schedule",StepSelection.PREPARE),
    
    INITIALIZE_VARS("Initialize Variables",StepSelection.INIT_VARS),
    
    COMPRESS_ROUND("Compress Round", StepSelection.COMPRESS),
    
    ROTATE_BITS("Rotate n BITS", StepSelection.ROTATE_BITS),
    
    SHIFT_BITS("Shift Bits", StepSelection.SHIFT_RIGHT),
    
    XOR_BITS("XOR Bits", StepSelection.XOR),
    
    ADD_BITS("Add Bits", StepSelection.ADD_TWO_BIT),
    
    MAJORITY_FUNCTION("Majority Function", StepSelection.MAJ_FUNCTION),
    
    CHOICE_FUNCTION("Choice Function", StepSelection.CHOICE_FUNCTION),

    SHA_ZERO("SHA Sum 0 Function", StepSelection.SHA_ZERO),
    
    STEP_COMPLETION_REPLY("Step Completion Reply", null),
    
    REQUEST_HINT("Request Hint", null),    
    /**
     * The initial default value in a NewExampleRequest
     */
    DEFAULT("Unknown", null),
    
    ERROR("Error", null);
    
    /**
     * The name used by the server to identify this request.
     */
    private final String subType;
    
    /**
     * The name of the view that should be displayed, if any, when this 
     * step sub type is current (null means don't change views).
     */
    private final StepSelection viewName;
    
    /**
     * Initialize this enum object with the given title.
     * 
     * @param subType 
     * @param viewName the view that is displayed when this step is current with
     *                 null indicating to keep the currently displayed view.
     */
    StepSubType(String subType, StepSelection viewName) {
        this.subType = subType;
        this.viewName = viewName;
    }
    
    /**
     * Return the request name that is used by the server.
     * 
     * @return a String 
     */
    public String getSubType() {
        return subType;
    }
    
    public StepSelection getViewName() {
        return viewName;
    }
    
    /**
     * Return the subType name that is used by the server
     * 
     * @return a String
     */
    @Override
    public String toString() {
        return subType;
    }
    
    /**
     * Return the enum value for the given title.
     * 
     * @param aTitle
     * @return 
     */
    public static StepSubType findValue(String aTitle) {
        for (StepSubType kind : values()) {
            if (kind.getSubType().equalsIgnoreCase(aTitle))
                return kind;
        }
        
        return ERROR;
    }
}
