package co.edu.unicauca.liswiit2g9.security;

public interface PasswordHasher {

    String hash(String password);

    boolean matches(String password, String hashedPassword);
}
