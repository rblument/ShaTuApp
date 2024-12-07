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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.regis.shatu.err.IllegalArgException;
import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;
import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.User;
import edu.regis.shatu.svc.UserSvc;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A Data Access Object implementing {@link UserSvc} behaviors.
 * 
 * This DAO is NOT used in the current implementation, but demonstrates
 * how to update information from a file such as User_test_regis_edu.json
 * in the resources.Data directory on the CLASSPATH. Along with the current
 * UserDAO, which uses a MySQL database instead of a file, it also demonstrates
 * how two different persistent approaches can implement the same UserSvc.
 * 
 * NOT USED in the current implementation.
 *
 * @author rickb
 */
public class UserFileDAO implements UserSvc {
    /**
     * Data directory containing student user account files.
     */
    private static final String DATA_DIRECTORY = "src/main/java/resources/Data/";
    
    /**
     * Initialize this DAO via the parent constructor.
     */
    public UserFileDAO() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(Account acct) throws IllegalArgException, NonRecoverableException {      
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
       
        String userId = acct.getUserId();
        String fileName = fullyQualifiedFileName(userId); 
        File file = new File(fileName);
        File absFile = new File(file.getAbsolutePath());

        try {
            absFile.createNewFile();
      
            Path path = Paths.get(fileName);
        
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                gson.toJson(acct, writer);                
            } catch (IOException ex) {
                throw new NonRecoverableException("Create User Accountt Writer Error", ex);
            }
            
        }   catch (IOException ex) {
            throw new NonRecoverableException("Create User Accountt File Error", ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String userId) throws NonRecoverableException {
        //ToDo: add functionality
       throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User retrieve(String userId) throws ObjNotFoundException {
        Gson gson = new Gson();
       
        String fileName = fullyQualifiedFileName(userId);
        
        Path path = Paths.get(fileName);
     
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonObject jsonObj = JsonParser.parseReader(reader).getAsJsonObject();
            
            return gson.fromJson(jsonObj, User.class);

        } catch (IOException ex) { // If username is not found
            throw new ObjNotFoundException(String.valueOf(userId));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User retrieveQuestion(String userId) throws ObjNotFoundException {
        Gson gson = new Gson();
       
        String fileName = fullyQualifiedFileName(userId);
        
        Path path = Paths.get(fileName);
     
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonObject jsonObj = JsonParser.parseReader(reader).getAsJsonObject();
            
            return gson.fromJson(jsonObj, User.class);

        } catch (IOException ex) { // If username is not found
            throw new ObjNotFoundException(String.valueOf(userId));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public User retrieveAnswer(String userId) throws ObjNotFoundException {
        Gson gson = new Gson();
       
        String fileName = fullyQualifiedFileName(userId);
        
        Path path = Paths.get(fileName);
     
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonObject jsonObj = JsonParser.parseReader(reader).getAsJsonObject();
            
            return gson.fromJson(jsonObj, User.class);

        } catch (IOException ex) { // If username is not found
            throw new ObjNotFoundException(String.valueOf(userId));
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void update(User user, String newPassword) throws ObjNotFoundException, NonRecoverableException {
        //ToDo: add functionality
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
     /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String userId) throws NonRecoverableException {
        try {
            retrieve(userId);
            return true;
        } catch (ObjNotFoundException e) {
            return false;
        }
    }
    
    /**
     * Return the fully qualified name of the user file for the given user.
     * 
     * @param userId the id of the user whose session file name is returned.
     * @return a String specifying a fully qualified file name.
     */
    private String fullyQualifiedFileName(String userId) {
        return DATA_DIRECTORY + "User_" + userId.replace('@', '_').replace('.', '_') + ".json";
    }
}
