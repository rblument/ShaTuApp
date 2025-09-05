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
package edu.regis.shatu.model.aol;

/**
 * An example representing a ASCII encoding, which may be used in a request
 * from the client or as a reply from the tutor server. 
 * 
 * In a request, only the length of the string field is supplied.
 * In a reply, all fields will have values.
 * 
 * @author rickb
 */
public class EncodeAsciiExample {
    /**
     * The number of characters in the example string.
     * 
     * In a request from the client, if the length is zero, the tutor will
     * generate the string length based on the student model. Otherwise, the
     * student is asking to practice a specific length string. 
     */
    private int stringLength;
    
    /**
     * The example string that is encoded in this example.
     */
    private String exampleString;
    
    /**
     * The ASCII encoding of the example string as int values.
     */
    private int[] asciiEncoding;
    
    /**
     * If non-zero, the number of seconds the student has to complete the
     * example. 
     */
    private int timeOut;
    
    public EncodeAsciiExample(String example) {
    this.exampleString = example;  // Initialize with the provided string
    asciiEncoding = new int[example.length()];  // Initialize based on string length
}

    public int getStringLength() {
        return stringLength;
    }

    public void setStringLength(int stringLength) {
        this.stringLength = stringLength;
    }

    public String getExampleString() {
        return exampleString;
    }

    public void setExampleString(String exampleString) {
        this.exampleString = exampleString;
    }

    public int[] getAsciiEncoding() {
        return asciiEncoding;
    }

    public void setAsciiEncoding(int[] asciiEncoding) {
        this.asciiEncoding = asciiEncoding;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }
}
