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

<<<<<<< HEAD
=======
import edu.regis.shatu.view.StepSelection;

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
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
<<<<<<< HEAD
    INFO_MESSAGE("Information Message"),
=======
    INFO_MESSAGE("Information Message", null),
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    
    /**
    * A step in which the student must perform some GUI action, which is 
    * typically performed outside of actual tutoring for purposes of learning 
    * the GUI, such as learning to request a hint.
     */
<<<<<<< HEAD
    GUI_ACTION("GUI Action"),
=======
    GUI_ACTION("GUI Action", null),
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    
    /**
     * A request to encode a number as binary digits
     */
<<<<<<< HEAD
    ENCODE_BINARY("Encode Binary"),
=======
    ENCODE_BINARY("Encode Binary",StepSelection.ENCODE),
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    
    /**
     * A request to encode a number as hexadecimal digits.
     */
<<<<<<< HEAD
    ENCODE_HEX("Encode Hex"),
=======
    ENCODE_HEX("Encode Hex",StepSelection.ENCODE),
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    
    /**
     * Represents a request to 
     * 
     * Data: An EncodeAsciiExample specifying the length of the string to be
     *       encoded.
     */
<<<<<<< HEAD
    ENCODE_ASCII("ASCII Encode"),
    
    ADD_ONE_BIT("Add One Bit"),
    
    PAD_ZEROS("Pad with Zeros"),
    
    ADD_MSG_LENGTH("Add Message Length"),
    
    PREPARE_SCHEDULE("Prepare Schedule"),
    
    INITIALIZE_VARS("Initialize Variables"),
    
    COMPRESS_ROUND("Compress Round"),
    
    ROTATE_BITS("Rotate n BITS"),
    
    SHIFT_BITS("Shift Bits"),
    
    XOR_BITS("XOR Bits"),
    
    ADD_BITS("Add Bits"),
    
    MAJORITY_FUNCTION("Majority Function"),
    
    CHOICE_FUNCTION("Choice Function"),
    
    /**
     * The initial default value in a NewExampleRequest
     */
    DEFAULT("Unknown");
=======
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
    
    SHA_ONE("SHA Sum 1 Function", StepSelection.SHA_ONE),
    
    STEP_COMPLETION_REPLY("Step Completion Reply", null),
    
    REQUEST_HINT("Request Hint", null),    
    /**
     * The initial default value in a NewExampleRequest
     */
    DEFAULT("Unknown", null),
    
    ERROR("Error", null);
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    
    /**
     * The name used by the server to identify this request.
     */
    private final String subType;
    
    /**
<<<<<<< HEAD
     * Initialize this enum object with the given title.
     * 
     * @param subType 
     */
    StepSubType(String subType) {
        this.subType = subType;
=======
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
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }
    
    /**
     * Return the request name that is used by the server.
     * 
     * @return a String 
     */
    public String getSubType() {
        return subType;
    }
    
<<<<<<< HEAD
=======
    public StepSelection getViewName() {
        return viewName;
    }
    
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * Return the subType name that is used by the server
     * 
     * @return a String
     */
    @Override
    public String toString() {
        return subType;
    }
<<<<<<< HEAD
=======
    
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
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
}
