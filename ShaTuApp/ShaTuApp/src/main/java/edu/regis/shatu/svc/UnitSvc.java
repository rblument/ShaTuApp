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
import edu.regis.shatu.model.Unit;

/**
 * Specifies the API for Unit life-cycle maintenance (database persistence).
 * 
 * @author rickb
 */
public interface UnitSvc {
    /**
     * Locate and return the Unit with the given id.
     *
     * @param id  integer key of the Unit to load.
     * @return The Unit with the given id.
     * @throws ObjNotFoundException No Unit with the given id exists.
     * @throws NonRecoverableException see documentation for this exception.
     */
    Unit findById(int id) throws ObjNotFoundException, NonRecoverableException;
}

