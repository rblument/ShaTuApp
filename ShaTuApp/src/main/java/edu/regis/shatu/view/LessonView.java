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

import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.NewExampleRequest;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author mwemapowanga
 */
public class LessonView extends UserRequestView implements ActionListener{
   
    private JLabel lesson;
    private JButton previousButton, nextButton;
    private JPanel buttonPanel; 
    private GPanel qrPanel;

    /**
     * Initialize this view including creating and laying out its child components.
     */
    public LessonView() {       
        initializeComponents();
        initializeLayout();
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
       
    }
 
    /**
     * Create the child GUI components appearing in this frame.
     */
    private void initializeComponents() {
        lesson = new JLabel("Overview");
        setUpButtons();
        setUpPanel();   
    }
    
    /**
     * Layout the child components in this view.
     */
    private void initializeLayout() {
        JLabel label = new JLabel("Lesson:");
        label.setLabelFor(lesson);
        addc(label, 0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                5, 5, 5, 5);
     
        addc(lesson, 1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                5, 5, 5, 5);
        
        addc(qrPanel, 0, 2, 3, 1, 0.0, 1.0,
                GridBagConstraints.SOUTH, GridBagConstraints.NONE,
                5, 5, 5, 5);
     
    }
    @Override
    protected void updateView() {
        if (model != null) {
            
            System.out.println("SHA-256 Lesson");
        }
    }

    @Override
    public NewExampleRequest newRequest() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public StepCompletion stepCompletion() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    /**
     * Sets up the Previous and Next buttons and their action listeners
     */
    private void setUpButtons() {
        
        
        previousButton = new JButton("Previous");
        previousButton.addActionListener(this);
        
        nextButton = new JButton("Next");
        nextButton.addActionListener(this);
        
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(previousButton);
        buttonPanel.add(nextButton);
    }
    
    /**
     * Creates a GPanel containing the button panel.
     *   
     */
    private void setUpPanel(){ 
        qrPanel = new GPanel();

        qrPanel.addc(buttonPanel, 0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
    }

}
