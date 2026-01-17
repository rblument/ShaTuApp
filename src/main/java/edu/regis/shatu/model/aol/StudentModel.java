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

import java.util.HashMap;

/**
 * Captures the current assessment for each learning outcome in a course, as 
 * well as, all tutoring sessions in which the student participated.
 * 
 * @author rickb
 */
public class StudentModel {
    /**
     * Convenience reference to the user id (email) of the student associated
     * with this student model.
     */
    private String userId;
    
    /**
     * The assessments of outcomes for the student associated with this model.
     * The key is the id of the knowledge component in the associated assessment.
     */
    private HashMap<Integer, Assessment> assessments;
    
    /**
     * The current scaffolding being used to support the student.
     */
    private ScaffoldLevel scaffoldLevel;
    
    /**
     * The current tutoring mode, which is derived from the scaffold level.
     */
    private TutoringMode tutoringMode;
    
    /**
     * Create a student model for the given user id and with default information.
     * 
     * @param userId the user id of the student whose model is being created.
     */
    public StudentModel(String userId) {
        this.userId = userId;
        
        scaffoldLevel = ScaffoldLevel.EXTREME;
        tutoringMode = TutoringMode.SEE_ONE;
        
        assessments = new HashMap<>();   
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public void addAssessment(int knowledgeComponentId, Assessment assessment) {
        assessments.put(knowledgeComponentId, assessment);
    }
    
    public void addAssessment(Assessment assessment) {
        addAssessment(assessment.getOutcome().getId(), assessment);
    }

    /**
     * Return whether this student has an assessment for the given outcome.
     * 
     * @param outcome
     * @return true if the student has an assessment for the given outcome.
     */
    public boolean containsAssessment(int knowledgeComponentId) {
        return assessments.containsKey(knowledgeComponentId);
    }
    
    /**
     * Return the student assessment, if any, for the given outcome.
     * 
     * @param outcome the Outcome being accessed
     * 
     * @return an Assessment of the student.
     */
    public Assessment findAssessment(int knowledgeComponentId) {
        return assessments.get(knowledgeComponentId);
    }
    
    public HashMap<Integer, Assessment> getAssessments() {
        return assessments;
    }
    
    /**
     * Return the current scaffolding level being used to support the student.
     * 
     * @return 
     */
    public ScaffoldLevel getScaffoldLevel() {
        return scaffoldLevel;
    }

    public void setScaffoldLevel(ScaffoldLevel scaffoldLevel) {
        this.scaffoldLevel = scaffoldLevel;
    }

    public TutoringMode getTutoringMode() {
        return tutoringMode;
    }

    public void setTutoringMode(TutoringMode tutoringMode) {
        this.tutoringMode = tutoringMode;
    }

    /**
     * Check if the student has completed all steps of a given problem type.
     */
    public boolean hasCompleted(ProblemType type) {
        for (Assessment a : assessments.values()) {
            if (a.getOutcome().getProblemType() == type) {
                return a.getAssessment() == AssessmentLevel.COMPLETED;
            }
        }
        return false; // If no assessment exists for this type, treat as not completed
    }

    /**
     * Return problem type student is weakest .
     */
    public ProblemType getWeakestProblemType() {
        ProblemType weakest = null;
        AssessmentLevel minLevel = AssessmentLevel.COMPLETED;

        for (Assessment a : assessments.values()) {
            if (a.getAssessment().ordinal() < minLevel.ordinal()) {
                minLevel = a.getAssessment();
                weakest = a.getOutcome().getProblemType();
            }
        }
        return weakest != null ? weakest : ProblemType.ASCII_ENCODE;
    }

}