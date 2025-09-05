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
package edu.regis.shatu.model.steps;

import edu.regis.shatu.model.aol.StepSubType;

/**
 *
 * @author chand
 */
public class CompressRoundStep extends Step {
    public CompressRoundStep() {
        super(1, 0, StepSubType.COMPRESS_ROUND);
    }

}
