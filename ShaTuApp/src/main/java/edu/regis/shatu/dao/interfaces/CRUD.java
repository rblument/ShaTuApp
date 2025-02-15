package edu.regis.shatu.dao.interfaces;


import edu.regis.shatu.err.IllegalArgException;
import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;

public interface CRUD<T> {
    /**
     * Create an object
     * @param obj
     */
    /* Really this should be more like a InvalidObjectException or maybe a DuplicateException depending on where it is used. */
    void create(T obj) throws IllegalArgException, NonRecoverableException;

    /**
     * Retrieve an object based on another object acting as the key
     * @param obj
     * @return Object
     */
    T retrive(T obj) throws ObjNotFoundException;

    /**
     * Update an object
     * @param obj
     */
    void update(T obj) throws ObjNotFoundException, NonRecoverableException;

    /**
     * Delete an object
     * @param obj
     */
    void delete(T obj) throws ObjNotFoundException, NonRecoverableException;

}
