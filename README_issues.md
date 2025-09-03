# School Management System Java — Issues & Solutions

## Summary of Problems

1. **Menu Navigation**
    - Main menu only runs once; users must restart for each action.
    - **Location:** `Main.java`, inside `public static void main(String[] args)` — after initial setup, only one switch statement is used for the main menu, so the program exits after one operation.
    - **Example:**
       ```java
       // now show main menu
       call.mainMenu();
       switch (input.getIntInput()) {
             // ...cases...
       }
       // After one case, program ends
       ```
    - **Solution:** Wrap the main menu and switch in a loop so users can perform multiple actions before exiting.

2. **Input Validation**
    - Inconsistent and sometimes missing; program may crash on bad input.
    - **Location:** `Main.java`, direct calls to `input.getIntInput()` in menu handlers and `validateShowInput` method.
    - **Example:**
       ```java
       int choice = input.getIntInput(); // no try-catch in some places
       // ...
       choice = validateShowInput(3, input); // better, but not always used
       ```
    - **Solution:** Always use a validation loop or method for user input, and handle exceptions gracefully.

3. **Error & Success Messages**
    - Messages are unclear or missing; users may not know if an operation succeeded.
    - **Location:** `Main.java`, in menu handler methods like `handleClassMenu`, `handleSubjectMenu`, etc. Also in CRUD methods for each entity.
    - **Example:**
       ```java
       if (!inputClass(room, input)) {
             System.out.println("Error inserting Class");
             return;
       }
       // Success message missing
       ```
    - **Solution:** Print clear messages for both success and failure, and always confirm successful operations.

4. **Redundant Code**
    - Many handlers are copy-pasted with minor changes.
    - **Location:** `Main.java`, methods like `handleClassMenu`, `handleSubjectMenu`, `handleTeacherMenu`, `handleStudentMenu` — all have similar switch-case logic.
    - **Example:**
       ```java
       void handleClassMenu(ClassDAO room, ...) { /* ...switch-case... */ }
       void handleSubjectMenu(SubjectDAO subject, ...) { /* ...switch-case... */ }
       // ...etc
       ```
    - **Solution:** Refactor using generic helper functions or interfaces to reduce duplication.

5. **Resource Management**
    - Risk of resource leaks due to improper closing of files/PDFs.
    - **Location:** `Main.java`, methods that create and use `PdfDisplay` objects, e.g., `stuPrintPDF`, `ManageDisplayStu`, `showClasses`, `showSubjects`, `showTeachers`, `showStudents`.
    - **Example:**
       ```java
       PdfDisplay pdf = new PdfDisplay();
       // ...
       pdf.closeDoc(); // sometimes missing or not in all code paths
       ```
    - **Solution:** Use try-with-resources or always ensure resources are closed in every code path.

6. **Hardcoded Strings**
    - Menu options and prompts are hardcoded throughout.
    - **Location:** `Main.java`, all menu and prompt print statements, e.g., in `mainMenu`, `showMenu`, handler methods, and CRUD methods.
    - **Example:**
       ```java
       System.out.print("Enter your ClassName: ");
       System.out.println("1. Console\n2. PDF\n");
       // ...etc
       ```
    - **Solution:** Use constants or resource bundles for strings.

7. **Menu Option Numbering**
    - Inconsistent numbering and exit options.
    - **Location:** `Main.java`, in all menu display methods and submenus, e.g., `showMenu`, `showboolMenu`, and handler methods.
    - **Example:**
       ```java
       System.out.print("Enter your choice (1-" + options.length + ", 0 for exit): ");
       // Sometimes exit option not handled in switch-case
       ```
    - **Solution:** Always show and handle exit options clearly.

8. **Typos & Copy-Paste Errors**
    - Duplicate lines and partial statements, especially in main method.
    - **Location:** `Main.java`, especially in `main` and menu handler methods. Look for stray lines and duplicate statements.
    - **Example:**
       ```java
       // if DB structure doeI.txt";s not exist return
       // ...typos and stray comments...
       ```
    - **Solution:** Review and clean up code, remove stray lines.

9. **Code Structure & Readability**
    - Large methods and unclear separation of concerns.
    - **Location:** `Main.java`, methods like `main`, `handleClassMenu`, `handleSubjectMenu`, etc. — some methods are long and do too much.
    - **Example:**
       ```java
       void handleClassMenu(...) { /* many responsibilities in one method */ }
       // ...
       ```
    - **Solution:** Break large methods into smaller functions and add comments.

---

## How to Fix
- Refactor for clarity and maintainability.
- Centralize input validation and error handling.
- Remove duplicate and stray lines.
- Use clear, consistent prompts and messages.
- Test each menu and handler for correct flow.
- Add comments and break up large methods.

---

**Use this checklist to guide your rewrite and improve your code quality.**
