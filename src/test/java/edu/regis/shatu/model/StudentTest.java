package edu.regis.shatu.model;

import org.junit.jupiter.api.Test;           
import org.junit.jupiter.api.BeforeEach;     
import org.junit.jupiter.api.DisplayName;    
import org.junit.jupiter.api.Nested;         
import static org.junit.jupiter.api.Assertions.*;  

import edu.regis.shatu.model.aol.StudentModel;  

/**
 * Unit tests for the Student class.
 * 
 * Tests verify that:
 * - Constructor properly initializes a Student with an Account
 * - Getter and setter methods work correctly
 * - The Student can track login/logout times
 * - The toString method returns expected format
 * 
 */
@DisplayName("Student Class Tests") 
public class StudentTest {
    
    private Student student;
    private Account testAccount;
    
    /**
     * Set up test objects before each test method runs.
     */
    @BeforeEach
    public void setUp() {
        testAccount = new Account("teststudent@regis.edu");
        student = new Student(testAccount);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        /**
         * Test that constructor properly stores the Account
         */
        @Test
        @DisplayName("Constructor should store the provided Account")
        public void testConstructorStoresAccount() {
            Student newStudent = new Student(testAccount);
            assertNotNull(newStudent, "Student should be created");
            assertNotNull(newStudent.getAccount(), "Student should have an account");
            assertEquals(testAccount, newStudent.getAccount(), 
                "Student should return the same Account we passed in");
        }
        
        /**
         * Test that constructor initializes StudentModel
         */
        @Test
        @DisplayName("Constructor should initialize StudentModel")
        public void testConstructorInitializesStudentModel() {
            assertNotNull(student.getStudentModel(), 
                "Constructor should create a StudentModel");
        }
        
        /**
         * Test that constructor initializes login times to 0
         * 
         */
        @Test
        @DisplayName("Constructor should initialize login times to 0")
        public void testConstructorInitializesLoginTimes() {
            assertEquals(0L, student.getLastLogin(), 
                "lastLogin should be initialized to 0");
            assertEquals(0L, student.getLastLogout(), 
                "lastLogout should be initialized to 0");
        }
    }
    
    //Test the getter methods
    @Nested
    @DisplayName("Getter Method Tests")
    class GetterTests {
        
        /**
         * Test getAccount returns the correct Account
         */
        @Test
        @DisplayName("getAccount should return the Account used in constructor")
        public void testGetAccount() {
            Account returnedAccount = student.getAccount();
            assertNotNull(returnedAccount, "getAccount should not return null");
            assertEquals(testAccount, returnedAccount, 
                "getAccount should return the Account from constructor");
            assertEquals("teststudent@regis.edu", returnedAccount.getUserId(),
                "Account should have the correct userId");
        }
        
        /**
         * Test getStudentModel returns a StudentModel
         */
        @Test
        @DisplayName("getStudentModel should return a StudentModel")
        public void testGetStudentModel() {
            // Arrange & Act: Student created in setUp
            StudentModel model = student.getStudentModel();
            
            // Assert: Should exist
            assertNotNull(model, "getStudentModel should not return null");
        }
        
        /**
         * Test getLastLogin returns the login time
         */
        @Test
        @DisplayName("getLastLogin should return the last login time")
        public void testGetLastLogin() {
            long expectedTime = 1234567890L;  
            student.setLastLogin(expectedTime);
            long actualTime = student.getLastLogin();
            assertEquals(expectedTime, actualTime, 
                "getLastLogin should return the time we set");
        }
        
        /**
         * Test getLastLogout returns the logout time
         */
        @Test
        @DisplayName("getLastLogout should return the last logout time")
        public void testGetLastLogout() {
            long expectedTime = 9876543210L;
            student.setLastLogout(expectedTime);
            long actualTime = student.getLastLogout();
            assertEquals(expectedTime, actualTime, 
                "getLastLogout should return the time we set");
        }
    }
    
    // Test the setter methods
    @Nested
    @DisplayName("Setter Method Tests")
    class SetterTests {
        
        /**
         * Test setStudentModel changes the model
         */
        @Test
        @DisplayName("setStudentModel should update the StudentModel")
        public void testSetStudentModel() {
            StudentModel newModel = new StudentModel("differentuser@regis.edu");
            student.setStudentModel(newModel);
            assertEquals(newModel, student.getStudentModel(), 
                "setStudentModel should store the new model");
        }
        
        /**
         * Test setLastLogin changes the login time
         */
        @Test
        @DisplayName("setLastLogin should update the last login time")
        public void testSetLastLogin() {
            long expectedTime = System.currentTimeMillis(); 
            student.setLastLogin(expectedTime);
            assertEquals(expectedTime, student.getLastLogin(), 
                "setLastLogin should store the time");
        }
        
        /**
         * Test setLastLogout changes the logout time
         */
        @Test
        @DisplayName("setLastLogout should update the last logout time")
        public void testSetLastLogout() {
            long expectedTime = System.currentTimeMillis();
            student.setLastLogout(expectedTime);
            assertEquals(expectedTime, student.getLastLogout(), 
                "setLastLogout should store the time");
        }
    }
    
    // Test edge cases and special scenarios
    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {
        
        /**
         * Test with very large timestamp values
         */
        @Test
        @DisplayName("Should handle very large timestamp values")
        public void testLargeTimestamps() {
            long maxTime = Long.MAX_VALUE;
            student.setLastLogin(maxTime);
            student.setLastLogout(maxTime);
            
            assertEquals(maxTime, student.getLastLogin(), 
                "Should handle MAX_VALUE for login");
            assertEquals(maxTime, student.getLastLogout(), 
                "Should handle MAX_VALUE for logout");
        }
        
        /**
         * Test with negative timestamp values (could happen)
         */
        @Test
        @DisplayName("Should handle negative timestamp values")
        public void testNegativeTimestamps() {
            long negativeTime = -1000L;
            student.setLastLogin(negativeTime);
            student.setLastLogout(negativeTime);
            
            assertEquals(negativeTime, student.getLastLogin(), 
                "Should handle negative values for login");
            assertEquals(negativeTime, student.getLastLogout(), 
                "Should handle negative values for logout");
        }
        
        /**
         * Test setting StudentModel to null (edge case)
         */
        @Test
        @DisplayName("Should handle null StudentModel")
        public void testNullStudentModel() {
            student.setStudentModel(null);
            
            assertNull(student.getStudentModel(), 
                "setStudentModel should accept null");
        }
    }
    
    //Test the toString method
    @Nested
    @DisplayName("ToString Method Tests")
    class ToStringTests {
        
        /**
         * Test toString returns expected format
         */
        @Test
        @DisplayName("toString should return 'Student: userId' format")
        public void testToString() {
            String expectedUserId = "teststudent@regis.edu";
            String result = student.toString();
            assertNotNull(result, "toString should not return null");
            assertEquals("Student: " + expectedUserId, result, 
                "toString should return 'Student: userId' format");
            assertTrue(result.contains(expectedUserId), 
                "toString should contain the userId");
            assertTrue(result.startsWith("Student: "), 
                "toString should start with 'Student: '");
        }
        
        /**
         * Test toString with different Account
         */
        @Test
        @DisplayName("toString should reflect different Account userId")
        public void testToStringWithDifferentAccount() {
            Account differentAccount = new Account("another@regis.edu");
            Student differentStudent = new Student(differentAccount);
            String result = differentStudent.toString();
            assertEquals("Student: another@regis.edu", result, 
                "toString should use the Account's userId");
        }
    }
    
    //Integration tests - test multiple features together
    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        /**
         * Test complete Student lifecycle
         */
        @Test
        @DisplayName("Should handle complete student lifecycle")
        public void testCompleteStudentLifecycle() {
            Account fullAccount = new Account("john.doe@regis.edu", "hashedPass");
            fullAccount.setFirstName("John");
            fullAccount.setLastName("Doe");
            
            Student fullStudent = new Student(fullAccount);
            
            long loginTime = System.currentTimeMillis();
            fullStudent.setLastLogin(loginTime);
            
            try {
                Thread.sleep(10); 
            } catch (InterruptedException e) {

            }
            
            long logoutTime = System.currentTimeMillis();
            fullStudent.setLastLogout(logoutTime);
            
            assertEquals(fullAccount, fullStudent.getAccount());
            assertEquals(loginTime, fullStudent.getLastLogin());
            assertEquals(logoutTime, fullStudent.getLastLogout());
            assertTrue(logoutTime >= loginTime, 
                "Logout should be after or equal to login");
            assertNotNull(fullStudent.getStudentModel());
        }
        
        /**
         * Test multiple operations don't interfere
         */
        @Test
        @DisplayName("Multiple operations should not interfere with each other")
        public void testMultipleOperations() {
            long time1 = 1000L;
            student.setLastLogin(time1);
            
            StudentModel newModel = new StudentModel("test@regis.edu");
            student.setStudentModel(newModel);
            
            long time2 = 2000L;
            student.setLastLogout(time2);
            
            assertEquals(time1, student.getLastLogin(), 
                "Login time should not be affected by other operations");
            assertEquals(newModel, student.getStudentModel(), 
                "StudentModel should not be affected by other operations");
            assertEquals(time2, student.getLastLogout(), 
                "Logout time should not be affected by other operations");
            assertEquals(testAccount, student.getAccount(), 
                "Account should remain unchanged");
        }
    }
}