package co.edu.unicauca.lisw2t5g02.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Implementacion de {@link IPasswordHasher} usando SHA-256 con sal aleatoria.
 *
 * El hash almacenado tiene el formato "saltBase64:hashBase64" para poder
 * reconstruir la verificacion sin guardar la sal en una columna aparte.
 *
 * Nota didactica: para un sistema en produccion se recomienda un algoritmo
 * pensado para contrasenas (BCrypt, SCrypt o Argon2) en lugar de SHA-256
 * plano, ya que estos son deliberadamente lentos y resistentes a ataques
 * de fuerza bruta con GPU. Gracias a que esta clase esta detras de la
 * interfaz IPasswordHasher (DIP), cambiar el algoritmo en el futuro no
 * afecta a UserService ni a AuthService.
 *
 * @author David Santiago Cruz Varón, Juan Felipe Gallardo Orozco
 */
public class Sha256PasswordHasher implements IPasswordHasher {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH_BYTES = 16;
    private final SecureRandom random = new SecureRandom();

    @Override
    public String hash(String plainPassword) {
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        random.nextBytes(salt);
        byte[] digest = digest(plainPassword, salt);

        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(digest);
    }

    @Override
    public boolean verify(String plainPassword, String storedHash) {
        String[] parts = storedHash.split(":");
        if (parts.length != 2) {
            return false;
        }
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expectedDigest = Base64.getDecoder().decode(parts[1]);
        byte[] actualDigest = digest(plainPassword, salt);

        return MessageDigest.isEqual(expectedDigest, actualDigest);
    }

    @Override
    public boolean isHashed(String storedValue) {
        if (storedValue == null) {
            return false;
        }
        String[] parts = storedValue.split(":");
        if (parts.length != 2) {
            return false;
        }
        return isValidBase64(parts[0]) && isValidBase64(parts[1]);
    }

    private boolean isValidBase64(String value) {
        try {
            Base64.getDecoder().decode(value);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private byte[] digest(String plainPassword, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            return md.digest(plainPassword.getBytes());
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Algoritmo de hashing no disponible: " + ALGORITHM, ex);
        }
    }
}
