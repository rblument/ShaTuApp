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
import edu.regis.shatu.model.aol.ExampleType;
import edu.regis.shatu.model.aol.Problem;
import edu.regis.shatu.model.aol.ScaffoldLevel;
import edu.regis.shatu.model.aol.TaskKind;
import edu.regis.shatu.model.aol.TaskState;
import java.util.ArrayList;
=======
import java.util.ArrayList;

import edu.regis.shatu.model.aol.Problem;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.TaskKind;
import edu.regis.shatu.model.aol.TaskState;
import edu.regis.shatu.model.steps.Step;
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3

/**
 * A multi-minute activity that can be skipped or interchanged with other tasks,
 * whose steps the student is expected to perform.
 * 
 * Tasks are based on tutoring behaviors in (VanLehn, 2006).
 * 
 * As a Task appears within actions/expectations created by an agent,
 * all fields are final so that other malicious agents cannot change them.
 * 
 * @author rickb
 */
public class Task extends TitledModel {
    /**
     * Indicates the type of task the student trying to complete.
     */
    private TaskKind kind = TaskKind.PROBLEM;
<<<<<<< HEAD
    
    /**
     * The type of this task, which can be used to determine the appropriate
     * view to display, if different from each of its steps.
     */
    private ExampleType type;
    
    /**
     * The scaffolding support for this task.
     */
    private ScaffoldLevel scaffolding = ScaffoldLevel.EXTREME;
    
    /**
     * Indicates the student's overall progress on this task.
     * (For IN_PROGRESS tasks, the student model has the current step.)
     */
    //private TaskState state = TaskState.PENDING;
    
    /**
     * The sequence in which this task is performed in its problem.
     */
    private int sequenceId;
    
=======

    /**
     * If the kind of this task is PROBLEM, then this is the type of problem
     * being presented in this task, which can be used to determin the view
     * to display.
     */
    private ProblemType type;

    /**
     * The sequence in which this task is performed in its problem.
     */
    private int sequenceIndex;

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * The current step (in index into steps).
     */
    private int currentStepIndex = 0;
<<<<<<< HEAD
    
=======

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * The steps that must be completed in this task.
     */
    private ArrayList<Step> steps;
<<<<<<< HEAD
    
=======

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * Convenience reference to the Problem to which this task belongs
     */
    private Problem problem;
<<<<<<< HEAD
    
=======

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * ToDo: the tasks already completed in this task???
     */
    private TaskState state;
<<<<<<< HEAD
    
=======

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * The knowledge component outcomes demonstrated/exercised by this step.
     */
    protected ArrayList<Integer> exercisedComponentIds;
<<<<<<< HEAD
 
    public Task() {
        this(Model.DEFAULT_ID);
    }
    
=======

    public Task() {
        this(Model.DEFAULT_ID);
    }

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * Instantiate this task with the given id.
     * 
     * @param id database id of this task.
     */
    public Task(int id) {
        super(id);
<<<<<<< HEAD
        
        this.steps = new ArrayList<>();
        
        exercisedComponentIds = new ArrayList<>();
        
        state = new TaskState();
    }
    
=======

        this.steps = new ArrayList<>();

        exercisedComponentIds = new ArrayList<>();

        state = new TaskState();
    }

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    public TaskKind getKind() {
        return kind;
    }

    public void setKind(TaskKind kind) {
        this.kind = kind;
    }

<<<<<<< HEAD
    public ExampleType getType() {
        return type;
    }

    public void setType(ExampleType type) {
        this.type = type;
    }

    public ScaffoldLevel getScaffolding() {
        return scaffolding;
    }

    public void setScaffolding(ScaffoldLevel scaffolding) {
        this.scaffolding = scaffolding;
    }
    
    
    
=======
    public ProblemType getType() {
        return type;
    }

    public void setType(ProblemType type) {
        this.type = type;
    }

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
<<<<<<< HEAD
    } 
=======
    }
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3

    public void addStep(Step step) {
        steps.add(step);
    }
<<<<<<< HEAD
    
    public ArrayList<Step> getSteps() {
        System.out.println("Task.getStepts: " + steps.size());
        return steps;
    }
    
    public void setSteps(ArrayList<Step> steps) {
        System.out.println("Task.setSepats: " + steps);
        this.steps = steps;
    }
    
    public Step getStep(int index) {
        return steps.get(index);
    }
    
    public Step lastStep() {
        return steps.get(steps.size() - 1);
    }
    
    public Step currentStep() {    
        System.out.println("*** Task.currentStep: " + steps.size());
        for (Step step : steps)
            if (step.getSequenceId() == currentStepIndex)
=======

    public ArrayList<Step> getSteps() {
        System.out.println("Task.getSteps: " + steps.size());
        return steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        System.out.println("Task.setSteps: " + steps);
        this.steps = steps;
    }

    public Step getStep(int index) {
        return steps.get(index);
    }

    public Step lastStep() {
        return steps.get(steps.size() - 1);
    }

    public Step currentStep() {
        System.out.println("*** Task.currentStep: " + steps.size());
        for (Step step : steps)
            if (step.getSequenceIndex() == currentStepIndex)
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
                return step;

        return null;
    }
<<<<<<< HEAD
    
    /**
     * Locally update the task state to note that the given step has been 
=======

    /**
     * 
     * @param stepId the database id of the step to find.
     * @return
     */
    public Step findStepById(int stepId) {
        for (Step step : steps)
            if (step.getId() == stepId)
                return step;

        return null;
    }

    /**
     * Locally update the task state to note that the given step has been
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
     * performed by the student (this doesn't notify the tutor).
     * 
     * @param completion the Step that was completed by the student.
     */
<<<<<<< HEAD
    public void completedStep(StepCompletion completion) { 
        completion.getStep().setIsCompleted(true);
        
        state.addStepCompletion(completion);
    }
    
    /**
     * Return whether this task is completed.
     * 
     * @return true if all of the steps in this task have been completed, 
     *         otherwise false
     */
    public boolean isTaskCompleted() {
        for (Step step : steps)
            if (!step.isCompleted())
                return false;
        
        return true;
    }
=======
    public void completedStep(StepCompletion completion) {
        // completion.getStep().setIsCompleted(true);

        state.addStepCompletion(completion);
    }
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
<<<<<<< HEAD
    }  

    public int getSequenceId() {
        return sequenceId;
    }
    
=======
    }

    public int getSequenceIndex() {
        return sequenceIndex;
    }

    public void setSequenceIndex(int sequenceIndex) {
        this.sequenceIndex = sequenceIndex;
    }

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    public Step getCurrentStep() {
        return steps.get(currentStepIndex);
    }

    public int getCurrentStepIndex() {
        return currentStepIndex;
    }

    public void setCurrentStepIndex(int currentStepIndex) {
        this.currentStepIndex = currentStepIndex;
    }
<<<<<<< HEAD
    
    public void addExercisedComponentId(int componentId) {
        exercisedComponentIds.add(componentId);
    }
    
=======

    public void addExercisedComponentId(int componentId) {
        exercisedComponentIds.add(componentId);
    }

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    public ArrayList<Integer> getExercisedComponentIds() {
        return exercisedComponentIds;
    }

    public void setExercisedComponentIds(ArrayList<Integer> componentIds) {
        this.exercisedComponentIds = componentIds;
    }
}
<<<<<<< HEAD

=======
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
