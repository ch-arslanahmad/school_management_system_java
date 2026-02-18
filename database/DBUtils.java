package database;

import java.util.function.Function;
import java.sql.*;

class DatabaseUtils {

    // this function takes a lambda that accepts a Connection and returns a result
    // of type T. It manages the transaction, committing if successful and rolling
    // back if an exception occurs.
    <T> T runInTransaction(Function<Connection, T> action) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                T result = action.apply(conn); // execute the lambda with the connection
                conn.commit();
                return result;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

}