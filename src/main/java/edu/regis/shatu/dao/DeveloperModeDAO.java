
/**
 * Data access object for the DeveloperMode table.
 *
 * This DAO supports SHAT-364 developer mode behavior. Developer mode lets a
 * developer control which TutoringMode a user should enter after sign in
 * without recompiling the application or hardcoding test values in Java.
 *
 * The DeveloperMode table stores one optional row per user. If no row exists,
 * or if developer mode is disabled, the application should continue using the
 * normal tutoring mode already loaded with the session.
 */

package edu.regis.shatu.dao;

import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.model.aol.TutoringMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeveloperModeDAO extends MySqlDAO {

    /**
     * Data access object for the DeveloperMode table.
     *
     * Developer mode lets developers choose a tutoring mode from the database
     * without recompiling or hardcoding test values. This class also verifies
     * that the DeveloperMode table exists before reading from it, so older local
     * databases do not break after pulling SHAT-364.
     */
    public DeveloperModeDAO() {
        super("DeveloperMode", "UserId");
    }

    /**
     * Retrieves the developer-selected tutoring mode for a user.
     *
     * If the DeveloperMode table is missing, this method creates it. If the
     * user has no row, developer mode is disabled, or the stored mode is blank,
     * this method returns null so normal login behavior continues.
     *
     * @param userId the account user id
     * @return selected TutoringMode, or null if no override should be applied
     * @throws NonRecoverableException if schema setup or retrieval fails
     */
    public TutoringMode retrieveTutoringMode(String userId) throws NonRecoverableException {
        ensureDeveloperModeTable();
        
        ensureUserDeveloperModeRow(userId);
        
        String sql = "SELECT IsEnabled, TutoringMode FROM DeveloperMode WHERE UserId = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                boolean isEnabled = rs.getInt("IsEnabled") == 1;
                if (!isEnabled) {
                    return null;
                }

                String mode = rs.getString("TutoringMode");
                if (mode == null || mode.isEmpty()) {
                    return null;
                }

                return TutoringMode.valueOf(mode);
            }

        } catch (SQLException ex) {
            throw new NonRecoverableException("Error retrieving developer mode: " + ex.toString(), ex);
        } catch (IllegalArgumentException ex) {
            throw new NonRecoverableException("Invalid developer tutoring mode for user: " + userId, ex);
        }
    }

    /**
     * Creates the DeveloperMode table if it is missing.
     *
     * This keeps older local databases compatible after developers pull the
     * SHAT-364 changes. The setup_ShaTuDB.sql file still contains the official
     * schema, but this method prevents runtime errors when the local database
     * has not been rebuilt.
     *
     * @throws NonRecoverableException if the table cannot be created
     */
    private void ensureDeveloperModeTable() throws NonRecoverableException {
        String createTableSql
                = "CREATE TABLE IF NOT EXISTS DeveloperMode ("
                + "UserId VARCHAR(255) NOT NULL PRIMARY KEY, "
                + "IsEnabled TINYINT DEFAULT 0, "
                + "TutoringMode ENUM('SEE_ONE', 'DO_ONE', 'TEACH_ONE') DEFAULT 'SEE_ONE'"
                + ")";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(createTableSql)) {

            stmt.executeUpdate();

        } catch (SQLException ex) {
            throw new NonRecoverableException("Error creating DeveloperMode table: " + ex.toString(), ex);
        }
    }
    
    
    /**
    * Creates a default DeveloperMode row for the user if one does not already
    * exist.
    *
    * The default keeps developer mode disabled so normal users are not affected.
    * A developer can later enable the row and choose SEE_ONE, DO_ONE, or TEACH_ONE.
    *
    * @param userId the account user id
    * @throws NonRecoverableException if the default row cannot be created
    */
   private void ensureUserDeveloperModeRow(String userId) throws NonRecoverableException {
       String sql
               = "INSERT IGNORE INTO DeveloperMode "
               + "(UserId, IsEnabled, TutoringMode) "
               + "VALUES (?, 0, 'SEE_ONE')";

       try (Connection conn = DriverManager.getConnection(URL);
            PreparedStatement stmt = conn.prepareStatement(sql)) {

           stmt.setString(1, userId);
           stmt.executeUpdate();

       } catch (SQLException ex) {
           throw new NonRecoverableException("Error creating default developer mode row: " + ex.toString(), ex);
       }
   }

    public void updateDeveloperMode(String userId, boolean isEnabled, TutoringMode mode) throws NonRecoverableException {
        ensureDeveloperModeTable();
        ensureUserDeveloperModeRow(userId);

        String sql
                = "UPDATE DeveloperMode "
                + "SET IsEnabled = ?, TutoringMode = ? "
                + "WHERE UserId = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, isEnabled ? 1 : 0);
            stmt.setString(2, mode.name());
            stmt.setString(3, userId);
            stmt.executeUpdate();

        } catch (SQLException ex) {
            throw new NonRecoverableException("Error updating developer mode: " + ex.toString(), ex);
        }
    }
   
   
}