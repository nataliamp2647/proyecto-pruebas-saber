package co.edu.unicauca.liswiit2g9.domain;

public class User {
    private int id;
    private String username;
    private String fullName;
    private Role role;
    private UserStatus status;
    private String password;

    public User(int id, String username, String fullName, Role role, UserStatus status, String password) {

        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.status = status;
        this.password = password;
    }

    public User( String username, String fullName, Role role, UserStatus status, String password) {
        this(0, username, fullName, role, status, password);
    }

        public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public Role getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
