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
package edu.regis.shatu.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.regis.shatu.util.XmlMgr;
import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;
import edu.regis.shatu.err.XmlException;
import edu.regis.shatu.model.GuiGesture;
import edu.regis.shatu.model.Course;
import edu.regis.shatu.model.Hint;
import edu.regis.shatu.model.InformationStep;
import edu.regis.shatu.model.aol.ScaffoldLevel;
import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.TaskSelectionKind;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.Task;
import edu.regis.shatu.model.aol.Timeout;
import edu.regis.shatu.model.Unit;
import edu.regis.shatu.model.BloomLevel;
import edu.regis.shatu.model.ExercisingLocation;
import edu.regis.shatu.model.KnowledgeComponent;
import static edu.regis.shatu.model.aol.StepSubType.INFO_MESSAGE;
import static edu.regis.shatu.model.aol.StepSubType.GUI_ACTION;
import edu.regis.shatu.svc.CourseSvc;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An XML-based Data Access Object implementing CourseSvc behaviors.
 *
 * @author rickb
 */
public class CourseDAO extends MySqlDAO implements CourseSvc {
    private static final String URL = "jdbc:mysql://localhost:3306/ShaTuDB"; // database name based on commands in setup.sql
    private static final String USER = "root";            // Your DB details - needs full privileges 
    private static final String PASSWORD = "bliss929";         // Your DB details
    
   
    

    /**
     * Instantiate this Course DAO with default values.
     */
    public CourseDAO() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Course retrieve(int id) throws ObjNotFoundException, NonRecoverableException {
        final String sql = "SELECT * FROM Course WHERE CourseId = " + id;

        Connection conn = null;
        PreparedStatement stmt = null;
        Course course = new Course(id);


        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            stmt = conn.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                
                course.setTitle(rs.getString("CourseTitle"));

                course.setDescription(rs.getString("CourseDescription"));
                
                course.courseSize = extractCourseSize(conn);

                course.setPrimaryPedagogy(extractPrimaryPedagogy(rs.getString("CoursePrimaryPedagogy")));

                course.setUnits(extractUnits(conn, 0)); 
                
                course.setTasks(extractTasks(conn, 0));
                
                course.setSteps(extractSteps(conn, 0));
 
                course.setOutcomes(extractKnowledgeComponents(conn,id));

                return course;


            } else {
                throw new ObjNotFoundException("Course Id:" + id);
            }
        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO_Err_1" + e.toString(), e);
        } finally {
            close(conn, stmt);
        }
        
    }
    
    /**
     * Extract child &lt;Outcome> elements from given XML DOM parent element
     * adding each as a Outcome to the given Course.
     *
     * @param parent an XML DOM Element containing one or more child
     * &lt;Outcome> node elements.
     * @throws ShaTuException a nonrecoverable exception also see getCause()
     * @return a List of KnowledgeComponent outcomes
     */
    private ArrayList<Integer> extractKnowledgeComponentsIds(Connection conn, int StepId) 
            throws NonRecoverableException {
        
        ArrayList<Integer> outcomes = new ArrayList<>();
        String sql = "SELECT * FROM Step WHERE StepId = " + StepId;
        

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet result = stmt.executeQuery(sql);
            
            int id = -1; // Declare here for better error reporting below

            while (result.next()) {
               
                outcomes.add(result.getInt("StepOutcome"));
                
            }
            
            return outcomes;
            
        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO_Err_2" + e.toString(), e);

        }
    }

    /**
     * Extract child &lt;Outcome> elements from given XML DOM parent element
     * adding each as a Outcome to the given Course.
     *
     * @param parent an XML DOM Element containing one or more child
     * &lt;Outcome> node elements.
     * @throws ShaTuException a nonrecoverable exception also see getCause()
     * @return a List of KnowledgeComponent outcomes
     */
    private ArrayList<KnowledgeComponent> extractKnowledgeComponents(Connection conn, int StepId) 
            throws NonRecoverableException {
        
        ArrayList<KnowledgeComponent> outcomes = new ArrayList<>();
        String sql = "SELECT * FROM KnowledgeComponent WHERE KnowledgeComponentExercisingLocation = " + StepId;
        
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet result = stmt.executeQuery(sql);
            
            while (result.next()) {
                
                 outcomes.add(extractKnowledgeComponent(conn, StepId));
                       
            }
            
            return outcomes;
            
        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO_Err_2" + e.toString(), e);

        }
       
           
    }

    /**
     * Extract and return the knowledge component outcome the given
     * &lt;KnowledgeComponent> element.
     * 
     * @param element a &lt;KnowledgeComponent> element
     * @return a KnowledgeComponent
     * @throws NonRecoverableException 
     */
    private KnowledgeComponent extractKnowledgeComponent(Connection conn, int StepId) 
            throws NonRecoverableException {
        
        String sql = "SELECT * FROM KnowledgeComponent WHERE KnowledgeComponentExercisingLocation = " + StepId;
        KnowledgeComponent outcome = null;

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet result = stmt.executeQuery(sql);
            
            int id = -1; // Declare here for better error reporting below

            while (result.next()) {
                
                id = result.getInt("KnowledgeComponentId");
                
                outcome = new KnowledgeComponent(id);
                
                outcome.setTitle(result.getString("KnowledgeComponentTitle"));
                
                outcome.setDescription(result.getString("KnowledgeComponentDescription"));
                
                outcome.setBloomLevel(extractBloomLevel(result.getString("KnowledgeComponentBloomLevel")));
               
                    if (result.getString("KnowledgeComponentPedagogy").equals("Tutor")) {
                        outcome.setIsDomainFocus(false); // teaching the tutor/GUI itself
                    } else {
                        outcome.setIsDomainFocus(true);  // teaching SHA-256 domain
                    }

                outcome.setExercisingLocations(extractExercisingLocations(conn, id));
        
           
            }
        
    
            
        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO_Err_2" + e.toString(), e);

        }   
        
    return outcome;

       
    }

    /**
     * Extract and return the units in this course from the &lt;Unit> elements.
     * 
     * @param parent a &lt;Course> element
     * @return a List of Units
     * @throws NonRecoverableException 
     */
    private int extractCourseSize (Connection conn) throws NonRecoverableException {
        int size = 0;
        String sql = "SELECT * FROM Course";
        
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet result = stmt.executeQuery(sql);
            
             while (result.next()) {
                 size++;
                 
             }
            return size;
            
        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO_Err_2" + e.toString(), e);

        }

    }
    
   
    
    /**
     * Extract and return the units in this course from the &lt;Unit> elements.
     * 
     * @param parent a &lt;Course> element
     * @return a List of Units
     * @throws NonRecoverableException 
     */
    private ArrayList<Unit> extractUnits (Connection conn, int CourseId) throws NonRecoverableException {
        ArrayList<Unit> units = new ArrayList<>();
        String sql = "SELECT * FROM Unit WHERE CourseId = " + CourseId;
        
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet result = stmt.executeQuery(sql);
            System.out.println("extractUnits: " + result.isClosed());
            int id = -1; // Declare here for better error reporting below

            while (result.next()) {
                
                id = result.getInt("UnitId");
                
                Unit unit = new Unit(id);
                
                unit.setSequenceId(result.getInt("UnitSequence"));
                
                unit.setTitle(result.getString("UnitTitle"));
                
                unit.setDescription(result.getString("UnitDescription"));
                
                unit.setTasks(extractTasks(conn, id));
                
                units.add(unit);
                
            }
            
            return units;
            
        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO_Err_2" + e.toString(), e);

        }

    }

    /**
     * Extract and return the tasks in the given &lt;Unit> element
     * 
     * @param parent &lt;Unit>   // ToDo is this always true?
     * @return a Task list.
     */
    private ArrayList<Task> extractTasks(Connection conn, int UnitId) throws NonRecoverableException {
        ArrayList<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM Task WHERE UnitId = " + UnitId;

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet result = stmt.executeQuery(sql);
            System.out.println("extractTasks: " + result.isClosed());

            while (result.next()) {
      
                tasks.add(extractTask(conn, UnitId));
                
            }
            
            return tasks;
            
        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO_Err_2" + e.toString(), e);

        }   
        
       
    }
     
    /**
     * Extract and return the task form the given &lt;Task> XML element.
     * @param element a &lt;Task> XML element
     * @return a Task
     */
    private Task extractTask(Connection conn, int UnitId) throws NonRecoverableException {
        String sql = "SELECT * FROM Task WHERE UnitId = " + UnitId;

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet result = stmt.executeQuery(sql);
            System.out.println("extractTask: " + result.isClosed());

            int id = -1; // Declare here for better error reporting below
            Task task = new Task();
            
            while (result.next()) {
                
                id = result.getInt("TaskId");
                
                task = new Task(id);
                
                task.setSequenceId(result.getInt("TaskSequence"));
                
                task.setTitle(result.getString("TaskTitle"));
                
                task.setDescription(result.getString("TaskDescription"));
                               
                task.setExercisedComponentIds(extractKnowledgeComponentsIds(conn, id));
                
                task.setSteps(extractSteps(conn, id));
                
                
  
            }
                
            return task;

            
        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO_Err_2" + e.toString(), e);

        }   
        
 
    }
    
    /**
     * Extract and return a list of child steps from the given parent element.
     * 
     * @param parent a parent XML element containing child &\lt<step type="...">
     *                 elements.
     * @return a List of Step elements, may be empty.
     */
    private ArrayList<Step> extractSteps(Connection conn, int TaskId) throws NonRecoverableException {
        ArrayList<Step> steps = new ArrayList<>();
        String sql = "SELECT * FROM Step WHERE TaskId = " + TaskId;

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet result = stmt.executeQuery(sql);

            while (result.next()) {
      
                steps.add(extractStep(conn, TaskId));
                
            }
            
            return steps;
            
        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO_Err_2" + e.toString(), e);

        }   
        
    }
    
    /**
     * Extract and return a Step from the given &lt;Step> element.
     * 
     * @param element a &lt;Step> element
     * @return a Step 
     */
    private Step extractStep(Connection conn, int TaskId) throws NonRecoverableException {
            
        ArrayList<Step> steps = new ArrayList<>();
        String sql = "SELECT * FROM Step WHERE TaskId = " + TaskId;
    
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet result = stmt.executeQuery(sql);
            int id = -1; // Declare here for better error reporting below
            int sequence;
            StepSubType type;
            Step step = null;
            

            while (result.next()) {
                
                id = result.getInt("StepId");
                
                sequence = result.getInt("StepSequence");

                type = extractStepType(result.getString("StepType"));
                
                step = new Step(id, sequence, type);
                
                step.setTitle(result.getString("StepTitle"));
                
                step.setDescription(result.getString("StepDescription"));
                
                step.setScaffolding(extractScaffolding(result.getString("StepScaffolding")));
                            System.out.println("extractStep: " + result.isClosed());
                
                step.setNotifyTutor(result.getBoolean("StepNotifyTutor"));
                                
                step.setExercisedComponentIds(extractKnowledgeComponentsIds(conn, id));
                

                switch (step.getSubType()) {
                case INFO_MESSAGE -> {
                    String msg = result.getString("StepMsg");
                    InformationStep subStep = new InformationStep();
                    subStep.setMsg(msg);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    step.setData(gson.toJson(subStep));
                    }
            }                
                
            }
        
            return step;
            
        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO_Err_2" + e.toString(), e);

        }  
       
        
    }
    
    /**
     * Extract child &lt;ExercisingLocation> elements from given XML DOM parent 
     * element and return the list of these locations.
     *
     * @param parent an XML DOM Element containing one or more child
     * &lt;ExercisingLocation> node elements.
     * @throws ShaTuException a nonrecoverable exception also see getCause()
     * @return a List of ExercisingElements
     */
    private ArrayList<ExercisingLocation> extractExercisingLocations(Connection conn, int KnowledgeComponentId) 
            throws NonRecoverableException {
        
        ArrayList<ExercisingLocation> locations = new ArrayList<>();
        String sql = "SELECT * FROM ExercisingLocation WHERE KnowledgeComponentId = " + KnowledgeComponentId;

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet result = stmt.executeQuery(sql);
            
            while (result.next()) {
                
                locations.add(extractExercisingLocation(conn, KnowledgeComponentId));

            }
            
        return locations;
            
        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO_Err_2" + e.toString(), e);

        } 
        
       
    }
    
    /**
     * Extract and return the exercising location from the given 
     * &lt;ExercisingLocation> element
     * 
     * @param element an &lt;ExercisingLocation> XML element
     * @return an ExercisingLocation
     * @throws NonRecoverableException 
     */
    private ExercisingLocation extractExercisingLocation(Connection conn, int KnowledgeComponentId) 
            throws NonRecoverableException {
        ExercisingLocation location = new ExercisingLocation();

        String sql = "SELECT * FROM ExercisingLocation WHERE KnowledgeComponentId = " + KnowledgeComponentId;


        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet result = stmt.executeQuery(sql);
            
            while (result.next()) {
                
                location.setCourseId(result.getInt("CourseId"));
                
                location.setUnitId(result.getInt("UnitId"));
                
                location.setTaskId(result.getInt("TaskId"));
                
                location.setStepId(result.getInt("StepId"));
        
            }
                
            return location;

            
        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO_Err_2" + e.toString(), e);

        }   
        
    }
    
    /**
     * Extract and return the BloomLevel specified in the corresponding
     * XML attribute of the given element.
     * 
     * @param element an XML element containing a 'bloomLevel' attribute.
     * @return a BloomLevel enum value.
     */
    private BloomLevel extractBloomLevel(String bloomLevel) {
        switch (bloomLevel) {
   
            case "Knowledge":
                return BloomLevel.KNOWLEDGE;
            case "Comprehension":
                return BloomLevel.COMPREHENSION;
            case "Application":
                return BloomLevel.APPLICATION;
            case "Analysis":
                return BloomLevel.ANALYSIS;
            case "Synthesis":
                return BloomLevel.SYNTHESIS;
            default:
                return BloomLevel.EVALUATION;
        }
    }
    
    /**
     * Extract the type from the given XML step element and return the
     * associated step subtype enum value.
     * 
     * @param stepElement &\lt<step type="..."> XML element.
     * @return a StepSubType
     */
    private StepSubType extractStepType(String stepType) {
        
        switch (stepType) {
            case "Information Message":
                return StepSubType.INFO_MESSAGE;
                
            default:
                String msg = "Unknown step type in CourseDAO: " + stepType; 
                Logger.getLogger(CourseDAO.class.getName()).log(Level.WARNING, msg);
                return StepSubType.DEFAULT;
        }
    }
    
    /**
     * Extract the scaffolding from the given XML task or step element and
     * return the associated scaffold level enum value.
     * 
     * @param element &\lt<task scaffolding="..."> &\lt<step scaffolding="...">
     *         XML element.
     * @return a ScaffoldLevel
     */
    private ScaffoldLevel extractScaffolding(String scaffolding) {
        
        switch (scaffolding) {
            case "None":
                return ScaffoldLevel.NONE;  
            case "Low":
                return ScaffoldLevel.LOW;
            case "Medium":
                return ScaffoldLevel.MEDIUM;
            case "High":
                return ScaffoldLevel.HIGH;
            case "Extreme":
                return ScaffoldLevel.EXTREME;
                
            default:
                String msg = "Unknown step scaffoling in CourseDAO: " + scaffolding; 
                Logger.getLogger(CourseDAO.class.getName()).log(Level.WARNING, msg);
                return ScaffoldLevel.NONE;
        }
    }
    
    /**
     * Extract and return the task selection from the given &lt;Course> element.
     * 
     * @param element a &lt;Course>
     * @return a TaskSelectionKind
     */
    private TaskSelectionKind extractPrimaryPedagogy(String pedagogy) {
       // String pedagogy = XmlMgr.getAttribute(element, "primaryPedagogy");
        
        String errMsg = "";
        
        switch (pedagogy) {
            case "Student Choice":
                return TaskSelectionKind.STUDENT_CHOICE;
                
            case "Fixed Sequence":
                return TaskSelectionKind.FIXED_SEQUENCE;
                
            case "Mastery Learning":
                return TaskSelectionKind.MASTERY_LEARNING;
               
            case "Microadaptation":
                return TaskSelectionKind.MICROADAPTATION;
                
            case "Other":
                // OTHER is used in an KnowledgeComponent Outcome to indicate
                // the task associated with the outcome is selected via another
                // strategy, such as Fixed Sequence in a Unit. Hence, OTHER
                // is not a legal value for an entire course. If an Outcome of
                // a course is used to trigger task select, then the pedagogy
                // should be MICROADAPTATION.
                errMsg = "Illegal pedagogy in CourseDAO (see Note): " + pedagogy; 
                Logger.getLogger(CourseDAO.class.getName()).log(Level.WARNING, errMsg);
                return TaskSelectionKind.OTHER;
           
            default:
                errMsg = "Unknown pedagogy in CourseDAO: " + pedagogy; 
                Logger.getLogger(CourseDAO.class.getName()).log(Level.WARNING,errMsg);
                return TaskSelectionKind.ERROR;
        }
    }
    
}
