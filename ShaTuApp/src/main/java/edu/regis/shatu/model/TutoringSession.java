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

<<<<<<< HEAD
=======
import edu.regis.shatu.model.aol.PendingTask;

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
/**
 * A SHA tutoring session, which is displayed in the tutor.
 * 
 * @author rickb
 */
<<<<<<< HEAD
public class TutoringSession {   
=======
public class TutoringSession { 
    /**
     * The id of this session in the database.
     */
    private int id;
    
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * An SHA-256 encrypted security token that must be communicated to the
     * tutor/server in all subsequent requests after signing in.
     */
    private String securityToken = "";
    
    /**
     * The student being tutored in this session.
     */
<<<<<<< HEAD
    private Account account;
=======
    private Student student;
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    
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
     * The current task list.
     * 
     * If there are multiple tasks, the first one is the current task and the
     * remaining tasks are pending. Multiple tasks occur when a student 
     * overrides the task proposed by the tutor.
     */
<<<<<<< HEAD
    private ArrayList<Task> tasks;

    /**
     * Initialize this session with default information.
     */
    public TutoringSession() {
        tasks = new ArrayList<>();
    }

=======
    private ArrayList<PendingTask> tasks;

    /**
     * Initialize this session with default information.
     * 
     * @param student the Student being tutored in this session.
     */
    public TutoringSession(Student student) {
        this.student = student;
        tasks = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

<<<<<<< HEAD
    /**
     * Return the student being tutored in this tutoring session.
     * 
     * @return a Student
     */
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
=======
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
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
    
<<<<<<< HEAD
    public boolean isIsActive() {
=======
    public boolean isActive() {
        return isActive;
    }
    
    public boolean getIsActive(){
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public GregorianCalendar getStartDate() {
        return startDate;
    }

    public void setStartDate(GregorianCalendar startDate) {
        this.startDate = startDate;
    }
    
<<<<<<< HEAD
    public Task currentTask() {
        return tasks.get(0);
    }
    
    public void addTask(Task task) {
        tasks.add(task);
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }
    
    public void removeTask(Task task) {
=======
    public PendingTask currentTask() {
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
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        tasks.remove(task);
    }
    
    public void removeTask(int taskId) {
<<<<<<< HEAD
        for (Task task : tasks) 
            if (task.getId() == taskId)
                removeTask(task);
=======
        for (PendingTask pendingTask : tasks) 
            if (pendingTask.getTask().getId() == taskId)
                removeTask(pendingTask);
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }
}
