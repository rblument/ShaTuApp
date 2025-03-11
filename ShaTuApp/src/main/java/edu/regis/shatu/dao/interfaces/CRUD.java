package edu.regis.shatu.dao.interfaces;

import java.util.HashMap;

import edu.regis.shatu.err.IllegalArgException;
import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;

public interface CRUD<T> {

    /*
     * Really this should be more like a InvalidObjectException or maybe a
     * DuplicateException depending on where it is used.
     */
    void create(T obj) throws NonRecoverableException;

    /**
     * Retrieve an object based on another object acting as the key
     * 
     * @param obj
     * @return Object
     */
    T retrieve(String key) throws ObjNotFoundException, NonRecoverableException;

    /**
     * Update an object
     * 
     * @param obj
     */
    void update(T obj) throws ObjNotFoundException, NonRecoverableException;

    /**
     * Delete an object
     * 
     * @param obj
     */
    void delete(String col, Object obj) throws NonRecoverableException;

    /**
     * Check a DB entry exists
     * 
     * @param obj
     * @return boolean
     * @throws NonRecoverableException
     */
    boolean exists(String col, Object obj) throws NonRecoverableException;

}
