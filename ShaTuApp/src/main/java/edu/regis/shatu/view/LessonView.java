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
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.ArrayList;


/**
 *
 * @author mwemapowanga
 */
public class LessonView extends UserRequestView implements ActionListener{
   
    String text;
    private JTextPane descriptionTextArea;
    //private JTextArea descriptionTextArea;
    private NodeList nodeList;
    private JLabel lesson;
    private JButton previousButton, nextButton; //startButton;
    //private ActionListener selection; //nextButtonListener, previousButtonListener;
    private JPanel buttonPanel; 
    private GPanel qrPanel;
    private static boolean buttonClicked = false;
    private int i = 0;
    Color white = new Color(255,255,255);
    ArrayList<String> cars = new ArrayList<String>();
    ArrayList<String> lessonText = new ArrayList<String>();

    /**
     * Initialize this view including creating and laying out its child components.
     */
    public LessonView() {       
        initializeComponents();
        initializeLayout();
    }
    
    
    
    public void getXML(String filename) {
SwingUtilities.invokeLater(() -> {
try {
            File xmlFile = new File("Course_1.xml"); // Replace with your XML file path
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            // Normalize the XML structure
            doc.getDocumentElement().normalize();

            // Get the root element
            Element root = doc.getDocumentElement();
             System.out.println("Root element: " + root.getNodeName());

            // Traverse the XML nodes
            NodeList nodeList = root.getChildNodes();
            final String[] lines;
//System.out.println(nodeList.item(i).getTextContent());
            for (int j = 0; j < nodeList.getLength(); j++) {            //get 1st tree child nodes
                Node node = nodeList.item(j);
                if (node.getNodeType() == Node.ELEMENT_NODE ) {
                    Element element = (Element) node;
                   
                    if (node.hasChildNodes()) {                         //get 2nd tree child nodes
                        NodeList tempNodeList = node.getChildNodes();
                        
                        for (int k = 0; k < tempNodeList.getLength(); k++) {
                            Node childNode = tempNodeList.item(k);
                            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                             
                                 Element childElement = (Element) childNode;
                                 if(childElement.getTextContent() != ""){
                                    lessonText.add(childElement.getTextContent());
                                    System.out.println(childElement.getTagName() + ": " + childElement.getTextContent()); 
                                 }
                            }
                        }
                    }
                    else {
                        lessonText.add(element.getTextContent());
                    }
                    

                    // Display XML Elements
                   // descriptionTextArea.append( element.getTagName() + ": " + element.getTextContent());
                   // descriptionTextArea.setText(element.getTagName() + ": " + element.getTextContent());
                    //descriptionTextArea.setText(element.getTextContent());  
                   // text = element.getTextContent();
                  //  playNext();

                   // System.out.println(element.getTextContent());
                    
                   // Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }});
        
       


/*// NodeList fileList = null;
       
          //                 descriptionTextArea.setText("Finally");

        SwingUtilities.invokeLater(() -> {
            NodeList nodeList = null;
            
        try {
           // descriptionTextArea.setText("Finally");

            File xmlFile = new File(filename); 
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            // Normalize the XML structure
            doc.getDocumentElement().normalize();

            // Get the root element
            Element root = doc.getDocumentElement();
             System.out.println("Root element: " + root.getNodeName());
            // Traverse the XML nodes
            nodeList = root.getChildNodes();
            //final String[] lines;
            // Wait for button click

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    
                    // Display XML Elements
                   // descriptionTextArea.append( element.getTagName() + ": " + element.getTextContent());
                    descriptionTextArea.setText(element.getTextContent());
                   // descriptionTextArea.append(element.getTextContent());        

                  System.out.println(element.getTextContent());
                   // Thread.sleep(1000);
                   // Wait for button click
                while (!buttonClicked) {
                try {
                    Thread.sleep(10); // Sleep to avoid busy waiting
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                   
                }
            }
            
            buttonClicked = false; // Reset flag

            }
        } catch (Exception e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }});
           // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    //return nodeList;*/
    }
 
    /**
     * Create the child GUI components appearing in this frame.
     */
    private void initializeComponents() {
            lesson = new JLabel("");
            
           cars.add("Volvo");
    cars.add("BMW");
    cars.add("Ford");
    
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
        
       /* startButton = new JButton("Start");
        startButton.addActionListener(this);    */    
        
        ActionListener selection = e -> {
            JButton source = (JButton) e.getSource();
        };
        
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(white);
      //  buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        buttonPanel.add(previousButton);
        buttonPanel.add(nextButton);
       // buttonPanel.add(startButton);
        
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
       // descriptionTextArea = new JTextArea();
        descriptionTextArea = new JTextPane();
        descriptionTextArea.setContentType("text/html");
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setBackground(null);
        //descriptionTextArea.setBorder(null); 
        descriptionTextArea.setText( "<html>" +
                    "<body>" +
                    "<h2>Overview</h2>" +
                    "<p>Click Next to Continue</p>" +
                    "</body>" +
                    "</html>"
            );
        getXML("Course_1.xml"); // Replace with your XML file path


    }
    
    private void playNext() { 
        //buttonClicked = true;
       // Node node = nodeList.item(i);
       if(i >= 0 && i < lessonText.size()) {
            System.out.println("playNext: " + i);
            descriptionTextArea.setText("<html>" +
                    "<body>" +
                    "<h2>" + lessonText.get(i) + "</h2>" +
                    "<p>" + "\n" + "\n" + "Click Next to Continue </p>" +
                    "</body>" +
                    "</html>");

       }
       else {
           descriptionTextArea.setText("End of Lesson");
       }
       /* if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) nodeList.item(i);
            descriptionTextArea.setText(nodeList.item(i).getTextContent());
        }*/

        //NodeList nodeList = getXML("Course_1.xml");
        
       /* Node node = nodeList.item(i);
        //descriptionTextArea.setText("Next");
       // System.out.println(nodeList);

        if (i < nodeList.getLength()) {
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    System.out.println(element.getTextContent());

                    
                    // Display XML Elements
                   // descriptionTextArea.append( element.getTagName() + ": " + element.getTextContent());
                    descriptionTextArea.setText(element.getTagName() + ": " + element.getTextContent()); 
                    i++;
                    
                     buttonClicked = true;
                   // descriptionTextArea.setText(element.getTagName() + ": " + element.getTextContent());
                    descriptionTextArea.append(element.getTextContent());        

                    //System.out.println(element.getTextContent());
                   // Thread.sleep(1000);
                }
        }

     */
    }
    
    private void playPrevious() {
       if(i >= 0 && i < lessonText.size()) {
            System.out.println("playPrevious: " + i);
            descriptionTextArea.setText("<html>" +
                    "<body>" +
                    "<h2>" + lessonText.get(i) + "</h2>" +
                    "<p>" + "\n" + "\n" + "Click Next to Continue </p>" +
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
        } /*else if (event.getSource() == startButton) {
           //playNext();
           //getXML("Course_1.xml");
        }*/ else if (event.getSource() == nextButton) {
           //if (i < nodeList.getLength()) {
            playNext();
            i++;
                    System.out.println("actionPerformed: " + i);

           //getXML("Course_1.xml");
          // }
        } 
    }

}
