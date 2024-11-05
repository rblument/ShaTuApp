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
package edu.regis.shatu.svc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.regis.shatu.dao.CourseDAO;
import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.User;
import edu.regis.shatu.model.aol.BitOpExample;
import edu.regis.shatu.model.aol.BitOpStep;
import edu.regis.shatu.model.Course;
import edu.regis.shatu.model.CourseDigest;
import edu.regis.shatu.model.InformationStep;
import edu.regis.shatu.model.aol.EncodeAsciiExample;
//import edu.regis.shatu.model.aol.EncodeAsciiStep;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.aol.StepSubType;
//import edu.regis.shatu.model.Task;
import edu.regis.shatu.model.aol.TaskKind;
import edu.regis.shatu.model.Unit;
import edu.regis.shatu.model.UnitDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Order;

/**
 * Unit test for the ShaTu tutor object.
 * 
 * See: testNewExample()
 *
 * @author rickb
 */
public class ShaTuTutorTest {
    /**
     * The Gson instance used by the unit tests.
     */
    private static Gson GSON_INS;

    /**
     * The ShaTu tutor instance used by the unit tests.
     */
    //private static ShaTuTutor TUTOR_INS;
    
    private  TutoringSession session;

    /**
     * Initialize the GSON_INS and TUTOR_INS instances.
     */
    @BeforeAll
    public static void setUpClass() {
        System.out.println("BeforeAll setUpClass");
        GSON_INS = new GsonBuilder().setPrettyPrinting().create();
        //TUTOR_INS = new ShaTuTutor();
       
    }
    
    // This should work, but this test isn't executed before testNewExample
/*
    @Test
    @Order(1)
    public void testSignIn() {
        System.out.println("Testing signIn");
        
        signIn();
        
    }

*/
    /**
     * Test a NEW_EXAMPLE request for an ASCII_ENCODE example, which also
     * tests that SIGN_IN is working.
     */
    /*
    @Test
    // @Order(2) 
    public void testAsciiEncode() {
        System.out.println("Testing newExample");

        // ToDo: If @Order works, this isn't needed.
        signIn();

        EncodeAsciiExample example = new EncodeAsciiExample();
        example.setStringLength(4);

        NewExampleRequest exampleRequest = new NewExampleRequest();
        exampleRequest.setExampleType(ExampleType.ASCII_ENCODE);
        exampleRequest.setData(GSON_INS.toJson(example));

        ClientRequest request = new ClientRequest();
        request.setRequest(ServerRequestType.NEW_EXAMPLE);
        System.out.println("SessionIns: " + session);
        
        request.setUserId(session.getAccount().getUserId());
        request.setSessionId(session.getSecurityToken());
        request.setData(GSON_INS.toJson(exampleRequest)); // Request type specific object

        System.out.println("Sending NEW_EXAMPLE ASCII_ENCODE request to tutor");
        TutorReply reply = TUTOR_INS.request(request);
        assertNotNull(reply);
        assertEquals(":Success", reply.getStatus());

        System.out.println("Sucessful reply received");

        Task task = GSON_INS.fromJson(reply.getData(), Task.class);
        assertNotNull(task);
        assertEquals(TaskKind.PROBLEM, task.getTaskType());

        Step step = task.currentStep();
        assertNotNull(step);

        assertEquals(StepSubType.ENCODE_ASCII, step.getSubType());

        EncodeAsciiStep subStep = GSON_INS.fromJson(step.getData(), EncodeAsciiStep.class);
        assertNotNull(subStep);

        example = subStep.getExample();
        assertNotNull(example);

        System.out.println("Test was successful");
    }
    */
    
    /*
    @Test
    // @Order(3) 
    public void testAddOneBit() {
        System.out.println("Testing addOneBitExample");

        // ToDo: If @Order works, this isn't needed.
        signIn();

        // Create the request and then send it to the tutor.
        BitOpExample example = new BitOpExample();
        example.setPreSize(8);

        NewExampleRequest exampleRequest = new NewExampleRequest();
        exampleRequest.setExampleType(ExampleType.ADD_ONE_BIT);
        exampleRequest.setData(GSON_INS.toJson(example));

        ClientRequest request = new ClientRequest();
        request.setRequest(ServerRequestType.NEW_EXAMPLE);
        
        request.setUserId(session.getAccount().getUserId());
        request.setSessionId(session.getSecurityToken());
        request.setData(GSON_INS.toJson(exampleRequest)); // Request type specific object
        
        
        TutorReply reply = TUTOR_INS.request(request);
        
        // Process the tutor's reply
        assertNotNull(reply);
        assertEquals(":Success", reply.getStatus());

        Task task = GSON_INS.fromJson(reply.getData(), Task.class);
        assertNotNull(task);
        assertEquals(TaskKind.PROBLEM, task.getTaskType());

        Step step = task.currentStep();
        assertNotNull(step);

        assertEquals(StepSubType.ADD_ONE_BIT, step.getSubType());

        BitOpStep subStep = GSON_INS.fromJson(step.getData(), BitOpStep.class);
        assertNotNull(subStep);

        example = subStep.getExample();
        assertNotNull(example);

        System.out.println("Test was successful");
    }
    
    /*
       @Test
    // @Order(3) 
    public void testXorBits() {
        System.out.println("Testing XorBitsExample");

        // ToDo: If @Order works, this isn't needed.
        signIn();

        // Create the request and then send it to the tutor.
        BitOpExample example = new BitOpExample();
        example.setPreSize(8);

        NewExampleRequest exampleRequest = new NewExampleRequest();
        exampleRequest.setExampleType(ExampleType.XOR_BITS);
        exampleRequest.setData(GSON_INS.toJson(example));

        ClientRequest request = new ClientRequest();
        request.setRequest(ServerRequestType.NEW_EXAMPLE);
        
        request.setUserId(session.getAccount().getUserId());
        request.setSessionId(session.getSecurityToken());
        request.setData(GSON_INS.toJson(exampleRequest)); // Request type specific object
        
        
        TutorReply reply = TUTOR_INS.request(request);
        
        // Process the tutor's reply
        assertNotNull(reply);
        assertEquals(":Success", reply.getStatus());

        Task task = GSON_INS.fromJson(reply.getData(), Task.class);
        assertNotNull(task);
        assertEquals(TaskKind.PROBLEM, task.getTaskType());

        Step step = task.currentStep();
        assertNotNull(step);

        assertEquals(StepSubType.XOR_BITS, step.getSubType());

        BitOpStep subStep = GSON_INS.fromJson(step.getData(), BitOpStep.class);
        assertNotNull(subStep);

        example = subStep.getExample();
        assertNotNull(example);

        System.out.println("Test was successful");
    }
    */
    
         /*
           @Test
    // @Order(3) 
      
    public void testAddBits() {
        System.out.println("Testing AddBitsExample");

        // ToDo: If @Order works, this isn't needed.
        signIn();

        // Create the request and then send it to the tutor.
        BitOpExample example = new BitOpExample();
        example.setPreSize(8);

        NewExampleRequest exampleRequest = new NewExampleRequest();
        exampleRequest.setExampleType(ExampleType.ADD_BITS);
        exampleRequest.setData(GSON_INS.toJson(example));

        ClientRequest request = new ClientRequest();
        request.setRequest(ServerRequestType.NEW_EXAMPLE);
        
        request.setUserId(session.getAccount().getUserId());
        request.setSessionId(session.getSecurityToken());
        request.setData(GSON_INS.toJson(exampleRequest)); // Request type specific object
        
        
        TutorReply reply = TUTOR_INS.request(request);
        
        // Process the tutor's reply
        assertNotNull(reply);
        assertEquals(":Success", reply.getStatus());

        Task task = GSON_INS.fromJson(reply.getData(), Task.class);
        assertNotNull(task);
        assertEquals(TaskKind.PROBLEM, task.getTaskType());

        Step step = task.currentStep();
        assertNotNull(step);

        assertEquals(StepSubType.ADD_BITS, step.getSubType());

        BitOpStep subStep = GSON_INS.fromJson(step.getData(), BitOpStep.class);
        assertNotNull(subStep);

        example = subStep.getExample();
        assertNotNull(example);

        System.out.println("Test was successful");
    }
           */
           
    /*
        @Test
    // @Order(3) 
     
    public void testCourseDAO() {
        System.out.println("Testing CourseDAO");
        CourseDAO dao = new CourseDAO();
        
        try {
            int id = 1;
            Course course = dao.findById(id);
            assertNotNull(course);
        
            System.out.println("Course: " + course.getTitle());
            
            List<Unit> units = course.getUnits();
            assertNotNull(units);
            System.out.println("units.size " + units.size());
            for (Unit unit : units) {
                System.out.println("Unit: " + unit.getTitle());
            }
          
            
        } catch (ObjNotFoundException e) {
            System.out.println("Course 1 not found");
            fail();
        } catch (NonRecoverableException ex) {
            Logger.getLogger(ShaTuTutorTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    
      
    }
*/
    /*
    @Test
    public void testFlow() {
        TutoringSession session = signIn();
        
        CourseDigest courseDigest = session.getCourse();
        assertNotNull(courseDigest);
        System.out.println("Course: " + courseDigest.getTitle());
        System.out.println("  " + courseDigest.getDescription());
        
        UnitDigest unitDigest = session.getUnit();
        assertNotNull(unitDigest);
        System.out.println("Unit: " + unitDigest.getTitle());
        System.out.println("   " + unitDigest.getDescription());
        
        Task task = session.currentTask();
        assertNotNull(task);
        
        Step step = task.getCurrentStep();
        assertNotNull(step);
        
        switch (step.getSubType()) {
            case INFO_MESSAGE:
                System.out.println("Infor Msg");
                InformationStep iStep = GSON_INS.fromJson(step.getData(), InformationStep.class);
                System.out.println("Msg: " + iStep.getMsg());
        }
        
        
    }
    */
    private TutoringSession signIn() {
        String userId = "test@regis.edu";

        User user = new User(userId, SHA_256.instance().sha256("TestP@ss"));

        // First we need to sign in to obtain the session security token.
        ClientRequest request = new ClientRequest(ServerRequestType.SIGN_IN);
        request.setData(GSON_INS.toJson(user));

        //TutorReply reply = TUTOR_INS.request(request);

        //assertEquals("Authenticated", reply.getStatus());

        //session = GSON_INS.fromJson(reply.getData(), TutoringSession.class);

        assertNotNull(session);
        assertEquals(userId, session.getAccount().getUserId());
        
        return session;
    }
}
