/*
 * SHATU: SHA-256 Tutor
 * 
 *  (C) Johanna & Richard Blumenthal, All rights reserved
 * 
 *  Unauthorized use, duplication or distribution without the authors'
 *  permission is strictly prohibted.
 * 
 *  Unless required by applicable law or agreed to in writing, this
 *  software is distributed on an "AS IS" basis without warranties
 *  or conditions of any kind, either expressed or implied.
 */
package edu.regis.shatu.model;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.model.aol.TutoringMode;
import edu.regis.shatu.model.aol.Problem;

/**
 * A SHA tutoring session, which is displayed in the tutor.
 * 
 * @author rickb
 */
public class TutoringSession { 
    /**
     * The id of this session in the database.
     */
    private int id;
    
    /**
     * The current tutoring mode for this session.
     */
    private TutoringMode tutoringMode;
    
    /**
     * An SHA-256 encrypted security token that must be communicated to the
     * tutor/server in all subsequent requests after signing in.
     */
    private String securityToken = "";
    
    /**
     * The student being tutored in this session.
     */
    private Student student;
    
    /**
     * A summary of the course currently being taught in this session.
     */
    private CourseDigest course;
    
    /**
     * A summary of the unit currently being taught in this session.
     */
    private UnitDigest unit;
    
     /**
     * True, if the session is currently active (though the student may not
     * be currently signed-in).
     */
    private boolean isActive = true;
    
    /**
     * The date and time when this session was initially created.
     */
    private GregorianCalendar startDate;
    
    /**
     * The overall problem being solved in this session.
     */
    private Problem problem;
 
     /**
     * The current task list.
     * 
     * If there are multiple tasks, the first one is the current task and the
     * remaining tasks are pending. Multiple tasks occur when a student 
     * overrides the task proposed by the tutor.
     */
    private ArrayList<PendingTask> tasks;

    /**
     * Initialize this session with default information.
     * 
     * @param student the Student being tutored in this session.
     */
    public TutoringSession(Student student) {
        this.student = student;
        tutoringMode = TutoringMode.SEE_ONE;
        tasks = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public CourseDigest getCourse() {
        return course;
    }

    public void setCourse(CourseDigest course) {
        this.course = course;
    }

    public UnitDigest getUnit() {
        return unit;
    }

    public void setUnit(UnitDigest unit) {
        this.unit = unit;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public boolean getIsActive(){
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    
    public TutoringMode getTutoringMode() {
    return tutoringMode;
    }

    public void setTutoringMode(TutoringMode tutoringMode) {
        this.tutoringMode = tutoringMode;
    }
    
    
    public GregorianCalendar getStartDate() {
        return startDate;
    }

    public void setStartDate(GregorianCalendar startDate) {
        this.startDate = startDate;
    }
    
    public Problem getProblem(){
        return problem;
    }
    
    public void setProblem(Problem problem){
        this.problem = problem;
    }
    
    public PendingTask currentTask() {
        //return null rather than crash if empty
        if (tasks == null || tasks.isEmpty()) {
            return null;
        }
        return tasks.get(0);
    }
    
    public void addTask(PendingTask task) {
        tasks.add(task);
    }
    
    public void addCurrentTask(PendingTask task) {
        tasks.add(0, task);
    }

    public ArrayList<PendingTask> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<PendingTask> tasks) {
        this.tasks = tasks;
    }
    
    public void removeTask(PendingTask task) {
        tasks.remove(task);
    }
    //Updated method to remove task by taskId to prevent ConcurrentModificationException

    public void removeTask(int taskId) {
        tasks.removeIf(pendingTask -> pendingTask.getTask().getId() == taskId);
    }
}