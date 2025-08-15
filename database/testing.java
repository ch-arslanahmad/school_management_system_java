package database;

import database.DAO.*;

class testing {

    public static void main(String[] args) {

        // TEST#2 - SUCCESSFULL
        // ClassDAO, SubectDAO
        /*
         * // CLASS ClassDAO room = new ClassDAO(); String name = "Matric-I"; int ID; if
         * ((ID = room.getValidClassID(name)) == -1) { System.out.println("Error -1"); }
         * else { System.out.println("ID of " + name + " is " + ID); }
         * 
         * String newName = "9th"; if (room.Classexists(newName)) {
         * room.deleteClass(newName); } else { room.insertClass(newName); }
         * ClassDAO.closeLog();
         * 
         * // SUBJECT SubjectDAO sub = new SubjectDAO(); String subname = "Matric-I";
         * int subID; if ((subID = sub.getClassIdBySubject(subname)) == -1) {
         * System.out.println("Error -1"); } else { System.out.println("ID of " +
         * subname + " is " + subID); }
         * 
         * String newSubName = "9th"; if (sub.subjectExists(newSubName)) {
         * sub.deleteSubject(name, newSubName); } else { sub.insertSubject(name,
         * newSubName); }
         * 
         * SubjectDAO.closeLog();
         */

        // TEST#1 COMPLETE - SUCCESS
        /*
         * StudentDAO student = new StudentDAO();
         * System.out.println("-----FOLLOWING PRINTS STUDENTS-----");
         * student.listStudent(); TeacherDAO teacher = new TeacherDAO();
         * System.out.println("-----FOLLOWING PRINTS TEACHERS-----");
         * teacher.listTeacher();
         * System.out.println("-----FOLLOWING PRINTS Class-----"); ClassDAO room = new
         * ClassDAO(); room.listClass();
         * System.out.println("-----FOLLOWING PRINTS Subjects-----"); SubjectDAO sub =
         * new SubjectDAO(); sub.listSubjects();
         */

    }
}