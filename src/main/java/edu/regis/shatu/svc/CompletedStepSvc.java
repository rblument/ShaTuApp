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
package edu.regis.shatu.svc;

import java.util.List;

import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.model.CompletedStep;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.TutoringMode;

/**
 * Specifies the API for persisting which Steps (of Tasks, in Units) each
 * student has completed.
 */
public interface CompletedStepSvc {
    /**
     * Persist that the given student completed the step in the given
     * completion while in the given tutoring mode.
     *
     * The Unit is the one whose sequence index matches the tutoring mode
     * (See One = 0, Do One = 1, Teach One = 2) within the given course. The
     * Task and Step are the ones stored in the Task and Step tables that match
     * the completed step. Ids that cannot be resolved are stored as NULL
     * rather than failing the tutoring request.
     *
     * @param userId     the student who completed the step
     * @param courseId   the course the student is taking
     * @param mode       the tutoring mode in effect when the step was completed
     * @param completion the completion submitted by the GUI
     * @return the persisted CompletedStep, including its generated id
     * @throws NonRecoverableException a database error occurred
     */
    CompletedStep record(String userId, int courseId, TutoringMode mode, StepCompletion completion)
            throws NonRecoverableException;

    /**
     * Return every step the student has completed, oldest first.
     *
     * @param userId the student's user id
     * @return the completed steps, possibly empty
     * @throws NonRecoverableException a database error occurred
     */
    List<CompletedStep> retrieve(String userId) throws NonRecoverableException;

    /**
     * Return the steps the student has completed within the given unit,
     * oldest first.
     *
     * @param userId the student's user id
     * @param unitId the Unit (See One, Do One, or Teach One) to restrict to
     * @return the completed steps, possibly empty
     * @throws NonRecoverableException a database error occurred
     */
    List<CompletedStep> retrieve(String userId, int unitId) throws NonRecoverableException;
}
