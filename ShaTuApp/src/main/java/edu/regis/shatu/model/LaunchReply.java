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
 * A reply from the tutor (server) that includes a courtroom model upon 
 * success or otherwise an error condition.
 * 
 * @author rickb
 */
public class LaunchReply {
    /**
     * The status of this reply: SUCCESS or ERR
     */
    private String status;
    
    private String errType;
    
    private TutoringSession session;
    
    
    public LaunchReply() {
        status = ":ERR";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrType() {
        return errType;
    }

    public void setErrTYpe(String errType) {
        this.errType= errType;
    }

    public TutoringSession getSession() {
        return session;
    }

    public void setSession(TutoringSession session) {
        this.session = session;
    }
}

