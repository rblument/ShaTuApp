package edu.regis.shatu.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the Account class.
 * 
 * This test class demonstrates best practices:
 * - Organized with @Nested classes for grouping related tests
 * - Uses descriptive @DisplayName annotations
 * - Follows the Arrange-Act-Assert pattern
 * - Tests both success and edge cases
 * 
 * 
 */
@DisplayName("Account Class Tests")
public class AccountTest {
    
    private Account account;
    
    /**
     * Set up a fresh Account instance before each test.
     * This ensures tests don't affect each other.
     */
    @BeforeEach
    public void setUp() {
        account = new Account();
    }
    
    /**
     * Group all constructor-related tests together
     */
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Default constructor should initialize with empty values and isStudent=true")
        public void testDefaultConstructor() {

            assertEquals("", account.getUserId(), "UserId should be empty string");
            assertEquals("", account.getPassword(), "Password should be empty string");
            assertEquals(0, account.getSecurityQuestion(), "Security question should be 0");
            assertEquals("", account.getSecurityAnswer(), "Security answer should be empty");
            assertTrue(account.isStudent(), "Default should be student");
        }
        
        @Test
        @DisplayName("Constructor with userId should initialize userId and defaults for other fields")
        public void testConstructorWithUserId() {
            String expectedUserId = "john@regis.edu";
            
            Account newAccount = new Account(expectedUserId);

            assertEquals(expectedUserId, newAccount.getUserId());
            assertEquals("", newAccount.getPassword());
            assertEquals(0, newAccount.getSecurityQuestion());
            assertEquals("", newAccount.getSecurityAnswer());
            assertTrue(newAccount.isStudent());
        }
        
        @Test
        @DisplayName("Constructor with userId and password should initialize both fields")
        public void testConstructorWithUserIdAndPassword() {
            String expectedUserId = "jane@regis.edu";
            String expectedPassword = "hashedPass123";
            
            Account newAccount = new Account(expectedUserId, expectedPassword);
            
            assertEquals(expectedUserId, newAccount.getUserId());
            assertEquals(expectedPassword, newAccount.getPassword());
            assertEquals(0, newAccount.getSecurityQuestion());
            assertEquals("", newAccount.getSecurityAnswer());
            assertTrue(newAccount.isStudent());
        }
        
        @Test
        @DisplayName("Full constructor should initialize all fields correctly")
        public void testFullConstructor() {
            String expectedUserId = "bob@regis.edu";
            String expectedPassword = "secureHash456";
            String expectedFirstName = "Bobby";
            String expectedLastName = "Bobberson";
            int expectedSecQuestion = 2;
            String expectedSecAnswer = "answerHash789";
            
            Account fullAccount = new Account(
                expectedUserId,
                expectedPassword,
                expectedFirstName,
                expectedLastName,
                expectedSecQuestion,
                expectedSecAnswer
            );
            
            assertEquals(expectedUserId, fullAccount.getUserId());
            assertEquals(expectedPassword, fullAccount.getPassword());
            assertEquals(expectedFirstName, fullAccount.getFirstName());
            assertEquals(expectedLastName, fullAccount.getLastName());
            assertEquals(expectedSecQuestion, fullAccount.getSecurityQuestion());
            assertEquals(expectedSecAnswer, fullAccount.getSecurityAnswer());
            assertTrue(fullAccount.isStudent());
        }
    }
    
    /**
     * Group all getter/setter tests together
     */
    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {
        
        @Test
        @DisplayName("setUserId and getUserId should work correctly")
        public void testUserIdGetterSetter() {
            String expectedUserId = "test@regis.edu";
            account.setUserId(expectedUserId);
            String actualUserId = account.getUserId();
            assertEquals(expectedUserId, actualUserId);
        }
        
        @Test
        @DisplayName("setPassword and getPassword should work correctly")
        public void testPasswordGetterSetter() {
            String expectedPassword = "hashedPassword123";
            account.setPassword(expectedPassword);
            String actualPassword = account.getPassword();
            assertEquals(expectedPassword, actualPassword);
        }
        
        @Test
        @DisplayName("setFirstName and getFirstName should work correctly")
        public void testFirstNameGetterSetter() {
            String expectedFirstName = "John";
            account.setFirstName(expectedFirstName);
            String actualFirstName = account.getFirstName();
            assertEquals(expectedFirstName, actualFirstName);
        }
        
        @Test
        @DisplayName("setLastName and getLastName should work correctly")
        public void testLastNameGetterSetter() {
            String expectedLastName = "Doe";
            account.setLastName(expectedLastName);
            String actualLastName = account.getLastName();
            assertEquals(expectedLastName, actualLastName);
        }
        
        @Test
        @DisplayName("setSecurityQuestion and getSecurityQuestion should work correctly")
        public void testSecurityQuestionGetterSetter() {
            int expectedQuestion = 3;
            account.setSecurityQuestion(expectedQuestion);
            int actualQuestion = account.getSecurityQuestion();
            assertEquals(expectedQuestion, actualQuestion);
        }
        
        @Test
        @DisplayName("setSecurityAnswer and getSecurityAnswer should work correctly")
        public void testSecurityAnswerGetterSetter() {
            String expectedAnswer = "hashedAnswer999";
            account.setSecurityAnswer(expectedAnswer);
            String actualAnswer = account.getSecurityAnswer();
            assertEquals(expectedAnswer, actualAnswer);
        }
        
        @Test
        @DisplayName("setIsStudent should update student status")
        public void testSetIsStudent() {
            assertTrue(account.isStudent(), "Should start as student");
            account.setIsStudent(false);
            assertFalse(account.isStudent(), "Should no longer be student");
            account.setIsStudent(true);
            assertTrue(account.isStudent(), "Should be student again");
        }
    }
    
    /**
     * Test edge cases and special scenarios
     */
    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle null values for userId")
        public void testNullUserId() {
            account.setUserId(null);
            assertNull(account.getUserId(), "Should accept and store null");
        }
        
        @Test
        @DisplayName("Should handle empty string for userId")
        public void testEmptyStringUserId() {
            account.setUserId("");
            assertEquals("", account.getUserId(), "Should accept empty string");
        }
        
        @Test
        @DisplayName("Should handle very long userId")
        public void testVeryLongUserId() {
            String longUserId = "a".repeat(500) + "@regis.edu";
            account.setUserId(longUserId);
            assertEquals(longUserId, account.getUserId(), 
                "Should handle long userIds");
        }
        
        @Test
        @DisplayName("Should handle null password")
        public void testNullPassword() {
            account.setPassword(null);
            assertNull(account.getPassword(), "Should accept null password");
        }
        
        @Test
        @DisplayName("Should handle negative security question number")
        public void testNegativeSecurityQuestion() {
            account.setSecurityQuestion(-1);
            assertEquals(-1, account.getSecurityQuestion(), 
                "Should accept negative numbers (though may not be valid in business logic)");
        }
        
        @Test
        @DisplayName("Should handle very large security question number")
        public void testLargeSecurityQuestion() {
            account.setSecurityQuestion(Integer.MAX_VALUE);
            assertEquals(Integer.MAX_VALUE, account.getSecurityQuestion(), 
                "Should accept large numbers");
        }
    }
    
    /**
     * Test special methods
     */
    @Nested
    @DisplayName("Special Method Tests")
    class SpecialMethodTests {
        
        @Test
        @DisplayName("clear() should reset all fields to null or default values")
        public void testClear() {
            account.setUserId("test@regis.edu");
            account.setPassword("password123");
            account.setFirstName("John");
            account.setLastName("Doe");
            account.setSecurityQuestion(1);
            account.setSecurityAnswer("answer123");
            account.setIsStudent(true);

            assertNotNull(account.getUserId(), "UserId should be set before clear");

            account.clear();
            
            assertNull(account.getUserId(), "UserId should be null after clear");
            assertNull(account.getPassword(), "Password should be null after clear");
            assertNull(account.getFirstName(), "FirstName should be null after clear");
            assertNull(account.getLastName(), "LastName should be null after clear");
            assertEquals(0, account.getSecurityQuestion(), 
                "Security question should be 0 after clear");
            assertNull(account.getSecurityAnswer(), 
                "Security answer should be null after clear");
        }
        
        @Test
        @DisplayName("clear() on already empty account should not throw exception")
        public void testClearOnEmptyAccount() {
            assertDoesNotThrow(() -> account.clear(), 
                "Clearing an empty account should not throw exception");
        }
        
        @Test
        @DisplayName("toString() should return formatted string with userId")
        public void testToString() {
            String userId = "test@regis.edu";
            account.setUserId(userId);
            String result = account.toString();
            assertEquals("User: " + userId, result);
            assertTrue(result.contains(userId), "toString should contain userId");
            assertTrue(result.startsWith("User: "), 
                "toString should start with 'User: '");
        }
        
        @Test
        @DisplayName("toString() with null userId should handle gracefully")
        public void testToStringWithNullUserId() {
            account.setUserId(null);
            String result = account.toString();
            assertEquals("User: null", result, 
                "toString should handle null userId gracefully");
        }
        
        @Test
        @DisplayName("toString() with empty userId should return 'User: '")
        public void testToStringWithEmptyUserId() {
            account.setUserId("");
            String result = account.toString();
            assertEquals("User: ", result);
        }
    }
    
    /**
     * Integration tests that combine multiple operations
     */
    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("Should create complete account profile")
        public void testCompleteAccountProfile() {
            String userId = "complete@regis.edu";
            String password = "secureHash";
            String firstName = "Complete";
            String lastName = "User";
            int secQuestion = 2;
            String secAnswer = "answerHash";
            
            account.setUserId(userId);
            account.setPassword(password);
            account.setFirstName(firstName);
            account.setLastName(lastName);
            account.setSecurityQuestion(secQuestion);
            account.setSecurityAnswer(secAnswer);
            account.setIsStudent(false);
            
            assertEquals(userId, account.getUserId());
            assertEquals(password, account.getPassword());
            assertEquals(firstName, account.getFirstName());
            assertEquals(lastName, account.getLastName());
            assertEquals(secQuestion, account.getSecurityQuestion());
            assertEquals(secAnswer, account.getSecurityAnswer());
            assertFalse(account.isStudent());
        }
        
        @Test
        @DisplayName("Should handle multiple updates to same account")
        public void testMultipleUpdates() {
            // First update
            account.setUserId("first@regis.edu");
            assertEquals("first@regis.edu", account.getUserId());
            
            // Second update
            account.setUserId("second@regis.edu");
            assertEquals("second@regis.edu", account.getUserId());
            
            // Third update
            account.setUserId("third@regis.edu");
            assertEquals("third@regis.edu", account.getUserId());
        }
        
        @Test
        @DisplayName("Should maintain data integrity after multiple operations")
        public void testDataIntegrity() {
            account.setUserId("integrity@regis.edu");
            account.setPassword("pass1");
            account.setFirstName("Data");
            
            account.setPassword("pass2");
            
            assertEquals("integrity@regis.edu", account.getUserId(), 
                "UserId should remain unchanged");
            assertEquals("Data", account.getFirstName(), 
                "FirstName should remain unchanged");
            assertEquals("pass2", account.getPassword(), 
                "Password should be updated");
        }
    }
}