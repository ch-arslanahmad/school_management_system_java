# SQLite Database Structure

## Overview
This document describes the database schema for the School Management System.

**Database File:** `storage/people.db`
**Database Type:** SQLite

---

## Tables

### 1. School
| Column | Type | Constraints |
|--------|------|-------------|
| id | INTEGER | PRIMARY KEY CHECK (id = 1) |
| Name | TEXT | |
| Principal | TEXT | |
| location | TEXT | DEFAULT 'Unknown' |

**Note:** Only one row allowed (CHECK id = 1).

---

### 2. Class
| Column | Type | Constraints |
|--------|------|-------------|
| ClassID | INTEGER | PRIMARY KEY AUTOINCREMENT |
| ClassName | TEXT | NOT NULL, UNIQUE |
| Tuition_Fee | INTEGER | |
| Stationary_Fee | INTEGER | |
| Paper_Fee | INTEGER | |

---

### 3. Student
| Column | Type | Constraints |
|--------|------|-------------|
| StudentID | INTEGER | PRIMARY KEY AUTOINCREMENT |
| StudentName | TEXT | NOT NULL |
| ClassID | INTEGER | FOREIGN KEY → Class(ClassID) ON DELETE CASCADE |

---

### 4. Subjects
| Column | Type | Constraints |
|--------|------|-------------|
| SubjectID | INTEGER | PRIMARY KEY AUTOINCREMENT |
| SubjectName | TEXT | NOT NULL |
| ClassID | INTEGER | FOREIGN KEY → Class(ClassID) ON DELETE CASCADE |

**Note:** The `Marks` column was removed. Marks are stored per-student in `StudentMarks` table.

---

### 5. Teacher
| Column | Type | Constraints |
|--------|------|-------------|
| TeacherID | INTEGER | PRIMARY KEY AUTOINCREMENT |
| TeacherName | TEXT | NOT NULL |
| SubjectID | INTEGER | FOREIGN KEY → Subjects(SubjectID) ON DELETE CASCADE |

---

### 6. StudentMarks
| Column | Type | Constraints |
|--------|------|-------------|
| StudentMarkID | INTEGER | PRIMARY KEY AUTOINCREMENT |
| StudentID | INTEGER | FOREIGN KEY → Student(StudentID) ON DELETE CASCADE |
| SubjectID | INTEGER | FOREIGN KEY → Subjects(SubjectID) ON DELETE CASCADE |
| ObtainedMarks | INTEGER | |

**Constraints:**
- UNIQUE(StudentID, SubjectID) - prevents duplicate marks for same student+subject

---

## Views

### getGrades
Returns student grades with calculated percentage and letter grade.

```sql
CREATE VIEW IF NOT EXISTS getGrades AS
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

**Note:** TotalMarks is fixed at 100 (not from Subjects table).

---

## Triggers

### CheckStudentClass
Ensures that when inserting grades, the student's class matches the subject's class.

```sql
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

---

## Entity Relationship Diagram

```
School
  │
  │ (1:M)
  ▼
Class ◄─────────────────── Student
  │                           │
  │ (1:M)                     │ (M:1)
  ▼                           ▼
Subjects              ┌─────── Class
  │                   │
  │ (1:M)             │
  ▼                   │
Teacher ──────────────┘
  │
  │ (1:M)
  ▼
StudentMarks ◄────────────── Student
    │                          │
    │ (M:1)                    │ (M:1)
    ▼                          ▼
Subjects ◄────────────────── Student
```

---

## Notes

- **Naming:** The table is named `StudentMarks` as it stores numeric marks (e.g., 85, 92). The actual letter grade (A, B, C, D, F) is calculated in the `getGrades` VIEW.
- All foreign keys use `ON DELETE CASCADE` where appropriate to maintain referential integrity.
- Total marks is fixed at 100 for all subjects (hardcoded in Java and views).
