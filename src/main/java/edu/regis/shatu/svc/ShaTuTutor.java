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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.regis.shatu.dao.SessionDAO;
import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjDuplicateException;
import edu.regis.shatu.err.ObjNotFoundException;
import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.Course;
import edu.regis.shatu.model.KnowledgeComponent;
import edu.regis.shatu.model.KnowledgeComponentKind;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.Task;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.Unit;
import edu.regis.shatu.model.aol.StudentModel;
import edu.regis.shatu.model.StudentModelFieldKind;
import edu.regis.shatu.model.aol.Assessment;
import edu.regis.shatu.model.aol.AssessmentLevel;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.aol.PendingStep;
import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.model.aol.Problem;
import edu.regis.shatu.model.aol.ProblemType;
import static edu.regis.shatu.model.aol.ProblemType.ADD_BITS;
import static edu.regis.shatu.model.aol.ProblemType.ADD_MSG_LENGTH;
import static edu.regis.shatu.model.aol.ProblemType.ADD_ONE_BIT;
import static edu.regis.shatu.model.aol.ProblemType.ASCII_ENCODE;
import static edu.regis.shatu.model.aol.ProblemType.CHOICE_FUNCTION;
import static edu.regis.shatu.model.aol.ProblemType.COMPRESS_ROUND;
import static edu.regis.shatu.model.aol.ProblemType.INITIALIZE_VARS;
import static edu.regis.shatu.model.aol.ProblemType.MAJORITY_FUNCTION;
import static edu.regis.shatu.model.aol.ProblemType.PAD_ZEROS;
import static edu.regis.shatu.model.aol.ProblemType.PREPARE_SCHEDULE;
import static edu.regis.shatu.model.aol.ProblemType.ROTATE_BITS;
import static edu.regis.shatu.model.aol.ProblemType.SHA_ONE;
import static edu.regis.shatu.model.aol.ProblemType.SHA_ZERO;
import static edu.regis.shatu.model.aol.ProblemType.SHIFT_BITS;
import static edu.regis.shatu.model.aol.ProblemType.XOR_BITS;
import edu.regis.shatu.model.aol.StepSubType;
import static edu.regis.shatu.model.aol.StepSubType.ENCODE_ASCII;
import static edu.regis.shatu.model.aol.StepSubType.ENCODE_BINARY;
import static edu.regis.shatu.model.aol.StepSubType.ENCODE_HEX;
import static edu.regis.shatu.model.aol.StepSubType.INFO_MESSAGE;
import edu.regis.shatu.model.aol.StudentModel;
import edu.regis.shatu.model.aol.TutoringMode;
import static edu.regis.shatu.model.aol.TutoringMode.DO_ONE;
import static edu.regis.shatu.model.aol.TutoringMode.SEE_ONE;
import static edu.regis.shatu.model.aol.TutoringMode.TEACH_ONE;
import edu.regis.shatu.model.steps.Step;
import edu.regis.shatu.model.steps.EncodeAsciiStep;
import edu.regis.shatu.objectives.*;
import java.util.HashMap;

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
    private static final Logger LOGGER = Logger.getLogger(ShaTuTutor.class.getName());

    /**
     * The current tutoring session, which contains information on the current
     * Student, StudentModel, Course, Task, Step, etc.
     */
    private TutoringSession session;

    private Student student;
    private StudentModel studentModel;

    /**
     * Convenience reference to the current gson object.
     */
    private Gson gson;

    private Objective currObjective;

    /**
     * Initialize the tutor singleton (a NoOp).
     */
    public ShaTuTutor() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TutorReply request(ClientRequest request) {
        // Uses reflection to invoke a method derived from the request name in
        // the client request (e.g., ":SignIn" invokes "signIn(...)").
        Logger.getLogger(ShaTuTutor.class.getName()).log(Level.INFO, request.getRequestType().getRequestName());

        // TODO: Consider making this parser into its own method.
        // Efficiently produce "signIn" from ":SignIn", for example.
        char c[] = request.getRequestType().getRequestName().toCharArray();
        c[1] = Character.toLowerCase(c[1]);

        char m[] = new char[c.length - 1];
        for (int i = 1; i < c.length; i++) {
            m[i - 1] = c[i];
        }

        String methodName = new String(m);

        // TODO: Consider making this switch into its own method.
        // Most methods require verifying the given security token with the
        // one current known in the DB for the given user.
        switch (methodName) {
            case "completedStep":
            case "completedTask":
            //case "getTask":
            case "newExample":
            case "requestHint":
            case "requestCorrectAnswer":
            case "resetPassword":
            case "updateAccount":
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

            default: // e.g., signIn itself, newAccount
                Logger.getLogger(ShaTuTutor.class.getName()).log(Level.INFO, "No token verification required");
        }

        // Security token has been verified or not required (e.g., signIn,
        // createAccount).
        try {
            Method method = getClass().getMethod(methodName, String.class);

            return (TutorReply) method.invoke(this, request.getData());

        } catch (NoSuchMethodException ex) {
            return createError("Tutor received an unknown request type: " + request.getRequestType().getRequestName(),
                    ex);
        } catch (SecurityException ex) {
            return createError("ShaTuTutor_ERR_2", ex);
        } catch (IllegalAccessException ex) {
            return createError("ShaTuTutor_ERR_3", ex);
        } catch (IllegalArgumentException ex) {
            return createError("ShaTuTutor_ERR_4", ex);
        } catch (InvocationTargetException ex) {
            return createError("ShaTuTutor_ERR_5", (Exception) ex.getCause());
        }
    }

    /**
     * Creates a new student account.
     *
     * This method handles ":CreateAccount" requests from the GUI client.
     *
     * @param jsonAcct a JSon encoded Account object
     * @throws NonRecoverableException perhaps see getCause().getErrorCode().
     * @return a TutorReply if successful the status is "Created", otherwise the
     *         status is ":ERR".
     */
    public TutorReply createAccount(String jsonAcct) throws NonRecoverableException {
        Account acct = gson.fromJson(jsonAcct, Account.class);

        int courseId = DEFAULT_COURSE_ID; // Currently only one course

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
     * Updates an existing student's account information in the database.
     * 
     * This method handles ":UpdateAccount" requests from the GUI client.
     * 
     * TODO: Database tables (Account & Student) are updated independently.
     * Determine if this should be done with a single transaction. Delete this TODO
     * when a decision is made and implemented.
     * 
     * @param jsonAcct a JSON representation of an account
     * @return a TutorReply indicating "Success" or ":ERR"
     */
    public TutorReply updateAccount(String jsonAcct) {
        Account account = gson.fromJson(jsonAcct, Account.class);
        Student student = new Student(account);
        
        try {
            ServiceFactory.findAccountSvc().update(account);    // Updates Account
            ServiceFactory.findStudentModelSvc().update(student);   // Updates Student
            return new TutorReply("Success");
        } catch (ObjNotFoundException ex) {
            Logger.getLogger(ShaTuTutor.class.getName()).log(Level.SEVERE, null, ex);
            return new TutorReply(":ERR");
        } catch (NonRecoverableException ex) {
            Logger.getLogger(ShaTuTutor.class.getName()).log(Level.SEVERE, null, ex);
            return new TutorReply(":ERR");
        }
    }

    /**
     * Verifies the user is in the database and the security question and answer
     * match.
     *
     * This method handles ":VerifyUser" requests from the GUI client.
     *
     * @param jsonAcct a JSon encoded Account object
     * @return a TutorReply if successful the status is "Verified", otherwise the
     *         status is ":ERR".
     * @throws edu.regis.shatu.err.NonRecoverableException
     */
    public TutorReply verifyUser(String jsonAcct) throws NonRecoverableException {
        Account requestAcct = gson.fromJson(jsonAcct, Account.class);
        AccountSvc acctSvc = ServiceFactory.findAccountSvc();

        if (!acctSvc.exists(acctSvc.getPrimaryKey(), requestAcct.getUserId())) {
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
     * Checks a user-provided password against the password stored in the
     * database for the user's account.
     * 
     * This method handles ":VerifyPassword" requests from the GUI client.
     * 
     * @param jsonAcct a JSON encoded Account object
     * @return a TutorReply object
     */
    public TutorReply verifyPassword(String jsonAcct) {
        Account requestAcct = gson.fromJson(jsonAcct, Account.class);
        
        try {
            Account dbAcct = ServiceFactory.findAccountSvc().retrieve(requestAcct.getUserId());
            
            if (dbAcct.getPassword().equals(requestAcct.getPassword())) {
                return new TutorReply("Authenticated");
            }
            else {
                return new TutorReply("InvalidPassword");
            }
        } catch (ObjNotFoundException e) {
            return new TutorReply(":ERR");
        } catch (NonRecoverableException ex) {
            Logger.getLogger(ShaTuTutor.class.getName()).log(Level.SEVERE, null, ex);
            return new TutorReply(":ERR");
        }
    }

    /**
     * Resets password for user currently in database.
     *
     * This method handles ":ResetPassword" requests from the GUI client.
     *
     * @param jsonAcct a JSon encoded Account object
     * @return a TutorReply if successful the status is "PasswordReset", otherwise the
     *         status is ":ERR".
     * @throws edu.regis.shatu.err.NonRecoverableException
     */
    public TutorReply resetPassword(String jsonAcct) throws NonRecoverableException {
        Account acct = gson.fromJson(jsonAcct, Account.class);
        AccountSvc acctSvc = ServiceFactory.findAccountSvc();

        if (!acctSvc.exists(acctSvc.getPrimaryKey(), acct.getUserId())) {
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
     *         data being a JSon encoded TutoringSession object.
     */
    public TutorReply signIn(String jsonUser) {
        Account requestAcct = gson.fromJson(jsonUser, Account.class);

        try {
            Account dbAcct = ServiceFactory.findAccountSvc().retrieve(requestAcct.getUserId());

            if (dbAcct.getPassword().equals(requestAcct.getPassword())) {
                //student = new Student(dbAcct);
                String userId = dbAcct.getUserId();             

                SessionSvc svc = ServiceFactory.findSessionSvc();
            
                session = svc.retrieve(dbAcct);
                
                student = session.getStudent();
                
                notifyLogin(student);

                TutorReply reply = new TutorReply("Authenticated");

                reply.setData(gson.toJson(session));

                return reply;

            } else {
                return new TutorReply("InvalidPassword");
            }

        } catch (ObjNotFoundException e) {
            return new TutorReply("UnknownUser");
        } catch (NonRecoverableException ex) {
            Logger.getLogger(ShaTuTutor.class
                    .getName()).log(Level.SEVERE, null, ex);
            return new TutorReply();
        }
    }
    
    /**
     * Attempts to sign a student out.
     * 
     * This method handles ":SignOut" requests from the GUI client.
     * 
     * It is invoked indirectly as a reflection from within request().
     *
     * @param jsonUser a JSON encoded User object
     * @return a TutorReply indicating "SIGN_OUT" for success or ":ERR" for failure
     */
    public TutorReply signOut(String jsonUser) {
        Account requestAcct = gson.fromJson(jsonUser, Account.class);

        try {
            student = new Student(requestAcct);
            notifyLogout(student);
            return new TutorReply("SIGN_OUT");
        }
        catch (ObjNotFoundException ex) {
            TutorReply reply = new TutorReply(":ERR");
            reply.setData("Student model not found for: " + requestAcct.getUserId());
            return reply;
        }
        catch (NonRecoverableException ex) {
            Logger.getLogger(ShaTuTutor.class.getName()).log(Level.SEVERE, null, ex);
            TutorReply reply = new TutorReply(":ERR");
            reply.setData("NonRecoverableException occured during sign-out");
            return reply;
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

    /**
     * Returns a hint to the GUI client, if any.
     *
     * This method handles ":RequestHint" requests from the GUI client.
     *
     * @param jsonObj
     * @return a TutorReply, if successful, the status is "Hint" with data being
     *         a displayable hint text string.
     */
    public TutorReply requestHint(String jsonObj) {
        System.out.println("requestHint");
        StepCompletion completion = gson.fromJson(jsonObj, StepCompletion.class);

        Step step = completion.getStep();

        switch (step.getSubType()) {
            case INFO_MESSAGE:
                return completeInfoMsgStep(completion);

            default:
                currObjective = getCurrentObjectiveByProbelmType(convertStepToProblemType(step.getSubType()));
                return currObjective.hint(completion);
        }
    }

    /**
     * Records that the student asked to see the correct answer after an
     * incorrect attempt.
     *
     * This method handles ":RequestCorrectAnswer" requests from the GUI client.
     *
     * @param jsonObj a JSon encoded StepCompletion object
     * @return a TutorReply with status ":Success" or ":ERR"
     */
    public TutorReply requestCorrectAnswer(String jsonObj) {
        StepCompletion completion = gson.fromJson(jsonObj, StepCompletion.class);

        if (completion == null || completion.getStep() == null) {
            return createError("Missing step information for correct answer request", null);
        }

        ProblemType problemType = convertStepToProblemType(completion.getStep().getSubType());
        KnowledgeComponentKind knowledgeComponentKind = mapProblemTypeToKnowledgeComponent(problemType);

        if (knowledgeComponentKind == null) {
            return createError("Unknown problem type for correct answer request: " + problemType, null);
        }

        Assessment assessment = studentModel.findAssessment(knowledgeComponentKind.dbId());

        if (assessment == null) {
            return createError("Assessment not found for: " + knowledgeComponentKind.title(), null);
        }

        assessment.incrementCorrectAnswersRequested();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.CORRECT_ANSWERS_REQUESTED);
        } catch (NonRecoverableException ex) {
            return createError("Unknown error updating correct answer request count", ex);
        }

        return new TutorReply(":Success");
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


    /**
     * Handles the INFO_MESSAGE step completion
     * @param completion
     * @return
     */
    public TutorReply completeInfoMsgStep(StepCompletion completion) {
        // Advance past the one-time INFO_MESSAGE so it won't reappear on the next sign-in.
        // IMPORTANT: The client UI assumes there is ALWAYS at least one pending task in the session,
        // so we update the existing PendingTask row instead of deleting it.
        try {
        if (session != null
                && session.getStudent() != null
                && session.getStudent().getStudentModel() != null) {

            StudentModel studentModel = session.getStudent().getStudentModel();
            Assessment assessment = studentModel.findAssessment(0);

            if (assessment != null) {
                assessment.setExposures(assessment.getExposures() + 1);
                assessment.setSuccessess(assessment.getSuccessess() + 1);
                assessment.setAssessment(AssessmentLevel.LOW);
                
                StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ATTEMPTS);
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.SUCCESSES);
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ASSESSMENT_LEVEL);
            }
        }
        } catch (Exception ex) {
        Logger.getLogger(ShaTuTutor.class.getName())
              .log(Level.WARNING, "Unable to update INFORMATION_MESSAGE assessment on info-step completion.", ex);
        }
        
        try {
           
            
        if (session != null) {
            SessionDAO dao = (SessionDAO) ServiceFactory.findSessionSvc();
            // Welcome task is always TaskId = 0
            dao.deletePendingTask(session.getId(), 0);

            // Optional: also remove from in-memory model if it’s present
            if (session.getTasks() != null) {
                session.getTasks().removeIf(pt -> pt.getTask() != null && pt.getTask().getId() == 0);
            }
        }
    } catch (Exception ex) {
        Logger.getLogger(ShaTuTutor.class.getName())
              .log(Level.WARNING, "Unable to clear welcome task from PendingTask", ex);
    }
        TutoringMode mode = session.getTutoringMode();
        ProblemType firstProblemType;
        
        //Does this need to be updated to display where they last where for DO_ONE,  TEACH_ONE?? Will they always start with ASCII?
        switch (mode) {
            case SEE_ONE:
                // First demonstration: ASCII Encoding
                firstProblemType = ProblemType.ASCII_ENCODE;
                break;
            case DO_ONE:
                // First practice: typically starts with ASCII_ENCODE as well
                firstProblemType = ProblemType.ASCII_ENCODE;
                break;
            case TEACH_ONE:
                // Teaching mode: starts with ASCII_ENCODE
                firstProblemType = ProblemType.ASCII_ENCODE;
                break;
            default:
                firstProblemType = ProblemType.ASCII_ENCODE;
        }
    
        // Get the objective for the first task
        currObjective = getCurrentObjectiveByProbelmType(firstProblemType);
        
        // Generate an example for this task
        // Use the message from the current problem in the session, or a default message
        String messageToHash;
        if (session.getProblem() != null && session.getProblem().getMessageToHash() != null) {
            messageToHash = session.getProblem().getMessageToHash();
        } else {
            // Default message for demonstration
            messageToHash = "Regis Computer Science Rocks!";
        }
        
        EncodeAsciiStep encodeStep = new EncodeAsciiStep();
        encodeStep.setQuestion(messageToHash);
        String jsonData = gson.toJson(encodeStep);

        return currObjective.example(session, jsonData);
    }

    public TutorReply completedTask(String taskInfo) {
        return new TutorReply();
    }

    /**
     * Handles :NewExample requests from the client.
     * Returns a new problem for the student, either specified or adaptively chosen.
     *
     * @param json JSon encoding a NewExampleRequest; may specify ProblemType
     * @return TutorReply with the generated Problem
     */
    public TutorReply newExample(String json) {
        NewExampleRequest request;
        if (json != null && !json.isEmpty()) {
            request = gson.fromJson(json, NewExampleRequest.class);
        } else {
            request = new NewExampleRequest(); // default request
        }

        ProblemType type = (request.getExampleType() != null)
                ? request.getExampleType()
                : determineNextProblemType(studentModel);

        currObjective = getCurrentObjectiveByProbelmType(type);

        // Use the existing method in Objective subclasses
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
        }
    }

    /**
     * Map a problem type to the associated knowledge component.
     *
     * @param problemType the problem type for which the student asked to see the answer
     * @return the matching KnowledgeComponentKind or null if none exists
     */
    private KnowledgeComponentKind mapProblemTypeToKnowledgeComponent(ProblemType problemType) {
        if (problemType == null) {
            return null;
        }

        switch (problemType) {
            case ASCII_ENCODE:
                return KnowledgeComponentKind.ASCII_ENCODE;
            case ADD_ONE_BIT:
                return KnowledgeComponentKind.ADD_ONE_BIT;
            case PAD_ZEROS:
                return KnowledgeComponentKind.PAD_ZEROS;
            case ADD_MSG_LENGTH:
                return KnowledgeComponentKind.ADD_MSG_LENGTH;
            case PREPARE_SCHEDULE:
                return KnowledgeComponentKind.PREPARE_SCHEDULE;
            case INITIALIZE_VARS:
                return KnowledgeComponentKind.INITIALIZE_VARS;
            case COMPRESS_ROUND:
                return KnowledgeComponentKind.COMPRESS_ROUND;
            case ROTATE_BITS:
                return KnowledgeComponentKind.ROTATE_BITS;
            case SHIFT_BITS:
                return KnowledgeComponentKind.SHIFT_BITS;
            case XOR_BITS:
                return KnowledgeComponentKind.XOR_BITS;
            case ADD_BITS:
                return KnowledgeComponentKind.ADD_BITS;
            case MAJORITY_FUNCTION:
                return KnowledgeComponentKind.MAJORITY_FUNCTION;
            case CHOICE_FUNCTION:
                return KnowledgeComponentKind.CHOICE_FUNCTION;
            case SHA_ZERO:
                return KnowledgeComponentKind.SHA_ZERO;
            case SHA_ONE:
                return KnowledgeComponentKind.SHA_ONE;
            default:
                return null;
        }
    }

    /**
     * Create and save a new tutoring session associated with the given account.
     *
     * @param account the student user
     * @throws NonRecoverableException
     * @return the new TutoringSession
     */
    private TutoringSession createSession(Student student, Course course) throws NonRecoverableException, ObjNotFoundException {
        Account account = student.getAccount();

        TutoringSession tSession = new TutoringSession(student);
        tSession.setStartDate(new GregorianCalendar());
        tSession.setCourse(course.getDigest());
        tSession.setUnit(course.currentUnit().getDigest());

        //set tutoringmode to SEE_ONE or Unit 0
        tSession.setTutoringMode(TutoringMode.SEE_ONE);
        
        Unit unit = course.findUnitBySequenceId(0); // The first unit
        tSession.setUnit(unit.getDigest());
        
        Problem problem = unit.findProblemBySequence(0); // First Problem
        tSession.setProblem(problem);
        
        Task task = problem.findTaskBySequence(0); //Task task = getFirstTask(course);
        PendingTask pendingTask = new PendingTask(task);
        pendingTask.setCurrentStep(new PendingStep(task.getCurrentStep()));
        tSession.addTask(pendingTask);
        

        // Generate the security token for this tutoring session.
        Random rnd = new Random();
        String clearToken = "Session" + account.getUserId() + Integer.toString(rnd.nextInt());
        tSession.setSecurityToken(SHA_256.instance().sha256(clearToken));
        
        try {
            ServiceFactory.findSessionSvc().create(tSession);

            return tSession;

        } catch (ObjDuplicateException ex) {
            throw new NonRecoverableException("Session already exists", ex);
        }
    }

    /**
     * Create and save the student and their initial student model.
     *
     * @param acct
     * @param course
     * @return
     */
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

    }

    /**
     * Verify that the user with the given id has a session with the given
     * session id.
     *
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
    }

    /**
     * Return the first task that should be performed in the given course.
     *
     * @param course
     * @return a Task that should be completed first.
     * @throws NonRecoverableException see the message text.
     */
    /*
    private Task getFirstTask(Course course) throws NonRecoverableException {
        switch (course.getPrimaryPedagogy()) {
            case STUDENT_CHOICE:
                return null; // ToDo

            case FIXED_SEQUENCE:
                Unit unit = course.findUnitBySequenceId(0);

                if (unit == null) {
                    throw new NonRecoverableException("Unit 0 not found in course: " + course.getId());
                }

                Task task = unit.findTaskBySequence(0);

                if (task == null) {
                    throw new NonRecoverableException("Task 0 not found in Unit 0 of course: " + course.getId());
                }

                return task;

            case MASTERY_LEARNING:
                return null; // ToDo

            case MICROADAPTATION:
                return null; // ToDo

            default:
                throw new NonRecoverableException("Unknwon task selection in course: " + course.getId());
        }
    }
    */

    /**
     * Utility for logging an error and an creating a tutoring reply error with
     * the given message, and optional originating exception.
     *
     * @param errMsg a displayable error message
     * @param ex     the original exception, if any, that caused the error,
     *               otherwise null.
     * @return a TutorReply with an ":ERR" status
     */
    public TutorReply createError(String errMsg, Exception ex) {
        if (ex == null) {
            Logger.getLogger(ShaTuTutor.class.getName()).log(Level.SEVERE, errMsg);
        } else {
            Logger.getLogger(ShaTuTutor.class.getName()).log(Level.SEVERE, errMsg, ex);
        }

        return new TutorReply(":ERR", errMsg);
    }

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

    /**
     * Update the database to record the fact the student logged in.
     * 
     * @param student the student who logged in.
     * @throws ObjNotFoundException
     * @throws NonRecoverableException 
     */
    private void notifyLogin(Student student) throws ObjNotFoundException, NonRecoverableException {
        StudentModelSvc stuModelSvc = ServiceFactory.findStudentModelSvc();
        
        Date now = new Date();
        long milliseconds = now.getTime();
        
        student.setLastLogin(milliseconds);
        stuModelSvc.recordLoginEvent(student.getAccount().getUserId(), milliseconds);

       // LOGGER.log(Level.INFO, "Student {0} logged in at {1}", new Object[]{student.getAccount().getUserId(), milliseconds});
    }

    /**
     * Update the database to record the fact the student logged out.
     * 
     * @param student the student who logged out.
     * @throws ObjNotFoundException
     * @throws NonRecoverableException 
     */
    private void notifyLogout(Student student) throws ObjNotFoundException, NonRecoverableException {
        StudentModelSvc stuModelSvc = ServiceFactory.findStudentModelSvc();
        
        Date now = new Date();
        long milliseconds = now.getTime();
        
        student.setLastLogout(milliseconds);
        stuModelSvc.recordLogoutEvent(student.getAccount().getUserId(), milliseconds);

       // LOGGER.log(Level.INFO, "Student {0} logged out at {1}", new Object[]{student.getAccount().getUserId(), milliseconds});
    }

    /**
     * Determines the next problem type to suggest for the student
     * based on their progress and performance.
     *
     * @param student the Student whose model is used
     * @return the suggested next ProblemType
     */
    private ProblemType determineNextProblemType(StudentModel model) {
        for (ProblemType type : ProblemType.values()) {
            if (!model.hasCompleted(type)) {
                return type;
            }
        }
        return model.getWeakestProblemType();
    }
}