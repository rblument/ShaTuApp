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

/**
 *
 * @author Devin Morrison
 */
public class SHA_256BreakPoint {

    public enum SHA256Breakpoint {
        ENCODE_ASCII,
        PAD_WITH_ZEROS,
        INIT_HASH_VALUES,
        SCHEDULE_EXPANSION,
        COMPRESS_ROUND,
        FINAL_HASH
    }

    private final SHA256Breakpoint type;

    public SHA_256BreakPoint(SHA256Breakpoint type) {
        this.type = type;
    }

    public SHA256Breakpoint getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SHA_256BreakPoint)) return false;
        SHA_256BreakPoint other = (SHA_256BreakPoint) obj;
        return this.type == other.type;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
