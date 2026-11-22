package edu.regis.shatu.svc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.KnowledgeComponent;
import edu.regis.shatu.model.KnowledgeComponentKind;
import edu.regis.shatu.model.Student;
import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.Assessment;
import edu.regis.shatu.model.aol.AssessmentLevel;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.model.aol.StudentModel;
import edu.regis.shatu.model.aol.TutoringMode;

/**
 * Tests how the tutor picks the session's tutoring mode after a New Example
 * or Check. Developer mode is read from the database, so these tests
 * override developerModeOverride() instead.
 */
class ShaTuTutorModeSyncTest {

    @Test
    void assessmentLevelsMapToModes() {
        assertEquals(TutoringMode.SEE_ONE, ShaTuTutor.modeFor(AssessmentLevel.NOT_STARTED));
        assertEquals(TutoringMode.SEE_ONE, ShaTuTutor.modeFor(AssessmentLevel.VERY_LOW));
        assertEquals(TutoringMode.SEE_ONE, ShaTuTutor.modeFor(null));
        assertEquals(TutoringMode.DO_ONE, ShaTuTutor.modeFor(AssessmentLevel.IN_PROGRESS));
        assertEquals(TutoringMode.DO_ONE, ShaTuTutor.modeFor(AssessmentLevel.MEDIUM));
        assertEquals(TutoringMode.DO_ONE, ShaTuTutor.modeFor(AssessmentLevel.HIGH));
        assertEquals(TutoringMode.DO_ONE, ShaTuTutor.modeFor(AssessmentLevel.VERY_HIGH));
        assertEquals(TutoringMode.TEACH_ONE, ShaTuTutor.modeFor(AssessmentLevel.COMPLETED));
    }

    @Test
    void developerModeWinsOverTheAssessment() throws Exception {
        // Not started would normally mean SEE_ONE.
        TutoringSession session = new TutoringSession(new Student(new Account("test@regis.edu")));
        ShaTuTutor tutor = tutorWith(TutoringMode.DO_ONE, AssessmentLevel.NOT_STARTED, session);

        TutoringMode synced = sync(tutor, ProblemType.ADD_ONE_BIT);

        assertEquals(TutoringMode.DO_ONE, synced);
        assertEquals(TutoringMode.DO_ONE, session.getTutoringMode());
    }

    @Test
    void withoutDeveloperModeTheAssessmentDecides() throws Exception {
        TutoringSession session = new TutoringSession(new Student(new Account("test@regis.edu")));
        session.setTutoringMode(TutoringMode.TEACH_ONE);
        ShaTuTutor tutor = tutorWith(null, AssessmentLevel.MEDIUM, session);

        TutoringMode synced = sync(tutor, ProblemType.ADD_ONE_BIT);

        assertEquals(TutoringMode.DO_ONE, synced);
        assertEquals(TutoringMode.DO_ONE, session.getTutoringMode());
    }

    /** A tutor whose student has one Add One Bit assessment at the given level. */
    private static ShaTuTutor tutorWith(TutoringMode developerMode, AssessmentLevel level,
            TutoringSession session) throws Exception {
        ShaTuTutor tutor = new ShaTuTutor() {
            @Override
            TutoringMode developerModeOverride() {
                return developerMode;
            }
        };

        int kc = KnowledgeComponentKind.ADD_ONE_BIT.dbId();
        StudentModel model = new StudentModel("test@regis.edu");
        model.addAssessment(kc, new Assessment(new KnowledgeComponent(kc), level));

        set(tutor, "studentModel", model);
        set(tutor, "session", session);
        return tutor;
    }

    private static void set(ShaTuTutor tutor, String name, Object value) throws Exception {
        Field field = ShaTuTutor.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(tutor, value);
    }

    private static TutoringMode sync(ShaTuTutor tutor, ProblemType type) throws Exception {
        Method method = ShaTuTutor.class.getDeclaredMethod("syncSessionTutoringMode", ProblemType.class);
        method.setAccessible(true);
        return (TutoringMode) method.invoke(tutor, type);
    }
}
