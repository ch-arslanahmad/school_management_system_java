// BETA VERSION FINALIZED - ClassDAO
package database.DAO;

// package imports
import display.LogHandler;
import classroom.*;
import database.DBUtils;

// imports
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

// public class
public class ClassDAO {

    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(ClassDAO.class.getName());

    // STATIC block for **LOGGING**
    static {
        LogHandler.createLog(logger, "ClassDAO");
    }

    public ClassRoom fetchClass(Connection conn, String name) {
        ClassRoom cls = new ClassRoom(name);
        String classIDSQL = "SELECT * FROM Class WHERE ClassName = ?;";

        try {

            if (!ClassExists(conn, cls.getName())) {
                logger.warning("Class NOT found.");
                return new ClassRoom();
            }

            try (PreparedStatement rm = conn.prepareStatement(classIDSQL)) {
                rm.setString(1, name);
                try (ResultSet rs = rm.executeQuery()) {
                    if (rs.next()) {
                        // fetches and stores the ClassID in a variable from the matched row
                        cls.setID(rs.getInt("ClassID"));
                        cls.setTuitionFee(rs.getInt("Tuition_Fee"));
                        cls.setStationaryFee(rs.getInt("Stationary_Fee"));
                        cls.setPaperFee(rs.getInt("Paper_Fee"));
                    } else {
                        logger.warning("Unable to get Class.");
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while fetching Class.", e);
        }

        return cls;

    }

    public ClassRoom fetchClass(Connection conn, int id) {
        ClassRoom cls = new ClassRoom();
        cls.setID(id);
        String sql = "SELECT * FROM Class WHERE ClassID = ?;";

        try {

            if (!ClassExists(conn, cls.getID())) {
                logger.warning("Class NOT found.");
                return new ClassRoom();
            }

            try (PreparedStatement rm = conn.prepareStatement(sql)) {
                rm.setInt(1, id);

                try (ResultSet rs = rm.executeQuery()) {
                    if (rs.next()) {
                        cls.setName(rs.getString("ClassName"));
                        cls.setTuitionFee(rs.getInt("Tuition_Fee"));
                        cls.setStationaryFee(rs.getInt("Stationary_Fee"));
                        cls.setPaperFee(rs.getInt("Paper_Fee"));
                    } else {
                        logger.warning("Unable to get Class.");
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while fetching Class by ID.", e);
        }

        return cls;

    }

    // see if class exists by name
    public boolean ClassExists(Connection conn, String name) {
        String check = "SELECT 1 FROM Class WHERE ClassName = ?;";

        try (PreparedStatement rm = conn.prepareStatement(check)) {
            rm.setString(1, name);

            try (ResultSet rs = rm.executeQuery()) {
                if (rs.next()) {
                    // If rs.next() returns true, a row exists - meaning class exists
                    logger.info("Match found, Class Exists.");
                    return true;
                }
                logger.warning("No match found, Class Does Not Exist.");
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error checking Class existence.", e);
            return false;
        }
    }

    // see if class exists by ID
    public boolean ClassExists(Connection conn, int id) {
        String check = "SELECT 1 FROM Class WHERE ClassID = ?;";

        try (PreparedStatement rm = conn.prepareStatement(check)) {
            rm.setObject(1, id, Types.INTEGER);

            try (ResultSet rs = rm.executeQuery()) {
                if (rs.next()) {
                    // If rs.next() returns true, a row exists - meaning class exists
                    logger.info("Match found, Class Exists.");
                    return true;
                }
                logger.warning("No match found, Class Does Not Exist.");
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error checking Class existence.", e);
            return false;
        }
    }

    // to insert a class
    public boolean insertClass(ClassRoom cls) {

        return DBUtils.runInTransaction(conn -> {
            if (ClassExists(conn, cls.getName())) {
                logger.warning("Class already exists.");
                return false;
            }

            /**
             * <p>
             * preparedStatement(query, Statement.RETURN_GENERATED_KEYS) is used to execute
             * parameterized SQL queries, which helps
             * prevent SQL injection attacks and allows for efficient query execution.
             * 
             * Here ``Statement.RETURN_GENERATED_KEYS`` is used to indicate that we want to
             * retrieve any auto-generated keys (like an auto-incremented ID) that result
             * from executing the query. This is particularly useful when inserting new
             * records into a database, as it allows us to easily obtain the unique
             * identifier of the newly inserted record without needing to execute a separate
             * query.
             * 
             */

            String classSQL = "INSERT INTO Class(ClassName, Tuition_Fee,Stationary_Fee,Paper_Fee) VALUES(?,?,?,?)";
            try (PreparedStatement rm = conn.prepareStatement(classSQL, Statement.RETURN_GENERATED_KEYS);) {
                // set values in the query
                rm.setString(1, cls.getName());
                rm.setObject(2, cls.getTuitionFee(), Types.INTEGER);
                rm.setObject(3, cls.getStationaryFee(), Types.INTEGER);
                rm.setObject(4, cls.getPaperFee(), Types.INTEGER);
                // execute query
                int rs = rm.executeUpdate();

                ResultSet ID = rm.getGeneratedKeys();

                if (ID.next()) {
                    int genID = ID.getInt(1);
                    logger.info("Inserted Class with ID: " + genID);
                    cls.setID(genID);
                }

                return rs > 0; // return true if at least one row is affected, otherwise false
            }
        });
    }

    public boolean deleteClass(ClassRoom cls) {

        return DBUtils.runInTransaction(conn -> {
            String deleteClassSQL = "DELETE FROM Class WHERE ClassID = ?";
            try (PreparedStatement rm = conn.prepareStatement(deleteClassSQL)) {
                // set values in the query
                rm.setObject(1, cls.getID(), Types.INTEGER);

                // execute query
                int rs = rm.executeUpdate();

                return rs > 0;
            }
        });
    }

    // update class
    public boolean updateClass(ClassRoom cls) {
        return DBUtils.runInTransaction(conn -> {
            if (cls.getID() == null || cls.getID() == 0) {
                logger.warning("Class ID is required.");
                return false;
            }

            if (!ClassExists(conn, cls.getID())) {
                logger.warning("Class does not exist.");
                return false;
            }

            StringBuilder sql = new StringBuilder("UPDATE Class SET ");
            List<Object> params = new ArrayList<>();

            if (cls.getName() != null && !cls.getName().isEmpty()) {
                sql.append("ClassName = ?,");
                params.add(cls.getName());
            }
            if (cls.getTuitionFee() != null) {
                sql.append("Tuition_Fee = ?,");
                params.add(cls.getTuitionFee());
            }
            if (cls.getStationaryFee() != null) {
                sql.append("Stationary_Fee = ?,");
                params.add(cls.getStationaryFee());
            }
            if (cls.getPaperFee() != null) {
                sql.append("Paper_Fee = ?,");
                params.add(cls.getPaperFee());
            }

            if (params.isEmpty()) {
                logger.warning("No fields to update.");
                return false;
            }

            sql.setLength(sql.length() - 1); // removing trailing ','
            sql.append(" WHERE ClassID = ?");

            try (PreparedStatement rm = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    rm.setObject(i + 1, params.get(i));
                }
                rm.setInt(params.size() + 1, cls.getID());
                return rm.executeUpdate() > 0;
            }
        });
    }

    // list All Classes
    public List<ClassRoom> listClass(Connection conn) {

        List<ClassRoom> classrooms = new ArrayList<>();
        String listClassSQL = "SELECT * FROM Class";
        try (PreparedStatement rm = conn.prepareStatement(listClassSQL);
                ResultSet rs = rm.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                System.out.println("No Data is available.");
                return new ArrayList<>();
            }
            while (rs.next()) {

                ClassRoom cls = new ClassRoom(rs.getInt("ClassID"), rs.getString("ClassName"), rs.getInt("Tuition_Fee"),
                        rs.getInt("Stationary_Fee"), rs.getInt("Paper_Fee"));
                classrooms.add(cls);
            }
            return classrooms;

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Unable to list all Classes", e);
        }
        return new ArrayList<>();
    }

    public ClassRoom getClassFees(Connection conn, String className) {
        return fetchClass(conn, className);
    }

}
