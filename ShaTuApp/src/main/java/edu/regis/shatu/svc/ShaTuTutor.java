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
import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.regis.shatu.err.IllegalArgException;
import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;
import edu.regis.shatu.model.*;
import edu.regis.shatu.model.aol.Assessment;
import edu.regis.shatu.model.aol.AssessmentLevel;
import edu.regis.shatu.model.aol.BitOpExample;
import edu.regis.shatu.model.aol.BitOpStep;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.aol.PendingStep;
import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.RotateStep;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.aol.StudentModel;
import edu.regis.shatu.model.aol.TaskKind;
import edu.regis.shatu.model.aol.Timeout;

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
    private static final Logger LOGGER
            = Logger.getLogger(ShaTuTutor.class.getName());

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

        // Efficiently produce "signIn" from ":SignIn", for example.         
        char c[] = request.getRequestType().getRequestName().toCharArray();
        c[1] = Character.toLowerCase(c[1]);

        char m[] = new char[c.length - 1];
        for (int i = 1; i < c.length; i++) {
            m[i - 1] = c[i];
        }

        String methodName = new String(m);

        // Most methods require verifying the given security token with the 
        // one current known in the DB for the given user.
        switch (methodName) {
            case "completedStep":
            case "completedTask":
            case "getTask":
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
                            reply.setData("Student model not found for: " + userId );
                            return reply;
                        }
    
                        
                        session = ServiceFactory.findSessionSvc().retrieve(student);
        
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

        // Security token has been verified or not required (e.g., signIn, createAccount).
        try {
            Method method = getClass().getMethod(methodName, String.class);

            return (TutorReply) method.invoke(this, request.getData());

        } catch (NoSuchMethodException ex) {
            return createError("Tutor received an unknown request type: " + request.getRequestType().getRequestName(), ex);
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
     * Creates a new student account
     *
     * This method handles ":CreateAccount" requests from the GUI client.
     *
     * @param jsonAcct a JSon encoded Account object
     * @throws NonRecoverableException perhaps see getCause().getErrorCode().
     * @return a TutorReply if successful the status is "Created", otherwise the
     * status is ":ERR".
     */
    public TutorReply createAccount(String jsonAcct) throws NonRecoverableException {
        Account acct = gson.fromJson(jsonAcct, Account.class);

        int courseId = DEFAULT_COURSE_ID; // Currently only one course

        try {
            ServiceFactory.findAccountSvc().create(acct);

            try {
                Course course = ServiceFactory.findCourseSvc().retrieve(courseId);

                student = createStudent(acct, course);

                createSession(student, course);

                return new TutorReply("Created");

            } catch (ObjNotFoundException ex) {
                return createError("Unknown course: " + courseId, null);
            }

        } catch (IllegalArgException ex) { // The account already exists
            return new TutorReply("IllegalUserId");
        }
    }

    /**
     * Verifies the user is in the database and the security question and answer
     * match.
     *
     * This method handles ":VerifyUser" requests from the GUI client.
     *
     * @param jsonAcct a JSon encoded Account object
     * @return a TutorReply if successful the status is "Created", otherwise the
     * status is ":ERR".
     * @throws edu.regis.shatu.err.NonRecoverableException
     */
    public TutorReply verifyUser(String jsonAcct) throws NonRecoverableException {
        Account requestAcct = gson.fromJson(jsonAcct, Account.class);

        AccountSvc acctSvc = ServiceFactory.findAccountSvc();
        if (!acctSvc.exists(requestAcct.getUserId())) {
            return new TutorReply("IllegalUserId");
        }

        try {
            Account dbAcct = acctSvc.retrieve(requestAcct.getUserId());

            if ((dbAcct.getSecurityAnswer().equals(requestAcct.getSecurityAnswer()))
                    && (dbAcct.getSecurityQuestion() == requestAcct.getSecurityQuestion())) {

                TutorReply reply = new TutorReply("Verified");

                return reply;

            } else {
                return new TutorReply("InvalidAnswer");
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
     * Resets password for user currently in database
     *
     * This method handles ":ResetPassword" requests from the GUI client.
     *
     * @param jsonAcct a JSon encoded Account object
     * @return a TutorReply if successful the status is "Created", otherwise the
     * status is ":ERR".
     * @throws edu.regis.shatu.err.NonRecoverableException
     */
    public TutorReply resetPassword(String jsonAcct) throws NonRecoverableException {
        Account acct = gson.fromJson(jsonAcct, Account.class);

        AccountSvc acctSvc = ServiceFactory.findAccountSvc();

        if (!acctSvc.exists(acct.getUserId())) {
            return new TutorReply("IllegalUserId");
        }

        try {
            acctSvc.update(acct);

            // ToDo: Account updated.
            return new TutorReply("PasswordReset");

        } catch (ObjNotFoundException ex) {
            // Should never get here since we tested whether the account exists
            return new TutorReply("IllegalUserId");
        } catch (IllegalArgException ex) {
            // ToDo: More specific err information returned 
            return new TutorReply("IllegalArg");
        }
    }

    /**
     * Attempts to sign a student in.
     *
     * This method handles ":SignIn" requests from the GUI client.
     *
     * @param jsonUser a JSon encoded User object
     * @return a TutorReply, if successful, the status is "Authenticated" with
     * data being a JSon encoded TutoringSession object.
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
                    reply.setData("Student model not found in sign in for: " + userId );
                    return reply;
                }

                SessionSvc svc = ServiceFactory.findSessionSvc();
                TutoringSession session = svc.retrieve(student);

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

    public TutorReply getTask(String jsonObj) {
        System.out.println("get task method");

        Task task = new Task();
        int[] taskOrder = {102, 103, 104, 105, 106, 107, 108, 109, 101, 110, 111, 100, 112};
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
                break;
            case 108:
                task.setType(ProblemType.COMPRESS_ROUND);
                break;
            case 101:
                task.setType(ProblemType.SHIFT_BITS);
                break;
            case 110:
                task.setType(ProblemType.XOR_BITS);
                break;
            case 111:
                task.setType(ProblemType.ADD_BITS);
                break;
            case 112:
                task.setType(ProblemType.MAJORITY_FUNCTION);
                break;
            case 103:
                task.setType(ProblemType.ADD_ONE_BIT);
                break;
            case 104:
                task.setType(ProblemType.PAD_ZEROS);
                break;
            case 109:
                task.setType(ProblemType.ROTATE_BITS);
                break;
            case 107:
                task.setType(ProblemType.INITIALIZE_VARS);
                break;
            case 100:
                task.setType(ProblemType.CHOICE_FUNCTION);
                break;
            case 105:
                task.setType(ProblemType.ADD_MSG_LENGTH);
                break;
            default:
                task.setType(ProblemType.ASCII_ENCODE);
                break;
        }
        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(task));
        return reply;
    }

    /**
     * Returns a hint to the GUI client, if any
     *
     * This method handles ":RequestHint" requests from the GUI client.
     *
     * @param jsonObj
     * @return a TutorReply, if successful, the status is "Hint" with data being
     * a displayable hint text string.
     */
    public TutorReply requestHint(String jsonObj) {
        System.out.println("requestHint");
        StepCompletion completion = gson.fromJson(jsonObj, StepCompletion.class);

        Step step = completion.getStep();

        switch (step.getSubType()) {
            case INFO_MESSAGE:
                return completeInfoMsgStep(completion);
            case ENCODE_BINARY: // TO_DO: Really the same
            case ENCODE_HEX:
            case ENCODE_ASCII:
                return hintEncode(completion);

            case ADD_ONE_BIT:
                return hintAddOne(completion);

            case PAD_ZEROS:
                return hintPadZeros(completion);

            case ADD_MSG_LENGTH:
                return hintAddMsgLen(completion);

            case PREPARE_SCHEDULE:
                return hintPrepareSchedule(completion);

            case INITIALIZE_VARS:
                return hintInitVars(completion);

            case COMPRESS_ROUND:
                return hintCompressRound(completion);

            case ROTATE_BITS:
                return hintRotateBits(completion);

            case SHIFT_BITS:
                return hintShiftBits(completion);

            case XOR_BITS:
                return hintXorBits(completion);

            case ADD_BITS:
                return hintAddBits(completion);

            case MAJORITY_FUNCTION:
                return hintMajorityFunction(completion);

            case CHOICE_FUNCTION:
                return hintChoiceFunction(completion);

            case SHA_ZERO:
                return hintShaZeroFunction(completion);

            default:
                return createError("Unknown step completion: " + step.getSubType(), null);
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

            case SHA_ZERO:
                return completeSigmaZeroStep(completion);

            default:
                return createError("Unknown step completion: " + step.getSubType(), null);
        }
    }

    public TutorReply completeRotateStep(StepCompletion completion) {
        System.out.println("Tutor completeRotateStep");
        RotateStep example = gson.fromJson(completion.getData(), RotateStep.class);
        int amount = example.getAmount();
        String data = example.getData();

        String expectedResult = performBitRotation(data, amount);

        StepCompletionReply stepReply = new StepCompletionReply();
        String result = example.getUserResponse();

        stepReply.setCorrectAnswer(expectedResult);
        stepReply.setResponse(result);

        System.out.println("Answer: " + expectedResult);

        if (expectedResult.equals(result)) {
            stepReply.setIsCorrect(true);
            stepReply.setIsRepeatStep(false);
            stepReply.setIsNewStep(true);
            stepReply.setIsNewTask(true);
            stepReply.setIsNextStep(false);

            // Update the assessment data and save it to the database.
            int dbId = KnowledgeComponentKind.fromString("Rotate n BITS").dbId();
            Assessment assessment = studentModel.findAssessment(dbId);
            assessment.incrementSuccessess();

            int exposures = assessment.getExposures();
            int successes = assessment.getSuccessess();

            if (exposures > 0 && (double) successes / exposures > 0.6) {
                stepReply.setIsNewTask(true);
                System.out.println("%%%%%%%%%%% Next Task Recommended");
                assessment.setAssessment(AssessmentLevel.COMPLETED);
            } else {
                stepReply.setIsNewTask(false);
            }

            try {
                StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.SUCCESSES);

            } catch (NonRecoverableException ex) {
                return createError("Unknown error", ex);
            }

        } else {
            stepReply.setIsCorrect(false);
            stepReply.setIsRepeatStep(true);
            stepReply.setIsNewStep(false);
            stepReply.setIsNewTask(false);
            stepReply.setIsNextStep(false);
        }

        Step step = new Step(1, 0, StepSubType.STEP_COMPLETION_REPLY);

        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.STEP_COMPLETION_REPLY);
        task.setDescription("Choose your next action");
        task.addStep(step);

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        PendingTask pendingTask = new PendingTask(task);
        pendingTask.setCurrentStep(pendingStep);

        TutorReply reply = new TutorReply(":Success");

        reply.setData(gson.toJson(pendingTask));

        return reply;
    }

    public TutorReply completeInfoMsgStep(StepCompletion completion) {
        TutorReply reply = new TutorReply(":StepCompletionReply");

        return reply;
    }

    /**
     * This method is called from the EncodeAsciiView when the check button is
     * clicked, will check the users answer to the correct answer generated by
     * the newEncodeAsciiStep method in this file.
     *
     * @param completion
     *
     * @return
     */
    public TutorReply completeEncodeStep(StepCompletion completion) {
        EncodeAsciiStep completedEncodeAsciiStep = gson.fromJson(completion.getData(), EncodeAsciiStep.class); // EncodeAsciiStep that was created in the stepCompletion function in the EncodeAsciiView

        String userAnswer = completedEncodeAsciiStep.getUserAnswer(); // What the user submitted as the answer. 
        String correctAnswer = completedEncodeAsciiStep.getResult();

        System.out.println("Correct Answer: " + correctAnswer); // Error checking
        System.out.println("User Answer: " + userAnswer); // Error checking

        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setCorrectAnswer(correctAnswer);
        stepReply.setResponse(userAnswer);

        if (userAnswer.equals(correctAnswer)) { // User was correct
            System.out.println("Answer was correct, correct if branch taken."); // Error checking.
            stepReply.setIsCorrect(true);
            stepReply.setIsRepeatStep(false);
            stepReply.setIsNewStep(true);
            stepReply.setIsNewTask(true);
            stepReply.setIsNextStep(false);

            // Update the assessment data and save it to the database.
            int dbId = KnowledgeComponentKind.fromString("ASCII Encode").dbId();
            Assessment assessment = studentModel.findAssessment(dbId);
            assessment.incrementSuccessess();

            int exposures = assessment.getExposures();
            int successes = assessment.getSuccessess();

            if (exposures > 0 && (double) successes / exposures > 0.6) {
                stepReply.setIsNewTask(true);
                System.out.println("%%%%%%%%%%% Next Task Recommended");
                assessment.setAssessment(AssessmentLevel.COMPLETED);
            } else {
                stepReply.setIsNewTask(false);
            }

            try {
                StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.SUCCESSES);
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ASSESSMENT_LEVEL);

            } catch (NonRecoverableException ex) {
                return createError("Unknown error", ex);
            }

        } else { // User was wrong
            System.out.println("Answer was not correct, correct if branch taken."); // Error checking
            stepReply.setIsCorrect(false);
            stepReply.setIsRepeatStep(true);
            stepReply.setIsNewStep(false);
            stepReply.setIsNewTask(false);
            stepReply.setIsNextStep(false);
        }

        Step step = new Step(1, 0, StepSubType.STEP_COMPLETION_REPLY);

        // ToDo: fix timeouts  
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.STEP_COMPLETION_REPLY);
        task.setDescription("Choose your next action");
        task.addStep(step);

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        PendingTask pendingTask = new PendingTask(task);
        pendingTask.setCurrentStep(pendingStep);

        TutorReply reply = new TutorReply(":Success");

        reply.setData(gson.toJson(pendingTask));

        return reply;
    }

    /**
     * This method is called from the AddOneView when the check button is
     * clicked, will check the users answer to the correct answer generated by
     * the newAddOneStep method in this file.
     *
     * @param completion
     *
     * @return
     */
    public TutorReply completeAddOneStep(StepCompletion completion) {

        AddOneStep completedAddOneStep = gson.fromJson(completion.getData(), AddOneStep.class); // AddOneStep that was created in the stepCompletion function in the AddOneView

        String userAnswer = completedAddOneStep.getUserAnswer(); // What the user submitted as the answer. 
        String correctAnswer = completedAddOneStep.getResult();

        System.out.println("Correct Answer: " + correctAnswer); // Error checking
        System.out.println("User Answer: " + userAnswer); // Error checking

        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setCorrectAnswer(correctAnswer);
        stepReply.setResponse(userAnswer);

        if (userAnswer.equals(correctAnswer)) { // User was correct
            System.out.println("Answer was correct, correct if branch taken."); // Error checking.
            stepReply.setIsCorrect(true);
            stepReply.setIsRepeatStep(false);
            stepReply.setIsNewStep(true);

            // ToDo: Use the student model to figure out whether we want
            // to give the student another practice problem of the same
            // type or move on to an entirely different problem.
            stepReply.setIsNewTask(true);

            // ToDo: currently only one step in a task, so there isn't a next one???
            stepReply.setIsNextStep(false);

            // Update the assessment data and save it to the database.
            int dbId = KnowledgeComponentKind.fromString("Add One Bit").dbId();
            Assessment assessment = studentModel.findAssessment(dbId);
            assessment.incrementSuccessess();

            int exposures = assessment.getExposures();
            int successes = assessment.getSuccessess();

            if (exposures > 0 && (double) successes / exposures > 0.6) {
                stepReply.setIsNewTask(true);
                System.out.println("%%%%%%%%%%% Next Task Recommended");
                assessment.setAssessment(AssessmentLevel.COMPLETED);
            } else {
                stepReply.setIsNewTask(false);
            }

            try {
                StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.SUCCESSES);
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ASSESSMENT_LEVEL);

            } catch (NonRecoverableException ex) {
                return createError("Unknown error", ex);
            }

        } else { // User was wrong
            System.out.println("Answer was not correct, correct if branch taken."); // Error checking
            stepReply.setIsCorrect(false);
            stepReply.setIsRepeatStep(true);
            stepReply.setIsNewStep(false);
            stepReply.setIsNewTask(false);
            stepReply.setIsNextStep(false);
        }

        Step step = new Step(1, 0, StepSubType.STEP_COMPLETION_REPLY);
        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.STEP_COMPLETION_REPLY);
        task.setDescription("Choose your next action");
        task.addStep(step);

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        PendingTask pendingTask = new PendingTask(task);
        pendingTask.setCurrentStep(pendingStep);

        TutorReply reply = new TutorReply(":Success");

        reply.setData(gson.toJson(pendingTask));

        return reply;
    }

    /**
     * This method is called from the Pad0View when the check button is clicked,
     * will check the users answer to the correct answer generated by the
     * newPad0Step method in this file.
     *
     * @param completion
     *
     * @return
     */
    public TutorReply completePadZerosStep(StepCompletion completion) {

        Pad0Step completedPadZeroStep = gson.fromJson(completion.getData(), Pad0Step.class);

        String userAnswer = completedPadZeroStep.getUserAnswer();
        String correctAnswer = completedPadZeroStep.getResult();

        System.out.println("user answer: " + userAnswer); // Error checking
        System.out.println("Correct answer: " + correctAnswer); // Error checking

        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setCorrectAnswer(correctAnswer);
        stepReply.setResponse(userAnswer);

        if (userAnswer.equals(correctAnswer)) { // User was correct
            System.out.println("Answer was correct, correct if branch taken."); // Error checking
            stepReply.setIsCorrect(true);
            stepReply.setIsRepeatStep(false);
            stepReply.setIsNewStep(true);

            // ToDo: Use the student model to figure out whether we want
            // to give the student another practice problem of the same
            // type or move on to an entirely different problem.
            stepReply.setIsNewTask(true);

            // ToDo: currently only one step in a task, so there isn't a next one???
            stepReply.setIsNextStep(false);

            // Update the assessment data and save it to the database.
            int dbId = KnowledgeComponentKind.fromString("Pad with Zeros").dbId();
            Assessment assessment = studentModel.findAssessment(dbId);
            assessment.incrementSuccessess();

            try {
                StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.SUCCESSES);

            } catch (NonRecoverableException ex) {
                return createError("Unknown error", ex);
            }

            int exposures = assessment.getExposures();
            int successes = assessment.getSuccessess();

            if (exposures > 0 && (double) successes / exposures > 0.6) {
                stepReply.setIsNewTask(true);
                System.out.println("%%%%%%%%%%% Next Task Recommended");
            } else {
                stepReply.setIsNewTask(false);
            }

        } else { // User was wrong
            System.out.println("Answer was not correct, correct if branch taken."); // Error checking
            stepReply.setIsCorrect(false);
            stepReply.setIsRepeatStep(true);
            stepReply.setIsNewStep(false);
            stepReply.setIsNewTask(false);
            stepReply.setIsNextStep(false);
        }

        Step step = new Step(1, 0, StepSubType.STEP_COMPLETION_REPLY);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.STEP_COMPLETION_REPLY);
        task.setDescription("Choose your next action");
        task.addStep(step);

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        PendingTask pendingTask = new PendingTask(task);
        pendingTask.setCurrentStep(pendingStep);

        TutorReply reply = new TutorReply(":Success");

        reply.setData(gson.toJson(pendingTask));

        return reply;
    }

    /**
     * Function that is called from the overrided stepCompletion method from the
     * MessageLenView. Checks the users answer with the correct answer and will
     * provide the user with further guidance.
     *
     * @param completion
     * @return
     */
    public TutorReply completeAddMsgLenStep(StepCompletion completion) {
        MessageLenStep completedMessageLenStep = gson.fromJson(completion.getData(), MessageLenStep.class);

        String userAnswer = completedMessageLenStep.getUserAnswer();
        String correctAnswer = completedMessageLenStep.getResult();

        System.out.println("user answer: " + userAnswer); // Error checking
        System.out.println("Correct answer: " + correctAnswer); // Error checking

        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setCorrectAnswer(correctAnswer);
        stepReply.setResponse(userAnswer);

        if (userAnswer.equals(correctAnswer)) { // User was correct
            System.out.println("Answer was correct, correct if branch taken."); // Error checking
            stepReply.setIsCorrect(true);
            stepReply.setIsRepeatStep(false);
            stepReply.setIsNewStep(true);

            // ToDo: Use the student model to figure out whether we want
            // to give the student another practice problem of the same
            // type or move on to an entirely different problem.
            stepReply.setIsNewTask(true);

            // ToDo: currently only one step in a task, so there isn't a next one???
            stepReply.setIsNextStep(false);

            // Update the assessment data and save it to the database.
            int dbId = KnowledgeComponentKind.fromString("Add Message Length").dbId();
            Assessment assessment = studentModel.findAssessment(dbId);
            assessment.incrementSuccessess();

            int exposures = assessment.getExposures();
            int successes = assessment.getSuccessess();

            if (exposures > 0 && (double) successes / exposures > 0.6) {
                stepReply.setIsNewTask(true);
                System.out.println("%%%%%%%%%%% Next Task Recommended");
                assessment.setAssessment(AssessmentLevel.COMPLETED);
            } else {
                stepReply.setIsNewTask(false);
            }

            try {
                StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.SUCCESSES);
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ASSESSMENT_LEVEL);

            } catch (NonRecoverableException ex) {
                return createError("Unknown error", ex);
            }

        } else { // User was wrong
            System.out.println("Answer was not correct, correct if branch taken."); // Error checking
            stepReply.setIsCorrect(false);
            stepReply.setIsRepeatStep(true);
            stepReply.setIsNewStep(false);
            stepReply.setIsNewTask(false);
            stepReply.setIsNextStep(false);
        }

        Step step = new Step(1, 0, StepSubType.STEP_COMPLETION_REPLY);
        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.STEP_COMPLETION_REPLY);
        task.setDescription("Choose your next action");
        task.addStep(step);

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        PendingTask pendingTask = new PendingTask(task);
        pendingTask.setCurrentStep(pendingStep);

        TutorReply reply = new TutorReply(":Success");

        reply.setData(gson.toJson(pendingTask));

        return reply;
    }

    public TutorReply completePrepareScheduleStep(StepCompletion completion) {
        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setResponse(" ");

        stepReply.setIsCorrect(true);
        stepReply.setIsRepeatStep(false);
        stepReply.setIsNewStep(true);

        // ToDo: Use the student model to figure out whether we want
        // to give the student another practice problem of the same
        // type or move on to an entirely different problem.
        stepReply.setIsNewTask(true);

        // ToDo: currently only one step in a task, so there isn't a next one???
        stepReply.setIsNextStep(false);

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Prepare Schedule").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementSuccessess();

        int exposures = assessment.getExposures();
        int successes = assessment.getSuccessess();

        if (exposures > 0 && (double) successes / exposures > 0.6) {
            stepReply.setIsNewTask(true);
            System.out.println("%%%%%%%%%%% Next Task Recommended");
            assessment.setAssessment(AssessmentLevel.COMPLETED);
        } else {
            stepReply.setIsNewTask(false);
        }

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.SUCCESSES);
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ASSESSMENT_LEVEL);

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }

        Step step = new Step(1, 0, StepSubType.STEP_COMPLETION_REPLY);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.STEP_COMPLETION_REPLY);
        task.setDescription("Choose your next action");
        task.addStep(step);

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        PendingTask pendingTask = new PendingTask(task);
        pendingTask.setCurrentStep(pendingStep);

        TutorReply reply = new TutorReply(":Success");

        reply.setData(gson.toJson(pendingTask));

        return reply;
    }

    public TutorReply completeInitVarsStep(StepCompletion completion) {
        System.out.println("Tutor completeInitVarsStep");

        InitVarStep example = gson.fromJson(completion.getData(), InitVarStep.class);

        // Extract user-provided answers and expected results
        Map<String, String> userAnswers = example.getUserAnswers();
        Map<String, String> correctAnswers = example.getCorrectAnswers();

        // Track correctness and prepare feedback
        boolean allCorrect = true;
        StringBuilder feedback = new StringBuilder();

        for (String variable : correctAnswers.keySet()) {
            String userAnswer = userAnswers.getOrDefault(variable, "");
            String correctAnswer = correctAnswers.get(variable);

            feedback.append(variable)
                    .append(": User Answer: ")
                    .append(userAnswer)
                    .append(", Correct Answer: ")
                    .append(correctAnswer)
                    .append("\n");

            if (!userAnswer.equals(correctAnswer)) {
                allCorrect = false;
            }
        }

        System.out.println("Feedback:\n" + feedback);

        // Create StepCompletionReply
        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setIsCorrect(allCorrect);
        stepReply.setCorrectAnswer(correctAnswers.toString().replaceAll("[{}]", "").replaceAll("@\\w{2}=|,", "").trim());

        stepReply.setResponse(userAnswers.toString().replaceAll("[{}]", "").replaceAll("@\\w{2}=|,", "").trim());

        // Update the student model
        int dbId = KnowledgeComponentKind.fromString("Initialize Variables").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);

        // Increment success or exposure based on correctness
        if (allCorrect) {
            assessment.incrementSuccessess();
        }
        assessment.incrementExposures();

        // Determine whether to recommend a new task
        int successes = assessment.getSuccessess();
        if (successes > 0) {
            stepReply.setIsNewTask(true);
            System.out.println("Next task is recommended.");
            assessment.setAssessment(AssessmentLevel.COMPLETED);
        } else {
            stepReply.setIsNewTask(false);
        }

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.SUCCESSES);
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ASSESSMENT_LEVEL);
        } catch (NonRecoverableException ex) {
            return createError("Failed to update assessment data in database", ex);
        }

        stepReply.setIsRepeatStep(!allCorrect);
        stepReply.setIsNewStep(allCorrect);
        stepReply.setIsNextStep(false);

        // Wrap the StepCompletionReply into a Task and TutorReply
        Step step = new Step(1, 0, StepSubType.STEP_COMPLETION_REPLY);

        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.STEP_COMPLETION_REPLY);
        task.setDescription("Review your results and choose the next action.");
        task.addStep(step);

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        PendingTask pendingTask = new PendingTask(task);
        pendingTask.setCurrentStep(pendingStep);

        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(pendingTask));

        return reply;
    }

    public TutorReply completeCompressRoundStep(StepCompletion completion) {
        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setResponse(" ");

        stepReply.setIsCorrect(true);
        stepReply.setIsRepeatStep(false);
        stepReply.setIsNewStep(true);

        // ToDo: Use the student model to figure out whether we want
        // to give the student another practice problem of the same
        // type or move on to an entirely different problem.
        stepReply.setIsNewTask(true);

        // ToDo: currently only one step in a task, so there isn't a next one???
        stepReply.setIsNextStep(false);

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Compress Round").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementSuccessess();

        int exposures = assessment.getExposures();
        int successes = assessment.getSuccessess();

        if (exposures > 0 && (double) successes / exposures > 0.6) {
            stepReply.setIsNewTask(true);
            System.out.println("%%%%%%%%%%% Next Task Recommended");
            assessment.setAssessment(AssessmentLevel.COMPLETED);
        } else {
            stepReply.setIsNewTask(false);
        }

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.SUCCESSES);
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ASSESSMENT_LEVEL);

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }

        Step step = new Step(1, 0, StepSubType.STEP_COMPLETION_REPLY);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.STEP_COMPLETION_REPLY);
        task.setDescription("Choose your next action");
        task.addStep(step);

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        PendingTask pendingTask = new PendingTask(task);
        pendingTask.setCurrentStep(pendingStep);

        TutorReply reply = new TutorReply(":Success");

        reply.setData(gson.toJson(pendingTask));

        return reply;
    }

    public TutorReply completeShiftBitsStep(StepCompletion completion) {
        System.out.println("Tutor completeShiftBitsStep");

        BitShiftStep example = gson.fromJson(completion.getData(), BitShiftStep.class);
        String operand = example.getOperand();
        int shiftLength = example.getShiftLength();
        boolean shiftRight = example.isShiftRight();
        int bitLength = example.getBitLength();
        String result = example.getResult();

        String expectedResult = bitShiftFunction(operand,
                shiftLength,
                shiftRight,
                bitLength);

        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setCorrectAnswer(expectedResult);
        stepReply.setResponse(result);

        if (expectedResult.equals(result)) {
            stepReply.setIsCorrect(true);
            stepReply.setIsRepeatStep(false);
            stepReply.setIsNewStep(true);

            // ToDo: currently only one step in a task, so there isn't a next one???
            stepReply.setIsNextStep(false);

            // Update the assessment data and save it to the database.
            int dbId = KnowledgeComponentKind.fromString("Shift Bits").dbId();
            Assessment assessment = studentModel.findAssessment(dbId);
            assessment.incrementSuccessess();

            int exposures = assessment.getExposures();
            int successes = assessment.getSuccessess();

            if (exposures > 0 && (double) successes / exposures > 0.6) {
                stepReply.setIsNewTask(true);
                System.out.println("%%%%%%%%%%% Next Task Recommended");
                assessment.setAssessment(AssessmentLevel.COMPLETED);
            } else {
                stepReply.setIsNewTask(false);
            }

            try {
                StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.SUCCESSES);
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ASSESSMENT_LEVEL);

            } catch (NonRecoverableException ex) {
                return createError("Unknown error", ex);
            }

        } else {
            stepReply.setIsCorrect(false);
            stepReply.setIsRepeatStep(true);
            stepReply.setIsNewStep(false);
            stepReply.setIsNewTask(false);
            stepReply.setIsNextStep(false);
        }

        Step step = new Step(1, 0, StepSubType.STEP_COMPLETION_REPLY);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.STEP_COMPLETION_REPLY);
        task.setDescription("Choose your next action");
        task.addStep(step);

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        PendingTask pendingTask = new PendingTask(task);
        pendingTask.setCurrentStep(pendingStep);

        TutorReply reply = new TutorReply(":Success");

        reply.setData(gson.toJson(pendingTask));

        return reply;
    }

    public TutorReply completeXorBitsStep(StepCompletion completion) {
        Random rnd = new Random();
        System.out.println("Tutor completeXorBitsStep");

        BitOpStep example = gson.fromJson(completion.getData(), BitOpStep.class);

        String operand1 = example.getExample().getOperand1();
        String operand2 = example.getExample().getOperand2();
        String result = example.getExample().getResult();

        int m = 8; //this will be changed

        String expectedResult = xorBitsFunction(operand1, operand2);

        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setCorrectAnswer(expectedResult);
        stepReply.setResponse(result);

        if (expectedResult.equals(result)) {
            stepReply.setIsCorrect(true);
            stepReply.setIsRepeatStep(false);
            stepReply.setIsNewStep(true);

            // ToDo: Use the student model to figure out whether we want
            // to give the student another practice problem of the same
            // type or move on to an entirely different problem.
            stepReply.setIsNewTask(true);

            // ToDo: currently only one step in a task, so there isn't a next one???
            stepReply.setIsNextStep(false);

            // Update the assessment data and save it to the database.
            int dbId = KnowledgeComponentKind.fromString("XOR Bits").dbId();
            Assessment assessment = studentModel.findAssessment(dbId);
            assessment.incrementSuccessess();

            int exposures = assessment.getExposures();
            int successes = assessment.getSuccessess();

            if (exposures > 0 && (double) successes / exposures > 0.6) {
                stepReply.setIsNewTask(true);
                System.out.println("%%%%%%%%%%% Next Task Recommended");
                assessment.setAssessment(AssessmentLevel.COMPLETED);
            } else {
                stepReply.setIsNewTask(false);
            }

            try {
                StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.SUCCESSES);
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ASSESSMENT_LEVEL);

            } catch (NonRecoverableException ex) {
                return createError("Unknown error", ex);
            }

        } else {
            stepReply.setIsCorrect(false);
            stepReply.setIsRepeatStep(true);
            stepReply.setIsNewStep(false);
            stepReply.setIsNewTask(false);
            stepReply.setIsNextStep(false);
        }

        Step step = new Step(1, 0, StepSubType.STEP_COMPLETION_REPLY);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        step.setData(gson.toJson(stepReply));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.STEP_COMPLETION_REPLY);
        task.setDescription("Choose your next action");
        task.addStep(step);

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        PendingTask pendingTask = new PendingTask(task);
        pendingTask.setCurrentStep(pendingStep);

        TutorReply reply = new TutorReply(":Success");

        reply.setData(gson.toJson(pendingTask));

        return reply;
    }

    public TutorReply completeAddBitsStep(StepCompletion completion) {
        Random rnd = new Random();
        System.out.println("Tutor completeAddBitsStep");

        BitOpStep example = gson.fromJson(completion.getData(), BitOpStep.class);

        String operand1 = example.getExample().getOperand1();
        String operand2 = example.getExample().getOperand2();
        String result = example.getExample().getResult();

        int m = 8; //this will be changed

        String expectedResult = addBitsFunction(operand1, operand2, m);

        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setCorrectAnswer(expectedResult);
        stepReply.setResponse(result);

        if (expectedResult.equals(result)) {
            stepReply.setIsCorrect(true);
            stepReply.setIsRepeatStep(false);
            stepReply.setIsNewStep(true);

            // ToDo: Use the student model to figure out whether we want
            // to give the student another practice problem of the same
            // type or move on to an entirely different problem.
            stepReply.setIsNewTask(true);

            // ToDo: currently only one step in a task, so there isn't a next one???
            stepReply.setIsNextStep(false);

            // Update the assessment data and save it to the database.
            int dbId = KnowledgeComponentKind.fromString("Add Bits").dbId();
            Assessment assessment = studentModel.findAssessment(dbId);
            assessment.incrementSuccessess();

            int exposures = assessment.getExposures();
            int successes = assessment.getSuccessess();

            if (exposures > 0 && (double) successes / exposures > 0.6) {
                stepReply.setIsNewTask(true);
                System.out.println("%%%%%%%%%%% Next Task Recommended");
                assessment.setAssessment(AssessmentLevel.COMPLETED);
            } else {
                stepReply.setIsNewTask(false);
            }

            try {
                StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.SUCCESSES);
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ASSESSMENT_LEVEL);

            } catch (NonRecoverableException ex) {
                return createError("Unknown error", ex);
            }

        } else {
            stepReply.setIsCorrect(false);
            stepReply.setIsRepeatStep(true);
            stepReply.setIsNewStep(false);
            stepReply.setIsNewTask(false);
            stepReply.setIsNextStep(false);
        }

        Step step = new Step(1, 0, StepSubType.STEP_COMPLETION_REPLY);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        step.setData(gson.toJson(stepReply));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.STEP_COMPLETION_REPLY);
        task.setDescription("Choose your next action");
        task.addStep(step);

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        PendingTask pendingTask = new PendingTask(task);
        pendingTask.setCurrentStep(pendingStep);

        TutorReply reply = new TutorReply(":Success");

        reply.setData(gson.toJson(pendingTask));

        return reply;
    }

    /**
     * Handler for completion of the problem in the SigmaZero client view
     *
     * @param completion The StepCompletion that has occurred
     * @return Returns a TutorReply which tells which tasks the user has left
     */
    public TutorReply completeSigmaZeroStep(StepCompletion completion) {
        ShaZeroStep example = gson.fromJson(completion.getData(), ShaZeroStep.class);
        String operand1 = example.getOperandA();
        int bitLength = example.getBitLength();
        String result = example.getResult();

        String expectedResult = calculateSigma(operand1, bitLength);
        System.out.println("Expected result: " + expectedResult);

        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setCorrectAnswer(expectedResult);
        stepReply.setResponse(result);

        if (expectedResult.equals(result)) {
            stepReply.setIsCorrect(true);
            stepReply.setIsRepeatStep(false);
            stepReply.setIsNewStep(true);

            // ToDo: Use the student model to figure out whether we want
            // to give the student another practice problem of the same
            // type or move on to an entirely different problem.
            stepReply.setIsNewTask(true);

            // ToDo: currently only one step in a task, so there isn't a next one???
            stepReply.setIsNextStep(false);

            // Update the assessment data and save it to the database.
            int dbId = KnowledgeComponentKind.fromString("SHA Sum 0 Function").dbId();
            Assessment assessment = studentModel.findAssessment(dbId);
            assessment.incrementSuccessess();

            int exposures = assessment.getExposures();
            int successes = assessment.getSuccessess();

            if (exposures > 0 && (double) successes / exposures > 0.6) {
                stepReply.setIsNewTask(true);
                System.out.println("%%%%%%%%%%% Next Task Recommended");
                assessment.setAssessment(AssessmentLevel.COMPLETED);
            } else {
                stepReply.setIsNewTask(false);
            }

            try {
                StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.SUCCESSES);
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ASSESSMENT_LEVEL);

            } catch (NonRecoverableException ex) {
                return createError("Unknown error", ex);
            }

        } else {
            stepReply.setIsCorrect(false);
            stepReply.setIsRepeatStep(true);
            stepReply.setIsNewStep(false);
            stepReply.setIsNewTask(false);
            stepReply.setIsNextStep(false);
        }

        Step step = new Step(1, 0, StepSubType.STEP_COMPLETION_REPLY);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.STEP_COMPLETION_REPLY);
        task.setDescription("Choose your next action");
        task.addStep(step);

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        PendingTask pendingTask = new PendingTask(task);
        pendingTask.setCurrentStep(pendingStep);

        TutorReply reply = new TutorReply(":Success");

        reply.setData(gson.toJson(pendingTask));

        return reply;
    }

    public TutorReply completeChoiceStep(StepCompletion completion) {
        System.out.println("Tutor completeChoiceStep");

        ChoiceFunctionStep example = gson.fromJson(completion.getData(), ChoiceFunctionStep.class);
        String operand1 = example.getOperand1();
        String operand2 = example.getOperand2();

        String operand3 = example.getOperand3();
        int bitLength = example.getBitLength();
        String result = example.getResult();

        String expectedResult = choiceFunction(operand1, operand2, operand3, bitLength);

        System.out.println("Expected result: " + expectedResult); // ToDo debuggin

        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setCorrectAnswer(expectedResult);
        stepReply.setResponse(result);

        if (expectedResult.equals(result)) {
            stepReply.setIsCorrect(true);
            stepReply.setIsRepeatStep(false);
            stepReply.setIsNewStep(true);

            // ToDo: currently only one step in a task, so there isn't a next one???
            stepReply.setIsNextStep(false);

            // Update the assessment data and save it to the database.
            int dbId = KnowledgeComponentKind.fromString("Choice Function").dbId();
            Assessment assessment = studentModel.findAssessment(dbId);
            assessment.incrementSuccessess();

            int exposures = assessment.getExposures();
            int successes = assessment.getSuccessess();

            if (exposures > 0 && (double) successes / exposures > 0.8) {
                stepReply.setIsNewTask(true);
                System.out.println("%%%%%%%%%%% Next Task Recommended");
                assessment.setAssessment(AssessmentLevel.COMPLETED);
            } else {
                stepReply.setIsNewTask(false);
            }

            try {
                StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.SUCCESSES);
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ASSESSMENT_LEVEL);

            } catch (NonRecoverableException ex) {
                return createError("Unknown error", ex);
            }

        } else {
            stepReply.setIsCorrect(false);
            stepReply.setIsRepeatStep(true);
            stepReply.setIsNewStep(false);
            stepReply.setIsNewTask(false);
            stepReply.setIsNextStep(false);
        }

        Step step = new Step(1, 0, StepSubType.STEP_COMPLETION_REPLY);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.STEP_COMPLETION_REPLY);
        task.setDescription("Choose your next action");
        task.addStep(step);

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        PendingTask pendingTask = new PendingTask(task);
        pendingTask.setCurrentStep(pendingStep);

        TutorReply reply = new TutorReply(":Success");

        reply.setData(gson.toJson(pendingTask));

        return reply;
    }

    public TutorReply completeMajorityStep(StepCompletion completion) {
        System.out.println("Tutor completeMajorityStep");

        MajorityStep example = gson.fromJson(completion.getData(), MajorityStep.class);
        String operand1 = example.getOperandA();
        String operand2 = example.getOperandB();
        String operand3 = example.getOperandC();
        int bitLength = example.getBitLength();
        String result = example.getResult();
        // System.out.println("Result: " + example.getResult());

        String expectedResult = majorityFunction(operand1, operand2, operand3, bitLength);
        System.out.println("Expected result: " + expectedResult);

        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setCorrectAnswer(expectedResult);
        stepReply.setResponse(result);

        if (expectedResult.equals(result)) {
            stepReply.setIsCorrect(true);
            stepReply.setIsRepeatStep(false);
            stepReply.setIsNewStep(true);

            // ToDo: Use the student model to figure out whether we want
            // to give the student another practice problem of the same
            // type or move on to an entirely different problem.
            stepReply.setIsNewTask(true);

            // ToDo: currently only one step in a task, so there isn't a next one???
            stepReply.setIsNextStep(false);

            // Update the assessment data and save it to the database.
            int dbId = KnowledgeComponentKind.fromString("Majority Function").dbId();
            Assessment assessment = studentModel.findAssessment(dbId);
            assessment.incrementSuccessess();

            int exposures = assessment.getExposures();
            int successes = assessment.getSuccessess();

            if (exposures > 0 && (double) successes / exposures > 0.6) {
                stepReply.setIsNewTask(true);
                System.out.println("%%%%%%%%%%% Next Task Recommended");
                assessment.setAssessment(AssessmentLevel.COMPLETED);
            } else {
                stepReply.setIsNewTask(false);
            }

            try {
                StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.SUCCESSES);
                modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ASSESSMENT_LEVEL);

            } catch (NonRecoverableException ex) {
                return createError("Unknown error", ex);
            }

        } else {
            stepReply.setIsCorrect(false);
            stepReply.setIsRepeatStep(true);
            stepReply.setIsNewStep(false);
            stepReply.setIsNewTask(false);
            stepReply.setIsNextStep(false);
        }

        Step step = new Step(1, 0, StepSubType.STEP_COMPLETION_REPLY);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.STEP_COMPLETION_REPLY);
        task.setDescription("Choose your next action");
        task.addStep(step);

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        PendingTask pendingTask = new PendingTask(task);
        pendingTask.setCurrentStep(pendingStep);

        TutorReply reply = new TutorReply(":Success");

        reply.setData(gson.toJson(pendingTask));

        return reply;
    }

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
        //gson = new GsonBuilder().setPrettyPrinting().create();

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

            case SHA_ZERO:
                return newSigmaZeroFunctionExample(session, request.getData());

            default:
                return createError("Unknown new example request: " + request.getExampleType(), null);
        }
    }

    /**
     * Create and save a new tutoring session associated with the given account.
     *
     * @param account the student user
     * @throws NonRecoverableException
     * @return the new TutoringSession
     */
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
            ServiceFactory.findSessionSvc().create(tSession);

            return tSession;

        } catch (IllegalArgException ex) {
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
     * @param userId String "user@regis.edu"
     * @param sessionId String identifying a previously generated session id.
     * @return the current TutoringSession associated with the given user id and
     * session id
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

    /**
     * Handles client requests for a new ASCII encode example.
     *
     * @return a TutorReply whose data contains a JSon EncodeAsciiExample
     * object.
     */
    private TutorReply newAsciiEncodeExample(TutoringSession session, String jsonData) {
        System.out.println("Start tutor newEncodeAsciiexample"); // Error checking

        EncodeAsciiStep newEncodeAscii = gson.fromJson(jsonData, EncodeAsciiStep.class); // This is the EncodeAsciiStep created in the newExample function from the EncodeAsciiView.

        if (newEncodeAscii.getQuestion().isEmpty() || newEncodeAscii.getQuestion() == null) {

            System.out.println("Question was empty"); // Error checking

            int messageLength = newEncodeAscii.getMessageLength(); // Set in the newExample function from the EncodeAsciiView, represents the String length that will be generated for the question.

            String newQuestion = generateRandomString(messageLength); // Generates a random string to convert to binary

            newEncodeAscii.setQuestion(newQuestion);

            newEncodeAscii.setResult(toBinaryFunction(newQuestion)); // Generates the binary version of the question, which is now the answer

        } else {
            newEncodeAscii.setResult(toBinaryFunction(newEncodeAscii.getQuestion())); // Generates the binary version of the question, which is now the answer
        }

        System.out.println(newEncodeAscii.getResult()); // Error checking

        Step step = new Step(1, 0, StepSubType.ENCODE_ASCII);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        step.setData(gson.toJson(newEncodeAscii));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.ASCII_ENCODE);
        task.setDescription("Convert the question to binary.");
        task.addStep(step);

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("ASCII Encode").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementExposures();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ATTEMPTS);

            PendingStep pendingStep = new PendingStep(step);
            pendingStep.setCurrentHintIndex(0);
            pendingStep.setNotifyTutor(true);
            pendingStep.setIsCompleted(false);

            PendingTask pendingTask = new PendingTask(task);
            pendingTask.setCurrentStep(pendingStep);

            TutorReply reply = new TutorReply(":Success");
            reply.setData(gson.toJson(pendingTask));

            return reply;

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }
    }

    /**
     * Handles client requests for a new add one bit example.
     *
     * @return a TutorReply
     */
    private TutorReply newAddOneBitExample(TutoringSession session, String jsonData) {

        System.out.println("Start tutor newaddonebitexample"); // Error checking

        AddOneStep newAddOneBit = gson.fromJson(jsonData, AddOneStep.class); // This is the AddOneStep created in the newExample function from the AddOneView.

        int messageLength = newAddOneBit.getMessageLength(); // Set in the newExample function from the AddOneView, represents the String length that will be generated for the question.

        String question = generateRandomString(messageLength); // Generates a random string to convert to binary

        newAddOneBit.setQuestion(question);

        newAddOneBit.setResult(addOneFunction(question)); // Generates the binary version of the question, which is now the answer

        System.out.println(newAddOneBit.getResult()); // Error checking

        Step step = new Step(1, 0, StepSubType.ADD_ONE_BIT);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        step.setData(gson.toJson(newAddOneBit));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.ADD_ONE_BIT);
        task.setDescription("Add one bit to the given bit string");
        task.addStep(step);

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Add One Bit").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementExposures();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ATTEMPTS);

            PendingStep pendingStep = new PendingStep(step);
            pendingStep.setCurrentHintIndex(0);
            pendingStep.setNotifyTutor(true);
            pendingStep.setIsCompleted(false);

            PendingTask pendingTask = new PendingTask(task);
            pendingTask.setCurrentStep(pendingStep);
            TutorReply reply = new TutorReply(":Success");
            reply.setData(gson.toJson(pendingTask));

            return reply;

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }
    }

    /**
     * Handles client requests for a new pad with zeros example.
     *
     * @return a TutorReply
     */
    private TutorReply newPadZerosExample(TutoringSession session, String jsonData) {
        System.out.println("Start tutor newPadZerosExample"); // Error checking

        Pad0Step subStep = gson.fromJson(jsonData, Pad0Step.class);

        int messageLength = subStep.getMessageLength();

        String question = generateRandomString(messageLength);

        subStep.setQuestion(question);

        subStep.setResult(Integer.toString(calculateZerosNeededForPadding(messageLength))); // Calculates the number of zeros needed to pad the message correctly

        System.out.println(subStep.getResult()); // Error checking

        Step step = new Step(1, 0, StepSubType.PAD_ZEROS);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        step.setData(gson.toJson(subStep));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.PAD_ZEROS);
        task.setDescription("Calculate zeros needed to pad the message");
        task.addStep(step);

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Pad with Zeros").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementExposures();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ATTEMPTS);

            PendingStep pendingStep = new PendingStep(step);
            pendingStep.setCurrentHintIndex(0);
            pendingStep.setNotifyTutor(true);
            pendingStep.setIsCompleted(false);

            PendingTask pendingTask = new PendingTask(task);
            pendingTask.setCurrentStep(pendingStep);

            TutorReply reply = new TutorReply(":Success");
            reply.setData(gson.toJson(pendingTask));

            return reply;

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }
    }

    /**
     * Handles client requests for a new add message length example.
     *
     * @return a TutorReply
     */
    private TutorReply newAddMsgLenExample(TutoringSession session, String jsonData) {
        System.out.println("Start tutor newAddMsgLenExample"); // Error checking

        MessageLenStep subStep = gson.fromJson(jsonData, MessageLenStep.class);

        int messageLength = subStep.getMessageLength();

        String question = generateRandomString(messageLength);

        subStep.setQuestion(question);

        subStep.setResult(Integer.toBinaryString(messageLength * 8)); // Calculates the number of bits that the message length represents then converts that int to a binary string. (8 bits per char)

        System.out.println(subStep.getResult()); // Error checking

        Step step = new Step(1, 0, StepSubType.ADD_MSG_LENGTH);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        step.setData(gson.toJson(subStep));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.ADD_MSG_LENGTH);
        task.setDescription("Calculate the message length for the last 64 bits of the message length step");
        task.addStep(step);

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Add Message Length").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementExposures();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ATTEMPTS);

            PendingStep pendingStep = new PendingStep(step);
            pendingStep.setCurrentHintIndex(0);
            pendingStep.setNotifyTutor(true);
            pendingStep.setIsCompleted(false);

            PendingTask pendingTask = new PendingTask(task);
            pendingTask.setCurrentStep(pendingStep);

            TutorReply reply = new TutorReply(":Success");
            reply.setData(gson.toJson(pendingTask));

            return reply;

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }
    }

    /**
     * Handles client requests for a new prepare schedule example.
     *
     * @return a TutorReply
     */
    private TutorReply newPrepareScheduleExample(TutoringSession session, String jsonData) {
        PrepScheduleStep subStep = gson.fromJson(jsonData, PrepScheduleStep.class);

        Step step = new Step(1, 0, StepSubType.PREPARE_SCHEDULE);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(subStep));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.PREPARE_SCHEDULE);
        task.addStep(step);

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Prepare Schedule").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementExposures();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ATTEMPTS);

            PendingStep pendingStep = new PendingStep(step);
            pendingStep.setCurrentHintIndex(0);
            pendingStep.setNotifyTutor(true);
            pendingStep.setIsCompleted(false);

            PendingTask pendingTask = new PendingTask(task);
            pendingTask.setCurrentStep(pendingStep);

            TutorReply reply = new TutorReply(":Success");
            reply.setData(gson.toJson(pendingTask));

            return reply;

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }
    }

    /**
     * Handles client requests for a new initialize vars example.
     *
     * @return a TutorReply
     */
    private TutorReply newInitializeVarsExample(TutoringSession session, String jsonData) {

        InitVarStep subStep = gson.fromJson(jsonData, InitVarStep.class);

        Step step = new Step(1, 0, StepSubType.INITIALIZE_VARS);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(subStep));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.INITIALIZE_VARS);
        task.addStep(step);

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Initialize Variables").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ATTEMPTS);

            PendingStep pendingStep = new PendingStep(step);
            pendingStep.setCurrentHintIndex(0);
            pendingStep.setNotifyTutor(true);
            pendingStep.setIsCompleted(false);

            PendingTask pendingTask = new PendingTask(task);
            pendingTask.setCurrentStep(pendingStep);

            TutorReply reply = new TutorReply(":Success");
            reply.setData(gson.toJson(pendingTask));

            return reply;

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }
    }

    /**
     * Handles client requests for a new compress round example.
     *
     * @return a TutorReply
     */
    private TutorReply newCompressRoundExample(TutoringSession session, String jsonData) {
        CompressRoundStep subStep = gson.fromJson(jsonData, CompressRoundStep.class);

        Step step = new Step(1, 0, StepSubType.COMPRESS_ROUND);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(subStep));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.COMPRESS_ROUND);
        task.addStep(step);

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Compress Round").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementExposures();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ATTEMPTS);

            PendingStep pendingStep = new PendingStep(step);
            pendingStep.setCurrentHintIndex(0);
            pendingStep.setNotifyTutor(true);
            pendingStep.setIsCompleted(false);

            PendingTask pendingTask = new PendingTask(task);
            pendingTask.setCurrentStep(pendingStep);

            TutorReply reply = new TutorReply(":Success");
            reply.setData(gson.toJson(pendingTask));

            return reply;

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }
    }

    /**
     * Handles client requests for a new rotate bits example.
     *
     * @return a TutorReply
     */
    private TutorReply newRotateBitsExample(TutoringSession session, String jsonData) {
        RotateStep example = gson.fromJson(jsonData, RotateStep.class);

        // Check if the data (bit string) is provided, if not, generate it
        if (example.getData() == null || example.getData().isEmpty()) {
            String generatedData = generateInputString(example.getLength());
            example.setData(generatedData);
        }

        Step step = new Step(1, 0, StepSubType.ROTATE_BITS);
        step.setData(gson.toJson(example));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.ROTATE_BITS);
        task.addStep(step);

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Rotate n BITS").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementExposures();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ATTEMPTS);

            PendingStep pendingStep = new PendingStep(step);
            pendingStep.setCurrentHintIndex(0);
            pendingStep.setNotifyTutor(true);
            pendingStep.setIsCompleted(false);

            PendingTask pendingTask = new PendingTask(task);
            pendingTask.setCurrentStep(pendingStep);

            TutorReply reply = new TutorReply(":Success");
            reply.setData(gson.toJson(pendingTask));

            return reply;

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }
    }

    /**
     * Performs bit rotation on the example string to get correct answer for
     * comparison to user's answer.
     *
     * @param data
     * @param amount
     * @return result
     */
    private String performBitRotation(String data, int amount) {
        String fdata = data.replaceAll("\\s+", "");
        int length = fdata.length();
        amount = amount % length;

        if (amount < 0) {
            amount = length + amount;
        }
        String result = fdata.substring(length - amount) + fdata.substring(0, length - amount);
        return result;
    }

    /**
     * Handles client requests for a new shift bits zeros example.
     *
     * @return a TutorReply
     */
    private TutorReply newShiftBitsExample(TutoringSession session, String jsonData) {
        System.out.println("newShiftBitsExample");
        BitShiftStep substep = gson.fromJson(jsonData, BitShiftStep.class);

        int bitLength = substep.getBitLength();

        String operand = generateInputString(bitLength);
        int shiftLength = new Random().nextInt(bitLength);
        boolean shiftRight = substep.isShiftRight();

        substep.setOperand(operand);
        substep.setShiftLength(shiftLength);
        substep.setShiftRight(shiftRight);

        substep.setResult(bitShiftFunction(operand, shiftLength, shiftRight, bitLength));

        Step step = new Step(1, 0, StepSubType.SHIFT_BITS);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        step.setData(gson.toJson(substep));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.SHIFT_BITS);
        task.setDescription("Compute the result of the bitshift on the operand");
        task.addStep(step);

        // Update the assessment data and save it to the database.
        int knowledgeCompId = KnowledgeComponentKind.fromString("Shift Bits").dbId();
        System.out.println("knowledCompId: " + knowledgeCompId);
        Assessment assessment = studentModel.findAssessment(knowledgeCompId);
        
        assessment.incrementExposures();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ATTEMPTS);

            PendingStep pendingStep = new PendingStep(step);
            pendingStep.setCurrentHintIndex(0);
            pendingStep.setNotifyTutor(true);
            pendingStep.setIsCompleted(false);

            PendingTask pendingTask = new PendingTask(task);
            pendingTask.setCurrentStep(pendingStep);

            TutorReply reply = new TutorReply(":Success");
            reply.setData(gson.toJson(pendingTask));

            return reply;

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }
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

        int xor = (int) example.getOperand1Val() ^ (int) example.getOperand2Val();
        example.setResultVal(xor);

        BitOpStep subStep = new BitOpStep();
        subStep.setExample(example);
        //ToDo: multistep should be determined by the student model.
        subStep.setMultiStep(rnd.nextBoolean());

        Step step = new Step(1, 0, StepSubType.XOR_BITS);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        step.setData(gson.toJson(subStep));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.XOR_BITS);
        task.setDescription("Xor the bits in the two operands");
        task.addStep(step);

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("XOR Bits").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementExposures();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ATTEMPTS);

            PendingStep pendingStep = new PendingStep(step);
            pendingStep.setCurrentHintIndex(0);
            pendingStep.setNotifyTutor(true);
            pendingStep.setIsCompleted(false);

            PendingTask pendingTask = new PendingTask(task);
            pendingTask.setCurrentStep(pendingStep);

            TutorReply reply = new TutorReply(":Success");
            reply.setData(gson.toJson(pendingTask));

            return reply;

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }
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

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        step.setData(gson.toJson(subStep));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.ADD_BITS);
        task.setDescription("addition modulo 2^256 the bits in the two operands");
        task.addStep(step);

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Add Bits").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementExposures();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ATTEMPTS);

            PendingStep pendingStep = new PendingStep(step);
            pendingStep.setCurrentHintIndex(0);
            pendingStep.setNotifyTutor(true);
            pendingStep.setIsCompleted(false);

            PendingTask pendingTask = new PendingTask(task);
            pendingTask.setCurrentStep(pendingStep);

            TutorReply reply = new TutorReply(":Success");
            reply.setData(gson.toJson(pendingTask));

            return reply;

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }
    }

    /**
     * Handles client requests for a new majority function example.
     *
     * @return a TutorReply
     */
    private TutorReply newMajorityFunctionExample(TutoringSession session, String jsonData) {
        System.out.println("newMajorityFunctionExample");
        MajorityStep substep = gson.fromJson(jsonData, MajorityStep.class);

        int bitLength = substep.getBitLength();

        String operand1 = generateInputString(bitLength);
        String operand2 = generateInputString(bitLength);
        String operand3 = generateInputString(bitLength);

        substep.setOperandA(operand1);
        substep.setOperandB(operand2);
        substep.setOperandC(operand3);

        substep.setResult(majorityFunction(operand1, operand2, operand3, bitLength));

        Step step = new Step(1, 0, StepSubType.MAJORITY_FUNCTION);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        step.setData(gson.toJson(substep));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.MAJORITY_FUNCTION);
        task.setDescription("Compute the result of the majority function on the three operands");
        task.addStep(step);

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Majority Function").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementExposures();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ATTEMPTS);

            PendingStep pendingStep = new PendingStep(step);
            pendingStep.setCurrentHintIndex(0);
            pendingStep.setNotifyTutor(true);
            pendingStep.setIsCompleted(false);

            PendingTask pendingTask = new PendingTask(task);
            pendingTask.setCurrentStep(pendingStep);

            TutorReply reply = new TutorReply(":Success");
            reply.setData(gson.toJson(pendingTask));

            return reply;

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }
    }

    /**
     * Handles client requests for a new choice function example.
     *
     * @return a TutorReply
     */
    private TutorReply newChoiceFunctionExample(TutoringSession session, String jsonData) {
        System.out.println("newChoiceFunctionExample");
        ChoiceFunctionStep substep = gson.fromJson(jsonData, ChoiceFunctionStep.class);

        int bitLength = substep.getBitLength();

        String operand1 = generateInputString(bitLength);
        String operand2 = generateInputString(bitLength);
        String operand3 = generateInputString(bitLength);

        substep.setOperand1(operand1);
        substep.setOperand2(operand2);
        substep.setOperand3(operand3);

        substep.setResult(choiceFunction(operand1, operand2, operand3, bitLength));

        Step step = new Step(1, 0, StepSubType.CHOICE_FUNCTION);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        step.setData(gson.toJson(substep));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.CHOICE_FUNCTION);
        task.setDescription("Compute the result of the choice function on the three operands");
        task.addStep(step);

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Choice Function").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementExposures();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ATTEMPTS);

            PendingStep pendingStep = new PendingStep(step);
            pendingStep.setCurrentHintIndex(0);
            pendingStep.setNotifyTutor(true);
            pendingStep.setIsCompleted(false);

            PendingTask pendingTask = new PendingTask(task);
            pendingTask.setCurrentStep(pendingStep);

            TutorReply reply = new TutorReply(":Success");
            reply.setData(gson.toJson(pendingTask));

            return reply;

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }
    }

    /**
     * Handler that returns a new example problem to the ShaSigmaZero client view
     *
     * @param session The active Tutoring Session
     * @param jsonData The JSON sent from the client which models the SigmaZero step
     * @return Returns a response which can be sent back to the client with a new example problem in the body
     */
    private TutorReply newSigmaZeroFunctionExample(TutoringSession session, String jsonData)
    {
        ShaZeroStep substep = gson.fromJson(jsonData, ShaZeroStep.class);

        substep.setOperandA(generateInputString(substep.getBitLength()));

        substep.setResult(calculateSigma(substep.getOperandA(), substep.getBitLength()));

        Step step = new Step(1, 0, StepSubType.SHA_ZERO);

        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        step.setData(gson.toJson(substep));

        Task task = new Task();
        task.setKind(TaskKind.PROBLEM);
        task.setType(ProblemType.SHA_ZERO);
        task.setDescription("Compute the result of the Σ₀ function");
        task.addStep(step);

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("SHA Sum 0 Function").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementExposures();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.ATTEMPTS);

            PendingStep pendingStep = new PendingStep(step);
            pendingStep.setCurrentHintIndex(0);
            pendingStep.setNotifyTutor(true);
            pendingStep.setIsCompleted(false);

            PendingTask pendingTask = new PendingTask(task);
            pendingTask.setCurrentStep(pendingStep);

            TutorReply reply = new TutorReply(":Success");
            reply.setData(gson.toJson(pendingTask));

            return reply;

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }
    }

    /**
     * Performs a bit shift (left or right) on a binary string operand.
     *
     * @param operand The binary string to be shifted.
     * @param shiftLength The number of positions to shift the bits.
     * @param bitLength The length of the resulting binary string.
     * @param shiftRight If true, performs a right shift; if false, performs a
     * left shift.
     * @return The binary string result after shifting.
     */
    private String bitShiftFunction(String operand, int shiftLength, boolean shiftRight, int bitLength) {
        // Convert the binary string to a long integer
        String tempOperand = operand.replaceAll("\\s", "");
        long intOperand = Long.parseLong(tempOperand, 2);

        // Perform the shift
        long shiftedOperand;
        if (shiftRight) {
            shiftedOperand = intOperand >>> shiftLength;
        } else {
            shiftedOperand = intOperand << shiftLength;
        }

        // Convert the result back to binary string
        String binaryResult = formatResult(shiftedOperand, bitLength);

        return binaryResult;
    }

    /**
     * Performs binary addition modulo 2^256
     *
     * @param operand1 The binary string for operand 1.
     * @param operand2 The binary string for operand 2.
     * @param m The int for calculating the modulo
     * @return The binary string result after adding two bits
     */
    private String addBitsFunction(String operand1, String operand2, int m) {

        if (operand1 == null || operand1.isEmpty()) {
            return "";
        }
        if (operand2 == null || operand2.isEmpty()) {
            return "";
        }

        // Convert binary strings to BigIntegers
        BigInteger num1 = new BigInteger(operand1, 2);
        BigInteger num2 = new BigInteger(operand2, 2);

        // Perform addition
        BigInteger sum = num1.add(num2);

        // Calculate the result modulo 2^256
        BigInteger modulo = new BigInteger("2").pow(m);
        BigInteger result = sum.mod(modulo);

        // Convert the result back to a binary string
        String resultBinary = result.toString(2);

        // Ensure the binary string has 256 bits (pad with leading zeros if necessary)
        while (resultBinary.length() < m) {
            resultBinary = "0" + resultBinary;
        }

        System.out.println("Result : " + resultBinary);

        return resultBinary;
    }

    /**
     * Evaluates the choice function Ch(x, y, z).
     *
     * @param x Binary string representation of x.
     * @param y Binary string representation of y.
     * @param z Binary string representation of z.
     * @return Binary string result of Ch(x, y, z).
     */
    private String choiceFunction(String x, String y, String z, int bitLength) {
        // Convert the binary strings to integer values
        String tempX = x.replaceAll("\\s", "");
        String tempY = y.replaceAll("\\s", "");
        String tempZ = z.replaceAll("\\s", "");

        long intX = Long.parseLong(tempX, 2);
        long intY = Long.parseLong(tempY, 2);
        long intZ = Long.parseLong(tempZ, 2);

        long xy = intX & intY;

        long notX = ~intX & intZ;

        long result = xy ^ notX;

        // Convert the result back to binary string
        String binaryResult = formatResult(result, bitLength);

        return binaryResult;
    }

    /**
     * This method takes the question from the newAddOneStep ad converts it to
     * binary and returns the binary answer.
     *
     * @param question
     *
     * @return
     */
    private String addOneFunction(String question) {
        String answer;

        char stringArray[] = question.toCharArray();

        StringBuilder binary = new StringBuilder();

        for (int i = 0; i < stringArray.length; i++) {
            String binaryChar = String.format("%8s", Integer.toBinaryString(stringArray[i])).replaceAll(" ", "0");

            binary.append(binaryChar).append(" ");
        }

        answer = binary + "1";

        return answer;
    }

    /**
     * Function that can take a string and convert it to binary
     *
     * @param question
     * @return
     */
    private String toBinaryFunction(String question) {
        String answer;

        char stringArray[] = question.toCharArray();

        StringBuilder binary = new StringBuilder();

        for (int i = 0; i < stringArray.length; i++) {
            String binaryChar = String.format("%8s", Integer.toBinaryString(stringArray[i])).replaceAll(" ", "0");

            binary.append(binaryChar);
        }

        answer = binary.toString();

        return answer;
    }

    /**
     * Evaluates the maj function maj(x, y, z).
     *
     * @param x Binary string representation of x.
     * @param y Binary string representation of y.
     * @param z Binary string representation of z.
     * @return Binary string result of maj(x, y, z).
     */
    private String majorityFunction(String x, String y, String z, int bitLength) {
        // Convert the binary strings to integer values
        String tempX = x.replaceAll("\\s", "");
        String tempY = y.replaceAll("\\s", "");
        String tempZ = z.replaceAll("\\s", "");

        long intX = Long.parseLong(tempX, 2);
        long intY = Long.parseLong(tempY, 2);
        long intZ = Long.parseLong(tempZ, 2);

        long xy = intX & intY;

        long xz = intX & intZ;

        long yz = intY & intZ;

        long result = xy ^ xz ^ yz;

        // Convert the result back to binary string
        String binaryResult = formatResult(result, bitLength);

        return binaryResult;
    }

    /**
     * Performs rotation (ROR) on the given input string for the
     * specified number of positions.
     *
     * @param input     The input value to rotate.
     * @param positions The number of positions for the rotation.
     * @return The rotated string.
     */

    private long rotateRight(long input, int positions) {
        return (input >>> positions) | (input << (32 - positions));
    }

    /**
     * Calculates the SHA Σ₀ function involving rotation and right shift operations.
     *
     * @param input The input binary number.
     * @return The result after performing the SHA Σ₀ function.
     */
    private String calculateSigma(String input, int bitLength) {
        input = input.replaceAll("\\s", "");
        long a = Long.parseLong(input, 2);

        // Perform rotations and shift operations
        return formatResult(rotateRight(a, 2) ^ rotateRight(a, 13) ^ rotateRight(a, 22), bitLength);
    }

    /**
     * Suppose to translate a String into a binary representation and returns
     * the binary translation back as a String.
     *
     * @param question - String value representing the randomly generated
     * question that needs to be translated into binary form.
     *
     * @return - String binary representation of the question.
     */
//    private String addOneFunction(String question) {
//        String answer; // Will contain the binary answer that is returned
//        
//        char stringArray[] = question.toCharArray(); // Splits the string into characters
//        
//        StringBuilder binary = new StringBuilder(); // Ease of altering the string.
//
//        for (int i = 0; i < stringArray.length; i++) { // Visit each character, turn it into binary form, then add it to binary variable.
//            String binaryChar = String.format("%8s", Integer.toBinaryString(stringArray[i])).replaceAll(" ", "0");
//            
//            binary.append(binaryChar).append(" ");
//        }
//        
//        answer = binary + "1";
//
//        return answer;
//    }
    /**
     * This function is called from the newPadZeroStep method and will calculate
     * the zeros needed for the SHA256 message. The variables are written
     * purposely to give the developer a easier understanding of whats being
     * calculated instead of just using the integers.
     *
     * @param messageLength
     *
     * @return
     */
    private int calculateZerosNeededForPadding(int messageLength) {
        int sha256MessageLength = 512;
        int messageLengthStepBitsNeeded = 64;
        int padZerosStepBitsLength = sha256MessageLength - messageLengthStepBitsNeeded;
        int bitsPerCharacter = 8;
        int totalBitsPerMessage = bitsPerCharacter * messageLength;
        int appendOneBit = 1;
        int totalMessageLengthInBits = totalBitsPerMessage + appendOneBit;
        int zerosNeededToPadMessage = padZerosStepBitsLength - totalMessageLengthInBits;

        return zerosNeededToPadMessage;
    }

    /**
     * Formats the result output by the choice function based on the size of the
     * problem.
     *
     * @param answer the output of the choice function
     *
     * @return the binary string representation of the answer
     */
    private String formatResult(long answer, int bitLength) {
        String finalResult = "";

        switch (bitLength) {
            case 4:
                finalResult = String.format("%4s", Long.toBinaryString(answer)).replace(' ', '0');
                break;
            case 8:
                finalResult = String.format("%8s", Long.toBinaryString(answer)).replace(' ', '0');
                break;
            case 16:
                finalResult = String.format("%16s", Long.toBinaryString(answer)).replace(' ', '0');
                break;
            case 32:
                finalResult = String.format("%32s", Long.toBinaryString(answer)).replace(' ', '0');
                break;
            default:
                break;
        }
        return finalResult;
    }

    /**
     * Generates an n-bit binary string (length 4, 8, 16, or 32) to be used as
     * an input into the Ch function. Every four bits are separated by a space
     * to improve readability.
     *
     * @return A string to be used as an input into the function.
     */
    private String generateInputString(int problemSize) {
        Random random = new Random();

        String inputString;
        String tempString;
        StringBuilder inputStringBuilder = new StringBuilder();
        int num;

        switch (problemSize) {
            case 4:
                num = random.nextInt();
                tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                inputStringBuilder.append(tempString);
                break;
            case 8:
                num = random.nextInt();
                tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                inputStringBuilder.append(tempString);

                inputStringBuilder.append(" ");
                num = random.nextInt();
                tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                inputStringBuilder.append(tempString);
                break;
            case 16:
                num = random.nextInt();
                tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                inputStringBuilder.append(tempString);

                for (int i = 0; i < 3; i++) {
                    inputStringBuilder.append(" ");
                    num = random.nextInt();
                    tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                    inputStringBuilder.append(tempString);
                }
                break;
            case 32:
                num = random.nextInt();
                tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                inputStringBuilder.append(tempString);

                for (int i = 0; i < 7; i++) {
                    inputStringBuilder.append(" ");
                    num = random.nextInt();
                    tempString = String.format("%4s", Integer.toBinaryString(num & 0xF)).replace(' ', '0');
                    inputStringBuilder.append(tempString);
                }
                break;
            default:
                break;
        }

        inputString = inputStringBuilder.toString();

        return inputString;
    }

    /**
     * Utility for logging an error and an creating a tutoring reply error with
     * the given message, and optional originating exception.
     *
     * @param errMsg a displayable error message
     * @param ex the original exception, if any, that caused the error,
     * otherwise null.
     * @return a TutorReply with an ":ERR" status
     */
    private TutorReply createError(String errMsg, Exception ex) {
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
    private String generateRandomString(int length) {

        Random random = new Random();
        StringBuilder sb = new StringBuilder(length); // StringBuilder allows easier altering of a string.

        for (int i = 0; i < length; i++) {
            // Generates a random integer between 32 (inclusive) and 126 (inclusive)
            int randomChar = 32 + random.nextInt(95); // 126 - 32 + 1 = 95
            sb.append((char) randomChar);
        }

        return sb.toString();
    }

    private TutorReply hintEncode(StepCompletion completion) {
        System.out.println("Tutor hintEncode");

        StepCompletionReply stepReply = new StepCompletionReply();

        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);

        Hint hintOne = new Hint();
        hintOne.setId(0);
        hintOne.setText("Convert each character to its ASCII value");

        Hint hintTwo = new Hint();
        hintTwo.setId(1);
        hintTwo.setText("Each character should be represented by 8 bits");

        Step step = completion.getStep();
        step.addHint(hintOne);
        step.addHint(hintTwo);

        step.setSubType(StepSubType.REQUEST_HINT);
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(pendingStep));

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("ASCII Encode").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementHints();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.HINTS);

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }

        return reply;
    }

    private TutorReply hintAddOne(StepCompletion completion) {
        System.out.println("Tutor hintAddOne");

        StepCompletionReply stepReply = new StepCompletionReply();

        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);

        Hint hintOne = new Hint();
        hintOne.setId(0);
        hintOne.setText("Add a single '1' bit to the end of the message");

        Step step = completion.getStep();
        step.addHint(hintOne);

        step.setSubType(StepSubType.REQUEST_HINT);
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(pendingStep));

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Add One Bit").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementHints();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.HINTS);

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }

        return reply;
    }

    private TutorReply hintPadZeros(StepCompletion completion) {
        System.out.println("Tutor hintPadZeros");

        StepCompletionReply stepReply = new StepCompletionReply();

        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);

        Hint hintOne = new Hint();
        hintOne.setId(0);
        hintOne.setText("Add '0' bits until the message length is 448 mod 512");

        Step step = completion.getStep();
        step.addHint(hintOne);

        step.setSubType(StepSubType.REQUEST_HINT);
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(pendingStep));

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Pad with Zeros").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementHints();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.HINTS);

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }

        return reply;
    }

    private TutorReply hintAddMsgLen(StepCompletion completion) {
        System.out.println("Tutor hintAddMsgLen");

        StepCompletionReply stepReply = new StepCompletionReply();

        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);

        Hint hintOne = new Hint();
        hintOne.setId(0);
        hintOne.setText("Append the original message length as a 64-bit big-endian integer");

        Step step = completion.getStep();
        step.addHint(hintOne);

        step.setSubType(StepSubType.REQUEST_HINT);
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(pendingStep));

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Add Message Length").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementHints();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.HINTS);

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }

        return reply;
    }

    private TutorReply hintPrepareSchedule(StepCompletion completion) {
        System.out.println("Tutor hintPrepareSchedule");

        StepCompletionReply stepReply = new StepCompletionReply();

        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);

        Hint hintOne = new Hint();
        hintOne.setId(0);
        hintOne.setText("Extend the first 16 words to a total of 64 words");

        Hint hintTwo = new Hint();
        hintTwo.setId(1);
        hintTwo.setText("Use bitwise operations to generate each new word");

        Step step = completion.getStep();
        step.addHint(hintOne);
        step.addHint(hintTwo);

        step.setSubType(StepSubType.REQUEST_HINT);
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(pendingStep));

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Prepare Schedule").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementHints();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.HINTS);

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }

        return reply;
    }

    private TutorReply hintInitVars(StepCompletion completion) {
        System.out.println("Tutor hintInitVars");

        // Deserialize the StepCompletion object to access the InitVarStep
        InitVarStep initVarStep = gson.fromJson(completion.getData(), InitVarStep.class);

        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);

        // Dynamically fetch hints for each variable
        Step step = completion.getStep();
        for (String variable : initVarStep.getCorrectAnswers().keySet()) {
            // Fetch hints for the variable at different levels
            Hint hintLevel1 = new Hint();
            hintLevel1.setId(1);
            hintLevel1.setText(initVarStep.getHint(variable, 1));
            step.addHint(hintLevel1);

            Hint hintLevel2 = new Hint();
            hintLevel2.setId(2);
            hintLevel2.setText(initVarStep.getHint(variable, 2));
            step.addHint(hintLevel2);

            Hint hintLevel3 = new Hint();
            hintLevel3.setId(3);
            hintLevel3.setText(initVarStep.getHint(variable, 3));
            step.addHint(hintLevel3);
        }

        step.setSubType(StepSubType.REQUEST_HINT);

        // Add timeout
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);

        // Serialize the reply with hints
        step.setData(gson.toJson(stepReply));

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(pendingStep));

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Initialize Variables").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementHints();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.HINTS);

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }

        return reply;
    }

    private TutorReply hintCompressRound(StepCompletion completion) {
        System.out.println("Tutor hintCompressRound");

        StepCompletionReply stepReply = new StepCompletionReply();

        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);

        Hint hintOne = new Hint();
        hintOne.setId(0);
        hintOne.setText("Perform the main compression function for this round");

        Hint hintTwo = new Hint();
        hintTwo.setId(1);
        hintTwo.setText("Use the schedule word and round constant for this round");

        Step step = completion.getStep();
        step.addHint(hintOne);
        step.addHint(hintTwo);

        step.setSubType(StepSubType.REQUEST_HINT);
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(pendingStep));

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Compress Round").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementHints();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.HINTS);

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }

        return reply;
    }

    private TutorReply hintRotateBits(StepCompletion completion) {
        System.out.println("Tutor hintRotateBits");

        StepCompletionReply stepReply = new StepCompletionReply();

        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);

        Hint hintOne = new Hint();
        hintOne.setId(0);
        hintOne.setText("The bits that 'fall off' one end should be added to the other end");

        Step step = completion.getStep();
        step.addHint(hintOne);

        step.setSubType(StepSubType.REQUEST_HINT);
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(pendingStep));

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Rotate n BITS").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementHints();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.HINTS);

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }

        return reply;
    }

    private TutorReply hintShiftBits(StepCompletion completion) {
        System.out.println("Tutor hintShiftBits");

        BitShiftStep example = gson.fromJson(completion.getData(), BitShiftStep.class);
        String operand = example.getOperand();
        int shiftLength = example.getShiftLength();
        boolean shiftRight = example.isShiftRight();
        int bitLength = example.getBitLength();
        String result = example.getResult();

        String expectedResult = bitShiftFunction(operand,
                shiftLength,
                shiftRight,
                bitLength);

        StepCompletionReply stepReply = new StepCompletionReply();
        stepReply.setCorrectAnswer(expectedResult);
        stepReply.setResponse(result);

        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);

        Hint hintOne = new Hint();
        hintOne.setId(0);
        String hintText = "There will be " + shiftLength + " zeros on the left";
        hintOne.setText(hintText);

        Hint hintTwo = new Hint();
        hintTwo.setId(1);
        hintText = "Remove " + shiftLength + " bits from the right";
        hintTwo.setText(hintText);

        Step step = completion.getStep();
        step.addHint(hintOne);
        step.addHint(hintTwo);

        step.setSubType(StepSubType.REQUEST_HINT);
        // ToDo: fix timeouts
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        TutorReply reply = new TutorReply(":Success");

        reply.setData(gson.toJson(pendingStep));

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Shift Bits").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementHints();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.HINTS);

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }

        return reply;
    }

    private TutorReply hintXorBits(StepCompletion completion) {
        System.out.println("Tutor hintXorBits");

        StepCompletionReply stepReply = new StepCompletionReply();

        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);

        Hint hintOne = new Hint();
        hintOne.setId(0);
        hintOne.setText("XOR operation results in 1 only when the bits are different");

        Step step = completion.getStep();
        step.addHint(hintOne);

        step.setSubType(StepSubType.REQUEST_HINT);
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(pendingStep));

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("XOR Bits").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementHints();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.HINTS);

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }

        return reply;
    }

    private TutorReply hintAddBits(StepCompletion completion) {
        System.out.println("Tutor hintAddBits");

        StepCompletionReply stepReply = new StepCompletionReply();

        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);

        Hint hintOne = new Hint();
        hintOne.setId(0);
        hintOne.setText("Add the bits as if they were unsigned integers, discarding any overflow beyond 32 bits");

        Step step = completion.getStep();
        step.addHint(hintOne);

        step.setSubType(StepSubType.REQUEST_HINT);
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(pendingStep));

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Add Bits").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementHints();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.HINTS);

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }

        return reply;
    }

    private TutorReply hintMajorityFunction(StepCompletion completion) {
        System.out.println("Tutor hintMajorityFunction");

        StepCompletionReply stepReply = new StepCompletionReply();

        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);

        Hint hintOne = new Hint();
        hintOne.setId(0);
        hintOne.setText("The majority function returns the bit value that appears most frequently among the three inputs");

        Step step = completion.getStep();
        step.addHint(hintOne);

        step.setSubType(StepSubType.REQUEST_HINT);
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(pendingStep));

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Majority Function").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementHints();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.HINTS);

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }

        return reply;
    }

    private TutorReply hintChoiceFunction(StepCompletion completion) {
        System.out.println("Tutor hintChoiceFunction");

        StepCompletionReply stepReply = new StepCompletionReply();

        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);

        Hint hintOne = new Hint();
        hintOne.setId(0);
        hintOne.setText("The choice function selects bits from one input or another based on the value of the first input");

        Step step = completion.getStep();
        step.addHint(hintOne);

        step.setSubType(StepSubType.REQUEST_HINT);
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(pendingStep));

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("Choice Function").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementHints();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.HINTS);

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }

        return reply;
    }

    /**
     * Handler for returning Hint information to the client for the ShaSigmaZero View
     *
     * @param completion The StepCompletion that the user is on
     * @return Returns a response to the Tutor with the hint in the response body
     */
    private TutorReply hintShaZeroFunction(StepCompletion completion) {
        System.out.println("Tutor hintChoiceFunction");

        StepCompletionReply stepReply = new StepCompletionReply();

        stepReply.setIsCorrect(false);
        stepReply.setIsRepeatStep(true);
        stepReply.setIsNewStep(false);
        stepReply.setIsNewTask(false);
        stepReply.setIsNextStep(false);

        Hint hintOne = new Hint();
        hintOne.setId(0);
        hintOne.setText("The Σ₀ function involves three ROTR operations XOR'd together ");

        Step step = completion.getStep();
        step.addHint(hintOne);

        step.setSubType(StepSubType.REQUEST_HINT);
        Timeout timeout = new Timeout("Complete Step", 0, ":No-Op", "Exceed time");
        step.setTimeout(timeout);
        step.setData(gson.toJson(stepReply));

        PendingStep pendingStep = new PendingStep(step);
        pendingStep.setCurrentHintIndex(0);
        pendingStep.setNotifyTutor(true);
        pendingStep.setIsCompleted(false);

        TutorReply reply = new TutorReply(":Success");
        reply.setData(gson.toJson(pendingStep));

        // Update the assessment data and save it to the database.
        int dbId = KnowledgeComponentKind.fromString("SHA Sum 0 Function").dbId();
        Assessment assessment = studentModel.findAssessment(dbId);
        assessment.incrementHints();

        try {
            StudentModelSvc modelSvc = ServiceFactory.findStudentModelSvc();
            modelSvc.updateAssessment(studentModel, assessment, StudentModelFieldKind.HINTS);

        } catch (NonRecoverableException ex) {
            return createError("Unknown error", ex);
        }

        return reply;
    }

    private String xorBitsFunction(String operand1, String operand2) {
        StringBuilder result = new StringBuilder();
        int length = Math.min(operand1.length(), operand2.length());

        for (int i = 0; i < length; i++) {
            char bit1 = operand1.charAt(i);
            char bit2 = operand2.charAt(i);
            char xorBit = (char) (((bit1 - '0') ^ (bit2 - '0')) + '0');
            result.append(xorBit);
        }

        return result.toString();
    }
}
