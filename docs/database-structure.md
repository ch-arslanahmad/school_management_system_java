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
| SchoolID | INTEGER | PRIMARY KEY AUTOINCREMENT |
| SchoolName | TEXT | NOT NULL |
| Address | TEXT | |
| Phone | TEXT | |
| Email | TEXT | |

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
| ClassID | INTEGER | FOREIGN KEY → Class(ClassID) |

---

### 4. Subjects
| Column | Type | Constraints |
|--------|------|-------------|
| SubjectID | INTEGER | PRIMARY KEY AUTOINCREMENT |
| SubjectName | TEXT | NOT NULL |
| ClassID | INTEGER | FOREIGN KEY → Class(ClassID) |
| Marks | INTEGER | Default: 100 |

---

### 5. Teacher
| Column | Type | Constraints |
|--------|------|-------------|
| TeacherID | INTEGER | PRIMARY KEY AUTOINCREMENT |
| TeacherName | TEXT | NOT NULL |
| SubjectID | INTEGER | FOREIGN KEY → Subjects(SubjectID) |
| Salary | INTEGER | |

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
  JOIN Class c ON s.ClassID = c.ClassID
```

---

## Triggers

### CheckStudentClass
Ensures that when inserting grades, the student's class matches the subject's class.

```sql
CREATE TRIGGER CheckStudentClass
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

### CheckObtainedMarks
Ensures that obtained marks cannot exceed the total marks for a subject.

```sql
CREATE TRIGGER CheckObtainedMarks
BEFORE INSERT ON StudentMarks
FOR EACH ROW
BEGIN
    SELECT
        CASE
            WHEN NEW.ObtainedMarks > (SELECT Marks FROM Subjects WHERE SubjectID = NEW.SubjectID)
            THEN RAISE(ABORT, 'Obtained marks cannot be greater than total marks of the subject.')
        END;
END;
```

---

## Entity Relationship Diagram

```
School
  ↑
  │ (1:M)
  ↓
Class ←───────────── Student (M:1)
  ↑                        │
  │ (1:M)                   │ (M:1)
  ↓                         ↓
Subjects ←───── Teacher    Class (1:M)
  ↑                │
  │ (M:1)          │ (M:1)
  ↓                ↓
StudentMarks ←───────────── Student
  (M:1)                  (1:M)
  ↓
Subjects
```

---

## Notes

- **Naming:** The table is named `StudentMarks` as it stores numeric marks (e.g., 85, 92). The actual letter grade (A, B, C, D, F) is calculated in the `getGrades` VIEW.
- All foreign keys use `ON DELETE CASCADE` where appropriate to maintain referential integrity.
