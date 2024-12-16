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
package edu.regis.shatu.view;

import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;
import java.awt.CardLayout;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Display current task and step in Lessons
 * @author mwemapowanga
 */
public class LessonStepView extends JPanel {       
    
    /**
     * The cards that can be displayed in this view 
     */
    private LessonView genericLesson; //Generic Lesson View 

    /**
     * Initialize and layout the child components (cards) displayed in this view.
     */
    public LessonStepView() {
        LessonGuiController.instance().setLessonView(this);
        
        setLayout(new CardLayout());
        
        try {
            initializeComponents();
        } catch (ObjNotFoundException ex) {
            Logger.getLogger(LessonStepView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NonRecoverableException ex) {
            Logger.getLogger(LessonStepView.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        initializeLayout();
        
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    /**
     * Create the child GUI components appearing in this frame.
     */
    private void initializeComponents() throws ObjNotFoundException, NonRecoverableException { 
        genericLesson = new LessonView();
    }
    
    /**
     * Layout the child components in this view
     */
    private void initializeLayout() {
        add(genericLesson, StepSelection.OVERVIEW.toString());
        
    }
}
