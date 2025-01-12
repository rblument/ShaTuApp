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
import edu.regis.shatu.model.Course;
import edu.regis.shatu.model.CourseDigest;
import edu.regis.shatu.model.Task;
import java.sql.Connection;

/**
 * Specifies the API for Course life-cycle maintenance (database persistence).
 * 
 * @author rickb
 */
public interface CourseSvc {
    /**
     * Locate and return the course with the given id.
     * 
     * The method is similar to {@link #retrieveDigest()} except it returns
     * the entire course content.
     *
     * @param courseId  integer key of the course to load.
     * @return The Course with the given id.
     * @exception ObjNotFoundException No course with the given id exists.
     * @throws NonRecoverableException also see getCause().getErrorCode()..
     */
    Course retrieve(int courseId) throws ObjNotFoundException, NonRecoverableException;
    
    /**
     * Locate and return a digest of the course with the given id.
     * 
     * Use {@link #retrieve()} except it returns to return the entire course.
     *
     * @param courseId  integer key of the course to load.
     * @return The CourseDigest with the given id.
     * @exception ObjNotFoundException No course with the given id exists.
     * @throws NonRecoverableException also see getCause().getErrorCode().
     */
    CourseDigest retrieveDigest(int courseId) throws ObjNotFoundException, NonRecoverableException;
    
    /**
     * Retrieve the identified task from the database.
     * 
     * @param courseId the id of the course owning the task
     * @param taskId the db id of the task
     * @param conn an open connection to the database, which isn't closed.
     * @return the Task identified by the course and task id
     * @throws ObjNotFoundException No task with the given id exists in the DB.NonRecoverableException=
     * @throws NonRecoverableException also see getCause().getErrorCode().
     */
    Task retrieveTask(int courseId, int taskId, Connection conn) throws ObjNotFoundException, NonRecoverableException;

}
