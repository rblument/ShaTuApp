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

import java.awt.Color;
import java.awt.GridBagConstraints;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

/**
 *
 * @author mwemapowanga
 */
public class LessonStepSelectorView extends GPanel {
    /**
     * The currently selected lesson step name.
     */
    private StepSelection selectedStep = StepSelection.OVERVIEW;
    
    /**
     * Initialize this view including creating and laying out its child components.
     */
    public LessonStepSelectorView() {
        LessonGuiController.instance().setLessonStepSelectorView(this);
 
        setBackground(new Color(241,196,0));
        setBorder(BorderFactory.createLineBorder(Color.WHITE));
        initializeComponents();
        layoutComponents();
        
        StepSelection.OVERVIEW.getLabel().select(); // Will callback displayStep
    }
    
    /**
     * Display the given selection's view in the LessonStepView handling appropriate
     * highlighting of the associated JLabel selector (see StepSelection enum).
     * 
     * @param selection 
     */
    public void displayStep(StepSelection selection) {
        if (selectedStep != selection)
            selectedStep.getLabel().deselect();
        
        selectedStep = selection;
    }
    
    /**
     * Create the child components used in this frame.
     */
    private void initializeComponents() {  
        // Note: the child components are found in the StepSelection enum.
        //       For example, StepSelection.ENCODE.getLabel()
    }
    
     /**
     * Layout the child components used in this frame.
     */
    private void layoutComponents() {
        JLabel label = new JLabel("SHA-256 Lessons");
        label.setForeground(new Color(0,43,73));
        addc(label, 0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
        addc(createLessonMenuPanel(), 0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        
    }
    
    /**
     * Create and layout the Lesson Menu panel.
     * 
     * @return the LessonMenuPanel GPanel
     */
    private GPanel createLessonMenuPanel() {
        GPanel panel = new GPanel();
        
        panel.setBackground(new Color(0, 43, 73)); // Blue
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        panel.addc(StepSelection.OVERVIEW.getLabel(), 0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        panel.addc(StepSelection.MESSAGE_PREPROCESSING.getLabel(), 0, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        panel.addc(StepSelection.HASH_COMPUTATION.getLabel(), 0, 3, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        panel.addc(StepSelection.CONCLUSION.getLabel(), 0, 4, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        return panel;
    }
 
}

