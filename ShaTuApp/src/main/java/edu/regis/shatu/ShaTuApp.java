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

package edu.regis.shatu;

<<<<<<< HEAD
=======
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
import edu.regis.shatu.svc.ShaTuServer;
import edu.regis.shatu.util.ResourceMgr;
import edu.regis.shatu.view.MainFrame;
import edu.regis.shatu.view.SplashFrame;
<<<<<<< HEAD
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
=======
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3

/**
 * A standalone implementation of the SHA-256 intelligent tutoring application.
 * @author rickb
 */
public class ShaTuApp {
     /**
     * Property file located on the CLASSPATH, which is used to configure the LOGGER.
     */
    private static final String LOGGER_PROPERTIES = "/Logging.properties";
<<<<<<< HEAD
    // ./resources/logging.properties
=======

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * Events of interest occurring in this class are logged to this logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ShaTuApp.class.getName());
    
<<<<<<< HEAD
     /**
     * When deserializing a polymorphic Step object, GSon requires the subclass
     * step type in order to create the correct subclass instance, this type
     * adapter factory provides this information (see its use in SignInAction).
     */
   // private static final RuntimeTypeAdapterFactory<Step> STEP_ADAPTER_FACTORY =
      //      RuntimeTypeAdapterFactory.of(Step.class, "myTypeName")
               // .registerSubtype(EncodeAsciiStep.class, "EncodeAsciiStep")
               // .registerSubtype(CompleteTaskStep.class, "CompleteTaskStep")
               // .registerSubtype(CompleteStepStep.class, "CompleteStepStep")
          //      .registerSubtype(RotateStep.class, "RotateStep");
    //
    /**
     * Convenience providing access to the Step Type Adapter Factory (see
     * comments documenting the static STEP_ADAPTER_FACTORY field).
     * 
     * @return 
     */
    //public static RuntimeTypeAdapterFactory<Step> typeAdapterFactory() {
     //   return STEP_ADAPTER_FACTORY;
   // }
=======
    /**
     * Configure the LOGGER with the properties found in the LOGGER_PROPERTIES 
     * file found on the CLASSPATH.
     */
    static {
        final InputStream strm = 
            ShaTuApp.class.getResourceAsStream(LOGGER_PROPERTIES);
        
        try {
            LogManager.getLogManager().readConfiguration(strm);
        } catch (IOException e) {
        
            LOGGER.severe("Error loading ./logging.properties");
            LOGGER.severe(e.getMessage());
        }        
    }
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    
    /**
     * Main entry point for the ShaTut application, which will display the UI.
     * 
     * @param args ignored
     */
    public static void main(String[] args) {
        LOGGER.info("ShaTuApp Initializing...:");
        
<<<<<<< HEAD
=======
        // Initialize the logging properties from the logging properties files
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        try {
            final InputStream strm = ShaTuApp.class.getResourceAsStream(LOGGER_PROPERTIES);
        
            LogManager.getLogManager().readConfiguration(strm);
            
            LOGGER.info("Message logging initialization completed.");
        } catch (IOException e) {
        
            LOGGER.severe("Error loading ./logging.properties");
            LOGGER.severe(e.getMessage());
        }      
        
<<<<<<< HEAD
        // Initializes the properties from ShaTu.properties and sets the locale.
=======
        // Initializes the application properties from, which also sets the locale.
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        ResourceMgr.instance();
        
        LOGGER.info("ShaTu properties initialization completed.");
        
        System.out.println("Finished initializing");
<<<<<<< HEAD
        //Damian
        // ToDo: Remove this example of using the SHA-256 algorithm.
        //  SHA_256 alg = new SHA_256();
        //alg.doit("Regis Computer Science Rocks!");
        
=======
        
        // Create the server and then initialize the GUI client (see ntoes).
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        try {
            LOGGER.info(" Starting ShaTu Server (Tutoring Service)...");
            // ToDo: Separate the initialization of client and server
            // Start the socket server for the ShaTu tutor.
            (new Thread(new ShaTuServer())).start();
            
            // ToDo: This puts the main client UI thread to sleep to give the 
            // server a chance to finish starting. This won't be required once 
<<<<<<< HEAD
            // we separate the server into its own application that is separate
            // from the GUI client since the server should "always" be running.
=======
            // we separate the server into its own application that executes
            // on a different host from the GUI client since the server should 
            // "always" be running.
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
            Thread.sleep(4000);
                
            LOGGER.info(" Server is running.");
                
            LOGGER.info(" Starting Client GUI...");
<<<<<<< HEAD
            
=======

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
            // Force the creation of the MainFrame singleton, which is not
            // made visible to the user until after they sign-in.
            MainFrame.instance();

            // Force the creation of the SplashFrame, which is displayed and
            // allows the user to sign-in or create a new student account.
            // If sign-in is successful the MainFrame is displayed.
            SplashFrame.instance();            
                
            LOGGER.info("ShaTu Initialization successful.");
                
<<<<<<< HEAD
            } catch (InterruptedException ex) {
                Logger.getLogger(ShaTuApp.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException e) {
            LOGGER.severe("Couldn't create Data directory in NetBeans Project.");
            LOGGER.severe("Perhaps, try changing permissions.");
=======
        } catch (InterruptedException ex) {
                Logger.getLogger(ShaTuApp.class.getName()).log(Level.SEVERE, null, ex);
       // } catch (SecurityException e) {
                // ToDo: Check this, I don't think we're still doing this.
                // Hence, this data directory creation error cannot occur.
         //   LOGGER.severe("Couldn't create Data directory in NetBeans Project.");
          //  LOGGER.severe("Perhaps, try changing permissions.");
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        }
    }
}
