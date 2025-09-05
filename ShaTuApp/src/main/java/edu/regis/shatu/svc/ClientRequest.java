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
 * A decorator that wraps a user interface request being sent to the tutor
 * (sever), which includes the type of request being mad.
 * 
 * The request type specifies how to interpret the JSon encoded data.
 * 
 * @author rickb
 */
public class ClientRequest {    
    /**
     * The specific type of request being made by the client.
     */
    private ServerRequestType requestType;
    
    /**
     * The id of the user that made this request.
     */
    private String userId;
    
    /**
<<<<<<< HEAD
     * The sing-in session id associated with the user making this request.
     */
    private String sessionId;
=======
     * The sing-in security token associated with the user making this request.
     */
    private String securityToken;
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    
    /**
     * A JSon encoded object whose format depends on the associated request.
     * (See the requestType documentation.)
     */
    private String data;
    
    /**
     * Initialize an empty client request.
     * 
     * @param requestType the type of request encoded in client request.
     */
    public ClientRequest(ServerRequestType requestType) {
        this.requestType = requestType;
        
        userId = "";
<<<<<<< HEAD
        sessionId = "";
=======
        securityToken = "";
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        data = "";
    }

    public ServerRequestType getRequestType() {
        return requestType;
    }

    public void setRequest(ServerRequestType requestType) {
        this.requestType = requestType;
    }
    
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

<<<<<<< HEAD
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
=======
    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String sessionId) {
        this.securityToken = sessionId;
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }

    /**
     * Return the data associated with this request.
     * 
     * @return a JSon encoded object corresponding to the request type.
     *         See the documentation for the data field.
     */
    public String getData() {
        return data;
    }

    /**
     * Set the data.
     * 
     * @param data a JSon encoded object corresponding to the request type.
     *             See the documentation for the data field.
     */
    public void setData(String data) {
        this.data = data;
    }
}