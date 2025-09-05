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
package edu.regis.shatu.model;

/**
 * Encapsulates the tutor's reply to a user sign-in attempt.
 * 
 * @author rickb
 */
public class SignInResult {
    /**
     * Server reply status:
     * "Authenticated" - session id indicates new (0) or existing (id) user 
     * "UnknownUser" - 
     * "InvalidPassword" - user exists
     * "ERR" - an unexpected non-recoverable error occurred
     */
    private String status;
    
    /**
     * The current session id, as a String, for a successfully authenticated 
     * sign-in attempt.
     */
    private String sessionId ;
    
    
    public SignInResult() {
        sessionId = "-1";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
