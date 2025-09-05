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
package edu.regis.shatu.view;

<<<<<<< HEAD
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.view.act.ActionFactory;
=======
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
<<<<<<< HEAD
import javax.swing.JFrame;
=======

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.ViewType;

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3

/**
 * The primary GUI window in the ShaTu application.
 * 
 * Most of the display functionality is handled by child component views.
 * 
 * @author rickb
 */
public class MainFrame extends JFrame implements WindowListener {
<<<<<<< HEAD
=======

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * The singleton instance of this frame.
     */
    private final static MainFrame SINGLETON;
    
    // Invoked when this class is loaded
<<<<<<< HEAD
    static {
        ActionFactory.createActions();
        
=======
    static {     
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        SINGLETON = new MainFrame();
    }
    
    /**
     * Return the singleton instance of this frame.
     * 
     * @return the MainFrame singleton
     */
    public static MainFrame instance() {
        return SINGLETON;
    }
    
    /**
     * The size of this frame will the size of the user's screen minus this
     * screen size inset.
     */
    private static final int SCREEN_SIZE_INSET = 50;
    
    /**
     * The SHA tutoring session displayed in this frame.
     */
    private TutoringSession model;
    
    /**
     * The primary view displayed in this frame.
     */
    private TutoringSessionView view;
    
<<<<<<< HEAD
=======
    private Timer inactivityTimer; // Timer for inactivity tracking
    
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * Initialize and layout the child components displayed in this frame.
     */
    private MainFrame() {
        super("ShaTut");

<<<<<<< HEAD
        Dimension screenSize = Toolkit. getDefaultToolkit(). getScreenSize();
        screenSize.width = screenSize.width - SCREEN_SIZE_INSET ;
        screenSize.height = screenSize.height - SCREEN_SIZE_INSET - 10;
        setSize(screenSize);
        setLocation(10, 10);
        
        setJMenuBar(new ShaTuMenuBar());
        
        initializeComponents();
        layoutComponents();
 
        addWindowListener(this);
        
        setVisible(false);
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // see windowClosing()
    }

=======
        // Get screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Calculate the initial size (80% of the splashFrame width)
        int width = (int) (screenSize.width * 0.5);
        int height = (int) (screenSize.height);
        setSize(width, height);

        setJMenuBar(new ShaTuMenuBar());

        initializeComponents();
        layoutComponents();

        addWindowListener(this);

        setVisible(false);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // see windowClosing()
    }
  
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * Update the current model with changes made in this frame and return it.
     * 
     * @return the Session model 
     */
    public TutoringSession getModel() {
        // ToDo: Ask to update?
        updateModel();
        
        return model;
    }

    /**
     * Display the given model in this frame.
     * 
     * @param model a Session model
     */
    public void setModel(TutoringSession model) {
        // ToDo: Ask to save changes to existing model?
<<<<<<< HEAD
        
        this.model = model;
    }
    
   // public int getSessionId() {
     //   return model.getId();
   // }

   // public void setSessionId(int sessionId) {
     //   model.setId(sessionId);
    //}
=======
        this.model = model;
        view.setModel(model);
    }
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        // ToDo: Save etc.
        this.dispose();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
    
    /**
     * Create the child components used in this frame.
     */
    private void initializeComponents() {
<<<<<<< HEAD
        view = new TutoringSessionView(); 
=======
        view = new TutoringSessionView(ViewType.DO_ONE);

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }
    
    /**
     * Layout the child components used in this frame.
     */
    private void layoutComponents() {
        setContentPane(view);
<<<<<<< HEAD
=======
        
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }
    
    /**
     * Update the current model with any changes made in this frame's view.
     */
    private void updateModel() {
        
    }
    
    /**
     * Display the current model in this frame's view.
     */
    private void updateView() {
<<<<<<< HEAD
        
=======
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }
}
