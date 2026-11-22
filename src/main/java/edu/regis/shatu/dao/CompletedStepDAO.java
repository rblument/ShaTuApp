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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.model.CompletedStep;
import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.StepSubType;
import edu.regis.shatu.model.aol.TutoringMode;
import edu.regis.shatu.model.steps.Step;
import edu.regis.shatu.svc.CompletedStepSvc;

/**
 * MySQL implementation of {@link CompletedStepSvc}, which persists the
 * CompletedStep table: one row per student step completion.
 */
public class CompletedStepDAO extends MySqlDAO implements CompletedStepSvc {

    private static final String SELECT_COLUMNS
            = "SELECT Id, UserId, UnitId, TaskId, StepId, StepSubType, TutoringMode, "
            + "HintsGiven, TimeoutOccurred, CompletionDate FROM CompletedStep ";

    public CompletedStepDAO() {
        super("CompletedStep", "Id");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletedStep record(String userId, int courseId, TutoringMode mode, StepCompletion completion)
            throws NonRecoverableException {

        final String sql = "INSERT INTO CompletedStep "
                + "(UserId, UnitId, TaskId, StepId, StepSubType, TutoringMode, HintsGiven, TimeoutOccurred, CompletionDate) "
                + "VALUES (?,?,?,?,?,?,?,?,?)";

        Step step = completion.getStep();

        // The date is set by the client; fall back to now if it was never set (0).
        long date = completion.getDate() > 0 ? completion.getDate() : System.currentTimeMillis();

        try (Connection conn = DriverManager.getConnection(URL)) {
            Integer unitId = findUnitId(conn, courseId, mode);
            int[] taskAndStep = findTaskAndStep(conn, step);

            CompletedStep done = new CompletedStep();
            done.setUserId(userId);
            done.setUnitId(unitId == null ? -1 : unitId);
            done.setTaskId(taskAndStep == null ? -1 : taskAndStep[0]);
            done.setStepId(taskAndStep == null ? -1 : taskAndStep[1]);
            done.setSubType(step.getSubType());
            done.setTutoringMode(mode);
            done.setHintsGiven(completion.getHintsGiven());
            done.setTimeoutOccurred(completion.isTimeoutOccur());
            done.setCompletionDate(date);

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, userId);
                setNullableInt(stmt, 2, unitId);
                setNullableInt(stmt, 3, taskAndStep == null ? null : taskAndStep[0]);
                setNullableInt(stmt, 4, taskAndStep == null ? null : taskAndStep[1]);
                stmt.setString(5, step.getSubType().name());
                stmt.setString(6, mode.name());
                stmt.setInt(7, completion.getHintsGiven());
                stmt.setBoolean(8, completion.isTimeoutOccur());
                stmt.setTimestamp(9, new Timestamp(date));
                stmt.executeUpdate();

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        done.setId(keys.getInt(1));
                    }
                }
            }

            return done;

        } catch (SQLException e) {
            throw new NonRecoverableException("CompletedStepDAO-ERR-1 " + e, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CompletedStep> retrieve(String userId) throws NonRecoverableException {
        return query(SELECT_COLUMNS + "WHERE UserId = ? ORDER BY Id", userId, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CompletedStep> retrieve(String userId, int unitId) throws NonRecoverableException {
        return query(SELECT_COLUMNS + "WHERE UserId = ? AND UnitId = ? ORDER BY Id", userId, unitId);
    }

    private List<CompletedStep> query(String sql, String userId, Integer unitId) throws NonRecoverableException {
        List<CompletedStep> steps = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            if (unitId != null) {
                stmt.setInt(2, unitId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CompletedStep done = new CompletedStep();
                    done.setId(rs.getInt("Id"));
                    done.setUserId(rs.getString("UserId"));
                    done.setUnitId(nullToUnknown(rs, "UnitId"));
                    done.setTaskId(nullToUnknown(rs, "TaskId"));
                    done.setStepId(nullToUnknown(rs, "StepId"));
                    done.setSubType(StepSubType.valueOf(rs.getString("StepSubType")));
                    done.setTutoringMode(TutoringMode.valueOf(rs.getString("TutoringMode")));
                    done.setHintsGiven(rs.getInt("HintsGiven"));
                    done.setTimeoutOccurred(rs.getBoolean("TimeoutOccurred"));
                    done.setCompletionDate(rs.getTimestamp("CompletionDate").getTime());
                    steps.add(done);
                }
            }
        } catch (SQLException e) {
            throw new NonRecoverableException("CompletedStepDAO-ERR-2 " + e, e);
        }

        return steps;
    }

    /**
     * The Unit for a tutoring mode is the unit of the course whose sequence
     * index equals the mode's ordinal: See One = 0, Do One = 1, Teach One = 2.
     *
     * @return the unit id, or null if the course has no such unit
     */
    private Integer findUnitId(Connection conn, int courseId, TutoringMode mode) throws SQLException {
        final String sql = "SELECT Id FROM Unit WHERE CourseId = ? AND SequenceIndex = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, mode.ordinal());

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }

    /**
     * Locate the Task and Step rows for the completed step.
     *
     * The step id sent by the GUI is trusted only when that row exists with
     * the same sub type. Steps generated by an objective's example() don't
     * carry a database id, so otherwise fall back to the first step of that
     * sub type in task sequence order.
     *
     * @return {taskId, stepId}, or null if the Step table has no such step
     */
    private int[] findTaskAndStep(Connection conn, Step step) throws SQLException {
        final String byId = "SELECT TaskId, Id FROM Step WHERE Id = ? AND StepSubType = ?";
        final String bySubType
                = "SELECT s.TaskId, s.Id FROM Step s JOIN Task t ON t.Id = s.TaskId "
                + "WHERE s.StepSubType = ? ORDER BY t.SequenceIndex, s.SequenceIndex LIMIT 1";

        try (PreparedStatement stmt = conn.prepareStatement(byId)) {
            stmt.setInt(1, step.getId());
            stmt.setString(2, step.getSubType().name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new int[]{rs.getInt(1), rs.getInt(2)};
                }
            }
        }

        try (PreparedStatement stmt = conn.prepareStatement(bySubType)) {
            stmt.setString(1, step.getSubType().name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new int[]{rs.getInt(1), rs.getInt(2)};
                }
            }
        }

        return null;
    }

    /** Return the column's value, or -1 if it is NULL (the id was unresolved). */
    private int nullToUnknown(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? -1 : value;
    }

    private void setNullableInt(PreparedStatement stmt, int index, Integer value) throws SQLException {
        if (value == null) {
            stmt.setNull(index, Types.INTEGER);
        } else {
            stmt.setInt(index, value);
        }
    }
}
