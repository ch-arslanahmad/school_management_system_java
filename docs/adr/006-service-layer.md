# ADR 006: Service Layer Architecture

**Status:** Accepted

**Date:** 2026-04-29

---

## Context

The system needs a clean way to handle transactions and data access:

- Multiple DAO operations need to run in a single transaction
- Keep presentation, service, and data layers separate
- Avoid nested transactions that break composability
- Make business logic testable

---

## Decision: Three-Layer Pattern

### How it works

```
Presentation (MenuHandler, Actions)
         │
         ▼ calls Service methods
Service Layer (wraps with runInTransaction)
         │
         ▼ passes Connection to
DAO Layer (uses the Connection, no transaction wrapping)
```

**Rule:** Service creates the connection, DAO receives it.

---

## How to Write Each Type

### READ operations

**DAO:**
```java
public List<Student> listStudents(Connection conn) {
    List<Student> students = new ArrayList<>();
    String sql = "SELECT * FROM Student";
    
    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            // populate objects
        }
    } catch (SQLException e) {
        logger.log(Level.WARNING, "Error fetching students", e);
    }
    return students;
}
```

**Service:**
```java
public static List<Student> getStudents() {
    return DBUtils.runInTransaction(conn -> 
        new StudentDAO().listStudents(conn)
    );
}
```

READ operations don't strictly need transaction wrapping, but keeping the same pattern everywhere makes the code predictable.

---

### WRITE operations (INSERT, UPDATE, DELETE)

**DAO:**
```java
public boolean insertStudent(Connection conn, Student student) {
    if (studentExists(conn, student.getName())) {
        logger.warning("Student already exists");
        return false;
    }
    
    String sql = "INSERT INTO Student (StudentName, ClassID) VALUES (?, ?)";
    try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, student.getName());
        ps.setInt(2, student.getClassRoom().getID());
        
        int rows = ps.executeUpdate();
        
        ResultSet keys = ps.getGeneratedKeys();
        if (keys.next()) {
            student.setID(keys.getInt(1));
        }
        
        return rows > 0;
    } catch (SQLException e) {
        logger.log(Level.WARNING, "Error inserting student", e);
        return false;
    }
}
```

**Service:**
```java
public static boolean addStudent(Student student) {
    return DBUtils.runInTransaction(conn -> 
        new StudentDAO().insertStudent(conn, student)
    );
}
```

---

### Multiple DAOs in One Transaction

This is where the pattern really helps:

```java
public static boolean transferStudent(int studentId, int newClassId) {
    return DBUtils.runInTransaction(conn -> {
        StudentDAO studentDao = new StudentDAO();
        ClassDAO classDao = new ClassDAO();
        
        Student student = studentDao.fetchStudent(conn, studentId);
        if (student.getID() == null) return false;
        
        ClassRoom newClass = classDao.fetchClass(conn, newClassId);
        if (newClass.isEmpty()) return false;
        
        student.setClassID(newClassId);
        boolean updated = studentDao.updateStudent(conn, student);
        
        // If anything fails, everything rolls back
        return updated;
    });
}
```

Same connection, same transaction, all-or-nothing semantics.

---

## Method Signatures

### READ methods
```java
// DAO
public List<Entity> listEntities(Connection conn)
public Entity fetchEntity(Connection conn, String name)
public Entity fetchEntity(Connection conn, int id)

// Service
public static List<Entity> getEntities() {
    return DBUtils.runInTransaction(conn -> dao.listEntities(conn));
}
```

### WRITE methods
```java
// DAO - must receive Connection
public boolean insertEntity(Connection conn, Entity entity)
public boolean updateEntity(Connection conn, Entity entity)
public boolean deleteEntity(Connection conn, int id)

// Service - wraps with runInTransaction
public static boolean addEntity(Entity entity) {
    return DBUtils.runInTransaction(conn -> 
        dao.insertEntity(conn, entity)
    );
}
```

---

## Common Mistake

**Wrong:**
```java
public boolean insertStudent(Student student) {
    return DBUtils.runInTransaction(conn -> {
        // Problem: DAO wraps transaction, can't reuse in larger operation
        try (PreparedStatement ps = conn.prepareStatement(...)) {
            // code
        }
    });
}
```

This breaks because the DAO controls the transaction. You can't call it as part of a larger operation that needs multiple DAOs in one transaction.

---

## Presentation Layer

MenuHandler, Actions, and controllers should call services, never DAOs:

```java
public class Actions {
    
    public static void addNewStudent() {
        Student student = new Student();
        student.setName(Input.validateString("Enter student name"));
        student.setClassRoom(classRoom);
        
        boolean success = StudentService.addStudent(student);
        
        if (success) {
            System.out.println("Student added successfully");
        } else {
            System.out.println("Failed to add student");
        }
    }
}
```

---

## Error Handling

| Layer | Handles | What it does |
|-------|---------|--------------|
| **DAO** | SQLException | Logs it, returns false/empty |
| **Service** | Nothing | Lets transaction fail, rollback happens automatically |
| **Presentation** | Nothing | Shows user-friendly message based on returned false |

DAO knows the SQL details, so it logs them. Service doesn't hide exceptions. Presentation shows simple messages.

---

## Important Note: READ vs WRITE

**WRITE operations (INSERT, UPDATE, DELETE):**
- Service MUST pass `Connection` to DAO
- DAO MUST use that Connection (not create its own)
- This enables composable multi-DAO transactions

**READ operations:**
- Optional - DAO can either:
  - Accept `Connection` from Service (preferred for consistency), OR
  - Create its own connection internally (manual connection handling)
- The ADR 001 allows manual connection handling for READ since SELECT doesn't need rollback/commit

**Convention Note:** While this codebase allows READ operations to use their own connection, industry frameworks like Spring (Java) or Django/Flask (Python) wrap ALL database operations in transactions for consistency. By convention, RECEIVING a Connection in DAO methods (even for READ) is the preferred pattern - it keeps the code uniform and makes it easier to add write operations later without refactoring.

---

## What to Change

- All DAO WRITE methods accept `Connection conn` parameter
- All DAO WRITE methods do NOT call `DBUtils.runInTransaction()`
- All DAO methods catch SQLException
- All Service methods wrap with `DBUtils.runInTransaction()`
- All Service methods pass `conn` to DAO methods (WRITE only required, READ optional)
- Presentation calls Services, never DAOs