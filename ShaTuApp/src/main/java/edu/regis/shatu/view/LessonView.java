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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import java.io.File;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author mwemapowanga
 */
public class LessonView extends UserRequestView implements ActionListener{
   
   // private JTextPane descriptionTextArea;
    private JTextArea descriptionTextArea;
    private JLabel lesson;
    private JButton previousButton, nextButton;
    private JPanel buttonPanel; 
    private GPanel qrPanel;
   // private static boolean continueLoop = false;
   // private int i = 0;
    Color white = new Color(255,255,255);

    /**
     * Initialize this view including creating and laying out its child components.
     */
    public LessonView() {       
        initializeComponents();
        initializeLayout();
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    
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
        
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(white);
      //  buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder());
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
        descriptionTextArea = new JTextArea();
       // descriptionTextArea = new JTextPane();
       // descriptionTextArea.setContentType("text/html");
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setBackground(null);
        //descriptionTextArea.setBorder(null); 

        
        try {
            File xmlFile = new File("Course_1.xml"); // Replace with your XML file path
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            // Normalize the XML structure
            doc.getDocumentElement().normalize();

            // Get the root element
            Element root = doc.getDocumentElement();
            // System.out.println("Root element: " + root.getNodeName());

            // Traverse the XML nodes
            NodeList nodeList = root.getChildNodes();
            final String[] lines;

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    
                    // Display XML Elements
                   // descriptionTextArea.append( element.getTagName() + ": " + element.getTextContent());
                   // descriptionTextArea.setText(element.getTagName() + ": " + element.getTextContent());
                    descriptionTextArea.append(element.getTextContent());        

                    //System.out.println(element.getTextContent());
                   // Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        
        
    }

}
