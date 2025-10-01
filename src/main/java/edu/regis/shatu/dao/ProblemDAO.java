/*
 *  SHATU: SHA-256 Tutor
 * 
 *   (C) Johanna & Richard Blumenthal, All rights reserved
 * 
 *   Unauthorized use, duplication or distribution without the authors'
 *   permission is strictly prohibited.
 * 
 *   Unless required by applicable law or agreed to in writing, this
 *   software is distributed on an "AS IS" basis without warranties
 *   or conditions of any kind, either expressed or implied.
 */
package edu.regis.shatu.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;
import edu.regis.shatu.model.aol.Problem;
import edu.regis.shatu.svc.ProblemSvc;

/**
 * A Data Access Object for Problems.
 * 
 */
public class ProblemDAO extends MySqlDAO implements ProblemSvc{
    /**
     * Instantiate this Problem DAO with default values.
     */
    public ProblemDAO() {
        super("Problem", "ProblemId");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Problem retrieve(int problemId) throws ObjNotFoundException, NonRecoverableException {
        final String sql = "SELECT ProblemId, Title, Description, Message FROM Problem WHERE ProblemId = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DriverManager.getConnection(URL);
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, problemId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {               
                Problem problem = new Problem(rs.getInt("ProblemId"));
                problem.setTitle(rs.getString("Title"));
                problem.setDescription(rs.getString("Description"));
                problem.setMessageToHash(rs.getString("Message"));
                return problem;

            } else {
                throw new ObjNotFoundException("Problem Id:" + problemId);
            }
        } catch (SQLException e) {
            throw new NonRecoverableException("ProblemDAO-ERR-1" + e.toString(), e);
        } finally {
            close(conn, stmt);
        }
    }
}
