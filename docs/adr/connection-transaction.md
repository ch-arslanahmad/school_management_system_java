# 001 - Connection and Transaction Strategy

Status: Accepted

## Context

The system needs to establish a connection to the database and manage transactions effectively to ensure data integrity and performance.

## Decision

Use function, `runInTransaction`, to manage database transactions. This function will handle the connection, begin the transaction, execute the provided operations, and commit or rollback as necessary.

However only for:

- updating, creating, deleting records in the database.

Not for:

- fetching records from the database. For fetching records,

That is manual connection handling will be used, where the connection is established, the query is executed, and the connection is closed within the same method.

As, fetching uses, `SELECT` and does not require, `rollback` or `commit` operations, it is more efficient to manage connections manually for these operations.
