package co.edu.unicauca.liswiit2g9.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private final String url;
    private Connection connection;

    public DatabaseConnection() {
        this("jdbc:sqlite:users.db");
    }

    public DatabaseConnection(String url) {
        this.url = url;
    }

    public Connection connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url);
        }

        return connection;
    }
}