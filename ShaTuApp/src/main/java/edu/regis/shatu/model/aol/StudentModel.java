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

<<<<<<< HEAD
import edu.regis.shatu.model.Model;
=======
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
import java.util.HashMap;

/**
 * Captures the current assessment for each learning outcome in a course, as 
 * well as, all tutoring sessions in which the student participated.
 * 
 * @author rickb
 */
<<<<<<< HEAD
public class StudentModel extends Model {
=======
public class StudentModel {
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
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
    private ScaffoldLevel scaffoldLevel = ScaffoldLevel.EXTREME;
    
    /**
<<<<<<< HEAD
     * Instantiate this student model with default information.
     */
    public StudentModel() {
        super(DEFAULT_ID);
        
        assessments = new HashMap<>();
=======
     * Create a student model for the given user id and with default information.
     * 
     * @param userId the user id of the student whose model is being created.
     */
    public StudentModel(String userId) {
        this.userId = userId;
        
        assessments = new HashMap<>();   
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public void addAssessment(int knowledgeComponentId, Assessment assessment) {
<<<<<<< HEAD
        assessments.put(knowledgeComponentId, assessment);
    }
=======
        System.out.println("***** StuMod.addAssess: id: " + knowledgeComponentId);
        assessments.put(knowledgeComponentId, assessment);
    }
    
    public void addAssessment(Assessment assessment) {
        addAssessment(assessment.getOutcome().getId(), assessment);
    }
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3

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
    
<<<<<<< HEAD
=======
    public HashMap<Integer, Assessment> getAssessments() {
        return assessments;
    }
    
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
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
}