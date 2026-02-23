// BETA VERSION FINALIZED - TeacherDAO
package database.DAO;

// package imports
import display.LogHandler;
import people.Teacher;
import classroom.Subjects;
import database.DBUtils;

// imports
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class TeacherDAO {

    private static final Logger logger = Logger.getLogger(TeacherDAO.class.getName());

    static {
        LogHandler.createLog(logger, "TeacherDAO");
    }

    public Teacher fetchTeacher(Connection conn, String name) {
        Teacher teacher = new Teacher();

        String teacherSQL = "SELECT * FROM Teacher WHERE TeacherName = ?;";

        try {
            if (!teacherExists(conn, name)) {
                logger.warning("Teacher NOT found.");
                return teacher;
            }

            try (PreparedStatement rm = conn.prepareStatement(teacherSQL)) {
                rm.setString(1, name);

                try (ResultSet rs = rm.executeQuery()) {
                    if (rs.next()) {
                        teacher.setID(rs.getInt("TeacherID"));
                        teacher.setName(rs.getString("TeacherName"));
                        Subjects subj = new Subjects();
                        subj.setID(rs.getInt("SubjectID"));
                        teacher.setSubject(subj);
                    } else {
                        logger.warning("Unable to get Teacher.");
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while fetching Teacher.", e);
        }
        return teacher;
    }

    public Teacher fetchTeacher(Connection conn, int id) {
        Teacher teacher = new Teacher();

        String teacherSQL = "SELECT * FROM Teacher WHERE TeacherID = ?;";

        try {
            if (!teacherExists(conn, id)) {
                logger.warning("Teacher NOT found.");
                return teacher;
            }

            try (PreparedStatement rm = conn.prepareStatement(teacherSQL)) {
                rm.setInt(1, id);

                try (ResultSet rs = rm.executeQuery()) {
                    if (rs.next()) {
                        teacher.setID(rs.getInt("TeacherID"));
                        teacher.setName(rs.getString("TeacherName"));
                        Subjects subj = new Subjects();
                        subj.setID(rs.getInt("SubjectID"));
                        teacher.setSubject(subj);
                    } else {
                        logger.warning("Unable to get Teacher.");
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while fetching Teacher.", e);
        }
        return teacher;
    }

    public boolean teacherExists(Connection conn, String name) {
        String check = "SELECT 1 FROM Teacher WHERE TeacherName = ?;";

        try (PreparedStatement rm = conn.prepareStatement(check)) {
            rm.setString(1, name);

            try (ResultSet rs = rm.executeQuery()) {
                if (rs.next()) {
                    logger.info("Match found, Teacher Exists.");
                    return true;
                } else {
                    logger.warning("No match found, Teacher Does Not Exist.");
                    return false;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error checking Teacher existence.", e);
        }
        return false;
    }

    public boolean teacherExists(Connection conn, int id) throws SQLException {
        String check = "SELECT 1 FROM Teacher WHERE TeacherID = ?;";

        try (PreparedStatement rm = conn.prepareStatement(check)) {
            rm.setInt(1, id);

            try (ResultSet rs = rm.executeQuery()) {
                if (rs.next()) {
                    logger.info("Match found, Teacher Exists.");
                    return true;
                } else {
                    logger.warning("No match found, Teacher Does Not Exist.");
                    return false;
                }
            }
        }
    }

    public boolean insertTeacher(Teacher teacher) {
        return DBUtils.runInTransaction(conn -> {
            if (teacherExists(conn, teacher.getName())) {
                logger.warning("Teacher already exists.");
                return false;
            }

            if (teacher.getSubject() == null || teacher.getSubject().getID() == 0) {
                logger.warning("Subject not set for Teacher.");
                return false;
            }

            String teacherSQL = "INSERT INTO Teacher (TeacherName, SubjectID) VALUES (?,?)";
            try (PreparedStatement rm = conn.prepareStatement(teacherSQL, Statement.RETURN_GENERATED_KEYS)) {
                rm.setString(1, teacher.getName());
                rm.setInt(2, teacher.getSubject().getID());

                int rs = rm.executeUpdate();

                ResultSet ID = rm.getGeneratedKeys();
                if (ID.next()) {
                    int genID = ID.getInt(1);
                    logger.info("Inserted Teacher with ID: " + genID);
                    teacher.setID(genID);
                }

                return rs > 0;
            }
        });
    }

    public boolean deleteTeacher(Teacher teacher) {
        return DBUtils.runInTransaction(conn -> {
            String deleteTeacherSQL = "DELETE FROM Teacher WHERE TeacherID = ?";
            try (PreparedStatement rm = conn.prepareStatement(deleteTeacherSQL)) {
                rm.setObject(1, teacher.getID(), Types.INTEGER);

                int rs = rm.executeUpdate();
                return rs > 0;
            }
        });
    }

    public boolean updateTeacher(Teacher oldTeacher, Teacher newTeacher) {
        return DBUtils.runInTransaction(conn -> {


            Teacher fetchedOldTeacher = fetchTeacher(conn, oldTeacher.getID());
            if (fetchedOldTeacher.getID() == 0) {
                logger.warning("Teacher Doesnt exist.");
                return false;
            }

            if (newTeacher.getName() != null && teacherExists(conn, newTeacher.getName())) {
                logger.warning("Updated Name: " + newTeacher.getName() + "' already exists.");
                return false;
            }

            StringBuilder sql = new StringBuilder("UPDATE Teacher SET ");

            List<Object> parameters = new ArrayList<>();

            if (newTeacher.getName() != null) {
                sql.append("TeacherName = ?,");
                parameters.add(newTeacher.getName());
            }
            if (newTeacher.getSubject() != null && newTeacher.getSubject().getID() != 0) {
                sql.append(" SubjectID = ?,");
                parameters.add(newTeacher.getSubject().getID());
            }

            sql.append(" WHERE TeacherName = ?");

            try (PreparedStatement rm = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < parameters.size(); i++) {
                    rm.setObject(i + 1, parameters.get(i));
                }
                rm.setString(parameters.size() + 1, oldTeacher.getName());

                int rs = rm.executeUpdate();
                return rs > 0;
            }
        });
    }

    public List<Teacher> listTeacher(Connection conn) {
        List<Teacher> teachers = new ArrayList<>();
        String listTeacherSQL = "SELECT * FROM Teacher";

        try (PreparedStatement rm = conn.prepareStatement(listTeacherSQL);
                ResultSet rs = rm.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                System.out.println("No Data is available.");
                return new ArrayList<>();
            }

            while (rs.next()) {
                Teacher teacher = new Teacher();
                teacher.setID(rs.getInt("TeacherID"));
                teacher.setName(rs.getString("TeacherName"));
                Subjects subj = new Subjects();
                subj.setID(rs.getInt("SubjectID"));
                teacher.setSubject(subj);
                teachers.add(teacher);
            }
            return teachers;

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Unable to list all Teachers", e);
        }
        return new ArrayList<>();
    }

}
