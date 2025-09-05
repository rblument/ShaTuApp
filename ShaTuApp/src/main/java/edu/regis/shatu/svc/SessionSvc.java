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

<<<<<<< HEAD
import edu.regis.shatu.err.IllegalArgException;
=======
import edu.regis.shatu.dao.interfaces.CRUD;
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;
import edu.regis.shatu.model.TutoringSession;

/**
 * Specifies the API for Session life-cycle maintenance (CRUD persistence).
 * 
 * @author rickb
 */
<<<<<<< HEAD
public interface SessionSvc {
    /**
     * Insert the given session into the database.
     * 
     * @param session the TutoringSession to create.
     * @throws IllegalArgException a session for the associated student account
     *                             already exists.
     * @throws NonRecoverableException perhaps see getCause().getErrorCode().
     */
    void create(TutoringSession session) throws IllegalArgException, NonRecoverableException;
    
    /**
     * Return the session with the specified id (this is a full session with all
     * events versus a digest).
     * 
     * @param userId the user id of the session to return (user@university.edu)
     * @return the Session for the given user id
     * @throws ObjNotFoundException no trial with the given id exists
     * @throws NonRecoverableException perhaps see getCause().getErrorCode().
     */
    TutoringSession retrieve(String userId) throws ObjNotFoundException, NonRecoverableException;
    
    /**
     * Update the session in formation in the database using the given session.
     * 
     * @param session a Session containing new information.
     * @throws ObjNotFoundException the session does not exist in the database.
     * @throws NonRecoverableException perhaps see getCause().getErrorCode().
     */
    void update(TutoringSession session) throws ObjNotFoundException, NonRecoverableException;
    
    /**
     * Delete the session from the database for the given student user id.
     * 
     * @param userId the student's user id (email: user@university.edu)
     * @throws NonRecoverableException 
     */
    void delete(String userId) throws NonRecoverableException;
}

=======
public interface SessionSvc extends CRUD<TutoringSession> {
    // /**
    // * Insert the given session into the database.
    // *
    // * @param session the TutoringSession to create.
    // * @throws ObjDuplicateException a session for the associated student account
    // * already exists.
    // * @throws NonRecoverableException perhaps see getCause().getErrorCode().
    // */
    // void create(TutoringSession session) throws ObjDuplicateException,
    // NonRecoverableException;

    // /**
    // * Return the session with the specified id (this is a full session with all
    // * events versus a digest).
    // *
    // * @param student the student whose session to return
    // * @return the Session for the given user id
    // * @throws ObjNotFoundException no trial with the given id exists
    // * @throws NonRecoverableException perhaps see getCause().getErrorCode().
    // */
    // TutoringSession retrieve(String student) throws ObjNotFoundException,
    // NonRecoverableException;

    /**
     * Return the security token (from the DB) for the given user id
     * 
     * @param userId
     * @return The SHA-256 encrypted security token string.
     * @throws ObjNotFoundException    No session with the given user id exists.
     * @throws NonRecoverableException perhaps see getCause().getErrorCode().
     */
    String retrieveSecurityToken(String userId) throws ObjNotFoundException, NonRecoverableException;

    // /**
    // * Update the session in formation in the database using the given session.
    // *
    // * @param session a Session containing new information.
    // * @throws ObjNotFoundException the session does not exist in the database.
    // * @throws NonRecoverableException perhaps see getCause().getErrorCode().
    // */
    // void update(TutoringSession session) throws ObjNotFoundException,
    // NonRecoverableException;

    // /**
    // * Delete the session from the database for the given student user id.
    // *
    // * @param userId the student's user id (email: user@university.edu)
    // * @throws NonRecoverableException
    // */
    // void delete(String userId) throws NonRecoverableException;
}
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
