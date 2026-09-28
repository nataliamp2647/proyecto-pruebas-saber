package co.unicauca.iso2.taller2.users.domain;

/**
 * Entidad de dominio. No conoce nada de persistencia ni de validación
 * (Single Responsibility): sólo representa los datos de un usuario.
 *
 * @author Mani
 */
public class User {

    private int id;
    private String login;
    private String fullName;
    private Role role;
    private UserStatus status;
    /**
     * Hash de la contraseña (nunca se guarda en texto plano). El formato
     * usado por Pbkdf2PasswordHasher es "iteraciones:saltBase64:hashBase64".
     */
    private String passwordHash;

    public User() {
    }

    public User(int id, String login, String fullName, Role role, UserStatus status, String passwordHash) {
        this.id = id;
        this.login = login;
        this.fullName = fullName;
        this.role = role;
        this.status = status;
        this.passwordHash = passwordHash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", login=" + login + ", fullName=" + fullName
                + ", role=" + role + ", status=" + status + '}';
    }
}
