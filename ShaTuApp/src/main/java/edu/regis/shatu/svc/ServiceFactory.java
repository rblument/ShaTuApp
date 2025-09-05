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

<<<<<<< HEAD
import edu.regis.shatu.dao.CourseDAO;
import edu.regis.shatu.dao.SessionDAO;
import edu.regis.shatu.dao.StudentDAO;
import edu.regis.shatu.dao.UserDAO;
=======
import edu.regis.shatu.dao.AccountDAO;
import edu.regis.shatu.dao.CourseDAO;
import edu.regis.shatu.dao.SessionDAO;
import edu.regis.shatu.dao.StudentModelDAO;
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3

/**
 * A singleton providing a concrete implementation of the service factory used
 * to obtain references to various ShaTu tutoring services.
 * 
 * Use of the service factory allows easier changes to how the services are
 * actually implemented without directly affecting the consumers who use them.
<<<<<<< HEAD
=======
 * For example, returning a POJO versus returning a network-based service.
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
 * 
 * @author rickb
 */
public class ServiceFactory {
    /**
<<<<<<< HEAD
=======
     * Return a reference to the user service.
     * 
     * @return AccountSvc
     */
    public static AccountDAO findAccountSvc() {
        return new AccountDAO();
    }

    /**
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
     * Return a reference to the course service.
     * 
     * @return CourseSvc
     */
<<<<<<< HEAD
    public static CourseSvc findCourseSvc() {
        return new CourseDAO();
    }
    
=======
    public static CourseDAO findCourseSvc() {
        return new CourseDAO();
    }

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * Return a reference to the session service.
     * 
     * @return SessionSvc
     */
<<<<<<< HEAD
    public static SessionSvc findSessionSvc() {
        return new SessionDAO();
    }
    
    /**
     * Return a reference to the student service.
     * 
     * @return StudentSvc
     */
    public static StudentSvc findStudentSvc() {
        return new StudentDAO();
    }
    
    /**
     * Return a reference to the user service.
     * 
     * @return UserSvc
     */
    public static UserSvc findUserSvc() {
        return new UserDAO();
=======
    public static SessionDAO findSessionSvc() {
        return new SessionDAO();
    }

    /**
     * Return a reference to the student model service.
     * 
     * @return StudentModelSvc
     */

    public static StudentModelDAO findStudentModelSvc() {
        return new StudentModelDAO();
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }
}
