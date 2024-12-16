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

import edu.regis.shatu.dao.CourseDAO;
import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;
import edu.regis.shatu.model.Course;
import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Task;
import edu.regis.shatu.model.Unit;
import edu.regis.shatu.model.aol.NewExampleRequest;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import org.w3c.dom.NodeList;
import java.util.ArrayList;


/**
 *
 * @author mwemapowanga
 */
public class LessonView extends UserRequestView implements ActionListener {
   
    String text;
    private JTextPane descriptionTextArea;
    private NodeList nodeList;
    private JLabel lesson;
    private JButton previousButton, nextButton; //startButton;
    private JPanel buttonPanel; 
    private GPanel qrPanel;
    private static boolean buttonClicked = false;
    private int i = 0;
    Color white = new Color(255,255,255);
    ArrayList<String> lessonText = new ArrayList<String>();

    /**
     * Initialize this view including creating and laying out its child components.
     * @throws edu.regis.shatu.err.ObjNotFoundException
     * @throws edu.regis.shatu.err.NonRecoverableException
     */
    public LessonView() throws ObjNotFoundException, NonRecoverableException { 
        CourseDAO newCourse = new CourseDAO();
        Course course = null;
        int courseSize = 10;
        int unitSize = 10;
        int taskSize = 10;
        int stepSize = 10;
        ArrayList<Unit> unitList = null;
        ArrayList<Task> taskList = null;
        ArrayList<Step> stepList = null;
        
                
        initializeComponents();
        initializeLayout();
        
        
        try {
                
                for (int c = 0; c < courseSize; c++) { // Add all courses to list to be displayed to the screen
                  course = newCourse.retrieve(c);
                  lessonText.add(course.description);
                  courseSize = course.courseSize;
                  lessonText.add(Integer.toString(courseSize));
                  
                  for (int u = 0; u < unitSize; u++) { // Add all units to list to be displayed to the screen
                      unitList = course.getUnits();
                      lessonText.add(unitList.get(u).description);
                      unitSize = unitList.size();
                      
                      for (int t = 0; t < unitSize; t++) { // Add all tasks to list to be displayed to the screen
                        taskList = course.getTasks();
                        lessonText.add(taskList.get(t).description);
                        taskSize = taskList.size();
                      
                            for (int s = 0; s < unitSize; s++) { // Add all steps to list to be displayed to the screen
                                stepList = course.getSteps();
                                lessonText.add(stepList.get(s).description);
                                stepSize = stepList.size();
                            }
                      }
                  }
                }


        } catch (ObjNotFoundException e) {
            throw new ObjNotFoundException("Test");
        }
        
        System.out.println("Course: " + course);
        
    }
    
 
 
    /**
     * Create the child GUI components appearing in this frame.
     */
    private void initializeComponents() {
            lesson = new JLabel("");
            
            setUpButtons();   
            setUpPanel();
            setupDescriptionSection();
            
    }
    
    /**
     * Layout the child components in this view.
     */
    private void initializeLayout() {
        setBackground(white);
                
        JLabel label = new JLabel("");
        label.setLabelFor(lesson);
        addc(descriptionTextArea, 0, 0, 1, 1, 
                1.0, 0.0, GridBagConstraints.CENTER, 
                GridBagConstraints.HORIZONTAL, 5, 5, 5, 5);
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
            
            System.out.println("SHA-256 Lesson......this is from :Essm View Update");
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
        
        ActionListener selection = e -> {
            JButton source = (JButton) e.getSource();
        };
        
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(white);
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
    
     /**
     * Sets up the description section of the view, explaining the purpose of 
     * the encoding exercise.
     */
    private void setupDescriptionSection() {
        descriptionTextArea = new JTextPane();
        descriptionTextArea.setContentType("text/html");
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setBackground(null);
        descriptionTextArea.setText( "<html>" +
                    "<body>" +
                    "<h2>Overview</h2>" +
                    "<p>Click Next to Continue</p>" +
                    "</body>" +
                    "</html>"
            );


    }
    
    private void playNext() { 
       if(i >= 0 && i < lessonText.size()) {
            descriptionTextArea.setText("<html>" +
                    "<body>" +
                    "<h2>" + lessonText.get(i) + "</h2>" +
                    "<p>" + "\n" + "\n" + "Click Next To Continue </p>" +
                    "</body>" +
                    "</html>");

       }
       else {
           descriptionTextArea.setText("<html>" +
                    "<body>" +
                    "<h2>" + "End of Lesson" + "</h2>" +
                    "</body>" +
                    "</html>");
       }
       
    }
    
    private void playPrevious() {
       if(i >= 0 && i < lessonText.size()) {
            descriptionTextArea.setText("<html>" +
                    "<body>" +
                    "<h2>" + lessonText.get(i) + "</h2>" +
                    "<p>" + "\n" + "\n" + "Click Next To Continue </p>" +
                    "</body>" +
                    "</html>");

       }
       else {
           i = lessonText.size();
       }
         
    }
    
    /* Handles the actionPerformed event for buttons in the view.
     *
     * @param event The ActionEvent that occurred.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == previousButton) {
           i = i-2;
           playPrevious();
        } else if (event.getSource() == nextButton) {
            playNext();
            i++;
                    

        } 
    }

}
