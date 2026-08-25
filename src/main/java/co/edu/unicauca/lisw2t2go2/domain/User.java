package co.edu.unicauca.lisw2t2go2.domain;

import java.util.Objects;

/**
 * Entidad de dominio Usuario.
 *
 * Es un objeto "plano": solo conoce sus propios datos, no sabe nada de
 * base de datos, ni de reglas de negocio de registro/autenticacion.
 * Esas responsabilidades viven en las clases de repositorio y servicio
 * respectivamente (principio de responsabilidad unica - SRP).
 *
 * @author Claude
 */
public class User {

    private int id;
    private String username;
    private String name;
    private Role role;
    private UserState state;
    /**
     * Nunca se almacena ni se transmite la contrasena en texto plano,
     * solo su hash (ver {@link co.edu.unicauca.lisw2t2go2.security.IPasswordHasher}).
     */
    private String passwordHash;

    public User() {
    }

    public User(int id, String username, String name, Role role, UserState state, String passwordHash) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.role = role;
        this.state = state;
        this.passwordHash = passwordHash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', name='" + name
                + "', role=" + role + ", state=" + state + '}';
    }
}
