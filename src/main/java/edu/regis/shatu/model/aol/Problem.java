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

import java.util.ArrayList;

import edu.regis.shatu.model.Task;
import edu.regis.shatu.model.TitledModel;

/**
 * The primary task that a student is attempting to solve, as part of a unit 
 * within a course. 
 * 
 * In VanLehn's sense, this is a task for the student to complete, but we treat
 * tasks at a finer granularity i.e. as subproblems within the primary problem.
 * The tasks within a problem are derived from the specific nature of the
 * problem.
 * 
 * @author rickb
 */
public abstract class Problem extends TitledModel {
    // Inherited: id, title, description
    private String messageToHash;
    
    // ToDo:
    //public abstract ArrayList<Task> getTasks();
    
    public Problem() {
        super();
    }
    
    public Problem(int id) {
        super(id);
    }
    
    /**
     * Retrieves the message that will be hashed for the problem.
     * 
     * @return the message to be hashed
     */
    public String getMessageToHash(){
        return messageToHash;
    }
    
    /**
     * Sets the message that will be hashed in the problem.
     * 
     * @param messageToHash a String representing a message
     */
    public void setMessageToHash(String messageToHash){
        this.messageToHash = messageToHash;
    }

    @Override
    public String toString() {
        return "Title: " + getTitle() + 
                " Description: " + getDescription() + 
                " Message: " + this.messageToHash;
    }


    /**
     * Returns the list of tasks that make up this problem.
     * Each concrete problem type defines its own set of tasks.
     *
     * @return an ArrayList of Task objects
     */
    public abstract ArrayList<Task> getTasks();

}