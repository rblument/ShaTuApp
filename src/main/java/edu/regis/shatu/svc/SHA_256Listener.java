/*
 * SHATU: SHA-256 Tutor
 * 
 *  (C) Johanna & Richard Blumenthal, All rights reserved
 * 
 *  Unauthorized use, duplication or distribution without the authors'
 *  permission is strictly prohibted.
 * 
 *  Unless required by applicable law or agreed to in writing, this
 *  software is distributed on an "AS IS" basis without warranties
 *  or conditions of any kind, either expressed or implied.
 */
package edu.regis.shatu.svc;

/**
 * An observer that listens for SHA 256 algorithm messages.
 * 
 * @author rickb
 */
public interface SHA_256Listener {
    /**
     * Notify the listener of the ASCII encoding of the input message.
     * 
     * @param bytes  Each byte is one ASCII encoded character.
     */
    void notifyAsciiEncoding(byte[] bytes);
    
}