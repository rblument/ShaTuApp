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
 * The legal requests that can be made to the server.
 * 
 * @author rickb
 */
public enum ServerRequestType {
    /**
     * The student has completed the current step.
     */
    COMPLETED_STEP(":CompletedStep"),
    
    /**
     * Completed an entire task containing one ore more steps.
     */
    COMPLETED_TASK(":CompletedTask"),
    
    /**
     * A new user is requesting to create a new account.
     * 
     * The ClientRequest data is a JSon Account object.
     * 
     * The TutorReply status will be:
     *   "Created" with no data
     *   "ERR" 
     */
    CREATE_ACCOUNT(":CreateAccount"),
    
    /**
     * An existing user's attempt to edit account information.
     */
    UPDATE_ACCOUNT(":UpdateAccount"),
    
    /**
     * The student or client requested another example.
     * 
     */
    NEW_EXAMPLE(":NewExample"),
    
    /**
     * An existing student is attempting to sign in. 
     * 
     * The ClientRequest data is a JSon User object.
     * 
     * The TutorReply status will be:
     *  "Authenticated",
     *    The TutorReply data is a JSon TutoringSession object
     *  "InvalidPassword"
     *  "UnknownUser"
     *  "AttemptsExceeded"
     *  "ERR"
     */
    SIGN_IN(":SignIn"),
    
    /**
     * Student request to sign out.
     */
    SIGN_OUT(":SignOut"),
    
    /**
     * The student is requesting a hint for the current step.
     * 
     * 
     * The TutorReply status will be:
     * "Hint"
     *   The TutorReply data is a JSon Hint object
     * "NoHints"
     * "NoneLeft"
     * "ERR" data is error message
     */
    REQUEST_HINT(":RequestHint"),
    
    /**
     * The student has reset their password.
     */
    RESET_PASSWORD(":ResetPassword"),
    
    /**
     * A user's request to have their password verified.
     */
    VERIFY_PASSWORD(":VerifyPassword"),
    
    /**
     * The student has answered their security question.
     */
    VERIFY_USER(":VerifyUser"),
    
    /**
     * Get the current task of the user.
     */
    GET_TASK(":GetTask");

    /**
     * The name used by the server to identify this request.
     */
    private final String requestName;
    
    /**
     * Initialize this enum object with the given title.
     * 
     * @param requestName 
     */
    ServerRequestType(String requestName) {
        this.requestName = requestName;
    }
    
    /**
     * Return the request name that is used by the server.
     * 
     * @return a String 
     */
    public String getRequestName() {
        return requestName;
    }
    
    /**
     * Return the request name that is used by the server
     * 
     * @return a String
     */
    @Override
    public String toString() {
        return requestName;
    }
}