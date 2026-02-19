package database.DAO;

import school.School;

// imports
import java.sql.*;
import java.util.logging.*;

import database.DBUtils;
import display.LogHandler;

public class SchoolDAO {
    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(SchoolDAO.class.getName());

    // STATIC block for **LOGGING**
    static {
        LogHandler.createLog(logger, "SchoolDAO");
    }

    public School fetchSchoolInfo(Connection conn) {
        School school = new School();
        String sql = "SELECT * FROM School";
        try (PreparedStatement rm = conn.prepareStatement(sql); ResultSet rs = rm.executeQuery();) {

            if (rs.next()) {
                school = new School(rs.getString("Name"), rs.getString("Principal"),
                        rs.getString("location"));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error fetching School info: ", e);
        }
        return school;
    }

    // In DB there is a check that doesnt allow more than one row in School info
    // table, hence why updating is the best option, rather than adding, deleting
    // rows, simply updating would be the best option

    public boolean updateSchool(String name, String principal, String location) {

        return DBUtils.runInTransaction(conn -> {
            String sql = "UPDATE School SET Name = ?, Principal = ?, location = ? WHERE id = 1";
            try (PreparedStatement rm = conn.prepareStatement(sql)) {

                rm.setString(1, name);
                rm.setString(2, principal);
                rm.setString(3, location);

                int rs = rm.executeUpdate();
                return rs > 0 || rs == 1;

            }
        });
    }

}
