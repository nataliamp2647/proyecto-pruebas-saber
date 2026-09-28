package co.edu.unicauca.liswiit2g9.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import co.edu.unicauca.liswiit2g9.database.DatabaseConnection;
import co.edu.unicauca.liswiit2g9.domain.Role;
import co.edu.unicauca.liswiit2g9.domain.User;
import co.edu.unicauca.liswiit2g9.domain.UserStatus;

public class SQLiteUserRepository implements UserRepository {

    private final DatabaseConnection databaseConnection;

    public SQLiteUserRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        createTable();
    }

    private void createTable() {

        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    full_name TEXT NOT NULL,
                    role TEXT NOT NULL,
                    status TEXT NOT NULL,
                    password TEXT NOT NULL
                )
                """;

        try {
            Connection connection = databaseConnection.connect();

            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al crear la tabla users", e
            );
        }
    }

    @Override
    public void save(User user) {

        String sql = """
                INSERT INTO users
                (username, full_name, role, status, password)
                VALUES (?, ?, ?, ?, ?)
                """;

        try {
            Connection connection = databaseConnection.connect();

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setString(1, user.getUsername());
                statement.setString(2, user.getFullName());
                statement.setString(3, user.getRole().name());
                statement.setString(4, user.getStatus().name());
                statement.setString(5, user.getPassword());

                statement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al guardar el usuario", e
            );
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {

        String sql = """
                SELECT id, username, full_name, role, status, password
                FROM users
                WHERE username = ?
                """;

        try {
            Connection connection = databaseConnection.connect();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, username);

                try (ResultSet resultSet = statement.executeQuery()) {

                    if (resultSet.next()) {
                        User user = mapUser(resultSet);
                        return Optional.of(user);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al buscar el usuario", e
            );
        }

        return Optional.empty();
    }

    @Override
    public List<User> findAll() {

        String sql = """
                SELECT id, username, full_name, role, status, password
                FROM users
                """;

        List<User> users = new ArrayList<>();

        try {
            Connection connection = databaseConnection.connect();

            try (PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    users.add(mapUser(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al obtener los usuarios", e
            );
        }

        return users;
    }

    private User mapUser(ResultSet resultSet) throws SQLException {

        return new User(
                resultSet.getInt("id"),
                resultSet.getString("username"),
                resultSet.getString("full_name"),
                Role.valueOf(resultSet.getString("role")),
                UserStatus.valueOf(resultSet.getString("status")),
                resultSet.getString("password")
        );
    }
}