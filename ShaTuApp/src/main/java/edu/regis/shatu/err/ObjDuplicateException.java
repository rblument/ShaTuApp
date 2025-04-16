package edu.regis.shatu.err;

/**
 * Thrown when a create method attempts to overwrite an existing object in the
 * database
 */
public class ObjDuplicateException extends ShaTuException {
    public ObjDuplicateException(String msg) {
        super(msg);
    }
}
