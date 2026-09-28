package co.edu.unicauca.lisw2t5g02.access;

import co.edu.unicauca.lisw2t5g02.domain.user.Role;
import co.edu.unicauca.lisw2t5g02.domain.user.User;
import co.edu.unicauca.lisw2t5g02.domain.user.UserState;
import co.edu.unicauca.lisw2t5g02.domain.user.IUserRepository;

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
 * Implementacion de {@link IUserRepository} respaldada por una base de
 * datos SQLite.
 *
 * Solo esta clase sabe de SQL/JDBC: es la unica "razon para cambiar" si
 * un dia se reemplaza el motor de base de datos (SRP). Ni UserService ni
 * AuthService conocen su existencia; solo conocen la interfaz
 * IUserRepository (DIP).
 *
 * @author David Santiago Cruz Varón, Juan Felipe Gallardo Orozco
 */
public class SQLiteUserRepository implements IUserRepository {

    private static final Logger LOGGER = Logger.getLogger(SQLiteUserRepository.class.getName());

    private final String jdbcUrl;

    /**
     * @param dbPath ruta del archivo .db. Ejemplo: "data.db" o "./data/usuarios.db"
     */
    public SQLiteUserRepository(String dbPath) {
        this.jdbcUrl = "jdbc:sqlite:" + dbPath;
        initSchema();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }

    private void initSchema() {
        String sql = "CREATE TABLE IF NOT EXISTS usuario (\n"
                + "    usu_id       INTEGER PRIMARY KEY,\n"
                + "    usu_usuario  VARCHAR(50)  NOT NULL UNIQUE,\n"
                + "    usu_nombre   VARCHAR(50)  NOT NULL,\n"
                + "    usu_rol      VARCHAR(20)  NOT NULL,\n"
                + "    usu_estado   VARCHAR(15)  NOT NULL,\n"
                + "    password     VARCHAR(255) NOT NULL\n"
                + ");";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "No fue posible crear/verificar la tabla usuario", ex);
            throw new RuntimeException("Error inicializando la base de datos", ex);
        }
    }

    @Override
    public void create(User user) {
        String sql = "INSERT INTO usuario (usu_usuario, usu_nombre, usu_rol, usu_estado, password) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getName());
            ps.setString(3, user.getRole().getDbValue());
            ps.setString(4, user.getState().getDbValue());
            ps.setString(5, user.getPasswordHash());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error creando usuario", ex);
            throw new RuntimeException("Error creando usuario en la base de datos", ex);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM usuario WHERE usu_usuario = ?";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error buscando usuario por username", ex);
            throw new RuntimeException("Error consultando la base de datos", ex);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM usuario WHERE usu_id = ?";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error buscando usuario por id", ex);
            throw new RuntimeException("Error consultando la base de datos", ex);
        }
        return Optional.empty();
    }

    @Override
    public List<User> listAll() {
        String sql = "SELECT * FROM usuario ORDER BY usu_id";
        List<User> result = new ArrayList<>();

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error listando usuarios", ex);
            throw new RuntimeException("Error consultando la base de datos", ex);
        }
        return result;
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE usuario SET usu_usuario = ?, usu_nombre = ?, usu_rol = ?, "
                + "usu_estado = ?, password = ? WHERE usu_id = ?";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getName());
            ps.setString(3, user.getRole().getDbValue());
            ps.setString(4, user.getState().getDbValue());
            ps.setString(5, user.getPasswordHash());
            ps.setInt(6, user.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error actualizando usuario", ex);
            throw new RuntimeException("Error actualizando la base de datos", ex);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM usuario WHERE usu_id = ?";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error eliminando usuario", ex);
            throw new RuntimeException("Error eliminando de la base de datos", ex);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("usu_id"));
        user.setUsername(rs.getString("usu_usuario"));
        user.setName(rs.getString("usu_nombre"));
        user.setRole(Role.fromDbValue(rs.getString("usu_rol")));
        user.setState(UserState.fromDbValue(rs.getString("usu_estado")));
        user.setPasswordHash(rs.getString("password"));
        return user;
    }
}
