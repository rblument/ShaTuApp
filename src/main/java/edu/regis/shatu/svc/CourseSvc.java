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

import java.sql.Connection;

import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;
import edu.regis.shatu.model.Course;
import edu.regis.shatu.model.CourseDigest;
import edu.regis.shatu.model.Task;
import edu.regis.shatu.model.UnitDigest;

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
     * @param conn an open DB connection , which isn't closed by this method
     * @return The CourseDigest with the given id.
     * @exception ObjNotFoundException No course with the given id exists.
     * @throws NonRecoverableException also see getCause().getErrorCode().
     */
    CourseDigest retrieveDigest(int courseId, Connection conn) 
            throws ObjNotFoundException, NonRecoverableException;
    
        /**
     * Locate and return a digest of the unit with the given course and unit ids.
     * 
     * Use {@link #retrieve()} except it returns to return the entire course.
     *
     * @param unitId  integer key of the course to load.
     * @param courseId integer key of the unit within this course to load.
     * @param conn an open DB connection , which isn't closed by this method
     * @return The UnitDigest with the given id.
     * @exception ObjNotFoundException No course with the given id exists.
     * @throws NonRecoverableException also see getCause().getErrorCode().
     */
    UnitDigest retrieveUnitDigest(int courseId, int unitId, Connection conn)
            throws ObjNotFoundException, NonRecoverableException;
}