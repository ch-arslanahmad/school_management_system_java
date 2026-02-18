package database;

import java.sql.*;

public class DBUtils {

    /**
     * Functional interface for transactional operations.
     * 
     * <p>
     * This interface defines a single method that takes a {@link
     * java.sql.Connection} and returns a result of type {@code T}. It is used to
     * represent a block of code that should be executed within a database
     * transaction. The transaction will be committed if the operation completes
     * successfully, and rolled back if an exception occurs.
     *
     * @argument it recieves a connection and returns a result of type T
     */

    @FunctionalInterface
    public interface TransactionalOperation<T> {
        T execute(Connection connection) throws SQLException;
    }

    /**
     * Executes the given action within a transaction.
     *
     * <p>
     * This method accepts a lambda that takes a {@link java.sql.Connection}
     * and returns a result of type {@code T}. The transaction is committed
     * if the action completes successfully, and rolled back if an exception occurs.
     *
     * @throws Exception throws any exception (like SQLException)
     */

    public static <T> T runInTransaction(TransactionalOperation<T> action) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                T result = action.execute(conn); // execute the lambda with the connection
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