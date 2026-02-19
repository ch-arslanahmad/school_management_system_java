# DAO (Data Object Access) Layer

The DAO (Data Object Access) layer is responsible for interacting with the database and performing CRUD (Create, Read, Update, Delete) operations on the data. It abstracts the underlying database interactions and provides a clean interface for the rest of the application to use.

In this layer, you generally have classes that correspond to the entities in your application (e.g., StudentDAO, TeacherDAO, SubjectDAO, ClassDAO). Each of these classes contains methods for performing operations on the corresponding database tables.

- Each Entity has its own DAO class with its CRUD Operations.


When working with JDBC, the DAO layer typically uses PreparedStatements to execute SQL queries and updates. This helps prevent SQL injection attacks and allows for more efficient query execution.
- `executeUpdate()` statements that modify the database (like `INSERT`, `UPDATE`, `DELETE`). It returns the `int` of rows affected by the operation. 
- `executeQuery()` statements that retrieve data from the database (like `SELECT`). It returns a ResultSet object that contains the data retrieved by the query.

