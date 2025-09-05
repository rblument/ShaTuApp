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
 * Information (userId and trialId) required to launch an existing session.
 * 
 * This class supports GSon conversion between JSon and Java.
 * 
 * @author rickb
 */
public class LaunchInfo {
    /**
     * The email id of the user launching the associated session.
     */
    private String userId;
    
    /**
     * The id of the session being launched by the associated user.
     */
    private String sessionId;
    
    /**
     * Initialize this instance with default information.
     * 
     * @param userId String (e.g. user@institution.edu)
     * @param sessionId a String representing an session id int
     */
    public LaunchInfo(String userId, String sessionId) {
        this.userId = userId;
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}

