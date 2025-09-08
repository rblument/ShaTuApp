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
 * The legal types of practice problems that a student can request.
 * 
 * @author rickb
 */
public enum ProblemType {
    /**
     * Represents a request to 
     * 
     * Data: An EncodeAsciiExample specifying the length of the string to be
     *       encoded.
     */
    ASCII_ENCODE("ASCII Encode"),
    
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
    
    SHA_ZERO("Sha sum 0"),
    
    SHA_ONE("Sha sum 1"),
    
    STEP_COMPLETION_REPLY("Step Completion Reply"),
    
    REQUEST_HINT("Request Hint"),
    
    /**
     * The initial default value in a NewExampleRequest
     */
    DEFAULT("Unknown"),
    
    ERROR("ERROR");
    
     /**
     * The name used by the server to identify this request.
     */
    private final String requestName;
    
    /**
     * Initialize this enum object with the given title.
     * 
     * @param requestName 
     */
    ProblemType(String requestName) {
        this.requestName = requestName;
    }
    
    /**
     * Return the request name that is used by the server.
     * 
     * @return a String 
     */
    public String getRequestName() {
        return requestName;
    }
    
    /**
     * Return the request name that is used by the server
     * 
     * @return a String
     */
    @Override
    public String toString() {
        return requestName;
    }
    
       /**
     * Return the enum value for the given title.
     * 
     * @param aTitle
     * @return 
     */
    public static ProblemType findValue(String aTitle) {
        for (ProblemType kind : values()) {
            if (kind.getRequestName().equalsIgnoreCase(aTitle))
                return kind;
        }
        
        return ERROR;
    }
}