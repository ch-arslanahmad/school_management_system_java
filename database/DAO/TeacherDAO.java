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

    public boolean updateTeacher(Teacher teacher) {
        return DBUtils.runInTransaction(conn -> {
            if (teacher.getID() == null || teacher.getID() == 0) {
                logger.warning("Teacher ID is required for update.");
                return false;
            }

            if (!teacherExists(conn, teacher.getID())) {
                logger.warning("Teacher does not exist.");
                return false;
            }

            StringBuilder sql = new StringBuilder("UPDATE Teacher SET ");
            List<Object> params = new ArrayList<>();

            if (teacher.getName() != null && !teacher.getName().isEmpty()) {
                sql.append("TeacherName = ?,");
                params.add(teacher.getName());
            }
            if (teacher.getSubject() != null && teacher.getSubject().getID() != null && teacher.getSubject().getID() != 0) {
                sql.append("SubjectID = ?,");
                params.add(teacher.getSubject().getID());
            }

            if (params.isEmpty()) {
                logger.warning("No fields to update.");
                return false;
            }

            sql.setLength(sql.length() - 1); // remove trailing comma
            sql.append(" WHERE TeacherID = ?");

            try (PreparedStatement rm = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    rm.setObject(i + 1, params.get(i));
                }
                rm.setInt(params.size() + 1, teacher.getID());
                return rm.executeUpdate() > 0;
            }
        });
    }

    public List<Teacher> listTeachers(Connection conn) {
        List<Teacher> teachers = new ArrayList<>();
        String listTeacherSQL = "SELECT t.TeacherID, t.TeacherName, t.SubjectID, s.SubjectName FROM Teacher t LEFT JOIN Subjects s ON t.SubjectID = s.SubjectID";

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
                subj.setName(rs.getString("SubjectName"));
                teacher.setSubject(subj);
                teachers.add(teacher);
            }
            return teachers;

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Unable to list all Teachers", e);
        }
        return new ArrayList<>();
    }

    // Filter teachers by class
    public List<Teacher> listTeachersByClass(Connection conn, int classId) {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT DISTINCT t.TeacherID, t.TeacherName, t.SubjectID, s.SubjectName FROM Teacher t " +
                   "JOIN Subjects s ON t.SubjectID = s.SubjectID " +
                   "JOIN Class c ON s.ClassID = c.ClassID " +
                   "WHERE c.ClassID = ?";

        try (PreparedStatement rm = conn.prepareStatement(sql)) {
            rm.setInt(1, classId);
            try (ResultSet rs = rm.executeQuery()) {

                if (!rs.isBeforeFirst()) {
                    System.out.println("No Teachers found for this class.");
                    return new ArrayList<>();
                }

                while (rs.next()) {
                    Teacher teacher = new Teacher();
                    teacher.setID(rs.getInt("TeacherID"));
                    teacher.setName(rs.getString("TeacherName"));
                    Subjects subj = new Subjects();
                    subj.setID(rs.getInt("SubjectID"));
                    subj.setName(rs.getString("SubjectName"));
                    teacher.setSubject(subj);
                    teachers.add(teacher);
                }
                return teachers;
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Unable to list Teachers by class", e);
        }
        return new ArrayList<>();
    }

    // Filter teachers by subject
    public List<Teacher> listTeachersBySubject(Connection conn, int subjectId) {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT t.TeacherID, t.TeacherName, t.SubjectID, s.SubjectName FROM Teacher t " +
                   "JOIN Subjects s ON t.SubjectID = s.SubjectID " +
                   "WHERE t.SubjectID = ?";

        try (PreparedStatement rm = conn.prepareStatement(sql)) {
            rm.setInt(1, subjectId);
            try (ResultSet rs = rm.executeQuery()) {

                if (!rs.isBeforeFirst()) {
                    System.out.println("No Teachers found for this subject.");
                    return new ArrayList<>();
                }

                while (rs.next()) {
                    Teacher teacher = new Teacher();
                    teacher.setID(rs.getInt("TeacherID"));
                    teacher.setName(rs.getString("TeacherName"));
                    Subjects subj = new Subjects();
                    subj.setID(rs.getInt("SubjectID"));
                    subj.setName(rs.getString("SubjectName"));
                    teacher.setSubject(subj);
                    teachers.add(teacher);
                }
                return teachers;
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Unable to list Teachers by subject", e);
        }
        return new ArrayList<>();
    }


    // list by subject

    List<Subjects> listWithSubjects(Connection conn) {
        List<Subjects> subjects = new ArrayList<>();
        String listTeacherSQL = "SELECT t.TeacherID, t.TeacherName, s.SubjectID, s.SubjectName FROM Subjects s JOIN Teacher t ON s.SubjectID = t.SubjectID";

        try (PreparedStatement rm = conn.prepareStatement(listTeacherSQL);
                ResultSet rs = rm.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                System.out.println("No Data is available.");
                return new ArrayList<>();
            }

            while (rs.next()) {
                // TeacherName, TeacherID, SubjectID
                Teacher teacher = new Teacher();
                Subjects subj = new Subjects();
                teacher.setID(rs.getInt("TeacherID"));
                teacher.setName(rs.getString("TeacherName"));
                subj.setID(rs.getInt("SubjectID"));
                subj.setName(rs.getString("SubjectName"));
                teacher.setSubject(subj);
                subjects.add(subj);
            }
            return subjects;

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Unable to list all Teachers with Subjects", e);
        }
        return new ArrayList<>();
    }

}
