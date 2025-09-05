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

<<<<<<< HEAD
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
import edu.regis.shatu.svc.CourseSvc;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
=======
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.regis.shatu.err.InconsistentDBException;
import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;
import edu.regis.shatu.err.ShaTuException;
import edu.regis.shatu.model.BloomLevel;
import edu.regis.shatu.model.Course;
import edu.regis.shatu.model.CourseDigest;
import edu.regis.shatu.model.ExercisingLocation;
import edu.regis.shatu.model.Hint;
import edu.regis.shatu.model.KnowledgeComponent;
import edu.regis.shatu.model.Task;
import edu.regis.shatu.model.TaskSelectionKind;
import edu.regis.shatu.model.Unit;
import edu.regis.shatu.model.UnitDigest;
import edu.regis.shatu.model.aol.OutcomeGranularity;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.aol.TaskKind;
import edu.regis.shatu.model.aol.Timeout;
import edu.regis.shatu.model.steps.Step;
import edu.regis.shatu.svc.CourseSvc;
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3

/**
 * An XML-based Data Access Object implementing CourseSvc behaviors.
 *
 * @author rickb
 */
<<<<<<< HEAD
public class CourseDAO implements CourseSvc {
=======
public class CourseDAO extends MySqlDAO implements CourseSvc {
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3

    /**
     * Instantiate this Course DAO with default values.
     */
    public CourseDAO() {
<<<<<<< HEAD
=======
        super("Course", "Id");
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }

    /**
     * {@inheritDoc}
     */
    @Override
<<<<<<< HEAD
    public Course retrieve(int id) throws ObjNotFoundException, NonRecoverableException {
        String fileName = "Course_" + id + ".xml";

        try {
            XmlMgr xmlMgr = XmlMgr.instance();
            Element root = xmlMgr.findRoot(fileName);

            Course course = new Course(id);

            course.setTitle(XmlMgr.getAttribute(root, "title"));

            course.setDescription(XmlMgr.getChild(root, "Description").getTextContent());
            
            course.setPrimaryPedagogy(extractPrimaryPedagogy(root));

            course.setUnits(extractUnits(root));

            course.setOutcomes(extractKnowledgeComponents(root));
            System.out.println("CourseDAO outcomes: " + course.getOutcomes());
            System.out.println("size: " + course.getOutcomes().size());
            
            return course;

        } catch (ObjNotFoundException e) {
            throw new ObjNotFoundException(String.valueOf(id));
        } catch (XmlException e) {
            throw new NonRecoverableException("CourseDAO_Err_1", e);
=======
    public Course retrieve(int courseId) throws ObjNotFoundException, NonRecoverableException {
        final String sql = "SELECT Title,PrimaryPedagogy,Description FROM Course WHERE Id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DriverManager.getConnection(URL);
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, courseId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Course course = new Course(courseId);

                course.setTitle(rs.getString(1));
                course.setPrimaryPedagogy(TaskSelectionKind.findValue(rs.getString(2).toUpperCase()));
                course.setDescription(rs.getString(3));
                course.setExercisingLocations(retrieveExercisingLocations(courseId, conn));
                course.setUnits(retrieveUnits(course, conn));
                course.setOutcomes(retrieveKnowledgeComponents(course, conn));

                return course;

            } else {
                throw new ObjNotFoundException("Course Id:" + courseId);
            }
        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO-ERR-1" + e.toString(), e);
        } finally {
            close(conn, stmt);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CourseDigest retrieveDigest(int courseId, Connection conn)
            throws ObjNotFoundException, NonRecoverableException {

        final String sql = "SELECT Title,Description FROM Course WHERE Id = ?";

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, courseId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                CourseDigest digest = new CourseDigest(courseId);

                digest.setTitle(rs.getString(1));
                digest.setDescription(rs.getString(2));

                return digest;

            } else {
                throw new ObjNotFoundException("Course Id:" + courseId);
            }
        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO-ERR-2" + e.toString(), e);
        } finally {
            close(stmt);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnitDigest retrieveUnitDigest(int courseId, int unitId, Connection conn)
            throws ObjNotFoundException, NonRecoverableException {

        final String sql = "SELECT Title,Description FROM Unit WHERE CourseId = ? AND UnitId = ?";

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, courseId);
            stmt.setInt(2, unitId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                UnitDigest digest = new UnitDigest(unitId);

                digest.setTitle(rs.getString(1));
                digest.setDescription(rs.getString(2));

                return digest;

            } else {
                throw new ObjNotFoundException("Unit Id:" + courseId);
            }
        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO-ERR-3" + e.toString(), e);
        } finally {
            close(stmt);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Task retrieveTask(int courseId, int taskId, Connection conn)
            throws ObjNotFoundException, NonRecoverableException {
        final String sql = "SELECT Title,Description,Kind,SequenceIndex,ExampleType,ProblemId FROM Task WHERE CourseId = ? AND TaskId = ?";

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, courseId);
            stmt.setInt(2, taskId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Task task = new Task(taskId);
                task.setTitle(rs.getString(1));
                task.setDescription(rs.getString(2));
                task.setKind(TaskKind.findValue(rs.getString(3)));
                task.setSequenceIndex(rs.getInt(4));

                if (task.getKind() == TaskKind.PROBLEM) {
                    task.setType(ProblemType.findValue(rs.getString(5)));
                    // ToDo: What about problem id? (this is example type)
                }

                task.setSteps(retrieveSteps(courseId, task.getId(), conn));

                return task;

            } else {
                throw new ObjNotFoundException("Course Id:" + courseId);
            }
        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO-ERR-4" + e.toString(), e);
        } finally {
            close(stmt);
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        }
    }

    /**
     * Extract child &lt;Outcome> elements from given XML DOM parent element
     * adding each as a Outcome to the given Course.
     *
     * @param parent an XML DOM Element containing one or more child
<<<<<<< HEAD
     * &lt;Outcome> node elements.
     * @throws ShaTuException a nonrecoverable exception also see getCause()
     * @return a List of KnowledgeComponent outcomes
     */
    private ArrayList<KnowledgeComponent> extractKnowledgeComponents(Element parent) 
            throws NonRecoverableException {
        
        ArrayList<KnowledgeComponent> outcomes = new ArrayList<>();
        
        NodeList nodes = parent.getElementsByTagName("KnowledgeComponent");

        for (int i = 0; i < nodes.getLength(); i++)
            outcomes.add(extractKnowledgeComponent((Element) nodes.item(i)));
       
        return outcomes;
    }

    /**
     * Extract and return the knowledge component outcome the given
     * &lt;KnowledgeComponent> element.
     * 
     * @param element a &lt;KnowledgeComponent> element
     * @return a KnowledgeComponent
     * @throws NonRecoverableException 
     */
    private KnowledgeComponent extractKnowledgeComponent(Element element) throws NonRecoverableException {
        KnowledgeComponent outcome = new KnowledgeComponent(XmlMgr.getIntAttribute(element, "id"));

        outcome.setTitle(XmlMgr.getAttribute(element, "title"));

        outcome.setBloomLevel(extractBloomLevel(element));
        
        if (XmlMgr.getAttribute(element, "focus").equals("Tutor")) {
            outcome.setIsDomainFocus(false); // teaching the tutor/GUI itself
        } else {
            outcome.setIsDomainFocus(true);  // teaching SHA-256 domain
        }
        
        outcome.setExercisingLocations(extractExercisingLocations(element));
        
        return outcome;
    }

    /**
     * Extract and return the units in this course from the &lt;Unit> elements.
     * 
     * @param parent a &lt;Course> element
     * @return a List of Units
     * @throws NonRecoverableException 
     */
    private ArrayList<Unit> extractUnits(Element parent) throws NonRecoverableException {
        ArrayList<Unit> units = new ArrayList<>();

        int id = -1; // Declare here for better error reporting below

        NodeList nodes = parent.getElementsByTagName("Unit");

        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);

            id = XmlMgr.getIntAttribute(element, "id");

            Unit unit = new Unit(id);

            unit.setSequenceId(XmlMgr.getIntAttribute(element, "sequence"));

            unit.setTitle(XmlMgr.getAttribute(element, "title"));

            unit.setDescription(XmlMgr.contentText(element, "Description"));
            
            unit.setTasks(extractTasks(element));

            units.add(unit);
        }

        return units;

    }

    /**
     * Extract and create a problem outcome from the given element.
     *
     * @param element an &lt;Outcome> with type "Problem"
     * @return
     * @throws XmlException
     */
   /* private Outcome extractProblem(Element element) throws XmlException {
        int id = XmlMgr.getIntAttribute(element, "id");

        ProblemOutcome outcome = new ProblemOutcome(id);

        outcome.setSequenceId(XmlMgr.getIntAttribute(element, "sequenceId"));
        outcome.setProblemId(XmlMgr.getIntAttribute(element, "problemId"));

        return outcome;
    }
    */

    /**
     * Extract and return the tasks in the given &lt;Unit> element
     * 
     * @param parent &lt;Unit>   // ToDo is this always true?
     * @return a Task list.
     */
    private ArrayList<Task> extractTasks(Element parent) {
        ArrayList<Task> tasks = new ArrayList<>();
        
        for (Element child : XmlMgr.getChildren(parent, "Task"))
            tasks.add(extractTask(child));
        
        // Sort tasks by sequence order
        /*
        for (int i = 0; i < tasks.size() - 1; i++) {
            int minIdx = i;
            int minSeq = tasks.get(i).getSequenceId();
                
            for (int j = i + 1; j < tasks.size(); j++)   
                if (tasks.get(j).getSequenceId() < minSeq)
                    minIdx = j;
        
                
            if (i != minIdx) { // swap
                Task tmp = tasks.get(i);
                tasks.set(i, tasks.get(minIdx));
                tasks.set(minIdx, tmp);
            }
        }
        */
            
        return tasks;
    }
     
    /**
     * Extract and return the task form the given &lt;Task> XML element.
     * @param element a &lt;Task> XML element
     * @return a Task
     */
    private Task extractTask(Element element) {
        Task task = new Task(XmlMgr.getIntAttribute(element, "id"));
        task.setTitle(XmlMgr.getAttribute(element, "title"));
        task.setDescription(XmlMgr.contentText(element, "Description"));
        
        task.setSteps(extractSteps(element));
        
        NodeList nodes = element.getElementsByTagName("ExcercisedComponent");
            for (int i = 0; i < nodes.getLength(); i++)
                task.addExercisedComponentId(XmlMgr.getIntAttribute(((Element) nodes.item(i)), "componentId"));
     
        return task;
    }
    
    /**
     * Extract and return a list of child steps from the given parent element.
     * 
     * @param parent a parent XML element containing child &\lt<step type="...">
     *                 elements.
     * @return a List of Step elements, may be empty.
     */
    private ArrayList<Step> extractSteps(Element parent) {
        ArrayList<Step> steps = new ArrayList<>();
            
        for (Element child : XmlMgr.getChildren(parent, "Step")) 
             steps.add(extractStep(child));
        
        return steps;
    }
    
    /**
     * Extract and return a Step from the given &lt;Step> element.
     * 
     * @param element a &lt;Step> element
     * @return a Step 
     */
    private Step extractStep(Element element) {
            Step step = new Step(XmlMgr.getIntAttribute(element, "id"),
                                 XmlMgr.getIntAttribute(element, "sequence"),
                                 extractStepType(element));
         
            step.setTitle(XmlMgr.getAttribute(element, "title"));       
            step.setScaffolding(extractScaffolding(element));
            step.setTimeout(extractTimeout(element));
            step.setNotifyTutor(XmlMgr.getBooleanAttribute(element, "notifyTutor"));
            
            NodeList nodes = element.getElementsByTagName("ExcercisedComponent");
            for (int i = 0; i < nodes.getLength(); i++)
                step.addExercisedComponentId(XmlMgr.getIntAttribute(((Element) nodes.item(i)), "componentId"));
            
            for (Hint hint : extractHints(element))
                step.addHint(hint);

            switch (step.getSubType()) {
                case INFO_MESSAGE:
                    String msg = XmlMgr.contentText(element, "Msg");
                    InformationStep subStep = new InformationStep();
                    subStep.setMsg(msg);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    step.setData(gson.toJson(subStep));
                    break;
            }
            
           return step;
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
    private ArrayList<ExercisingLocation> extractExercisingLocations(Element parent) 
            throws NonRecoverableException {
        
        ArrayList<ExercisingLocation> locations = new ArrayList<>();
        
        NodeList nodes = parent.getElementsByTagName("ExercisingLocation");

        for (int i = 0; i < nodes.getLength(); i++)
            locations.add(extractExercisingLocation((Element) nodes.item(i)));
       
        return locations;
    }
    
    /**
     * Extract and return the exercising location from the given 
     * &lt;ExercisingLocation> element
     * 
     * @param element an &lt;ExercisingLocation> XML element
     * @return an ExercisingLocation
     * @throws NonRecoverableException 
     */
    private ExercisingLocation extractExercisingLocation(Element element) throws NonRecoverableException {
        ExercisingLocation location = new ExercisingLocation();

        location.setCourseId(XmlMgr.getIntAttribute(element, "course"));
        location.setUnitId(XmlMgr.getIntAttribute(element, "unit"));
        location.setTaskId(XmlMgr.getIntAttribute(element, "task"));
        location.setStepId(XmlMgr.getIntAttribute(element, "step"));
        
        return location;
    }
    
    /**
     * Extract and return the BloomLevel specified in the corresponding
     * XML attribute of the given element.
     * 
     * @param element an XML element containing a 'bloomLevel' attribute.
     * @return a BloomLevel enum value.
     */
    private BloomLevel extractBloomLevel(Element element) {
        switch (XmlMgr.getAttribute(element, "bloomLevel")) {
   
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
    private StepSubType extractStepType(Element stepElement) {
        String stepType = XmlMgr.getAttribute(stepElement, "type");
        
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
    private ScaffoldLevel extractScaffolding(Element element) {
        String scaffolding = XmlMgr.getAttribute(element, "scaffolding");
        
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
     * Extract a GUI gesture from the given element.
     * 
     * @param element an
     * @return GuiGesture
     */
    private GuiGesture extractGesture(Element element) {
        String gesture = XmlMgr.getAttribute(element, "gesture");
        
        switch(gesture) {
            case "Request Hint":
                return GuiGesture.REQUEST_HINT;
        
            default:
                String msg = "Unknown step gesture in CourseDAO: " + gesture; 
                Logger.getLogger(CourseDAO.class.getName()).log(Level.WARNING, msg);
                return GuiGesture.NO_OP;
        }
    }
    
    /**
     * Extract and return a list of hints from the given parent element.
     * 
     * @param parent an Element possibly containing child &lt;Hint> elements.
     * @return a List&lt;Hint>
     */
    private ArrayList<Hint> extractHints(Element parent) {
        ArrayList<Hint> hints = new ArrayList<>();

        for (Element child : XmlMgr.getChildren(parent, "Hint")) {
            Hint hint = new Hint(XmlMgr.getIntAttribute(child, "id"));
            hint.setSequenceId(XmlMgr.getIntAttribute(child, "sequence"));
            
            hint.setText(XmlMgr.getContentText(child));
            
            hints.add(hint);
        }
        
        return hints;
    }
    
    /**
     * Extract and return the task selection from the given &lt;Course> element.
     * 
     * @param element a &lt;Course>
     * @return a TaskSelectionKind
     */
    private TaskSelectionKind extractPrimaryPedagogy(Element element) {
        String pedagogy = XmlMgr.getAttribute(element, "primaryPedagogy");
        
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
    
    /**
     * Extract and return, if any, the child &lt;Timeout> element contained
     * within the given parent XML element
     * 
     * @param parent
     * @return a Timeout or null
     */
    private Timeout extractTimeout(Element parent) {
        Element element = XmlMgr.getChild(parent, "Timeout");
        
        if (element == null) {
            return null;
        } else {
 
            return new Timeout(XmlMgr.getAttribute(element, "type"),
                               XmlMgr.getIntAttribute(element, "seconds"),
                               XmlMgr.getAttribute(element, "event"),
                               XmlMgr.getAttribute(element, "text"));
        }  
=======
     *               &lt;Outcome> node elements.
     * @throws ShaTuException a nonrecoverable exception also see getCause()
     * @return a List of KnowledgeComponent outcomes
     */
    private ArrayList<KnowledgeComponent> retrieveKnowledgeComponents(Course course, Connection conn)
            throws NonRecoverableException {

        final String sql = "SELECT Id, Title, Description, BloomLevel, IsDomainFocus, Pedagogy, ExercisingLocations, Granularity FROM KnowledgeComponent WHERE CourseId = ?";

        ArrayList<KnowledgeComponent> outcomes = new ArrayList<>();

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, course.getId());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                KnowledgeComponent comp = new KnowledgeComponent(rs.getInt(1));
                comp.setTitle(rs.getString(2));
                comp.setDescription(rs.getString(3));
                comp.setBloomLevel(BloomLevel.findValue(rs.getString(4)));
                comp.setIsDomainFocus(rs.getBoolean(5));
                comp.setPedagogy(TaskSelectionKind.findValue(rs.getString(6)));

                comp.setGranularity(OutcomeGranularity.findValue(rs.getString(8)));

                // Link the exercising locations in this outcome to those in the course.
                ArrayList<ExercisingLocation> locations = new ArrayList<>();
                String[] ids = rs.getString(7).split(",");
                for (int i = 0; i < ids.length; i++)
                    locations.add(course.findLocation(i));

                comp.setExercisingLocations(locations);

                outcomes.add(comp);
            }

            return outcomes;

        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO-ERR-5" + e.toString(), e);
        } finally {
            close(stmt);
        }
    }

    /**
     * Return the units in this course from the &lt;Unit> elements.
     * 
     * @param parent a &lt;Course> element
     * @return a List of Units
     * @throws NonRecoverableException
     */
    private ArrayList<Unit> retrieveUnits(Course course, Connection conn) throws NonRecoverableException {
        final String sql = "SELECT UnitId,Title,Description,SequenceIndex,Pedagogy FROM Unit WHERE CourseId = ?";

        ArrayList<Unit> units = new ArrayList<>();

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, course.getId());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Unit unit = new Unit(rs.getInt(1));
                unit.setTitle(rs.getString(2));
                unit.setDescription(rs.getString(3));
                unit.setSequenceId(rs.getInt(4));
                unit.setPedagogy(TaskSelectionKind.findValue(rs.getString(5)));
                unit.setTasks(retrieveTasks(course, unit, conn));

                units.add(unit);
            }

            return units;
        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO-ERR-6" + e.toString(), e);
        } finally {
            close(stmt); // Don't close the connection, retrieve(courseId) will
        }
    }

    /**
     * Extract and return the tasks in the given &lt;Unit> element
     * 
     * @param parent &lt;Unit> // ToDo is this always true?
     * @return a Task list.
     */
    private ArrayList<Task> retrieveTasks(Course course, Unit unit, Connection conn)
            throws NonRecoverableException {
        final String sql = "SELECT TaskId,Title,Description,Kind,SequenceIndex,ExampleType,ProblemId FROM Task WHERE CourseId = ? AND UnitId = ?";

        ArrayList<Task> tasks = new ArrayList<>();

        PreparedStatement stmt = null;

        int courseId = course.getId();

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, courseId);
            stmt.setInt(2, unit.getId());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Task task = new Task(rs.getInt(1));
                task.setTitle(rs.getString(2));
                task.setDescription(rs.getString(3));
                task.setKind(TaskKind.findValue(rs.getString(4)));
                task.setSequenceIndex(rs.getInt(5));

                if (task.getKind() == TaskKind.PROBLEM) {
                    task.setType(ProblemType.findValue(rs.getString(6)));
                    // ToDo: What about problem id? (this is example type)
                }

                tasks.add(task);

                task.setSteps(retrieveSteps(courseId, task.getId(), conn));

                // ToDo: retrieve exercising locations
            }

            return tasks;
        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO-ERR-7" + e.toString(), e);
        } finally {
            close(stmt); // Don't close the connection, retrieve(courseId) will
        }

    }

    /**
     * Extract and return a list of child steps from the given parent element.
     * 
     * @param courseId
     * @param taskId
     * @param conn     an open connection to the DB, which isn't closed by this
     *                 method.
     * @return a List of Step elements, may be empty.
     */
    private ArrayList<Step> retrieveSteps(int courseId, int taskId, Connection conn)
            throws NonRecoverableException {
        final String sql = "SELECT Id,Title,Description,SequenceIndex,StepSubType,SubTypeId,TimeoutId FROM Step WHERE CourseId = ? AND TaskId = ?";

        ArrayList<Step> steps = new ArrayList<>();

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, courseId);
            stmt.setInt(2, taskId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                StepSubType subType = StepSubType.findValue(rs.getString(5));

                Step step = new Step(rs.getInt(1), rs.getInt(4), subType);

                step.setTitle(rs.getString(1));
                step.setDescription(rs.getString(2));
                step.setTimeout(retrieveTimeout(rs.getInt(7), conn));

                extractStepSubTypeData(subType, rs.getInt(6), conn);

                // ToDo retrieve exercising locations

                steps.add(step);

                step.setHints(retrieveHints(step.getId(), conn));
            }

            return steps;
        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO-ERR-8" + e.toString(), e);
        } finally {
            close(stmt); // Don't close the connection, retrieve(courseId) will
        }
    }

    /**
     * Extract and return a list of hints from the given parent element.
     * 
     *
     * @param conn an open connection to the DB, which isn't closed by this method.
     * @return an ArrayList of Hint
     */
    private ArrayList<Hint> retrieveHints(int stepId, Connection conn)
            throws NonRecoverableException {

        final String sql = "SELECT Id,Text,SequenceIndex FROM Hint WHERE StepId = ?";

        ArrayList<Hint> hints = new ArrayList<>();

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, stepId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Hint hint = new Hint(rs.getInt(1));
                hint.setText(rs.getString(2));
                hint.setSequenceIndex(rs.getInt(3));

                hints.add(hint);
            }

            return hints;

        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO-ERR-9" + e.toString(), e);
        } finally {
            close(stmt); // Don't close the connection, retrieve(courseId) will
        }
    }

    /**
     * 
     * @param subType
     * @param subTypeId index into the appropriate table determined by subType
     * @return
     */
    private String extractStepSubTypeData(StepSubType subType, int subTypeId, Connection conn)
            throws NonRecoverableException {
        switch (subType) {
            case INFO_MESSAGE:
                return extractInfoMsgData(subTypeId, conn);
            case GUI_ACTION:
                return ""; // TBD
            case ENCODE_BINARY:
                return ""; // TBD
            case ENCODE_HEX:
                return ""; // TBD
            case ENCODE_ASCII:
                return ""; // TBD
            case ADD_ONE_BIT:
                return ""; // TBD
            case PAD_ZEROS:
                return ""; // TBD
            case ADD_MSG_LENGTH:
                return ""; // TBD
            case PREPARE_SCHEDULE:
                return ""; // TBD
            case INITIALIZE_VARS:
                return ""; // TBD
            case COMPRESS_ROUND:
                return ""; // TBD
            case ROTATE_BITS:
                return ""; // TBD
            case SHIFT_BITS:
                return ""; // TBD
            case XOR_BITS:
                return ""; // TBD
            case ADD_BITS:
                return ""; // TBD
            case MAJORITY_FUNCTION:
                return ""; // TBD
            case CHOICE_FUNCTION:
                return ""; // TBD
            case STEP_COMPLETION_REPLY:
                return ""; // TBD
            case REQUEST_HINT:
                return ""; // TBD
            case SHA_ONE:
                return "";
            case SHA_ZERO:
                return "";
            default:
                return "";
        }
    }

    /**
     * 
     * The data is a POJO String object
     */
    private String extractInfoMsgData(int subTypeId, Connection conn) throws NonRecoverableException {
        final String sql = "SELECT Text FROM InfoMsgStep WHERE SubStepId = ?";

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, subTypeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString(1);
            } else {
                String errMsg = "ERROR: ToDo: Throw database inconsistency InfoMsgStep table: " + subTypeId;
                throw new NonRecoverableException(errMsg, new InconsistentDBException(errMsg));
            }
        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO-ERR-10" + e.toString(), e);
        } finally {
            close(stmt); // Don't close the connection, retrieve(courseId) will
        }
    }

    private Timeout retrieveTimeout(int timeoutId, Connection conn)
            throws NonRecoverableException {
        final String sql = "SELECT TimeoutType,Seconds,Event,Msg FROM Timeout WHERE Id = ?";

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, timeoutId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Timeout timeout = new Timeout(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getString(4));
                return timeout;
            } else {
                // ToDo: throw a dabase inconsistency error
                String errMsg = "Timeout not found, id: " + timeoutId;
                throw new NonRecoverableException(errMsg, new InconsistentDBException(errMsg));
            }
        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO-ERR-11" + e.toString(), e);
        } finally {
            close(stmt); // Don't close the connection, retrieve(courseId) will
        }
    }

    /**
     * Extract child &lt;ExercisingLocation> elements from given XML DOM parent
     * element and return the list of these locations.
     *
     * @param parent an XML DOM Element containing one or more child
     *               &lt;ExercisingLocation> node elements.
     * @throws ShaTuException a nonrecoverable exception also see getCause()
     * @return a List of ExercisingElements
     */
    private ArrayList<ExercisingLocation> retrieveExercisingLocations(int courseId, Connection conn)
            throws NonRecoverableException {

        final String sql = "SELECT Id, UnitId, TaskId, StepId FROM ExercisingLocation WHERE CourseId = ?";

        ArrayList<ExercisingLocation> locations = new ArrayList<>();

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, courseId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ExercisingLocation location = new ExercisingLocation(rs.getInt(1));
                location.setCourseId(courseId);
                location.setUnitId(rs.getInt(2));
                location.setTaskId(rs.getInt(3));
                location.setStepId(rs.getInt(4));

                locations.add(location);
            }

            return locations;

        } catch (SQLException e) {
            throw new NonRecoverableException("CourseDAO-ERR-12" + e.toString(), e);
        } finally {
            close(stmt); // Don't close the connection, retrieve(courseId) will
        }
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }
}
