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

<<<<<<< HEAD
import edu.regis.shatu.model.Model;

=======
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
/**
 * A suggestion as to the next Step the Student should perform in the tutor.
 * 
 * @author rickb
 */
public class Hint extends Model {
    // Todo: level? Scaffolding??
    
    /**
     * The hint string, which can be displayed to the student user.
     */
    private String text = "";
    
    /**
     * The order in which this hint should be displayed (when multiple hints
     * are available).
     */
<<<<<<< HEAD
    private int sequenceId = 1;
=======
    private int sequenceIndex = 1;
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3

    /**
     * Initialize this hint with a default id.
     */
    public Hint() {
        this(DEFAULT_ID);
    }
    
    /**
     * Initialize this hint with the given database id.
     * 
     * @param id int database is of this hint.
     */
    public Hint(int id) {
        super(id);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }   

<<<<<<< HEAD
    public int getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
=======
    public int getSequenceIndex() {
        return sequenceIndex;
    }

    public void setSequenceIndex(int sequenceIindex) {
        this.sequenceIndex = sequenceIndex;
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }
}
