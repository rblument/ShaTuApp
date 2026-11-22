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

import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.aol.TutoringMode;

/**
 * A record that a particular student correctly completed a Step of a Task
 * within a Unit (See One, Do One, or Teach One).
 *
 * Unlike {@link StepCompletion}, which is the message the GUI sends to the
 * tutor when a student submits an answer, a CompletedStep is the persisted
 * (per-student) fact stored in the CompletedStep table.
 */
public class CompletedStep {
    private int id;
    private String userId;
    private int unitId;
    private int taskId;
    private int stepId;
    private StepSubType subType;
    private TutoringMode tutoringMode;
    private int hintsGiven;
    private boolean timeoutOccurred;

    /** Time the step was completed (milliseconds since the epoch). */
    private long completionDate;

    public CompletedStep() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /** The Unit (Unit table) in which the step was completed. */
    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    /** The Task (Task table) the completed step belongs to. */
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    /** The Step (Step table) that was completed. */
    public int getStepId() {
        return stepId;
    }

    public void setStepId(int stepId) {
        this.stepId = stepId;
    }

    public StepSubType getSubType() {
        return subType;
    }

    public void setSubType(StepSubType subType) {
        this.subType = subType;
    }

    /** The tutoring mode the student was in when the step was completed. */
    public TutoringMode getTutoringMode() {
        return tutoringMode;
    }

    public void setTutoringMode(TutoringMode tutoringMode) {
        this.tutoringMode = tutoringMode;
    }

    public int getHintsGiven() {
        return hintsGiven;
    }

    public void setHintsGiven(int hintsGiven) {
        this.hintsGiven = hintsGiven;
    }

    public boolean isTimeoutOccurred() {
        return timeoutOccurred;
    }

    public void setTimeoutOccurred(boolean timeoutOccurred) {
        this.timeoutOccurred = timeoutOccurred;
    }

    public long getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(long completionDate) {
        this.completionDate = completionDate;
    }
}
