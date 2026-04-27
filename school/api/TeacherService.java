package school.api;

import java.util.List;

import classroom.Subjects;
import database.DBUtils;
import database.DAO.TeacherDAO;
import people.Teacher;

public class TeacherService {

	// ===== READ METHODS =====

	public static List<Teacher> getTeachers() {
		return DBUtils.runInTransaction(conn -> {
			TeacherDAO teacherDAO = new TeacherDAO();
			return teacherDAO.listTeachers(conn);
		});
	}

	public static Teacher getTeacher(String name) {
		return DBUtils.runInTransaction(conn -> {
			TeacherDAO teacherDAO = new TeacherDAO();
			return teacherDAO.fetchTeacher(conn, name);
		});
	}

	public static Teacher getTeacher(int id) {
		return DBUtils.runInTransaction(conn -> {
			TeacherDAO teacherDAO = new TeacherDAO();
			return teacherDAO.fetchTeacher(conn, id);
		});
	}

	public static boolean teacherExists(String name) {
		return DBUtils.runInTransaction(conn -> {
			TeacherDAO teacherDAO = new TeacherDAO();
			return teacherDAO.teacherExists(conn, name);
		});
	}

	// ===== WRITE METHODS =====

	public static boolean addTeacher(Teacher teacher) {
		return DBUtils.runInTransaction(conn -> {
			TeacherDAO teacherDAO = new TeacherDAO();
			return teacherDAO.insertTeacher(teacher);
		});
	}

	public static boolean addTeacher(String name, int subjectId) {
		Teacher teacher = new Teacher(name);
		teacher.setSubject(new Subjects());
		teacher.getSubject().setID(subjectId);
		return addTeacher(teacher);
	}

	public static boolean updateTeacher(String oldName, String newName) {
		return DBUtils.runInTransaction(conn -> {
			TeacherDAO teacherDAO = new TeacherDAO();
			Teacher existing = teacherDAO.fetchTeacher(conn, oldName);
			if (existing.getID() == null || existing.getID() == 0) {
				return false;
			}

			Teacher oldTeacher = new Teacher(existing.getName());
			oldTeacher.setID(existing.getID());

			Teacher updated = new Teacher();
			updated.setName(newName);
			return teacherDAO.updateTeacher(oldTeacher, updated);
		});
	}

	public static boolean assignSubject(String teacherName, int subjectId) {
		return DBUtils.runInTransaction(conn -> {
			TeacherDAO teacherDAO = new TeacherDAO();
			Teacher existing = teacherDAO.fetchTeacher(conn, teacherName);
			if (existing.getID() == null || existing.getID() == 0) {
				return false;
			}

			Teacher oldTeacher = new Teacher(existing.getName());
			oldTeacher.setID(existing.getID());

			Teacher updated = new Teacher();
			Subjects subject = new Subjects();
			subject.setID(subjectId);
			updated.setSubject(subject);

			return teacherDAO.updateTeacher(oldTeacher, updated);
		});
	}

	public static boolean updateTeacher(String oldName, String newName, Integer subjectId) {
		return DBUtils.runInTransaction(conn -> {
			TeacherDAO teacherDAO = new TeacherDAO();
			Teacher existing = teacherDAO.fetchTeacher(conn, oldName);
			if (existing.getID() == null || existing.getID() == 0) {
				return false;
			}

			Teacher oldTeacher = new Teacher(existing.getName());
			oldTeacher.setID(existing.getID());

			Teacher updated = new Teacher();
			updated.setName(newName);
			if (subjectId != null) {
				Subjects subject = new Subjects();
				subject.setID(subjectId);
				updated.setSubject(subject);
			}

			return teacherDAO.updateTeacher(oldTeacher, updated);
		});
	}

	public static boolean deleteTeacher(String name) {
		return DBUtils.runInTransaction(conn -> {
			TeacherDAO teacherDAO = new TeacherDAO();
			Teacher teacher = teacherDAO.fetchTeacher(conn, name);
			return teacherDAO.deleteTeacher(teacher);
		});
	}
}
