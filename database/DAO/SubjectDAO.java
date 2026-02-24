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
                        subj.setSubjectName(rs.getString("SubjectName"));
                        subj.setClassID(rs.getInt("ClassID"));
                        subj.setTotalMarks(rs.getInt("Marks"));

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
            if (subjectExists(conn, subj.getSubjectName())) {
                logger.warning("Subject already exists.");
                return false;
            }

            String subjectSQL = "INSERT INTO Subjects (SubjectName, ClassID, Marks) VALUES (?,?,?)";
            try (PreparedStatement rm = conn.prepareStatement(subjectSQL, Statement.RETURN_GENERATED_KEYS)) {
                rm.setString(1, subj.getSubjectName());
                if (subj.getClassID() != null) {
                    rm.setInt(2, subj.getClassID());
                } else {
                    rm.setNull(2, Types.INTEGER);
                }
                rm.setInt(3, subj.getTotalMarks());

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

    public boolean insertSubject(String className, String subjectName, int marks) {
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

            String subjectSQL = "INSERT INTO Subjects (SubjectName, ClassID, Marks) VALUES (?,?,?)";
            try (PreparedStatement rm = conn.prepareStatement(subjectSQL, Statement.RETURN_GENERATED_KEYS)) {
                rm.setString(1, subjectName);
                rm.setInt(2, cls.getID());
                rm.setInt(3, marks);

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

    public boolean updateSubject(Subjects oldSubject, Subjects newSubject) {
        return DBUtils.runInTransaction(conn -> {
            if (!subjectExists(conn, oldSubject.getSubjectName())) {
                logger.warning("Subject Doesnt exist.");
                return false;
            }

            StringBuilder sql = new StringBuilder("UPDATE Subjects SET ");

            List<Object> parameters = new ArrayList<>();

            if (newSubject.getSubjectName() != null) {
                sql.append("SubjectName = ?,");
                parameters.add(newSubject.getSubjectName());
            }

            // only has option of obtained marks, total are fixed at 100
            if (newSubject.getObtMarks() != null) {
                sql.append(" Marks = ?,");
                parameters.add(newSubject.getObtMarks());
            }

            sql.append(" WHERE SubjectName = ?");

            try (PreparedStatement rm = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < parameters.size(); i++) {
                    rm.setObject(i + 1, parameters.get(i));
                }

                int rs = rm.executeUpdate();
                return rs > 0;
            }
        });
    }

    public boolean updateSubject(String oldName, String newName) {
        return DBUtils.runInTransaction(conn -> {
            if (!subjectExists(conn, oldName)) {
                logger.warning("Subject Doesnt exist.");
                return false;
            }

            String sql = "UPDATE Subjects SET SubjectName = ? WHERE SubjectName = ?";
            try (PreparedStatement rm = conn.prepareStatement(sql)) {
                rm.setString(1, newName);
                rm.setString(2, oldName);

                int rs = rm.executeUpdate();
                return rs > 0;
            }
        });
    }

    public List<Subjects> listSubjects(Connection conn) {
        List<Subjects> subjects = new ArrayList<>();
        String listSubjectSQL = "SELECT * FROM Subjects";

        try (PreparedStatement rm = conn.prepareStatement(listSubjectSQL);
                ResultSet rs = rm.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                System.out.println("No Data is available.");
                return new ArrayList<>();
            }

            while (rs.next()) {
                Subjects subj = new Subjects();
                subj.setID(rs.getInt("SubjectID"));
                subj.setSubjectName(rs.getString("SubjectName"));
                subj.setTotalMarks(rs.getInt("Marks"));
                subj.setClassID(rs.getInt("ClassID"));
                subjects.add(subj);
            }
            return subjects;

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Unable to list all Subjects", e);
        }
        return new ArrayList<>();
    }

    public List<Subjects> listClassSubjectswithMarks(Connection conn, String className) {
        List<Subjects> subjects = new ArrayList<>();
        ClassDAO classDAO = new ClassDAO();
        ClassRoom cls = classDAO.fetchClass(conn, className);
        if (cls.isEmpty()) {
            logger.warning("Class does not exist.");
            return new ArrayList<>();
        }

        String sql = "SELECT * FROM Subjects WHERE ClassID = ?";
        try (PreparedStatement rm = conn.prepareStatement(sql)) {
            rm.setInt(1, cls.getID());
            try (ResultSet rs = rm.executeQuery()) {
                while (rs.next()) {
                    Subjects subj = new Subjects();
                    subj.setID(rs.getInt("SubjectID"));
                    subj.setSubjectName(rs.getString("SubjectName"));
                    subj.setTotalMarks(rs.getInt("Marks"));
                    subj.setClassID(cls.getID());
                    subjects.add(subj);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Unable to list subjects for class", e);
        }
        return subjects;
    }

}
