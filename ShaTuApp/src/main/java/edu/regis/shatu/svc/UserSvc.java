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

import edu.regis.shatu.err.IllegalArgException;
import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;
import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.User;

/**
 * Specifies the API for {@link User} life-cycle maintenance (CRUD persistence).
 * 
 * Note: this is only user and password information @see Student
 * 
 * @author rickb
 */
public interface UserSvc {
    /**
     * Insert the given User into the DB (cannot insert an admin).
     *
     * @param user a user containing the information to insert
     * @throws IllegalArgException an account with the given user id already exists
     * @throws NonRecoverableException also see getCause().getErrorCode().
     */
    void create(Account user) throws IllegalArgException, NonRecoverableException;
    
    /**
     * Return the user, if any, with the given user id.
     * 
     * @param userId the id of the user to return
     * @return a User with the given id
     * @throws ObjNotFoundException no user with the given id exists
     * @throws NonRecoverableException also see getCause().getErrorCode().
     */
    User retrieve(String userId) throws ObjNotFoundException, NonRecoverableException;
    
    /**
     * Return the user, if any, with the given user id.
     * 
     * @param userId the id of the user to return
     * @return a User with the given id
     * @throws ObjNotFoundException no user with the given id exists
     * @throws NonRecoverableException also see getCause().getErrorCode().
     */
    User retrieveQuestion(String userId) throws ObjNotFoundException, NonRecoverableException;
    
    /**
     * Return the user, if any, with the given user id.
     * 
     * @param userId the id of the user to return
     * @return a User with the given id
     * @throws ObjNotFoundException no user with the given id exists
     * @throws NonRecoverableException also see getCause().getErrorCode().
     */
    User retrieveAnswer(String userId) throws ObjNotFoundException, NonRecoverableException;
    
    /**
     * Update the given user's password (requires existing password).
     * 
     * @param user the userId and existing password
     * @param newPassword the new SHA-256 encrypted password
     * @throws ObjNotFoundException user doesn't exists in the database
     * @throws NonRecoverableException also see getCause().getErrorCode().
     */
    void update(User user, String newPassword) 
            throws ObjNotFoundException, NonRecoverableException;
    
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


