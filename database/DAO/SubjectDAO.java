// BETA VERSION FINALIZED - SubjectDAO
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

public class SubjectDAO {

    private static final Logger logger = Logger.getLogger(SubjectDAO.class.getName());

    static {
        LogHandler.createLog(logger, "SubjectDAO");
    }

    public Subjects fetchSubject(Connection conn, String name) {
        Subjects subj = new Subjects(name);

        String subjectSQL = "SELECT * FROM Subjects WHERE SubjectName = ?;";

        try {
            if (!subjectExists(conn, name)) {
                logger.warning("Subject NOT found.");
                return new Subjects();
            }

            try (PreparedStatement rm = conn.prepareStatement(subjectSQL)) {
                rm.setString(1, name);

                try (ResultSet rs = rm.executeQuery()) {
                    if (rs.next()) {
                        subj.setID(rs.getInt("SubjectID"));
                        subj.setName(rs.getString("SubjectName"));
                        subj.setClassID(rs.getInt("ClassID"));

                    } else {
                        logger.warning("Unable to get Subject.");
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while fetching Subject.", e);
        }

        return subj;
    }

    public Subjects fetchSubject(Connection conn, int id) {
        Subjects subj = new Subjects();

        String subjectSQL = "SELECT * FROM Subjects WHERE SubjectID = ?;";

        try {
            if (!subjectExists(conn, id)) {
                logger.warning("Subject NOT found.");
                return new Subjects();
            }

            try (PreparedStatement rm = conn.prepareStatement(subjectSQL)) {
                rm.setInt(1, id);

                try (ResultSet rs = rm.executeQuery()) {
                    if (rs.next()) {
                        subj.setID(rs.getInt("SubjectID"));
                        subj.setName(rs.getString("SubjectName"));
                        subj.setClassID(rs.getInt("ClassID"));

                    } else {
                        logger.warning("Unable to get Subject.");
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while fetching Subject by ID.", e);
        }

        return subj;
    }

    public boolean subjectExists(Connection conn, String name) {
        String check = "SELECT 1 FROM Subjects WHERE SubjectName = ?;";

        try (PreparedStatement rm = conn.prepareStatement(check)) {
            rm.setString(1, name);

            try (ResultSet rs = rm.executeQuery()) {
                if (rs.next()) {
                    logger.info("Match found, Subject Exists.");
                    return true;
                } else {
                    logger.warning("No match found, Subject Does Not Exist.");
                    return false;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error checking Subject existence.", e);
        }
        return false;
    }

    public boolean subjectExists(Connection conn, int id) {
        String check = "SELECT 1 FROM Subjects WHERE SubjectID = ?;";

        try (PreparedStatement rm = conn.prepareStatement(check)) {
            rm.setInt(1, id);

            try (ResultSet rs = rm.executeQuery()) {
                if (rs.next()) {
                    logger.info("Match found, Subject exists.");
                    return true;
                } else {
                    logger.warning("Subject does not exist.");
                    return false;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error checking Subject existence.", e);
        }
        return false;
    }

    public int fetchSubjectID(Connection conn, String name) {
        if (!subjectExists(conn, name)) {
            logger.warning("Subject does not exist.");
            return -1;
        }

        String sql = "SELECT SubjectID FROM Subjects WHERE SubjectName = ?";
        try (PreparedStatement rm = conn.prepareStatement(sql)) {
            rm.setString(1, name);
            try (ResultSet rs = rm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("SubjectID");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error fetching Subject ID.", e);
        }
        return -1;
    }

    public boolean insertSubject(Subjects subj) {
        return DBUtils.runInTransaction(conn -> {
            if (subjectExists(conn, subj.getName())) {
                logger.warning("Subject already exists.");
                return false;
            }

            String subjectSQL = "INSERT INTO Subjects (SubjectName, ClassID) VALUES (?,?)";
            try (PreparedStatement rm = conn.prepareStatement(subjectSQL, Statement.RETURN_GENERATED_KEYS)) {
                rm.setString(1, subj.getName());
                if (subj.getClassID() != null) {
                    rm.setInt(2, subj.getClassID());
                } else {
                    rm.setNull(2, Types.INTEGER);
                }

                int rs = rm.executeUpdate();

                ResultSet ID = rm.getGeneratedKeys();
                if (ID.next()) {
                    int genID = ID.getInt(1);
                    logger.info("Inserted Subject with ID: " + genID);
                    subj.setID(genID);
                }

                return rs > 0;
            }
        });
    }

    public boolean insertSubject(String className, String subjectName) {
        return DBUtils.runInTransaction(conn -> {
            if (subjectExists(conn, subjectName)) {
                logger.warning("Subject already exists.");
                return false;
            }

            ClassDAO classDAO = new ClassDAO();
            ClassRoom cls = classDAO.fetchClass(conn, className);
            if (cls.isEmpty()) {
                logger.warning("Class does not exist.");
                return false;
            }

            String subjectSQL = "INSERT INTO Subjects (SubjectName, ClassID) VALUES (?,?)";
            try (PreparedStatement rm = conn.prepareStatement(subjectSQL, Statement.RETURN_GENERATED_KEYS)) {
                rm.setString(1, subjectName);
                rm.setInt(2, cls.getID());

                int rs = rm.executeUpdate();
                return rs > 0;
            }
        });
    }

    public boolean deleteSubject(Subjects subj) {
        return DBUtils.runInTransaction(conn -> {
            String deleteSubjectSQL = "DELETE FROM Subjects WHERE SubjectID = ?";
            try (PreparedStatement rm = conn.prepareStatement(deleteSubjectSQL)) {
                rm.setObject(1, subj.getID(), Types.INTEGER);

                int rs = rm.executeUpdate();
                return rs > 0;
            }
        });
    }

    public boolean deleteSubject(String className, String subjectName) {
        return DBUtils.runInTransaction(conn -> {
            ClassDAO classDAO = new ClassDAO();
            ClassRoom cls = classDAO.fetchClass(conn, className);
            if (cls.isEmpty()) {
                logger.warning("Class does not exist.");
                return false;
            }

            String deleteSubjectSQL = "DELETE FROM Subjects WHERE SubjectName = ? AND ClassID = ?";
            try (PreparedStatement rm = conn.prepareStatement(deleteSubjectSQL)) {
                rm.setString(1, subjectName);
                rm.setInt(2, cls.getID());

                int rs = rm.executeUpdate();
                return rs > 0;
            }
        });
    }

    public boolean updateSubject(Subjects subj) {
        return DBUtils.runInTransaction(conn -> {
            if (subj.getID() == null || subj.getID() == 0) {
                logger.warning("Subject ID is required.");
                return false;
            }

            if (!subjectExists(conn, subj.getID())) {
                logger.warning("Subject does not exist.");
                return false;
            }

            StringBuilder sql = new StringBuilder("UPDATE Subjects SET ");
            List<Object> params = new ArrayList<>();

            if (subj.getName() != null && !subj.getName().isEmpty()) {
                sql.append("SubjectName = ?,");
                params.add(subj.getName());
            }
            if (subj.getClassID() != null && subj.getClassID() != 0) {
                sql.append("ClassID = ?,");
                params.add(subj.getClassID());
            }

            if (params.isEmpty()) {
                logger.warning("No fields to update.");
                return false;
            }

            sql.setLength(sql.length() - 1); // removing trailing ','
            sql.append(" WHERE SubjectID = ?");

            try (PreparedStatement rm = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    rm.setObject(i + 1, params.get(i));
                }
                rm.setInt(params.size() + 1, subj.getID());
                return rm.executeUpdate() > 0;
            }
        });
    }

    public List<Subjects> listSubjects(Connection conn) {
        List<Subjects> subjects = new ArrayList<>();
        String listSubjectSQL = "SELECT s.SubjectID, s.SubjectName, s.ClassID, c.ClassName FROM Subjects s LEFT JOIN Class c ON s.ClassID = c.ClassID";

        try (PreparedStatement rm = conn.prepareStatement(listSubjectSQL);
                ResultSet rs = rm.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                System.out.println("No Data is available.");
                return new ArrayList<>();
            }

            while (rs.next()) {
                Subjects subj = new Subjects();
                subj.setID(rs.getInt("SubjectID"));
                subj.setName(rs.getString("SubjectName"));
                subj.setClassID(rs.getInt("ClassID"));
                subj.setClassName(rs.getString("ClassName"));
                subjects.add(subj);
            }
            return subjects;

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Unable to list all Subjects", e);
        }
        return new ArrayList<>();
    }

    // list subjects by class name
    public List<Subjects> listSubjects(Connection conn, String className) {

        List<Subjects> subjects = new ArrayList<>();
        String listSubjectSQL = "SELECT s.SubjectID, s.SubjectName, s.ClassID, c.ClassName FROM Subjects s JOIN Class c ON s.ClassID = c.ClassID WHERE c.ClassName = ?";

        try (PreparedStatement rm = conn.prepareStatement(listSubjectSQL)) {
            rm.setString(1, className);

            try (ResultSet rs = rm.executeQuery()) {

                if (!rs.isBeforeFirst()) {
                    System.out.println("No Data is available.");
                    return new ArrayList<>();
                }

                while (rs.next()) {
                    Subjects subj = new Subjects();
                    subj.setID(rs.getInt("SubjectID"));
                    subj.setName(rs.getString("SubjectName"));
                    subj.setClassID(rs.getInt("ClassID"));
                    subj.setClassName(rs.getString("ClassName"));
                    subjects.add(subj);
                }
                return subjects;

            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Unable to list all Subjects", e);
        }
        return subjects;
    }

    // Filter subjects by class ID
    public List<Subjects> listSubjectsByClass(Connection conn, int classId) {
        List<Subjects> subjects = new ArrayList<>();
        String sql = "SELECT s.SubjectID, s.SubjectName, s.ClassID, c.ClassName FROM Subjects s " +
                   "JOIN Class c ON s.ClassID = c.ClassID " +
                   "WHERE s.ClassID = ?";

        try (PreparedStatement rm = conn.prepareStatement(sql)) {
            rm.setInt(1, classId);
            try (ResultSet rs = rm.executeQuery()) {

                if (!rs.isBeforeFirst()) {
                    System.out.println("No subjects found for this class.");
                    return new ArrayList<>();
                }

                while (rs.next()) {
                    Subjects subj = new Subjects();
                    subj.setID(rs.getInt("SubjectID"));
                    subj.setName(rs.getString("SubjectName"));
                    subj.setClassID(rs.getInt("ClassID"));
                    subj.setClassName(rs.getString("ClassName"));
                    subjects.add(subj);
                }
                return subjects;
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Unable to list Subjects by class", e);
        }
        return new ArrayList<>();
    }

    // Filter subjects by teacher
    public List<Subjects> listSubjectsByTeacher(Connection conn, int teacherId) {
        List<Subjects> subjects = new ArrayList<>();
        String sql = "SELECT DISTINCT s.SubjectID, s.SubjectName, s.ClassID, c.ClassName FROM Subjects s " +
                   "JOIN Teacher t ON s.SubjectID = t.SubjectID " +
                   "JOIN Class c ON s.ClassID = c.ClassID " +
                   "WHERE t.TeacherID = ?";

        try (PreparedStatement rm = conn.prepareStatement(sql)) {
            rm.setInt(1, teacherId);
            try (ResultSet rs = rm.executeQuery()) {

                if (!rs.isBeforeFirst()) {
                    System.out.println("No subjects found for this teacher.");
                    return new ArrayList<>();
                }

                while (rs.next()) {
                    Subjects subj = new Subjects();
                    subj.setID(rs.getInt("SubjectID"));
                    subj.setName(rs.getString("SubjectName"));
                    subj.setClassID(rs.getInt("ClassID"));
                    subj.setClassName(rs.getString("ClassName"));
                    subjects.add(subj);
                }
                return subjects;
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Unable to list Subjects by teacher", e);
        }
        return new ArrayList<>();
    }
}
