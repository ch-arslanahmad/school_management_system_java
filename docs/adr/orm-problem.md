# Object Relational Mapping Problem

Relational databases are tabular.
They work in terms of
- rows and columns
- foreign keys
- repetable data

It is optimized for set-based operations like indexes and set of rows/columns

Java is hierarchical & follows OOP.

For example, in SQL,

```sql
SELECT c.id, c.name, s.id, s.name
FROM class c
JOIN student s ON c.id = s.class_id;
```

This returns,

|ClassID|ClassName|StudentID|StudentName|
|--|--|--|--|
|1|Intermediate(Year-1)|1|Arslan|
|1|Intermediate(Year-1)|2|Jim|
|1|Intermediate(Year-1)|3|Ahmad|
|2|Intermediate(Year-2)|4|John|

In here, we have two tables, `class` and `student`, and we are joining them to get the class and student.

The `ClassID` & `ClassName` are repeated for each student in the same class, even though they are the same for all students in that class. This is an example of repetable data.

In Java, it would turn into,

If we use a simple approach, we would have a `Class` object that contains a list of `Student` objects. This would lead to a lot of redundant data and would not be efficient.

This is the problem of Object Relational Mapping (ORM) where we need to map the relational data to objects in a way that is efficient and does not lead to redundant data.


We solve this problem by using ORMs (Object Relational Mappers) which provide a way to map the relational data to objects in a way that is efficient and does not lead to redundant data.

## ORM Problem Solutions

Common Solutions to the ORM Problem (In General)

1. Manual Mapping (Data Mapper Pattern)
2. Active Record Pattern
3. Full ORM Frameworks
4. Lightweight SQL Mappers

### 3. ORMs Built-in Java

Frameworks like:
- Hibernate
- JPA
- Spring Data JPA


They exist because doing this manually becomes painful as your model grows.

Like mapping over 100 tables and their relationships manually would be a nightmare.