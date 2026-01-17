/*
 *  SHATU: SHA-256 Tutor
 * 
 *   (C) Johanna & Richard Blumenthal, All rights reserved
 * 
 *   Unauthorized use, duplication or distribution without the authors'
 *   permission is strictly prohibited.
 * 
 *   Unless required by applicable law or agreed to in writing, this
 *   software is distributed on an "AS IS" basis without warranties
 *   or conditions of any kind, either expressed or implied.
 */
package edu.regis.shatu.svc;

import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;
import edu.regis.shatu.model.Task;
import edu.regis.shatu.model.aol.Problem;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * The API for Problem life-cycle maintenance (database persistence).
 * 
 * @author rickb
 */
public interface ProblemSvc {
    /**
     * Locate and return the Problem with the given id.
     *
     * @param problemId integer key of the Problem to load.
     * @return The Problem with the given id.
     * @exception ObjNotFoundException No Problem with the given id exists.
     * @throws NonRecoverableException also see getCause().getErrorCode()..
     */
    Problem retrieve(int problemId) throws ObjNotFoundException, NonRecoverableException;
    
      /**
     * Return the problem with the given id.
     * 
     * @param id the id of the Problem to return
     * @param conn an open connection to the DB, which is not closed.
     * @return a Problem
     * @throws ObjNotFoundException no Problem with the given id exists.
     * @throws NonRecoverableException perhaps see getCause().getErrorCode().
     */
    Problem retrieve(int id, Connection conn) throws ObjNotFoundException, NonRecoverableException;
    
        /**
     * Retrieve one or more Problems for the Unit with the given unit id.
     * 
     * @param unitId the id of the Unit whose associated Problems are retrieved
     * @return a List of Problems in the given Unit
     * @throws NonRecoverableException perhaps see getCause().getErrorCode().
     */
    ArrayList<Problem> retrieveByUnitId(int unitId) throws NonRecoverableException;
    
        /**
     * Retrieve the identified task from the database.
     * 
     * @param problemId the id of the Problem owning the task
     * @param taskId the db id of the task
     * @param conn an open connection to the database, which isn't closed.
     * @return the Task identified by the course and task id
     * @throws ObjNotFoundException No task with the given id exists in the DB.NonRecoverableException=
     * @throws NonRecoverableException also see getCause().getErrorCode().
     */
   // Task retrieveTask(int problemId, int taskId, Connection conn) 
    //        throws ObjNotFoundException, NonRecoverableException;
}
