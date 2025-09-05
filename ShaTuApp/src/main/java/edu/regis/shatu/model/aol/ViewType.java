/*
 * SHATU: SHA-256 Tutor
<<<<<<< HEAD:ShaTuApp/src/main/java/edu/regis/shatu/view/act/ActionFactory.java
 * 
 *  (C) Johanna & Richard Blumenthal, All rights reserved
 * 
 *  Unauthorized use, duplication or distribution without the authors'
 *  permission is strictly prohibted.
 * 
=======
 *
 *  (C) Johanna & Richard Blumenthal, All rights reserved
 *
 *  Unauthorized use, duplication or distribution without the authors'
 *  permission is strictly prohibted.
 *
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3:ShaTuApp/src/main/java/edu/regis/shatu/model/aol/ViewType.java
 *  Unless required by applicable law or agreed to in writing, this
 *  software is distributed on an "AS IS" basis without warranties
 *  or conditions of any kind, either expressed or implied.
 */
<<<<<<< HEAD:ShaTuApp/src/main/java/edu/regis/shatu/view/act/ActionFactory.java
package edu.regis.shatu.view.act;

/**
 * Factory for creating the GUI actions used in the ShaTu user interface.
 * 
 * @author rickb
 */
public class ActionFactory {
    /**
     * Create each of the Java GUI actions by referencing their singleton.
     */
    public static void createActions() {
        CreateAcctAction.instance();
        NewUserAction.instance();
        SaveSessionAction.instance();
        SignInAction.instance();
        
    }
=======

package edu.regis.shatu.model.aol;

/**
 * Enumerates the different view types for the See One, Do One, and Teach One views
 */
public enum ViewType
{
    SEE_ONE,
    DO_ONE,
    TEACH_ONE
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3:ShaTuApp/src/main/java/edu/regis/shatu/model/aol/ViewType.java
}
