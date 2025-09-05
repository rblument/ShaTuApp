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
 * The level associated with an outcome
 * 
 * ToDo: TBD
 * 
 * @author rickb
 */
public enum OutcomeGranularity {
<<<<<<< HEAD
    COURSE,
    
    KNOWLEDGE_COMPONENT,
    
    UNIT;
=======
    COURSE("Course"),
    
    KNOWLEDGE_COMPONENT("Knowledge Component"),
    
    UNIT("Unit"),
    
    ERROR("Error");
    
    /**
     * A GUI displayable string identifying this granularity.
     */
    private final String title;
    
    OutcomeGranularity(String title) {
        this.title = title;
    }
     
    /**
     * Return the granularity
     * 
     * @return aString
     */
    public String title() {
        return title;
    }
    
    /**
     * Return the enum value for the given title.
     * 
     * @param aTitle
     * @return 
     */
    public static OutcomeGranularity findValue(String aTitle) {
        for (OutcomeGranularity kind : values()) {
            if (kind.title().equalsIgnoreCase(aTitle))
                return kind;
        }
        
        return ERROR;
    }
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
}