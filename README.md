# School Management System
![Language](https://img.shields.io/badge/language-Java-blue.svg)
![Status](https://img.shields.io/badge/status-Project-brightgreen)


A console-based application to manage basic school operations like adding, removing, updating, showing students, teachers, classes and subjects.

## Features
  - Add, update, and view classes.
  - Associate subjects with classes, manage subject details.
  - Assign teachers to subjects and classes.
  - Maintain student records, class enrollment, and academic details.
    - Create Student Report
  - **SQLite-based backend** for persistent storage.
  - Show/Export in Console or do it in PDF formats.
  - Detailed logging of system activities using Java's Logger.

## Structure

```
school_management_system_java/
└──  Main.java
├── lib
   └──text-2.1.7.jar
   └── sqlite-jdbc-3.50.3.0.jar
├── display
    └── OutputDisplay.java
├── ConsoleDisplay.java
├── PDFDisplay.java
├── testPDF.java  // temporarily test file
├── people
    ├── Person.java
    └── Admin.java
    ├── Student.java
    └── Teacher.java
├── classroom
    └── ClassRoom.java
    └── Subjects.java
├── database
    ├── Database.java
    ├── DBmaker.java
    └── DBManager.java
├── DATABASE.md
└── DB-queries.md
├── DAO
    └── ClassDAO.java
    └── SubjectDAO.java
    └── StudentDAO.java
    └── TeacherDAO.java
    └── GradeDAO.java
    ├── editDB.java
    └── testing.java

```

## Installation & Setup
- Java JDK (17 or later)
- SQLite JDBC & iText driver - **_(already installed in repo)_**
- Git

### Requirenments:
Clone the repo using the following command in Git Bash:
```
git clone https://github.com/ch-arslanahmad/school_management_system_java.git
```
Then,
```
cd school_management_system_java
ls
```
Now compile and run the program if using,
```
javac Main.java
java Main
```
> [!NOTE]
> This is a CLI based App


## Tools & Libraries Used
- **Language:** Java
- **Database:** SQLite, [Learn More here...](database/DATABASE.md)
- **IDE:** VS Code (Java)
- **Libraries:**
  - java.util.logging for logging
  - SQLite JDBC driver for database connectivity
  - PDF generation library (iText)

## Acknowledgements
This project was designed with the intention to follow the principles of:
- Separation of Concerns
- Single Responsibility Principle
It also uses:
- SQLite for lightweight database management.
- Java Logger framework for robust logging.
- PDF libraries (iText) for export functionality.
>[!Note]
> While these principles guided the structure of the project, some areas may not fully adhere to them, as this is a learning and practical implementation project.

## Usage
- Launch Main.java to start the system.
  - Use the menu options to navigate:
  - Add / Update / Delete Classes, Subjects, Teachers & Students
  - View / Export Reports
