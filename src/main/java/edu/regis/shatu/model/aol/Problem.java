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
public class Problem extends TitledModel {
    /**
     * The Tasks that a Student must complete to solve this Problem.
     */
    private ArrayList<Task> tasks;
    
    /**
     * The message that is being SHA-256 hashed in this problem.
     */
    private String messageToHash;
    
    /**
     * The id of the unit in which this Problem resides.
     */
    private int unitId;
    
    /**
     * The position of this problem in the unit's sequence of problems 
     */
    private int sequenceIndex;

    /**
     * Instantiate this problem with a DEFAULT_ID.
     */
    public Problem() {
        super(DEFAULT_ID);
    }
    
    /**
     * Instantiate this Problem with the given ID.
     * 
     * @param id the unique database id of this problem.
     */
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

    /**
     * Adds a new task to the task list the student must
     * complete to solve the problem
     * 
     * @param task a task object
     */
    public void addTask(Task task) {
        tasks.add(task);
    }
 
    /**
     * Returns the list of tasks that make up this problem.
     * Each concrete problem type defines its own set of tasks.
     *
     * @return an ArrayList of Task objects
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }
    
    /**
     * Replaces the current task list with the specified task list
     * 
     * @param tasks an ArrayList of task objects
     */
    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }
    
    /**
     * Finds if a specified taskID exists in the current task list
     * 
     * @param taskId the ID of the desired task to find
     * @return task if found, null otherwise
     */
     public Task findTaskById(int taskId) {
        for (Task task : tasks)
            if (task.getId() == taskId)
                return task;
        
        return null;
    }

    /**
     * Return the task, if any, with the given sequence id.
     * 
     * @param sequence the sequence position of the task to find.
     * @return the associated task object, or null if not found.
     */
    public Task findTaskBySequence(int sequence) {
        for (Task task : tasks)
            if (task.getSequenceIndex() == sequence)
                return task;
        
        return null;
    }
    
    /**
     * Returns the ID of the unit the current problem resides in
     * 
     * @return an integer that represents the active unit
     */
    public int getUnitId() {
        return unitId;
    }

    /**
     * Set the unit ID
     * @param unitId the integer ID that is to be set
     */
    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }
    
    /**
     *  Obtain the position of a problem in the sequence of problems
     * @return position of a problem
     */
    public int getSequenceIndex() {
        return sequenceIndex;
    }

    /**
     * Set the position of a problem
     * @param sequenceIndex  new integer index
     */
    public void setSequenceIndex(int sequenceIndex) {
        this.sequenceIndex = sequenceIndex;
    }
    
    @Override
    public String toString() {
        return "Title: " + getTitle() + 
                " Description: " + getDescription() + 
                " Message: " + this.messageToHash;
    }
}