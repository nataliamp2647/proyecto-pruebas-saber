package co.unicauca.iso2.taller2.users.domain.service;

/**
 * Abstracción para el cifrado/verificación de contraseñas. Permite cambiar
 * el algoritmo (PBKDF2, Argon2, BCrypt, ...) sin tocar AuthService.
 *
 * @author Mani
 */
public interface IPasswordHasher {

    String hash(String plainPassword);

    boolean verify(String plainPassword, String storedHash);
}
