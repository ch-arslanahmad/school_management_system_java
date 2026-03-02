# School Management System

**Last updated:** 2026-03-01 — files changed: `database/DAO/ClassDAO.java`, `database/DAO/StudentDAO.java`, `database/DBmaker.java`, `database/DBValidator.java`

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

## Recent Critical Fixes (applied)

- Added `StudentID` to the `getGrades` view in `database/DBmaker.java` so view queries can filter by student id. (file: `database/DBmaker.java`)
- Fixed a `PreparedStatement` misuse in `database/DAO/ClassDAO.java` (set parameters before `executeQuery`). (file: `database/DAO/ClassDAO.java`)
- Replaced a MySQL-style upsert with SQLite-compatible `ON CONFLICT(StudentID, SubjectID) DO UPDATE` in `database/DAO/StudentDAO.java`. (file: `database/DAO/StudentDAO.java`)
- Corrected a validation bug in `database/DAO/StudentDAO.java` (removed null-check against primitive `totalMarks`). (file: `database/DAO/StudentDAO.java`)

These changes were limited to critical fixes that would otherwise prevent correct behavior. See the listed files above for the exact code edits and rationale.

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

### CheckStudentClass Trigger
Ensures student's class matches subject's class before inserting marks.

---

## Suggestions

- Add a `DAO` interface that makes a role-model contract so that each `DAO` class becomes consistent and ease.


## When Updating This File

If the design, priorities, or codebase structure changes, update this file accordingly.
