# School Management System

## IMPORTANT - Read Before Editing Code

This file contains design decisions, known issues, and context about this codebase. **Before making any changes**, read this file to understand the current state and priorities.

---

## Project Overview

**Type**: Java + SQLite desktop application for school management
**Database**: `storage/people.db` (SQLite)

---

## Project Structure

```
Main.java
├── database/
│   ├── DAO/
│   │   ├── StudentDAO.java    # CRUD for Student + marks
│   │   ├── TeacherDAO.java    # CRUD for Teacher
│   │   ├── SubjectDAO.java    # CRUD for Subjects
│   │   ├── ClassDAO.java      # CRUD for Class (ClassRoom)
│   │   └── SchoolDAO.java     # CRUD for School
│   ├── Database.java          # Connection management
│   ├── DBmaker.java           # DB creation + views/triggers
│   ├── DBUtils.java           # Transaction helper
│   ├── DBValidator.java      # Input validation
│   └── editDB.java, testing.java
├── display/
│   ├── ConsoleDisplay.java    # Console UI
│   ├── MenuHandler.java       # Menu navigation
│   ├── PdfDisplay.java        # PDF export
│   ├── Display.java           # Interface
│   ├── Input.java             # Input handling
│   └── LogHandler.java        # Logging setup
├── people/
│   ├── Student.java
│   ├── Teacher.java
│   ├── Admin.java
│   └── Person.java            # Base class
├── classroom/
│   ├── ClassRoom.java         # Class entity
│   └── Subjects.java          # Subject entity (with marks for display)
├── school/
│   ├── School.java
│   └── Actions.java           # Business logic
└── storage/
    └── people.db              # SQLite database
```

---

## DAO Layer

All DAO classes use:
- **PreparedStatements** for SQL injection prevention
- **DBUtils.runInTransaction()** for transactional operations
- **Logging** via `LogHandler`

### Pattern
```java
public boolean insertSomething(Entity entity) {
    return DBUtils.runInTransaction(conn -> {
        // SQL operations
    });
}
```

---

## Database Design - FIXED

### Subjects Table (After Fix)
```sql
CREATE TABLE "Subjects" (
    "SubjectID" INTEGER,
    "SubjectName" TEXT,
    "ClassID" INTEGER,
    -- Marks column REMOVED (marks only belong in StudentMarks)
    PRIMARY KEY("SubjectID" AUTOINCREMENT),
    FOREIGN KEY("ClassID") REFERENCES "Class"("ClassID") ON DELETE CASCADE
);
```

### StudentMarks Table (Correct Design)
```sql
CREATE TABLE "StudentMarks" (
    "StudentMarkID" INTEGER PRIMARY KEY AUTOINCREMENT,
    "StudentID" INTEGER REFERENCES "Student"("StudentID"),
    "SubjectID" INTEGER REFERENCES "Subjects"("SubjectID"),
    "ObtainedMarks" INTEGER,
    UNIQUE(StudentID, SubjectID)
);
```

---

## Java Design - Denormalized Model

The Java classes use a **denormalized model** for convenience - this is **by design**, not a bug.

### Student Class
```java
class Student {
    ClassRoom room;
    List<Subjects> subjects = new ArrayList<>();  // Each student has their own subjects with marks
}
```

### Subjects Class (Java Only)
```java
class Subjects {
    private Integer subjectID;
    private String subjectName;
    private Integer classID;
    private String className;
    private int totalMarks = 100;        // Fixed at 100
    private Integer obtainedMarks;       // Per student
    private Double percentage;
    private String grade;
}
```

### ClassRoom Class
```java
class ClassRoom {
    private Integer classID;
    private String className;
    private Integer tuitionFee;
    private Integer stationaryFee;
    private Integer paperFee;
}
```

**Key distinction**:
- **DB**: Normalized (Subjects table has no marks)
- **Java**: Denormalized for display (Subjects objects carry marks per student)

---

## Views & Triggers

### getGrades View
```sql
CREATE VIEW getGrades AS
SELECT 
  s.StudentName,
  sub.SubjectName,
  100 AS TotalMarks,
  g.ObtainedMarks,
  ((g.ObtainedMarks * 100) / 100) || '%' AS Percentage,
  CASE
    WHEN ((g.ObtainedMarks * 100) / 100) >= 90 THEN 'A'
    WHEN ((g.ObtainedMarks * 100) / 100) >= 80 THEN 'B'
    WHEN ((g.ObtainedMarks * 100) / 100) >= 60 THEN 'C'
    WHEN ((g.ObtainedMarks * 100) / 100) >= 50 THEN 'D'
    ELSE 'F'
  END AS Grade,
  c.ClassName
FROM StudentMarks g
  JOIN Student s ON s.StudentID = g.StudentID
  JOIN Subjects sub ON sub.SubjectID = g.SubjectID
  JOIN Class c ON s.ClassID = c.ClassID;
```

### CheckStudentClass Trigger
Ensures student's class matches subject's class before inserting marks.

---



## When Updating This File

If the design, priorities, or codebase structure changes, update this file accordingly.
