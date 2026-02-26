================================================================================
                    SCHOOL MANAGEMENT SYSTEM - TASK LIST
                         Full Code Revamp Plan
================================================================================

================================================================================
PHASE 1: CLEANUP (Delete Useless Files)
================================================================================

DELETE:
  - school/SchoolData.java  →  NEVER USED, redundant, confusing


================================================================================
PHASE 2: DATABASE - NEW TABLE
================================================================================

Run SQL:

```sql
  // StudentMarks table (better naming - stores numeric marks, not letter grades)
  CREATE TABLE IF NOT EXISTS "StudentMarks" (
      "StudentMarkID" INTEGER PRIMARY KEY AUTOINCREMENT,
      "StudentID" INTEGER,
      "SubjectID" INTEGER,
      "ObtainedMarks" INTEGER,
      FOREIGN KEY("StudentID") REFERENCES "Student"("StudentID") ON DELETE CASCADE,
      FOREIGN KEY("SubjectID") REFERENCES "Subjects"("SubjectID") ON DELETE CASCADE,
      UNIQUE("StudentID", "SubjectID")
  );

  // VIEW for student report (calculates percentage and letter grade from marks)
  CREATE VIEW IF NOT EXISTS getGrades AS
  SELECT
    s.StudentName,
    sub.SubjectName,
    sub.Marks,
    g.ObtainedMarks,
    ((g.ObtainedMarks * 100) / sub.Marks) || '%' AS Percentage,
    CASE
      WHEN ((g.ObtainedMarks * 100) / sub.Marks) >= 90 THEN 'A'
      WHEN ((g.ObtainedMarks * 100) / sub.Marks) >= 80 THEN 'B'
      WHEN ((g.ObtainedMarks * 100) / sub.Marks) >= 60 THEN 'C'
      WHEN ((g.ObtainedMarks * 100) / sub.Marks) >= 50 THEN 'D'
      ELSE 'F'
    END AS Grade,
    c.ClassName
  FROM StudentMarks g
    JOIN Student s ON s.StudentID = g.StudentID
    JOIN Subjects sub ON sub.SubjectID = g.SubjectID
    JOIN Class c ON s.ClassID = c.ClassID;

  // Trigger to ensure student and subject are in the same class
  CREATE TRIGGER IF NOT EXISTS CheckStudentClass
  BEFORE INSERT ON StudentMarks
  FOR EACH ROW
  BEGIN
      SELECT
          CASE
              WHEN (SELECT ClassID FROM Student WHERE StudentID = NEW.StudentID)
                 != (SELECT ClassID FROM Subjects WHERE SubjectID = NEW.SubjectID)
              THEN RAISE(ABORT, 'MISMATCH: Subject is not in the Student Class.')
          END;
  END;
```

================================================================================
PHASE 3: DAO LAYER - NEW METHODS
================================================================================

┌─────────────────────────────────────────────────────────────────────────────┐
│ STUDENTDAO                                                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│ REPLACE broken methods with:                                                │
│                                                                             │
│   ✓ fetchStudentWithMarks(conn, studentName)                                │
│   → Returns Student object with:                                            │
│      - ID, name, class                                                      │
│      - List of Subjects (with obtainedMarks, percentage, grade)             │
│                                                                             │
│   ✓ listByClass(conn, className)                                            │
│   → Returns List<Student> students in a specific class                      │
│                                                                             │
│   ✓ insertOrUpdateMarks(studentId, subjectId, obtainedMarks)                │
│   → Saves/updates marks for a student in a subject                          │
│                                                                             │
│   ✓ deleteMarks(studentId, subjectId)                                       │
│   → Removes marks for a student                                             │
└─────────────────────────────────────────────────────────────────────────────┘

 METHODS NEEDED FOR STUDENT REPORT (already called by display layer):
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │ fetchStudentReport(conn, studentName)                                       │
 │   → Uses existing VIEW: getGrades                                           │
 │   → Query: SELECT * FROM getGrades WHERE StudentName = ?                    │
 │   → Returns: List<Subjects> with marks, percentage, grade                   │
 └─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│ TEACHERDAO                                                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│   ✓ listWithSubjects(conn)                                                  │
│   → Returns List<Teacher> with subject names (JOIN query)                   │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│ SUBJECTDAO (..DO THIS)                                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│   ✓listSubjects(conn, className)                                             │
│   → Returns List<Subjects> for a specific class                             │
└─────────────────────────────────────────────────────────────────────────────┘


================================================================================
PHASE 4: ACTIONS LAYER - NEW METHODS
================================================================================

NEW METHODS TO ADD:

  1. showStudentsInClass(ClassDAO, StudentDAO, Input, ConsoleDisplay)
     → Ask for className → display students in that class

  2. showSubjectsOfClass(SubjectDAO, ClassDAO, Input, ConsoleDisplay)
     → Ask for className → display subjects for that class

  3. showTeachersWithSubjects(TeacherDAO, SubjectDAO, Input, ConsoleDisplay)
     → Display all teachers with their subject names

  4. showClassFees(ClassDAO, Input, ConsoleDisplay)
     → Ask for className → display fees for that class

  5. addStudentMarks(StudentDAO, SubjectDAO, ClassDAO, Input)
     → Ask for student name → show subjects → input marks for each

  6. showAllStudentGrades(StudentDAO, SubjectDAO, Input, ConsoleDisplay)
     → Display all students with their total marks and grades

  7. showStudentGrade(StudentDAO, SubjectDAO, Input, ConsoleDisplay)
     → Ask for student name → display individual report card

FIX EXISTING:
  - addClassObtMarks() - currently collects marks but doesn't save to DB
    (rewrite to use insertOrUpdateMarks)


================================================================================
PHASE 5: DISPLAY LAYER - NEW METHODS
================================================================================

NEW METHODS TO ADD (ConsoleDisplay):

  1. displayStudentsInClass(List<Student> students)
     → Format: | StudentName | StudentID |

  2. displaySubjectsOfClass(List<Subjects> subjects)
     → Format: | SubjectName | TotalMarks |

  3. displayTeachersWithSubjects(List<Teacher> teachers)
     → Format: | TeacherName | SubjectName |

  4. displayClassFees(ClassRoom class)
     → Format: Tuition: X, Stationary: Y, Paper: Z, Total: XYZ

  5. displayStudentWithGrades(Student student, List<Subjects> marks)
     → Full report card format with table

  6. displayAllStudentGrades(List<Student> students)
     → Format: | StudentName | ClassName | TotalMarks | Percentage | Grade |


================================================================================
PHASE 6: CLEANUP - ACTIONS.JAVA
================================================================================

FIX:
  - Remove unreachable return false after lambda blocks
  - Remove unused instance field 'ClassDAO room' (line 237)
  - Fix ConsoleDisplay.displaySchoolInfo() parameter type
  - Fix School object not used (line 36 in addSchoolInfo)


================================================================================
PHASE 7: JAVADOC DOCUMENTATION
================================================================================

WHY JAVADOC:
  - Generates API documentation automatically
  - Makes code self-documenting
  - IDE support (tooltip hints, autocomplete)
  - Standard practice for Java projects
  - Helps future developers understand code quickly

ADD TO:
  - All public methods in DAO classes
  - All public methods in Actions.java
  - All public methods in Display classes
  - Model classes (Student, Teacher, ClassRoom, Subjects, School)

EXAMPLE:
  /**
   * Retrieves all students enrolled in a specific class.
   *
   * @param conn the database connection
   * @param className the name of the class to filter by
   * @return list of students in the specified class, empty list if none found
   * @throws SQLException if a database error occurs
   */
  public List<Student> listByClass(Connection conn, String className)


================================================================================
SUMMARY - MENU STRUCTURE
================================================================================

After implementation, Reports Menu should have:

  1. School Info
  2. All Classes
  3. Classes with Fees
  4. Subjects of a Class
  5. Students in a Class
  6. Teachers with Subjects
  7. All Students with Grades
  8. Individual Student Report
  9. Fee Receipt


================================================================================
