package co.unicauca.iso2.taller2.users.domain.access;

import co.unicauca.iso2.taller2.users.domain.Role;
import co.unicauca.iso2.taller2.users.domain.User;
import co.unicauca.iso2.taller2.users.domain.UserStatus;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementación concreta de IUserRepository sobre SQLite. Es intercambiable
 * por cualquier otra (por ejemplo InMemoryUserRepository en los tests) sin
 * tocar la capa de servicio: Open/Closed + Dependency Inversion.
 *
 * @author Mani
 */
public class UserRepository implements IUserRepository {

    private Connection conn;

    public UserRepository() {
        connect();
        initDatabase();
    }

    private void initDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS User (\n"
                + " Id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " Login TEXT NOT NULL UNIQUE,\n"
                + " FullName TEXT NOT NULL,\n"
                + " Role TEXT NOT NULL,\n"
                + " Status TEXT NOT NULL,\n"
                + " PasswordHash TEXT NOT NULL\n"
                + ");";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean save(User user) {
        if (user == null) {
            return false;
        }
        String sql = "INSERT INTO User (Login, FullName, Role, Status, PasswordHash) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getLogin());
            pstmt.setString(2, user.getFullName());
            pstmt.setString(3, user.getRole().name());
            pstmt.setString(4, user.getStatus().name());
            pstmt.setString(5, user.getPasswordHash());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean update(User user) {
        if (user == null) {
            return false;
        }
        String sql = "UPDATE User SET FullName = ?, Role = ?, Status = ?, PasswordHash = ? WHERE Login = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getRole().name());
            pstmt.setString(3, user.getStatus().name());
            pstmt.setString(4, user.getPasswordHash());
            pstmt.setString(5, user.getLogin());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public Optional<User> findByLogin(String login) {
        String sql = "SELECT Id, Login, FullName, Role, Status, PasswordHash FROM User WHERE Login = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Optional.empty();
    }

    @Override
    public List<User> list() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT Id, Login, FullName, Role, Status, PasswordHash FROM User";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return users;
    }

    @Override
    public boolean existsByLogin(String login) {
        return findByLogin(login).isPresent();
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("Id"));
        u.setLogin(rs.getString("Login"));
        u.setFullName(rs.getString("FullName"));
        u.setRole(Role.valueOf(rs.getString("Role")));
        u.setStatus(UserStatus.valueOf(rs.getString("Status")));
        u.setPasswordHash(rs.getString("PasswordHash"));
        return u;
    }

    public void connect() {
        // Base de datos SQLite en un archivo físico (se crea junto al .jar/proyecto
        // la primera vez que se ejecuta). Cambiar a "jdbc:sqlite::memory:" si en
        // algún momento prefieres una base de datos que no persista entre ejecuciones.
        String url = "jdbc:sqlite:./users.db";
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void disconnect() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
