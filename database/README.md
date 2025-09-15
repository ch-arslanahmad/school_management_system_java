# DATABSE - SQLite

## Basic Info

SQLite is a relational database management system. It operates via SQL language which is in its name.

This mechanism/application/software used for this software is SQLite, hence it is essential to have a base understanding of SQLite.

## SQLite Data Type Affinity

| Affinity/ Datatype | Preferred data | Example | Notes Behavior                                                         |
| --------------- | ----------------- | ------------- | ----------------------------------------------------------------------------------- |
| **TEXT**        | Strings           | 'Hello'       | Values are converted to text if possible.                                           |
| **NUMERIC**     | Numbers           | 42, 3.14      | Stores as INTEGER or REAL if possible; also supports boolean (0/1) or date strings. |
| **INTEGER**     | Whole numbers     | 7             | Tries to convert to integer; stores as integer if possible.                         |
| **REAL**        | Floating point    | 3.14          | Stores float; converts integers to float if needed.                       |
| **BLOB**        | Raw data          | X'010203'     | Stored exactly as-is; no conversion occurs.                                         |
| **NULL**        | Missing data      | NULL          | Represents absence of value.                                                        |

> [!NOTE]
> SQLite uses **type affinity**, not strict data types. You can store a value of any type in any column; SQLite will attempt to convert it to the column's affinity.

If you are familiar with normal SQL, you can easily use it:

- It may just be converted to the SQLite types or affinities that i have explained, as _SQLite is lazy on data types_ (**unlike MySQL or PostgreSQL**).

## What I have learned

- Database Connection & Understanding
- SQL (CRUD Queries, Joins, View, Trigger)

### Database & Connections

- To work with SQLite in Java, you need its [jar file](/lib/sqlite-jdbc-3.50.3.0.jar).
- Then after you have a jar file, to do an operation, you need a database connection.

So that's how it works:

- A connection is made between JDBC & Java.
- Java passes the queries/commands to JDBC which gets executed.
- However, you manually need to open and close connections.

To solve this, I made a separate file for Database connections i.e., `Database.java`, and made a static method, `getConnection()` for opening a connection.

- Afterwards rather than creating a separate closing method I put the calling of the method in **try-catch with resources** which automatically closes resources.

Sometimes, you run one query or multiple which causes unexpected or incomplete action, which can cause data corruption, hence why a `rollback()` method/feature is necessary, think of it like an undo button if something goes wrong.

#### Disable auto commit

**What is Auto commit?**

By default, `setAutoCommit()`,is set to `true` by default, which allows it to after executing the query, change the database without any validation, without any rollback(), an undo, which in this case may cause error and leave the database connection loose, which can cause database locks in SQLite or other problems like incorrect/incomplete/corrupted data inserted.
Hence, you should set the autocommit to `false`, that makes the changes temporary until `commit()`.

- It is used when you are changing data only.
- if during validation or error handling, an error occurs, you can simply rollback to the previous state.
- It is only applicable to all CRUD operations aside from read.
Hence it is recommended to:

```java
conn.setAutoCommit(false);
try {
} catch(Exception e) {
// if something goes wrong you can simply do
conn.rollback();
}
conn.setAutoCommit(true);
```

I have put this in the main database connection method `getConnection()`, in the `Database.java` file so every connection made must have autocommit set to `false`.

### SQL Learnings

I learned about basic SQL:

- Relational Databases
- Create, Read, Update, Delete (CRUD)
- Tables, Columns, Rows
- Views & Triggers

Before you learn SQL, and use any of its management servers or apps, you need to know some very basics:

First, what **relational databases** are?

So, basically, every data has to be in a cell of a table, so these tables make up the database. For these tables to communicate with each other there can be relations which are possible via foreign keys which have to be unique, which allow relations & primary keys which differentiate each row in a table.

Next up what is:

#### Primary Key

A key that makes a row unique, like how your national ID number is, but In a table.
Foreign Key:

#### Foreign KEY

A row that links or references to some foreign row which has a unique ID, mostly a primary key but can a unique key simply.

It's like your passport which allows you to visit another country, in this case another table.

So for a foreign relation, you need a key of row of the table and the one key for the one you are referencing, that reference is the foreign key.

In this analogy, country names are primary keys, as names are globally unique.

#### CRUD

I used queries to view, add, remove, update, and delete, values, columns rows or tables.

##### **View & Trigger**

Another thing that i came across while dealing with DB is

- View
- Triggers

##### **View**

View is like a set of instructions stored in the database and you can use it to view the result of the query, hence the name, it is best for read only. View is like a query for a shortcut.

_**REASON for its USE**_

I need a view solely (for now) for the reason of getting a grade table which has subjects, percentages, character grades with total and obtained marks.

##### **Triggers:**

Trigger is a stored formula in a column that will automatically change/execute based on a formula.
If you are familiar with excel, it is like in Excel, where formulas are stored in a cell. This is the same with the difference being the scale to a whole column rather than a single cell.

_**REASON for its USE:**_

Making a trigger:

- checking class consistency, as only adding marks of a subject of a student that his class has.
- Checking if the obtained marks given are not larger than the total marks.
