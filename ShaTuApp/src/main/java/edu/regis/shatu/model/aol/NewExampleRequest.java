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
 * The information associated with a :NEW_EXAMPLE request from the GUI (client)
 * to the tutor (server).
 * 
 * @author rickb
 */
public class NewExampleRequest {
    /**
     * Specifies the type of example being requested.
     */
<<<<<<< HEAD
    private ExampleType exampleType;
=======
    private ProblemType exampleType;
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
     
    /**
     * A JSon encoded object specific to our example type.
     */
    private String data;
    
    /**
     * Initialize with default values
     */
    public NewExampleRequest() {
<<<<<<< HEAD
        exampleType = ExampleType.DEFAULT;
=======
        exampleType = ProblemType.DEFAULT;
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3

        data = "";
    }

<<<<<<< HEAD
    public ExampleType getExampleType() {
        return exampleType;
    }

    public void setExampleType(ExampleType exampleType) {
=======
    public ProblemType getExampleType() {
        return exampleType;
    }

    public void setExampleType(ProblemType exampleType) {
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        this.exampleType = exampleType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
