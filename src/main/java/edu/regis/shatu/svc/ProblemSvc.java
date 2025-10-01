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
import edu.regis.shatu.model.aol.Problem;

/**
 * Specifies the API for Problem life-cycle maintenance (database persistence).
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
    Problem retrieve(int problemId) throws ObjNotFoundException, NonRecoverableException ;
}
