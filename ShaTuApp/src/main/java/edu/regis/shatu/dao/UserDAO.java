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

import edu.regis.shatu.err.IllegalArgException;
import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;
import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.User;
import edu.regis.shatu.svc.UserSvc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A Data Access Object implementing {@link UserSvc} behaviors.
 *
 * @author rickb
 */
public class UserDAO extends MySqlDAO implements UserSvc {
    /**
     * Initialize this DAO via the parent constructor.
     */
    public UserDAO() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(Account acct) throws IllegalArgException, NonRecoverableException {
        final String sql = "INSERT INTO User (Email, Password, Question, Answer) VALUES (?,?,?,?)";

        Connection conn = null;
        PreparedStatement stmt = null;

        String userId = acct.getUserId();

        try {
            conn = DriverManager.getConnection(URL);

            if (exists(userId, conn)) {
                throw new IllegalArgException("User exists " + userId);
            }

            String[] keyCol = {"Id"};
            stmt = conn.prepareStatement(sql, keyCol);

            stmt.setString(1, userId);
            stmt.setString(2, acct.getPassword());
            stmt.setInt(3, acct.getSecurityQuestion());
            stmt.setString(4, acct.getSecurityAnswer());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new NonRecoverableException("UserDAO-ERR-1", e);

        } finally {
            close(conn, stmt);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String userId) throws NonRecoverableException {
        final String sql = "DELETE FROM User WHERE Email = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DriverManager.getConnection(URL);

            stmt = conn.prepareStatement(sql);

            stmt.setString(1, userId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new NonRecoverableException("UserDAO-ERR-2" + e.toString(), e);
        } finally {
            close(conn, stmt);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String userId) throws NonRecoverableException {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(URL);
            return exists(userId, conn);

        } catch (SQLException e) {
            throw new NonRecoverableException("UserDAO-ERR-3" + e.toString(), e);
        } finally {
            close(conn);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User retrieve(String userId) throws ObjNotFoundException, NonRecoverableException {
        final String sql = "SELECT Password FROM User WHERE Email = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DriverManager.getConnection(URL);
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, userId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(userId);

                user.setPassword(rs.getString(1));

                return user;

            } else {
                throw new ObjNotFoundException("Student Id:" + userId);
            }
        } catch (SQLException e) {
            throw new NonRecoverableException("UserDAO-ERR-5" + e.toString(), e);
        } finally {
            close(conn, stmt);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(User user, String newPassword) throws ObjNotFoundException, NonRecoverableException {
        //ToDo: add functionality
        //throw new UnsupportedOperationException("Not supported yet.");
        Connection conn = null;
        PreparedStatement stmt = null;

        String userId = user.getUserId();

        try {
            User foundUser = retrieve(userId);

            final String sql = "UPDATE User SET Password = ? WHERE Email = ?";

            conn = DriverManager.getConnection(URL);
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, user.getPassword());
            stmt.setString(2, userId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new NonRecoverableException("UserDAO-ERR-6" + e.toString(), e);
        } finally {
            close(conn, stmt);
        }
    }

    /**
     * Return whether the given user (id) exists in the database.
     *
     * @param userId
     * @param conn an existing connection to the database, which is not closed
     * by this method.
     * @return true, if the user id exists in the database, otherwise false
     * @throws NonRecoverableException (see ex.getCause().getErrorCode())
     */
    private boolean exists(String userId, Connection conn) throws NonRecoverableException {
        final String sql = "SELECT Email FROM User WHERE Email = ?";

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, userId);

            ResultSet rs = stmt.executeQuery();

            return rs.next();

        } catch (SQLException ex) {
            throw new NonRecoverableException("UserDAO-ERR-3" + ex.toString(), ex);
        } finally {
            close(stmt);
        }
    }
}
