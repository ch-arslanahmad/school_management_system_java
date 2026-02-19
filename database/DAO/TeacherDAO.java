package database.DAO;

// package imports
import display.LogHandler;
import people.Teacher;

// imports
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

import classroom.Subjects;
import database.DBUtils;

import java.sql.*;

public class TeacherDAO {
    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(TeacherDAO.class.getName());

    // STATIC block for **LOGGING**
    static {
        LogHandler.createLog(logger, "TeacherDAO");
    }

    // fetch teacherID from name
    public int fetchTeacherID(Connection conn, String name) {
        if (!teacherExists(conn, name)) {
            logger.warning("Teacher does not exist. Add Teacher.");
            return -1;
        } else {
            // SQL Query
            String TeacherIDSQL = "SELECT TeacherID FROM Teacher where TeacherName= ?";

            // try-catch block
            try (
                    PreparedStatement rm = conn.prepareStatement(TeacherIDSQL)) {

                // putting value in query
                rm.setString(1, name);

                // executing query
                ResultSet rs = rm.executeQuery();

                // pointing to column and fetching ClassID
                if (rs.next()) {
                    return rs.getInt("TeacherID");
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error while fetching Teacher ID: ", e);
            }

        }
        return -1;
    }

    // see if teacher exists
    public boolean teacherExists(Connection conn, String name) {
        String ExistSQL = "SELECT COUNT(*) AS count FROM Teacher WHERE TeacherName = ?";

        // prepared statement in try block
        try (PreparedStatement rm = conn.prepareStatement(ExistSQL); ResultSet rs = rm.executeQuery();) {

            // adding value to query
            rm.setString(1, name);

            if (rs.next()) {
                int count = rs.getInt("count");
                if (count > 0) {
                    logger.info("Match found");
                    return true;
                } else {
                    logger.warning("No match found");
                    return false;
                }
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error finding Teacher Existance: ", e);
        }
        return false;
    }

    // fetch TeacherSubject from TeacherName
    public String fetchTeacherSubject(Connection conn, String name) {
        // this requires a fairly long query, similar explaination is already given in
        // TeacherDAO
        String fetchTeacherClass = "SELECT Subjects.SubjectName " + "FROM Teacher "
                + "JOIN Subjects ON Teacher.SubjectID = Subjects.SubjectID "
                + "WHERE TeacherName = ?";

        try (PreparedStatement rm = conn.prepareStatement(fetchTeacherClass)) {
            rm.setString(1, name);
            try (ResultSet rs = rm.executeQuery()) {

                if (rs.next()) {
                    return rs.getString("SubjectName");
                }

            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while fetching SubjectName of Teacher.");
        }

        return "-1";

    }

    // insert Teacher
    public boolean insertTeacher(String subjectName, String name) {
        SubjectDAO check = new SubjectDAO();

        DBUtils.runInTransaction(conn -> {
            int subjectID = check.fetchSubjectID(conn, subjectName);
            // -1 is error-code
            if (subjectID == -1) {
                logger.info("Subjects does not exist.");
                return false;
            }

            String TeacherSQL = "INSERT INTO Teacher (TeacherName, SubjectID) VALUES (?,?)";

            try (PreparedStatement rm = conn.prepareStatement(TeacherSQL)) {
                rm.setString(1, name);
                rm.setInt(2, subjectID);

                int rs = rm.executeUpdate();
                return rs > 0 || rs == 1;
            }

        });
        return false;

    }

    // delete Teacher
    public boolean deleteTeacher(String name) {

        return DBUtils.runInTransaction(conn -> {
            if (!(teacherExists(conn, name))) {
                System.out.println("No Match found");
                return false;
            }
            String delTeachSQL = "DELETE FROM Teacher WHERE TeacherName = ?";
            try (PreparedStatement rm = conn.prepareStatement(delTeachSQL)) {

                // set values in the query
                rm.setString(1, name);

                // execute query
                int rs = rm.executeUpdate();

                return (rs > 0) || rs == 1;
            }
        });

    }

    // update teachername with subject
    public boolean updateTeacherSubject(String name, String updateName, String subjectName) {
        return DBUtils.runInTransaction(conn -> {
            SubjectDAO subject = new SubjectDAO();
            int subjectID = subject.fetchSubjectID(conn, subjectName); // fetch subjectID
            int teachID = fetchTeacherID(conn, name);
            String updQuery = "UPDATE Teacher SET TeacherName = ?, SubjectID = ? WHERE TeacherID = ?";

            try (PreparedStatement rm = conn.prepareStatement(updQuery)) {
                rm.setString(1, updateName);
                rm.setInt(2, subjectID);
                rm.setInt(3, teachID);

                int rs = rm.executeUpdate();

                return rs > 0 || rs == 1;
            }
        });
    }

    // update teacher name only
    public boolean updateTeacher(String name, String updateName) {
        return DBUtils.runInTransaction(conn -> {
            int teachID = fetchTeacherID(conn, name);
            String updQuery = "UPDATE Teacher SET TeacherName = ? WHERE TeacherID = ?";

            try (PreparedStatement rm = conn.prepareStatement(updQuery)) {
                rm.setString(1, updateName);
                rm.setInt(2, teachID);

                int rs = rm.executeUpdate();

                if (rs > 0) {
                    logger.info("Teacher name updated.");
                    return true;
                } else {
                    logger.warning("Error updating teacher name.");
                    return false;
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "ERROR while updating teacher Name: ", e);
            }
            return false;
        });
    }

    

    /*
     * LEFT JOIN is better for this method as it will list all the columns of the
     * left and its related things of the column of the right if they exist unless
     * explicitly show, 'NULL'.
     */

    // list all Teachers
    public List<Teacher> listTeacher(Connection conn) {
        List<Teacher> teachers = new ArrayList<>();
        // Query to list all Teachers
        String listSubjectSQL = "SELECT Teacher.TeacherName, Subjects.SubjectName "
                + "FROM Teacher " + "LEFT JOIN Subjects ON Teacher.SubjectID = Subjects.SubjectID";

        // try-block
        try (
                PreparedStatement rm = conn.prepareStatement(listSubjectSQL)) {

            // inner try-block to fetch and display each row
            try (ResultSet rs = rm.executeQuery()) {
                while (rs.next()) {
                    teachers.add(new Teacher(rs.getString("TeacherName"),
                            new Subjects(rs.getString("SubjectName"))));
                    // IF NEEDED/POSSIBLE USE TENARY OPERATOR
                    /*
                     * String teachname = rs.getString("TeacherName") != null ?
                     * rs.getString("TeacherName") : "NAN"; String subname =
                     * rs.getString("SubjectName") != null ? rs.getString("SubjectName") : "NAN";
                     */

                }
                // return true only if teachers are displayed
                return teachers;
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error while executing Query to List Teachers: ", e);
            }

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while listing Teachers: ", e);
        }

        return new ArrayList<>();

    }

}
