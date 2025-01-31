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

import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.StudentModelFieldKind;
import edu.regis.shatu.model.aol.Assessment;
import edu.regis.shatu.model.aol.StudentModel;

/**
 * Specifies the API for {@link StudentModel} life-cycle maintenance 
 * (CRUD persistence).
 * 
 * @author rickb
 */
public interface StudentModelSvc {
    /**
     * Insert the given StudentModel into the DB.
     *
     * @param student whose model is being created.
     * @throws NonRecoverableException also see getCause().getErrorCode().
     */
    public void create(Student student) throws NonRecoverableException;
    
    /**
     * Return the student model with the given id.
     * 
     * @param userId the id of the student whose student model is returned
     * @return a student model with for the given user id
     * @throws ObjNotFoundException no student model with the given user id exists
     * @throws NonRecoverableException also see getCause().getErrorCode().
     */
    StudentModel retrieve(String userId) throws ObjNotFoundException, NonRecoverableException;
    
    /**
     * Update the given student model.
     * 
     * @param model a student model containing updated data
     * @param field the field(s) in the student model that should be updated
     *              in the database.
     * @throws ObjNotFoundException user doesn't exists in the database
     * @throws NonRecoverableException also see getCause().getErrorCode().
     */
    void update(StudentModel model) 
            throws ObjNotFoundException, NonRecoverableException;
    
    /**
     * Update the field of the assessment in the given student model.
     * 
     * @param model the student model to update.
     * @param assessment the assessment to update.
     * @param field the field to update, which might be ALL.
     * @throws NonRecoverableException 
     */
    void updateAssessment(StudentModel model, Assessment assessment, StudentModelFieldKind field)
            throws NonRecoverableException;
    
    /**
     * Delete the session from the database for the given student user id.
     * 
     * @param userId the student's user id (email: user@university.edu)
     * @throws NonRecoverableException 
     */
    void delete(String userId) throws NonRecoverableException;
    
    /**
     * Check if the StudentUser with the given user id exists in the database.
     * (less expensive then using findById).
     *
     * @param userId unique id of the StudentUser to find.
     * @return true, if the StudentUser exists in the DB, false otherwise
     * @throws NonRecoverableException (see getCause().getErrorCode()).
     */
    boolean exists(String userId) throws NonRecoverableException;
}



