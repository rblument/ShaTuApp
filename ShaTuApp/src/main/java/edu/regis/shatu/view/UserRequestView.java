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
package edu.regis.shatu.view;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.regis.shatu.model.LessonSession;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Task;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.aol.PendingTask;
import java.util.Random;

/**
 * A abstract view that supports various user gestures that results in a request 
 * being made to the tutor.
 * 
 * The implementation of the abstract methods in this class allows the various
 * Java actions, such NewExampleAction, to obtain the data to be used in 
 * constructing the request being sent to the tutor.
 * 
 * @author Oskar Thiede
 */
public abstract class UserRequestView extends GPanel {
    /**
     * The current task and step in this tutoring session are displayed in this 
     * view.
     */
    protected TutoringSession model;
    
    /**
     * The current task and step in this tutoring session are displayed in this 
     * view.
     */
    protected LessonSession lessonModel;
   
    /**
     * Convenience utility for converting between Java and JSon objects.
     */
    protected Gson gson;
    
    /**
     * Convenience utility for generating pseudo-random numbers.
     */
    protected Random random;

    /**
     * Create and return a new example request associated with the tutoring
     * topic presented to the student in this view. 
     * 
     * This new example request can be sent to the tutor, which will reply
     * with a task containing the new example problem to be presented to the
     * student.
     * 
     * @return NewExampleRequest
     */
    public abstract NewExampleRequest newRequest();

    /**
     * Create and return a new step completion request, which indicates the
     * student is asking the tutor to check their work.
     * 
     * @return StepCompletion
     */
    public abstract StepCompletion stepCompletion();

    public TutoringSession getModel() {
        return this.model;
    }
    
    public void setModel(TutoringSession model) {
        gson = new GsonBuilder().setPrettyPrinting().create();
        random = new Random();
        this.model = model;
        updateView();
    }
    
    public LessonSession getLessonModel() {
        return this.lessonModel;
    }
    
    public void setLessonModel(LessonSession model) {
        gson = new GsonBuilder().setPrettyPrinting().create();
        random = new Random();
        this.lessonModel = model;
        updateView();
    }
    
    /**
     * Assign the given task as the current task in our tutoring session model
     * and display this task and associated step(s) in the view.
     * 
     * @param task 
     */
    public void setCurrentTask(PendingTask task) {
       model.addCurrentTask(task);
       updateView();
    }
    
    /**
     * Display the current model in this view.
     */
    protected void updateView() {
        throw new UnsupportedOperationException("Not supported yet. Override this is subclass view"); 
    }
}
