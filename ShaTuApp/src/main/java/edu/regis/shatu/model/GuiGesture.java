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
 * The gestures (actions) that can be performed by a student in the GUI. 
 * 
 * @author rickb
 */
public enum GuiGesture {
    /**
     * A gesture in which the student requests a hint in the GUI.
     */
    REQUEST_HINT("Request Hint"),
    
    /**
     * Represent an error state read when creating a course.
     */
<<<<<<< HEAD
    NO_OP("No Op");
=======
    NO_OP("No Op"),
    
    ERROR("Error");
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    
    /**
     * A pretty print name for this GUI action.
     */
    private final String name;
    
    /**
     * Initialize this gesture with the given pretty print name
     * 
     * @param name a String that can be displayed to the user.
     */
    GuiGesture(String name) {
        this. name = name;
    }
    
    /**
     * Return the name of this gesture.
     * 
     * @return a pretty print String
     */
    public String getName() {
        return name;
    }
    
    /**
     * Return the name that is used by the server
     * 
     * @return a String
     */
    @Override
    public String toString() {
        return name;
    }
<<<<<<< HEAD
=======
    
     /**
     * Return the enum value for the given title.
     * 
     * @param aTitle
     * @return 
     */
    public static GuiGesture findValue(String aTitle) {
        for (GuiGesture kind : values()) {
            if (kind.getName().equalsIgnoreCase(aTitle))
                return kind;
        }
        
        return ERROR;
    }
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
}
