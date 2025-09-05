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
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.aol.EncodeAsciiExample;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.regis.shatu.err.IllegalArgException;
import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;
import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.Course;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.User;
import edu.regis.shatu.model.aol.BitOpExample;
import edu.regis.shatu.model.aol.BitOpStep;
import edu.regis.shatu.model.aol.EncodeAsciiStep;
import edu.regis.shatu.model.Hint;
import edu.regis.shatu.model.KnowledgeComponent;
import edu.regis.shatu.model.aol.ScaffoldLevel;
import edu.regis.shatu.model.Step;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.StepCompletionReply;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.Task;
import edu.regis.shatu.model.aol.TaskKind;
import edu.regis.shatu.model.aol.Timeout;
import edu.regis.shatu.model.Unit;
import edu.regis.shatu.model.aol.Assessment;
import edu.regis.shatu.model.aol.AssessmentLevel;
import edu.regis.shatu.model.aol.ExampleType;
import edu.regis.shatu.model.aol.StudentModel;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
=======
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.GregorianCalendar;
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

<<<<<<< HEAD
=======
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.regis.shatu.dao.AccountDAO;
import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjDuplicateException;
import edu.regis.shatu.err.ObjNotFoundException;
import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.Course;
import edu.regis.shatu.model.KnowledgeComponent;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.Task;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.Unit;
import edu.regis.shatu.model.aol.Assessment;
import edu.regis.shatu.model.aol.AssessmentLevel;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.aol.PendingStep;
import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.aol.StudentModel;
import edu.regis.shatu.model.steps.Step;
import edu.regis.shatu.objectives.AddBits;
import edu.regis.shatu.objectives.AddMsgLen;
import edu.regis.shatu.objectives.AddOne;
import edu.regis.shatu.objectives.ChoiceFunction;
import edu.regis.shatu.objectives.CompressRound;
import edu.regis.shatu.objectives.EncodeAscii;
import edu.regis.shatu.objectives.InitVars;
import edu.regis.shatu.objectives.MajorityFunction;
import edu.regis.shatu.objectives.Objective;
import edu.regis.shatu.objectives.PadZeros;
import edu.regis.shatu.objectives.PrepareSchedule;
import edu.regis.shatu.objectives.RotateBits;
import edu.regis.shatu.objectives.ShaOne;
import edu.regis.shatu.objectives.ShaZero;
import edu.regis.shatu.objectives.ShiftBits;
import edu.regis.shatu.objectives.XorBits;

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
/**
 * The ShaTu tutor, which implements the tutoring service.
 *
 * @author rickb
 */
public class ShaTuTutor implements TutorSvc {

    /**
     * The id of the default course taught by the this tutor.
     */
    private static final int DEFAULT_COURSE_ID = 1;
    /**
     * The maximum number of characters allowed for encoding a example ASCII
     * encoding request from the student.
     */
    private static final int MAX_ASCII_SIZE = 20;

    private static final int MAX_BITS_SIZE = 32;

    /**
     * Handler for logging non-exception messages from this class versus thrown
     * exception, which are logged by the exception.
     */
<<<<<<< HEAD
    private static final Logger LOGGER
            = Logger.getLogger(ShaTuTutor.class.getName());
=======
    private static final Logger LOGGER = Logger.getLogger(ShaTuTutor.class.getName());
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3

    /**
     * The current tutoring session, which contains information on the current
     * Student, StudentModel, Course, Task, Step, etc.
     */
    private TutoringSession session;

<<<<<<< HEAD
=======
    private Student student;
    private StudentModel studentModel;

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * Convenience reference to the current gson object.
     */
    private Gson gson;

<<<<<<< HEAD
=======
    private Objective currObjective;

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * Initialize the tutor singleton (a NoOp).
     */
    public ShaTuTutor() {
<<<<<<< HEAD
=======
        gson = new GsonBuilder().setPrettyPrinting().create();
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TutorReply request(ClientRequest request) {
        // Uses reflection to invoke a method derived from the request name in
        // the client request (e.g., ":SignIn" invokes "signIn(...)").
        Logger.getLogger(ShaTuTutor.class.getName()).log(Level.INFO, request.getRequestType().getRequestName());

<<<<<<< HEAD
        // Efficiently produce "signIn" from ":SignIn", for example.         
=======
        // Efficiently produce "signIn" from ":SignIn", for example.
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        char c[] = request.getRequestType().getRequestName().toCharArray();
        c[1] = Character.toLowerCase(c[1]);

        char m[] = new char[c.length - 1];
        for (int i = 1; i < c.length; i++) {
            m[i - 1] = c[i];
        }

        String methodName = new String(m);

<<<<<<< HEAD
        // Most methods require verifying the given security token with the known one.
        switch (methodName) {
            case "completedStep":
            case "completedTask":
            case "newExample":
            case "requestHint":
                try {
                session = verifySession(request.getUserId(), request.getSessionId());

            } catch (ObjNotFoundException ex) {
                return createError("No session exists for user: " + request.getUserId(), ex);
            } catch (IllegalArgException ex) {
                return createError("Illegal session token sent for user: " + request.getUserId(), ex);
            } catch (NonRecoverableException ex) {
                return createError(ex.toString(), ex);
            }

            String msg = "Session verified for " + request.getUserId();
            Logger.getLogger(ShaTuTutor.class.getName()).log(Level.INFO, msg);
            break;
=======
        // Most methods require verifying the given security token with the
        // one current known in the DB for the given user.
        switch (methodName) {
            case "completedStep":
            case "completedTask":
            //case "getTask":
            case "newExample":
            case "requestHint":
            case "resetPassword":
                String userId = request.getUserId();
                try {
                    if (verifySession(userId, request.getSecurityToken())) {

                        Account account = ServiceFactory.findAccountSvc().retrieve(userId);
                        student = new Student(account);

                        try {
                            StudentModelSvc stuModSvc = ServiceFactory.findStudentModelSvc();
                            studentModel = stuModSvc.retrieve(userId);
                            student.setStudentModel(studentModel);

                        } catch (ObjNotFoundException ex) {
                            TutorReply reply = new TutorReply(":ERR");
                            reply.setData("Student model not found for: " + userId);
                            return reply;
                        }

                        session = ServiceFactory.findSessionSvc().retrieve(student.getAccount().getUserId());

                    } else {
                        TutorReply reply = new TutorReply(":ERR");
                        reply.setData("Illegal Security Token");
                        return reply;
                    }

                } catch (ObjNotFoundException ex) {
                    return createError("No session exists for user: " + userId, ex);
                } catch (NonRecoverableException ex) {
                    return createError(ex.toString(), ex);
                }

                String msg = "Session verified for " + request.getUserId();
                Logger.getLogger(ShaTuTutor.class.getName()).log(Level.INFO, msg);
                break;
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3

            default: // e.g., signIn itself, newAccount
                Logger.getLogger(ShaTuTutor.class.getName()).log(Level.INFO, "No token verification required");
        }

<<<<<<< HEAD
        // Security token has been verified or not required (e.g., signIn, createAccount).
=======
        // Security token has been verified or not required (e.g., signIn,
        // createAccount).
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        try {
            Method method = getClass().getMethod(methodName, String.class);

            return (TutorReply) method.invoke(this, request.getData());

        } catch (NoSuchMethodException ex) {
<<<<<<< HEAD
            return createError("Tutor received an unknown request type: " + request.getRequestType().getRequestName(), ex);
=======
            return createError("Tutor received an unknown request type: " + request.getRequestType().getRequestName(),
                    ex);
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        } catch (SecurityException ex) {
            return createError("ShaTuTutor_ERR_2", ex);
        } catch (IllegalAccessException ex) {
            return createError("ShaTuTutor_ERR_3", ex);
        } catch (IllegalArgumentException ex) {
            return createError("ShaTuTutor_ERR_4", ex);
        } catch (InvocationTargetException ex) {
<<<<<<< HEAD
            return createError("ShaTuTutor_ERR_5", ex);
=======
            return createError("ShaTuTutor_ERR_5", (Exception) ex.getCause());
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        }
    }

    /**
     * Creates a new student account
     *
     * This method handles ":CreateAccount" requests from the GUI client.
     *
     * @param jsonAcct a JSon encoded Account object
<<<<<<< HEAD
     * @return a TutorReply if successful the status is "Created", otherwise the
     * status is "ERR".
     */
    public TutorReply createAccount(String jsonAcct) throws NonRecoverableException {
        gson = new GsonBuilder().setPrettyPrinting().create();

=======
     * @throws NonRecoverableException perhaps see getCause().getErrorCode().
     * @return a TutorReply if successful the status is "Created", otherwise the
     *         status is ":ERR".
     */
    public TutorReply createAccount(String jsonAcct) throws NonRecoverableException {
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        Account acct = gson.fromJson(jsonAcct, Account.class);

        int courseId = DEFAULT_COURSE_ID; // Currently only one course

<<<<<<< HEAD
        StudentSvc stuSvc = ServiceFactory.findStudentSvc();

        if (stuSvc.exists(acct.getUserId()))
            return new TutorReply("IllegalUserId");

        try {
            ServiceFactory.findUserSvc().create(acct);

            try {
                CourseSvc courseSvc = ServiceFactory.findCourseSvc();

                Course course = courseSvc.retrieve(courseId);

                session = createSession(acct, course);

                createStudent(acct, course, session);

                return new TutorReply("Created");

            } catch (ObjNotFoundException ex) {
                return createError("Unknown course: " + courseId, null);
            }

        } catch (IllegalArgException ex) {
            // Should never get here since we tested whether the account exists
=======
        try {
            ServiceFactory.findAccountSvc().create(acct);
        } catch (ObjDuplicateException e) {
            return createError(String.format("Account %s exists", acct.getUserId()), null);
        }

        try {
            Course course = ServiceFactory.findCourseSvc().retrieve(courseId);

            student = createStudent(acct, course);

            createSession(student, course);

            return new TutorReply("Created");

        } catch (ObjNotFoundException ex) {
            return createError("Unknown course: " + courseId, null);
        }

        // try {
        // }
        // catch (IllegalArgException ex) { // The account already exists
        // return new TutorReply("IllegalUserId");
        // }
    }

    /**
     * Verifies the user is in the database and the security question and answer
     * match.
     *
     * This method handles ":VerifyUser" requests from the GUI client.
     *
     * @param jsonAcct a JSon encoded Account object
     * @return a TutorReply if successful the status is "Created", otherwise the
     *         status is ":ERR".
     * @throws edu.regis.shatu.err.NonRecoverableException
     */
    public TutorReply verifyUser(String jsonAcct) throws NonRecoverableException {
        Account requestAcct = gson.fromJson(jsonAcct, Account.class);
        AccountDAO acctSvc = ServiceFactory.findAccountSvc();

        if (!acctSvc.exists(acctSvc.primaryKey, requestAcct.getUserId())) {
            return new TutorReply("IllegalUserId");
        }

        try {
            Account dbAcct = acctSvc.retrieve(requestAcct.getUserId());

            if ((dbAcct.getSecurityAnswer().equals(requestAcct.getSecurityAnswer())) &&
                    (dbAcct.getSecurityQuestion() == requestAcct.getSecurityQuestion())) {

                student = new Student(dbAcct);

                try {
                    StudentModelSvc stuModSvc = ServiceFactory.findStudentModelSvc();
                    studentModel = stuModSvc.retrieve(dbAcct.getUserId());
                    student.setStudentModel(studentModel);
                } catch (ObjNotFoundException e) {
                    student = createStudent(dbAcct, ServiceFactory.findCourseSvc().retrieve(DEFAULT_COURSE_ID));
                }

                // Check if session already exists before creating
                SessionSvc sessionSvc = ServiceFactory.findSessionSvc();
                TutoringSession session;

                try {
                    session = sessionSvc.retrieve(dbAcct.getUserId()); // already exists
                } catch (ObjNotFoundException e) {
                    // session does not exist, create it
                    session = createSession(student, ServiceFactory.findCourseSvc().retrieve(DEFAULT_COURSE_ID));
                }

                TutorReply reply = new TutorReply("Verified");
                reply.setData("\"" + session.getSecurityToken() + "\"");
                return reply;

            } else {
                return new TutorReply("InvalidAnswer");
            }

        } catch (ObjNotFoundException e) {
            return new TutorReply("UnknownUser");
        } catch (NonRecoverableException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return new TutorReply();
        }
    }

    /**
     * Resets password for user currently in database
     *
     * This method handles ":ResetPassword" requests from the GUI client.
     *
     * @param jsonAcct a JSon encoded Account object
     * @return a TutorReply if successful the status is "Created", otherwise the
     *         status is ":ERR".
     * @throws edu.regis.shatu.err.NonRecoverableException
     */
    public TutorReply resetPassword(String jsonAcct) throws NonRecoverableException {
        Account acct = gson.fromJson(jsonAcct, Account.class);
        AccountDAO acctSvc = ServiceFactory.findAccountSvc();

        if (!acctSvc.exists(acctSvc.primaryKey, acct.getUserId())) {
            return new TutorReply("IllegalUserId");
        }

        try {
            // Retrieve full DB account so we don't lose names or other info
            Account dbAcct = acctSvc.retrieve(acct.getUserId());

            // Only update the changed fields
            dbAcct.setPassword(acct.getPassword());

            // Only set security question/answer if needed
            dbAcct.setSecurityQuestion(acct.getSecurityQuestion());
            dbAcct.setSecurityAnswer(acct.getSecurityAnswer());

            acctSvc.update(dbAcct);

            return new TutorReply("PasswordReset");

        } catch (ObjNotFoundException ex) {
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
            return new TutorReply("IllegalUserId");
        }
    }

    /**
     * Attempts to sign a student in.
     *
     * This method handles ":SignIn" requests from the GUI client.
     *
     * @param jsonUser a JSon encoded User object
     * @return a TutorReply, if successful, the status is "Authenticated" with
<<<<<<< HEAD
     * data being a JSon encoded TutoringSession object.
     */
    public TutorReply signIn(String jsonUser) {
        System.out.println("Received sign in: " + jsonUser);
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        User user = gson.fromJson(jsonUser, User.class
        );

        try {
            User dbUser = ServiceFactory.findUserSvc().retrieve(user.getUserId());

            if (dbUser.getPassword().equals(user.getPassword())) {
                SessionSvc svc = ServiceFactory.findSessionSvc();
                TutoringSession session = svc.retrieve(user.getUserId());
=======
     *         data being a JSon encoded TutoringSession object.
     */
    public TutorReply signIn(String jsonUser) {
        System.out.println("Received sign in: " + jsonUser);
        Account requestAcct = gson.fromJson(jsonUser, Account.class);

        try {
            Account dbAcct = ServiceFactory.findAccountSvc().retrieve(requestAcct.getUserId());

            if (dbAcct.getPassword().equals(requestAcct.getPassword())) {
                student = new Student(dbAcct);
                String userId = dbAcct.getUserId();

                try {
                    StudentModelSvc stuModSvc = ServiceFactory.findStudentModelSvc();
                    studentModel = stuModSvc.retrieve(userId);
                    student.setStudentModel(studentModel);

                } catch (ObjNotFoundException ex) {
                    TutorReply reply = new TutorReply(":ERR");
                    reply.setData("Student model not found in sign in for: " + userId);
                    return reply;
                }

                SessionSvc svc = ServiceFactory.findSessionSvc();
                TutoringSession session = svc.retrieve(student.getAccount().getUserId());
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3

                TutorReply reply = new TutorReply("Authenticated");

                reply.setData(gson.toJson(session));

                return reply;

            } else {
                return new TutorReply("InvalidPassword");
<<<<<<< HEAD

=======
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
            }

        } catch (ObjNotFoundException e) {
            return new TutorReply("UnknownUser");
<<<<<<< HEAD

=======
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        } catch (NonRecoverableException ex) {
            Logger.getLogger(ShaTuTutor.class
                    .getName()).log(Level.SEVERE, null, ex);
            return new TutorReply();
<<<<<<< HEAD

        }
    }

=======
        }
    }

    /*
    public TutorReply getTask(String jsonObj) {
        System.out.println("get task method");

        Task task = new Task();
        int[] taskOrder = { 102, 103, 104, 105, 106, 107, 108, 109, 101, 110, 111, 100, 112, 113, 114 };
        int currentTask = 102;

        for (int id : taskOrder) {
            Assessment assessment = studentModel.findAssessment(id);
            if (!assessment.getAssessment().equals(AssessmentLevel.COMPLETED)) {
                currentTask = id;
                break;
            }
        }
        System.out.println("Current task: " + currentTask);

        switch (currentTask) {
            case 106:
                task.setType(ProblemType.PREPARE_SCHEDULE);
                currObjective = new PrepareSchedule(student);
                break;
            case 108:
                task.setType(ProblemType.COMPRESS_ROUND);
                currObjective = new CompressRound(student);
                break;
            case 101:
                task.setType(ProblemType.SHIFT_BITS);
                currObjective = new ShiftBits(student);
                break;
            case 110:
                task.setType(ProblemType.XOR_BITS);
                currObjective = new XorBits(student);
                break;
            case 111:
                task.setType(ProblemType.ADD_BITS);
                currObjective = new AddBits(student);
                break;
            case 112:
                task.setType(ProblemType.MAJORITY_FUNCTION);
                currObjective = new MajorityFunction(student);
                break;
            case 103:
                task.setType(ProblemType.ADD_ONE_BIT);
                currObjective = new AddOne(student);
                break;
            case 104:
                task.setType(ProblemType.PAD_ZEROS);
                currObjective = new PadZeros(student);
                break;
            case 109:
                task.setType(ProblemType.ROTATE_BITS);
                currObjective = new RotateBits(student);
                break;
            case 107:
                task.setType(ProblemType.INITIALIZE_VARS);
                currObjective = new InitVars(student);
                break;
            case 100:
                task.setType(ProblemType.CHOICE_FUNCTION);
                currObjective = new ChoiceFunction(student);
                break;
            case 105:
                task.setType(ProblemType.ADD_MSG_LENGTH);
                currObjective = new AddMsgLen(student);
                break;
            case 113:
                task.setType(ProblemType.SHA_ZERO);
                currObjective = new ShaZero(student);
                break;
            case 114:
                task.setType(ProblemType.SHA_ONE);
                currObjective = new ShaOne(student);
                break;
            default:
                task.setType(ProblemType.ASCII_ENCODE);
                currObjective = new EncodeAscii(student);
                break;
        }
        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(task));
        return reply;
    }
    */

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    /**
     * Returns a hint to the GUI client, if any
     *
     * This method handles ":RequestHint" requests from the GUI client.
     *
<<<<<<< HEAD
     * @param sessionInfo a
     * @return a TutorReply, if successful, the status is "Hint" with data being
     * a displayable hint text string.
     */
    public TutorReply requestHint(String sessionInfo) {
        // ToDo: this is simply a hard coded test case
        TutorReply reply = new TutorReply("Hint");
        reply.setData("This is a hint from the tutor.");

        return new TutorReply();
    }

    /**
     * 
     * 
     * @param jsonObj a JSon encoded StepCompletion object
     * @return 
     */
    public TutorReply completedStep(String jsonObj) {
        StepCompletion completion = gson.fromJson(jsonObj, StepCompletion.class);
        
        Step step = completion.getStep();
        
=======
     * @param jsonObj
     * @return a TutorReply, if successful, the status is "Hint" with data being
     *         a displayable hint text string.
     */
    public TutorReply requestHint(String jsonObj) {
        System.out.println("requestHint");
        StepCompletion completion = gson.fromJson(jsonObj, StepCompletion.class);

        Step step = completion.getStep();

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        switch (step.getSubType()) {
            case INFO_MESSAGE:
                return completeInfoMsgStep(completion);

<<<<<<< HEAD
            case ENCODE_BINARY: // TO_DO: Really the same
            case ENCODE_HEX:
            case ENCODE_ASCII:
               return completeEncodeStep(completion);
                
            case ADD_ONE_BIT:
               return completeAddOneStep(completion);
    
            case PAD_ZEROS:  
                return completePadZerosStep(completion);
                
            case ADD_MSG_LENGTH:
                return completeAddMsgLenStep(completion);
                
            case PREPARE_SCHEDULE:
                return completePrepareScheduleStep(completion);
            
            case INITIALIZE_VARS:
                return completeInitVarsStep(completion);
                
            case COMPRESS_ROUND:
                return completeCompressRoundStep(completion);
            
            case ROTATE_BITS:
                return completeRotateStep(completion);
    
            case SHIFT_BITS:
                return completeShiftBitsStep(completion);
            
            case XOR_BITS:
                return completeXorBitsStep(completion);
            
            case ADD_BITS:
                return completeAddBitsStep(completion);
            
            case MAJORITY_FUNCTION:
                return completeMajorityStep(completion);
            
            case CHOICE_FUNCTION:
                return completeChoiceStep(completion);
                
            default:
                return createError("Unknown step completion: " + step.getSubType(), null);
        }
    }
    
    public TutorReply completeRotateStep(StepCompletion completion) {
        TutorReply reply = new TutorReply(":StepCompletionReply");
        
        return reply;
    }
    
    public TutorReply completeInfoMsgStep(StepCompletion completion) {
        TutorReply reply = new TutorReply(":StepCompletionReply");
        
        return reply;
    }
    
    
    
      public TutorReply completeEncodeStep(StepCompletion completion) {
        TutorReply reply = new TutorReply(":StepCompletionReply");
        
        return reply;
    }
 
    public TutorReply completeAddOneStep(StepCompletion completion) {
        TutorReply reply = new TutorReply(":StepCompletionReply");
        
        // As adding one bit doesn't require any additional information,
        // the data is the string with one '1' bit added. 
        String data = completion.getData();
        
        // TO_DO: look up the problem given to the student , then check if one bit
        // added
        
        StepCompletionReply stepReply = new StepCompletionReply();
        
        // TO_DO: Use Student Model
        // ultimately, we'll probably only practice adding '1' bit twice
        // so this would correspond to the first replay
        
        stepReply.setIsCorrect(true);
        stepReply.setIsNewStep(true);
        stepReply.setIsNewTask(false);
        stepReply.setIsRepeatStep(false);
        
        // TO_DO: keep track of next step id and sequence id
        // this is really a new example at this point
        Step nextStep = new Step(10, 10, StepSubType.ADD_ONE_BIT);
        
        stepReply.setData(gson.toJson(nextStep));
        
        reply.setData(gson.toJson(stepReply));
        
        return reply;
    }
    
    public TutorReply completePadZerosStep(StepCompletion completion) {
        TutorReply reply = new TutorReply(":StepCompletionReply");
        
        return reply;
    }
        
    public TutorReply completeAddMsgLenStep(StepCompletion completion) {
        TutorReply reply = new TutorReply(":StepCompletionReply");
        
        return reply;
    }  
          
    public TutorReply completePrepareScheduleStep(StepCompletion completion) {
        TutorReply reply = new TutorReply(":StepCompletionReply");
        
        return reply;
    }
           
    public TutorReply completeInitVarsStep(StepCompletion completion) {
        TutorReply reply = new TutorReply(":StepCompletionReply");
        
        return reply;
    }
  
    public TutorReply completeCompressRoundStep(StepCompletion completion) {
        TutorReply reply = new TutorReply(":StepCompletionReply");
        
        return reply;
    }

    public TutorReply completeShiftBitsStep(StepCompletion completion) {
        TutorReply reply = new TutorReply(":StepCompletionReply");
        
        return reply;
    }
    public TutorReply completeXorBitsStep(StepCompletion completion) {
        TutorReply reply = new TutorReply(":StepCompletionReply");
        
        return reply;
    }
    public TutorReply completeAddBitsStep(StepCompletion completion) {
        TutorReply reply = new TutorReply(":StepCompletionReply");
        
        return reply;
    }

    public TutorReply completeMajorityStep(StepCompletion completion) {
        TutorReply reply = new TutorReply(":StepCompletionReply");
        
        return reply;
    }
                
    public TutorReply completeChoiceStep(StepCompletion completion) {
        TutorReply reply = new TutorReply(":StepCompletionReply");
        
        return reply;                
    }
=======
            default:
                currObjective = getCurrentObjectiveByProbelmType(convertStepToProblemType(step.getSubType()));
                return currObjective.hint(completion);
        }
    }

    /**
     * @param jsonObj a JSon encoded StepCompletion object
     * @return
     */
    public TutorReply completedStep(String jsonObj) {
        System.out.println("completedStep");

        StepCompletion completion = gson.fromJson(jsonObj, StepCompletion.class);

        Step step = completion.getStep();

        switch (step.getSubType()) {
            case INFO_MESSAGE:
                return completeInfoMsgStep(completion);

            default:
                currObjective = getCurrentObjectiveByProbelmType(convertStepToProblemType(step.getSubType()));
                return currObjective.completeStep(completion);
        }
    }

    public TutorReply completeInfoMsgStep(StepCompletion completion) {
        TutorReply reply = new TutorReply(":StepCompletionReply");

        return reply;
    }
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3

    public TutorReply completedTask(String taskInfo) {
        return new TutorReply();
    }

    /**
     * Handles :NewExample requests from the client.
     *
     * @param json a JSon String encoding a NewExampleRequest object
     * @return TutorReply
     */
    public TutorReply newExample(String json) {
<<<<<<< HEAD
        gson = new GsonBuilder().setPrettyPrinting().create();

        NewExampleRequest request = gson.fromJson(json, NewExampleRequest.class);

        switch (request.getExampleType()) {
            case ASCII_ENCODE:
                return newAsciiEncodeExample(session, request.getData());

            case ADD_ONE_BIT:
                return newAddOneBitExample(session, request.getData());

            case PAD_ZEROS:
                return newPadZerosExample(session, request.getData());

            case ADD_MSG_LENGTH:
                return newAddMsgLenExample(session, request.getData());

            case PREPARE_SCHEDULE:
                return newPrepareScheduleExample(session, request.getData());

            case INITIALIZE_VARS:
                return newInitializeVarsExample(session, request.getData());

            case COMPRESS_ROUND:
                return newCompressRoundExample(session, request.getData());

            case ROTATE_BITS:
                return newRotateBitsExample(session, request.getData());

            case SHIFT_BITS:
                return newShiftBitsExample(session, request.getData());

            case XOR_BITS:
                return newXorBitsExample(session, request.getData());

            case ADD_BITS:
                return newAddBitsExample(session, request.getData());

            case MAJORITY_FUNCTION:
                return newMajorityFunctionExample(session, request.getData());

            case CHOICE_FUNCTION:
                return newChoiceFunctionExample(session, request.getData());

            default:
                return createError("Unknown example request: " + request.getExampleType(), null);
=======
        System.out.println("nexExample()");

        NewExampleRequest request = gson.fromJson(json, NewExampleRequest.class);

        currObjective = getCurrentObjectiveByProbelmType(request.getExampleType());

        return currObjective.example(session, request.getData());
    }

    /**
     * 
     * ToDO: Can ProblemType and StepSubType be combined?
     * 
     * @param problemType
     * @return
     */
    private Objective getCurrentObjectiveByProbelmType(ProblemType problemType) {
        switch (problemType) {
            case PREPARE_SCHEDULE:
                return new PrepareSchedule(student);
            case COMPRESS_ROUND:
                return new CompressRound(student);
            case SHIFT_BITS:
                return new ShiftBits(student);
            case XOR_BITS:
                return new XorBits(student);
            case ADD_BITS:
                return new AddBits(student);
            case MAJORITY_FUNCTION:
                return new MajorityFunction(student);
            case ADD_ONE_BIT:
                return new AddOne(student);
            case PAD_ZEROS:
                return new PadZeros(student);
            case ROTATE_BITS:
                return new RotateBits(student);
            case INITIALIZE_VARS:
                return new InitVars(student);
            case CHOICE_FUNCTION:
                return new ChoiceFunction(student);
            case ADD_MSG_LENGTH:
                return new AddMsgLen(student);
            case ASCII_ENCODE:
                return new EncodeAscii(student);
            case SHA_ZERO:
                return new ShaZero(student);
            case SHA_ONE:
                return new ShaOne(student);
            default:
                System.out.println("ShaUnknown new example request: " + problemType);
                return null;
        }
    }

    /**
     * KLUDGE
     * ToDO: Can ProblemType and StepSubType be combined?
     * 
     * @param stepType
     * @return
     */
    private ProblemType convertStepToProblemType(StepSubType stepType) {
        switch (stepType) {
            case ENCODE_BINARY:
            case ENCODE_HEX:
            case ENCODE_ASCII:
                return ProblemType.ASCII_ENCODE;
            case ADD_ONE_BIT:
                return ProblemType.ADD_ONE_BIT;
            case PAD_ZEROS:
                return ProblemType.PAD_ZEROS;
            case ADD_MSG_LENGTH:
                return ProblemType.ADD_MSG_LENGTH;
            case PREPARE_SCHEDULE:
                return ProblemType.PREPARE_SCHEDULE;
            case INITIALIZE_VARS:
                return ProblemType.INITIALIZE_VARS;
            case COMPRESS_ROUND:
                return ProblemType.COMPRESS_ROUND;
            case ROTATE_BITS:
                return ProblemType.ROTATE_BITS;
            case SHIFT_BITS:
                return ProblemType.SHIFT_BITS;
            case XOR_BITS:
                return ProblemType.XOR_BITS;
            case ADD_BITS:
                return ProblemType.ADD_BITS;
            case MAJORITY_FUNCTION:
                return ProblemType.MAJORITY_FUNCTION;
            case CHOICE_FUNCTION:
                return ProblemType.CHOICE_FUNCTION;
            case SHA_ZERO:
                return ProblemType.SHA_ZERO;
            case SHA_ONE:
                return ProblemType.SHA_ONE;
            default:
                System.out.println("Unknown step type in convert: " + stepType);
                return null;
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        }
    }

    /**
     * Create and save a new tutoring session associated with the given account.
     *
     * @param account the student user
     * @throws NonRecoverableException
     * @return the new TutoringSession
     */
<<<<<<< HEAD
    private TutoringSession createSession(Account account, Course course) throws NonRecoverableException {
        try {
            Task task = getFirstTask(course);

            TutoringSession tSession = new TutoringSession();
            tSession.setAccount(account);

            Random rnd = new Random();
            String clearToken = "Session" + account.getUserId() + Integer.toString(rnd.nextInt());
            tSession.setSecurityToken(SHA_256.instance().sha256(clearToken));

            tSession.setCourse(course.getDigest());

            Unit unit = course.currentUnit();
            if (unit != null)
                tSession.setUnit(unit.getDigest());
         
            tSession.addTask(task);

=======
    private TutoringSession createSession(Student student, Course course) throws NonRecoverableException {
        Account account = student.getAccount();

        TutoringSession tSession = new TutoringSession(student);
        tSession.setStartDate(new GregorianCalendar());
        tSession.setCourse(course.getDigest());
        tSession.setUnit(course.currentUnit().getDigest());

        Task task = getFirstTask(course);
        PendingTask pendingTask = new PendingTask(task);
        pendingTask.setCurrentStep(new PendingStep(task.getCurrentStep()));
        tSession.addTask(pendingTask);

        // Generate the security token for this tutoring session.
        Random rnd = new Random();
        String clearToken = "Session" + account.getUserId() + Integer.toString(rnd.nextInt());
        tSession.setSecurityToken(SHA_256.instance().sha256(clearToken));

        try {
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
            ServiceFactory.findSessionSvc().create(tSession);

            return tSession;

<<<<<<< HEAD
        } catch (IllegalArgException ex) {
            // Should never get here
            throw new NonRecoverableException("Session already exists " + account.getUserId());
=======
        } catch (ObjDuplicateException ex) {
            throw new NonRecoverableException("Session already exists", ex);
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        }
    }

    /**
     * Create and save the student and their initial student model.
     *
     * @param acct
     * @param course
     * @return
     */
<<<<<<< HEAD
    private Student createStudent(Account acct, Course course, TutoringSession session)
            throws NonRecoverableException {
        
        Student student = new Student(acct.getUserId(), acct.getPassword());
        StudentModel model = student.getStudentModel();

        try {
            // As the student has at least one task and step to complete,
            // add the associated knowledge component assessment(s) to the 
            // student model of the student.
            HashSet<Integer> componentIds = new HashSet<>();
            for (Task task : session.getTasks()) {
                for (int componentId : task.getExercisedComponentIds()) {
                    componentIds.add(componentId);
                }

                for (Step step : task.getSteps()) {
                    for (int cid : step.getExercisedComponentIds()) {
                        componentIds.add(cid);
                    }
                }
            }

            for (int id : componentIds) {
                if (!model.containsAssessment(id)) {
                    KnowledgeComponent comp = course.findKnowledgeComponent(id);
                    model.addAssessment(id, new Assessment(comp, AssessmentLevel.VERY_LOW));
                    
                    
                }
            }

            StudentSvc svc = ServiceFactory.findStudentSvc();
            svc.create(student);

            return student;

        } catch (IllegalArgException e) {
            // We should never get here since 
            throw new NonRecoverableException("Student already exists " + acct.getUserId());

        } catch (ObjNotFoundException e) {
            throw new NonRecoverableException("Inconsistent Course in DB knowledge component" + course.getId());
        }

=======
    private Student createStudent(Account account, Course course)
            throws NonRecoverableException {

        student = new Student(account);
        studentModel = student.getStudentModel();

        for (KnowledgeComponent outcome : course.getOutcomes()) {
            Assessment assessment = new Assessment(outcome, AssessmentLevel.NOT_STARTED);

            studentModel.addAssessment(outcome.getId(), assessment);
        }

        StudentModelSvc stuSvc = ServiceFactory.findStudentModelSvc();

        stuSvc.create(student);

        return student;

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }

    /**
     * Verify that the user with the given id has a session with the given
     * session id.
     *
<<<<<<< HEAD
     * @param userId String "user@regis.edu"
     * @param sessionId String identifying a previously generated session id.
     * @return the current TutoringSession associated with the given user id and
     * session id
     */
    private TutoringSession verifySession(String userId, String sessionId)
            throws ObjNotFoundException, IllegalArgException, NonRecoverableException {

        SessionSvc svc = ServiceFactory.findSessionSvc();
        TutoringSession locSession = svc.retrieve(userId);

        if (locSession.getSecurityToken().equals(sessionId)) {
            return locSession;
        } else {
            throw new IllegalArgException("Illegal session id for user: " + userId);
        }
=======
     * @param userId    String "user@regis.edu"
     * @param sessionId String identifying a previously generated session id.
     * @return the current TutoringSession associated with the given user id and
     *         session id
     */
    private boolean verifySession(String userId, String sessionId)
            throws ObjNotFoundException, NonRecoverableException {

        SessionSvc svc = ServiceFactory.findSessionSvc();
        String dbToken = svc.retrieveSecurityToken(userId);

        return dbToken.equals(sessionId);
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
    }

    /**
     * Return the first task that should be performed in the given course.
     *
     * @param course
     * @return a Task that should be completed first.
<<<<<<< HEAD
     * @throws IllegalArgException see the message text.
     */
    private Task getFirstTask(Course course) throws IllegalArgException {
=======
     * @throws NonRecoverableException see the message text.
     */
    private Task getFirstTask(Course course) throws NonRecoverableException {
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        switch (course.getPrimaryPedagogy()) {
            case STUDENT_CHOICE:
                return null; // ToDo

            case FIXED_SEQUENCE:
                Unit unit = course.findUnitBySequenceId(0);

                if (unit == null) {
<<<<<<< HEAD
                    throw new IllegalArgException("Unit 0 not found in course: " + course.getId());
=======
                    throw new NonRecoverableException("Unit 0 not found in course: " + course.getId());
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
                }

                Task task = unit.findTaskBySequence(0);

                if (task == null) {
<<<<<<< HEAD
                    throw new IllegalArgException("Task 0 not found in Unit 0 of course: " + course.getId());
=======
                    throw new NonRecoverableException("Task 0 not found in Unit 0 of course: " + course.getId());
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
                }

                return task;

            case MASTERY_LEARNING:
                return null; // ToDo

            case MICROADAPTATION:
                return null; // ToDo

            default:
<<<<<<< HEAD
                throw new IllegalArgException("Unknwon task selection in course: " + course.getId());
=======
                throw new NonRecoverableException("Unknwon task selection in course: " + course.getId());
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        }
    }

    /**
<<<<<<< HEAD
     * Handles client requests for a new ASCII encode example.
     *
     * @return a TutorReply whose data contains a JSon EncodeAsciiExample
     * object.
     */
    private TutorReply newAsciiEncodeExample(TutoringSession session, String jsonData) {
        Random rnd = new Random();

        EncodeAsciiExample example = gson.fromJson(jsonData, EncodeAsciiExample.class);

        int strLen = example.getStringLength();

        if (strLen == 0) {
            // ToDo: The tutor should generate the string length and timeout
            // based on the the current student model.
            strLen = rnd.nextInt(MAX_ASCII_SIZE - 1) + 1;
            example.setTimeOut(600);

        } else if (strLen > MAX_ASCII_SIZE) {
            // The student is requesting practice for a specific string length.
            strLen = MAX_ASCII_SIZE;
            example.setTimeOut(0);
        }

        int[] encoding = new int[strLen];

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < strLen; i++) {
            // Printable ASCII in range 32 ' ' to 126 '-'
            encoding[i] = rnd.nextInt((126 - 32) + 1) + 32;
            builder.append((char) encoding[i]);
        }

        example.setStringLength(strLen);
        example.setExampleString(builder.toString());
        example.setAsciiEncoding(encoding);

        EncodeAsciiStep subStep = new EncodeAsciiStep();
        subStep.setExample(example);

        //ToDo: multistep should be determined by the student model.
        subStep.setMultiStep(rnd.nextBoolean());

        Step step = new Step(1, 0, StepSubType.ENCODE_ASCII);
        step.setCurrentHintIndex(0);
        step.setNotifyTutor(true);
        step.setIsCompleted(false);
        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        step.setData(gson.toJson(subStep));

        // TaskState state = new TaskState();
        // state.set
        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ExampleType.ASCII_ENCODE);
        task.setDescription("Encode a string as ASCII values");
        task.addStep(step);

        // ToDo: Add the task to the session and update it.
        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(task));

        return reply;
    }

    /**
     * Handles client requests for a new add one bit example.
     *
     * @return a TutorReply
     */
    private TutorReply newAddOneBitExample(TutoringSession session, String jsonData) {
        Random rnd = new Random();

        BitOpExample example = gson.fromJson(jsonData, BitOpExample.class);

        int size = example.getPreSize();

        Account account = session.getAccount();

        if (size == 0) {
            // ToDo: The tutor should generate the string length and timeout
            // based on the the current student model.

            size = rnd.nextInt(MAX_ASCII_SIZE - 1) + 1;
            example.setTimeOut(600);
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            builder.append(String.valueOf(rnd.nextBoolean() ? 0 : 1));
        }

        example.setPreSize(size);
        example.setOperand1(builder.toString());
        example.setOperand2("");

        builder.append("1");

        example.setResult(builder.toString());
        example.setPostSize(size + 1);

        BitOpStep subStep = new BitOpStep();
        subStep.setExample(example);
        subStep.setMultiStep(false);

        Hint hint = new Hint();
        hint.setSequenceId(0);
        hint.setText("Add one bit with a value to the given bits.");

        Step step = new Step(1, 0, StepSubType.ADD_ONE_BIT);
        step.setCurrentHintIndex(0);
        step.addHint(hint);
        step.setNotifyTutor(true);
        step.setIsCompleted(false);
        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        step.setData(gson.toJson(subStep));

        // TaskState state = new TaskState();
        // state.set
        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ExampleType.ADD_ONE_BIT);
        task.setDescription("Add one bit to the given bit string");
        task.addStep(step);

        // ToDo: Add the task to the session and update it.
        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(task));

        return reply;
    }

    /**
     * Handles client requests for a new pad with zeros example.
     *
     * @return a TutorReply
     */
    private TutorReply newPadZerosExample(TutoringSession session, String jsonData) {
        TutorReply reply = new TutorReply(":Success");

        return reply;
    }

    /**
     * Handles client requests for a new add message length example.
     *
     * @return a TutorReply
     */
    private TutorReply newAddMsgLenExample(TutoringSession session, String jsonData) {
        TutorReply reply = new TutorReply(":Success");

        return reply;
    }

    /**
     * Handles client requests for a new prepare schedule example.
     *
     * @return a TutorReply
     */
    private TutorReply newPrepareScheduleExample(TutoringSession session, String jsonData) {
        TutorReply reply = new TutorReply(":Success");

        return reply;
    }

    /**
     * Handles client requests for a new initialize vars example.
     *
     * @return a TutorReply
     */
    private TutorReply newInitializeVarsExample(TutoringSession session, String jsonData) {
        TutorReply reply = new TutorReply(":Success");

        return reply;
    }

    /**
     * Handles client requests for a new compress round example.
     *
     * @return a TutorReply
     */
    private TutorReply newCompressRoundExample(TutoringSession session, String jsonData) {
        TutorReply reply = new TutorReply(":Success");

        return reply;
    }

    /**
     * Handles client requests for a new rotate bits example.
     *
     * @return a TutorReply
     */
    private TutorReply newRotateBitsExample(TutoringSession session, String jsonData) {
        TutorReply reply = new TutorReply(":Success");

        return reply;
    }

    /**
     * Handles client requests for a new shift bits zeros example.
     *
     * @return a TutorReply
     */
    private TutorReply newShiftBitsExample(TutoringSession session, String jsonData) {
        TutorReply reply = new TutorReply(":Success");

        return reply;
    }

    /**
     * Handles client requests for a new XOR bits example.
     *
     * @return a TutorReply
     */
    private TutorReply newXorBitsExample(TutoringSession session, String jsonData) {

        Random rnd = new Random();

        BitOpExample example = gson.fromJson(jsonData, BitOpExample.class);

        int size = example.getPreSize();

        if (size == 0) {
            // ToDo: The tutor should generate the string length and timeout
            // based on the the current student model.
            size = rnd.nextInt(MAX_BITS_SIZE - 1) + 1;
            example.setTimeOut(600);

        } else if (size > MAX_BITS_SIZE) {
            // The student is requesting practice for a specific string length.
            size = MAX_ASCII_SIZE;
            example.setTimeOut(0);
        }

        example.generatedRandomOperands(size);

        //example.setPreSize(size);
        //example.setPostSize(size);
        //int maxOperandVal = (int) Math.pow(2.0d, size) - 1; // e.g., 2^8 - 1 = 255
        // int operand1 = rnd.nextInt((maxOperandVal - 1) + 1);
        //example.setOperand1Val(operand1);
        //int operand2 = rnd.nextInt((maxOperandVal - 1) + 1);
        //example.setOperand2Val(operand2);
        //int xor = operand1 ^ operand2;
        int xor = (int) example.getOperand1Val() ^ (int) example.getOperand2Val();
        example.setResultVal(xor);

        /*
        builder.setLength(0); // clear the builder
        for (int i = 0; i < size; i++) {
            char char1 = operand1.charAt(i);
            char char2 = operand2.charAt(i);
            if (((char1 == '0') && (char2 == '0')) || ((char1 == '1') && (char2 == '1'))) {
                builder.append('0');
            } else {
                builder.append('1');
            }
        }
        
        example.setResult(builder.toString());
         */
        BitOpStep subStep = new BitOpStep();
        subStep.setExample(example);
        //ToDo: multistep should be determined by the student model.
        subStep.setMultiStep(rnd.nextBoolean());

        Step step = new Step(1, 0, StepSubType.XOR_BITS);
        step.setCurrentHintIndex(0);
        step.setNotifyTutor(true);
        step.setIsCompleted(false);
        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        step.setData(gson.toJson(subStep));

        // TaskState state = new TaskState();
        // state.set
        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ExampleType.XOR_BITS);
        task.setDescription("Xor the bits in the two operands");
        task.addStep(step);

        // ToDo: Add the task to the session and update it.
        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(task));

        return reply;
    }

    /**
     * Handles client requests for a new add bits example.
     *
     * Generates two operands with pre-size n bits. These are added together
     * using modulo-preSize arithmetic. For example, with preSize 8 bits:</br>
     * Operand1: 11100111:231 </br>
     * Operand2: 10101111:175 </br>
     * Intermediate: 110010110:406 </br>
     * Result: 10010110:150 (e.g., 406 % 256)
     *
     * @param session the current tutoring session.
     * @param jsonData a BitOpExample encoded object
     * @return a TutorReply
     */
    private TutorReply newAddBitsExample(TutoringSession session, String jsonData) {
        Random rnd = new Random();

        BitOpExample example = gson.fromJson(jsonData, BitOpExample.class);

        int size = example.getPreSize(); // number of bits

        if (size == 0) {
            // ToDo: The tutor should generate the string length and timeout
            // based on the the current student model.
            size = rnd.nextInt(MAX_BITS_SIZE - 1) + 1;
            example.setTimeOut(600);

        } else if (size > MAX_BITS_SIZE) {
            // The student is requesting practice for a specific string length.
            size = MAX_BITS_SIZE;
            example.setTimeOut(0);
        }

        example.setPreSize(size);

        int maxOperandVal = (int) Math.pow(2.0d, size) - 1; // e.g., 2^8 - 1 = 255

        long operand1 = rnd.nextLong((maxOperandVal - 1) + 1);
        example.setOperand1Val(operand1);

        long operand2 = rnd.nextLong((maxOperandVal - 1) + 1);
        example.setOperand2Val(operand2);

        // This is Mod size arithmetic
        example.setPostSize(size);
        long result = ((long) operand1) + ((long) operand2);

        if (result > maxOperandVal) {
            result = result % (maxOperandVal + 1);
        }

        example.setResultVal(result);

        BitOpStep subStep = new BitOpStep();
        subStep.setExample(example);
        //ToDo: multistep should be determined by the student model.
        subStep.setMultiStep(rnd.nextBoolean());

        Step step = new Step(1, 0, StepSubType.ADD_BITS);
        step.setCurrentHintIndex(0);
        step.setNotifyTutor(true);
        step.setIsCompleted(false);
        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        step.setData(gson.toJson(subStep));

        // TaskState state = new TaskState();
        // state.set
        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ExampleType.ADD_BITS);
        task.setDescription("Xor the bits in the two operands");
        task.addStep(step);

        // ToDo: Add the task to the session and update it.
        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(task));

        return reply;
    }

    /**
     * Handles client requests for a new majority function example.
     *
     * @return a TutorReply
     */
    private TutorReply newMajorityFunctionExample(TutoringSession session, String jsonData) {
        TutorReply reply = new TutorReply(":Success");

        return reply;
    }

    /**
     * Handles client requests for a new choice function example.
     *
     * @return a TutorReply
     */
    private TutorReply newChoiceFunctionExample(TutoringSession session, String jsonData) {
        TutorReply reply = new TutorReply(":Success");

        return reply;
    }

    /**
=======
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
     * Utility for logging an error and an creating a tutoring reply error with
     * the given message, and optional originating exception.
     *
     * @param errMsg a displayable error message
<<<<<<< HEAD
     * @param ex the original exception, if any, that caused the error,
     * otherwise null.
     * @return a TutorReply with an ":ERR" status
     */
    private TutorReply createError(String errMsg, Exception ex) {
=======
     * @param ex     the original exception, if any, that caused the error,
     *               otherwise null.
     * @return a TutorReply with an ":ERR" status
     */
    public TutorReply createError(String errMsg, Exception ex) {
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
        if (ex == null) {
            Logger.getLogger(ShaTuTutor.class.getName()).log(Level.SEVERE, errMsg);
        } else {
            Logger.getLogger(ShaTuTutor.class.getName()).log(Level.SEVERE, errMsg, ex);
        }

        return new TutorReply(":ERR", errMsg);
    }
<<<<<<< HEAD
=======

    /**
     * Method that generates and returns a random string.
     *
     * @param length
     *
     * @return
     */
    protected String generateRandomString(int length) {

        Random random = new Random();
        StringBuilder sb = new StringBuilder(length); // StringBuilder allows easier altering of a string.

        for (int i = 0; i < length; i++) {
            // Generates a random integer between 32 (inclusive) and 126 (inclusive)
            int randomChar = 32 + random.nextInt(95); // 126 - 32 + 1 = 95
            sb.append((char) randomChar);
        }

        return sb.toString();
    }

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
}
