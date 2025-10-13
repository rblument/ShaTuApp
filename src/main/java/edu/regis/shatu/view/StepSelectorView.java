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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import edu.regis.shatu.svc.ServiceFactory;
import edu.regis.shatu.svc.StudentModelSvc;

/**
 * A view displaying the primary steps in the SHA-256 algorithm, which can
 * be selected to change the current step view.
 * 
 * @author rickb
 */
public class StepSelectorView extends GPanel {
    /**
     * The currently selected step name.
     */
    private StepSelection selectedStep = StepSelection.ENCODE;
    
    /**
     * Stores step assessment status (e.g., "Not Started", "In Progress", "Completed")
     * keyed by the StepSelection enum name.
     */
    private Map<String, String> stepAssessmentLevels = new HashMap<>();
    
    //Background Colors
    private static final Color REGIS_BLUE = new Color(0, 43, 73);
    private static final Color REGIS_YELLOW = new Color(241, 196, 0);
    
    /**
     * Initialize this view including creating and laying out its child components.
     */
    public StepSelectorView() {
        GuiController.instance().setStepSelectorView(this);
 
        setBackground(REGIS_YELLOW);
        setBorder(BorderFactory.createLineBorder(Color.WHITE));
        stepAssessmentLevels = fetchStepAssessments();
        initializeComponents();
        layoutComponents();
        
    }
    
    /**
     * Display the given selection's view in the StepView handling appropriate
     * highlighting of the associated JLabel selector (see StepSelection enum).
     * 
     * @param selection 
     */
    public void selectStep(StepSelection selection) {
        if (selectedStep != null)
            if (selectedStep != selection)
                selectedStep.getLabel().deselect();
        
        selectedStep = selection;
    }
    
    public void setSelectedStep(StepSelection selection) {
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
        JLabel label = new JLabel("Step Selections");
        label.setForeground(new Color(0,43,73));
        addc(label, 0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                5, 5, 5, 5);
        addc(createPreprocessingPanel(), 0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        addc(createHashCompPanel(), 0, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
    }
    
    /**
     * Create and layout the pre-processing panel.
     * 
     * @return the preprocessing GPanel
     */
    private GPanel createPreprocessingPanel() {
        GPanel panel = new GPanel();
        
        panel.setBackground(REGIS_BLUE);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        JLabel label = new JLabel("Message Preprocessing");
        label.setForeground(Color.WHITE);
        panel.addc(label, 0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        panel.addc(updateStepLabel(StepSelection.ENCODE), 0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        panel.addc(updateStepLabel(StepSelection.ADD1), 0, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        panel.addc(updateStepLabel(StepSelection.PAD), 0, 3, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        panel.addc(updateStepLabel(StepSelection.LENGTH), 0, 4, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        return panel;
    }
    
    /**
     * Create and layout the hash computation panel.
     * 
     * @return the hash computation GPanel
     */
    private GPanel createHashCompPanel() {
        GPanel panel = new GPanel();
        
        panel.setBackground(REGIS_BLUE); 
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        JLabel label = new JLabel("Hash Computation");
        label.setForeground(Color.WHITE);
        panel.addc(label, 0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        panel.addc(updateStepLabel(StepSelection.PREPARE), 0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        panel.addc(updateStepLabel(StepSelection.INIT_VARS), 0, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        panel.addc(updateStepLabel(StepSelection.COMPRESS), 0, 3, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        panel.addc(updateStepLabel(StepSelection.ROTATE_BITS), 0, 4, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        panel.addc(updateStepLabel(StepSelection.SHIFT_RIGHT), 0, 5, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        panel.addc(updateStepLabel(StepSelection.XOR), 0, 6, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        panel.addc(updateStepLabel(StepSelection.ADD_TWO_BIT), 0, 7, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        panel.addc(updateStepLabel(StepSelection.CHOICE_FUNCTION), 0, 8, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        panel.addc(updateStepLabel(StepSelection.MAJ_FUNCTION), 0, 9, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        panel.addc(updateStepLabel(StepSelection.SHA_ZERO), 0, 10, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        panel.addc(updateStepLabel(StepSelection.SHA_ONE), 0, 11, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                5, 5, 5, 5);
        return panel;
    }
    
    /**
    * Creates a JLabel for a step with assessment status appended.
    */
   private JLabel updateStepLabel(StepSelection step) {
       // Retrieve the existing label and its base text by stripping any trailing status.
       JLabel label = step.getLabel(); // Preserve existing label functionality
       String originalText = label.getText();
       // Remove any previously appended status strings.
       originalText = originalText.replaceAll(" \\(Teach Me\\)$", "")
                                  .replaceAll(" \\(Practice\\)$", "")
                                  .replaceAll(" ✅$", "");

       String status = stepAssessmentLevels.getOrDefault(step.name(), "Not Started");

       if ("Completed".equals(status)) {
           label.setText(originalText + " ✅");
       } else if ("In Progress".equals(status)) {
           label.setText(originalText + " (Practice)");
       } else {
           label.setText(originalText + " (Teach Me)");
       }
       return label;
   }
    
    /**
     * Fetches assessment levels for steps using the dashboard logic.
     * Replace "USER_ID" with the actual user identifier as needed.
     */
    private Map<String, String> fetchStepAssessments() {
        Map<String, String> assessmentMap = new HashMap<>();
        try {
            StudentModelSvc studentModelService = ServiceFactory.findStudentModelSvc();
            List<String> incompleteTeachMe = studentModelService.retrieveIncompleteLessons("USER_ID", "Teach Me");
            List<String> incompletePractice = studentModelService.retrieveIncompleteLessons("USER_ID", "Practice");
            // Use the StepSelection enum's name as the key.
            for (String step : incompleteTeachMe) {
                assessmentMap.put(step.toUpperCase(), "Not Started");
            }
            for (String step : incompletePractice) {
                assessmentMap.put(step.toUpperCase(), "In Progress");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return assessmentMap;
    }
}