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
 * Bloom's cognitive levels where each level includes the level before it.
 * 
 * ToDo: Work these out?
 *
 * @author rickb
 */
public enum BloomLevel {
    /**
     * The student is expected to know,
     */
     KNOWLEDGE("Knowledge"),
<<<<<<< HEAD

     /**
      *  
      */
     COMPREHENSION("Comprehension"),

=======
     COMPREHENSION("Comprehension"),
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
     /**
      *  Student is expected to apply the associated concept.
      */
     APPLICATION("Application"),
<<<<<<< HEAD

     /**
      * 
      */
     ANALYSIS("Analysis"),

     /**
      * 
      */
     SYNTHESIS("Synthesis"),

     /**
      * 
      */
     EVALUATION("Evaluation");
=======
     ANALYSIS("Analysis"),
     SYNTHESIS("Synthesis"),
     EVALUATION("Evaluation"),
     ERROR("Error");
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
     
    /**
     * A GUI displayable string identifying this taxonomy level.
     */
    private final String title;
    
    BloomLevel(String title) {
        this.title = title;
    }
     
    /**
     * Return the taxonomy level
     * 
     * @return aString
     */
    public String title() {
        return title;
    }
<<<<<<< HEAD
=======
    
    /**
     * Return the enum value for the given title.
     * 
     * @param aTitle
     * @return 
     */
    public static BloomLevel findValue(String aTitle) {
        for (BloomLevel kind : values()) {
            if (kind.title().equalsIgnoreCase(aTitle))
                return kind;
        }
        
        return ERROR;
    }
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
}