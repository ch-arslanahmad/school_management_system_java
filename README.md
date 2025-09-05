# School Management System

![Language](https://img.shields.io/badge/language-Java-blue.svg)
![Status](https://img.shields.io/badge/status-Project-brightgreen)

A console-based application to manage basic school operations like adding, removing, updating, showing students, teachers, classes and subjects.

---

## Features

- Add, update, and view classes (with fees)
   - Fee Reciept for Student 
- Associate subjects with classes, manage subject details
- Assign teachers to subjects
- Maintain student records, and academic details (including student reports)
- **SQLite-based backend** for persistent storage
- Show/export data in the console or as PDF reports
- Detailed logging of system activities using Java's Logger

---

## Project Structure

```structure
school_management_system_java/
├── Main.java
├── build.sh
├── lib/
│   ├── itext-2.1.7.jar
│   └── sqlite-jdbc-3.50.3.0.jar
├── display/
│   ├── Input.java
│   ├── Display.java
│   ├── ...
│   └── MenuHandler.java
│   └── LogHandler.java
├── people/
│   ├── Person.java
│   ├── Admin.java
│   ├── Student.java
│   └── Teacher.java
├── classroom/
│   ├── ClassRoom.java
│   └── Subjects.java
├── database/
│   ├── Database.java
│   ├── DBmaker.java
│   ├── DBManager.java
│   ├── DATABASE.md 
(now README.md)
│   ├── ... db files
│   └── DAO/
│       ├── ClassDAO.java
│       ├── SubjectDAO.java
│       ├── StudentDAO.java
│       ├── TeacherDAO.java
│       ├── SchoolDAO.java
│       ├── editDB.java
│       └── testing.java
└── ...
```

---

## Installation & Setup

**Requirements:**

- Java JDK (17 or later)
- SQLite JDBC & iText driver (already included in `lib/`)
- Git

**Steps:**

1. Clone the repo:

   ```sh
   git clone https://github.com/ch-arslanahmad/school_management_system_java.git
   cd school_management_system_java
   ```

2. Compile and run:

   ```sh
   javac Main.java
   java Main
   ```

> [!Note]
> This is a CLI-based app.
---

## Usage

- Launch `Main.java` to start the system.
- Use the menu options to navigate:
  - Add / Update / Delete Classes, Subjects, Teachers & Students
  - View / Export Reports (console or PDF)
  - Manage school info (view/update)
- All actions are available in a loop until you choose to exit.

---

## Tools & Libraries Used

- **Language:** Java
- **Database:** SQLite ([Learn More here...](database/README.md))
- **IDE:** VS Code (Java)
- **Libraries:**
  - java.util.logging for logging
  - SQLite JDBC driver for database connectivity
  - PDF generation library (iText)

---

## Code overview — how the program is organised

This short walkthrough shows the main call flow and where to look when you want to change behavior.

### General call flow

- `Main.java` -> `MenuHandler` -> `Actions` -> `(DAOs | Display)`

### Responsibilities (brief)

- `Main.java` — program entry point. Creates the main helpers (menu, input, DB manager) and runs the menu loop.
- `display/MenuHandler.java` — shows menus, reads/validates input, and dispatches to handlers.
- `school/Actions.java` — business-level operations that glue menus to DAOs and display code.
- `display/ConsoleDisplay.java` / `display/PdfDisplay.java` — presentation logic (console formatting and PDF export).
- `database/Database.java`, `DBManager.java` — connection, initialization, and transaction helpers.
- `database/DAO/*.java` — data access objects (CRUD) for classes, students, teachers, subjects, and school info.
- `people/`, `classroom/`, `school/` — model classes (Student, Teacher, ClassRoom, Subjects, School, etc.).
- `display/LogHandler.java` — logger setup; logs are written to files under `log/`.

### General Descriptions

- Menus and User flow: `display/MenuHandler.java` and `Main.java`.
- Action/Process logic: `school/Actions.java` and the DAOs in `database/DAO/`.
- Actual Database actions/queries Logic: `database/DAO/`
- Database (Connection & Creation): `database/Database.java` and `DBManager.java`.
- School info fetching: `database/DAO/SchoolDAO.java` and callers in display classes.

---

## Acknowledgements & Notes

This project was designed with the intention to follow the principles of:

- Separation of Concerns
- Single Responsibility Principle

It also uses:

- SQLite for lightweight database management
- Java Logger framework for robust logging
- PDF libraries (iText) for export functionality

> [!NOTE]
> While these principles guided the structure of the project, some areas may not fully adhere to them, as this is a learning and practical implementation project.

---
