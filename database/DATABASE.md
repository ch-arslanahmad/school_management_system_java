# database
Testing Database SQL-Lite with java to build school management system project


### Finalizing (Current-Version):
- ClassDAO (uncompleted)



<h2 align= center>Disable auto commit in JAVA<h2>

##### What is Auto commit?
It is set to true by default, which allows it to after executing the query, change the database without any validation, which in case may cause error and leave the database connection loose, which can cause database locks in SQLite or other problems like incorrect/incomplete/corrupted data inserted.
It is generally recommended that you set the autocommit to false, that makes the execution temporary.
- if during validation or error handling, an error occurs, you can simply rollback to the previous state.
- If no problems occur, the autocommit will change it to true and hence data will be successfully passed & made permanent in the database without any problems.
Hence it is recommended to:

```
conn.setAutoCommit(false);
try {
} catch(Exception e) {
// if something goes wrong you can simply do
conn.rollback();
}
conn.setAutoCommit(true);
```


# my 2 thinking
- MAKE A SEPERATE FILE FOR DB Queries
- think about getting grades along with marks









**CREATING VIEW**

Another thing that i came across while dealing with DB is
- View
- Triggers
I chose View for my DB at last.
Update: Now have to use trigger too.
**View:**
View is like a set of instructions stored in the database and you can use it to view the result of the query, hence the name, it is best for read only. View is like a query for a shortcut.
*REASON for its USE:*
I need a view soly (for now) simply for the reason of getting a grade table which has percentage, character grade with total and obtained marks.

**Triggers:**
Trigger is a stored formula in a column that will automatically change/execute based on a formula.
If you are familiar with excel, it is like in Excel, where formulas are stored in a cell. This is the same with the difference being the scale to a whole column rather than a single cell.

*REASON for its USE:*
Making a trigger:
- checking class consistency, as only adding marks of a subject of a student that his class has.
- Checking if the obtained marks given are not larger than the total marks.

