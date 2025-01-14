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
 * The valid knowledge components, or outcomes, with their associated
 * database ids.
 * 
 * @author rickb
 */
public enum KnowledgeComponentKind {
    /**
     * Define the corresponding database id for this knowledge component.
     */
     INFORMATION_MESSAGE("Information Message", 1),
     
    /**
     * Define the corresponding database id for this knowledge component.
     */
     ASCII_ENCODE("ASCII Encode", 100),
     
    /**
     * Define the corresponding database id for this knowledge component.
     */
     ADD_ONE_BIT("Add One Bit", 110),
     
    /**
     * Define the corresponding database id for this knowledge component.
     */
     CHOICE_FUNCTION("Choice Function", 210),
    
    /**
     * Define the corresponding database id for this knowledge component.
     */
     PAD_ZEROS("Pad with Zeros", 120),
     
    /**
     * Define the corresponding database id for this knowledge component.
     */
     ADD_MSG_LENGTH("Add Message Length", 130),
     
    /**
     * Define the corresponding database id for this knowledge component.
     */
     PREPARE_SCHEDULE("Prepare Schedule", 140),
     
    /**
     * Define the corresponding database id for this knowledge component.
     */
     INITIALIZE_VARS("Initialize Variables", 150),
     
    /**
     * Define the corresponding database id for this knowledge component.
     */
     COMPRESS_ROUND("Compress Round", 160),
     
    /**
     * Define the corresponding database id for this knowledge component.
     */
     ROTATE_BITS("Rotate n BITS", 170),
     
    /**
     * Define the corresponding database id for this knowledge component.
     */
     XOR_BITS("XOR Bits", 190),
     
    /**
     * Define the corresponding database id for this knowledge component.
     */
     ADD_BITS("Add Bits", 220),
     
    /**
     * Define the corresponding database id for this knowledge component.
     */
     MAJORITY_FUNCTION("Majority Function", 200),
     
    /**
     * Define the corresponding database id for this knowledge component.
     */
     SHIFT_BITS("Shift Bits", 180);
     
    /**
     * A GUI displayable string identifying this knowledge component.
     */
    private final String title;
    
    /**
     * The id of this knowledge component in the database.
     */
    private final int dbId;
    
    KnowledgeComponentKind(String title, int dbId) {
        this.title = title;
        this.dbId = dbId;
    }
     
    /**
     * Return the scaffold level
     * 
     * @return aString for display in the GUI
     */
    public String title() {
        return title;
    }
    
    /**
     * Return the database id of this knowledge component.
     * 
     * @return this component's database id.
     */
    public int dbId() {
        return dbId;
    }
    
    /**
     * Return the corresponding enum value for the given title
     * @param title
     * @return the matching enum value (If null is returned, you called
     *   this method with an unknown title, fix the call).
     */
    public static KnowledgeComponentKind fromString(String title) {
        for (KnowledgeComponentKind enumVal : KnowledgeComponentKind.values())
            if (enumVal.title().equalsIgnoreCase(title))
                return enumVal;
            
        return null;
    }
}
