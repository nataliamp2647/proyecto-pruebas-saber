package co.edu.unicauca.lisw2t5g02.security;

/**
 * Abstraccion para el hashing/verificacion de contrasenas.
 *
 * DIP: AuthService y UserService dependen de esta interfaz, no de un
 * algoritmo concreto. Argon2id puede sustituirse sin acoplar los servicios
 * de autenticación a una implementación particular.
 *
 * @author David Santiago Cruz Varón, Juan Felipe Gallardo Orozco
 */
public interface IPasswordHasher {

    /**
     * Genera el hash (con sal incluida) de una contrasena en texto plano.
     */
    String hash(String plainPassword);

    /**
     * Verifica si una contrasena en texto plano corresponde al hash almacenado.
     */
    boolean verify(String plainPassword, String storedHash);

    /**
     * Indica si el valor guardado tiene el formato producido por esta
     * implementacion de hashing (por ejemplo "salt:hash" en base64).
     *
     * Se usa para detectar datos "legacy": registros importados de una
     * base de datos existente donde la contrasena quedo guardada en
     * texto plano. AuthService usa este metodo para decidir si debe
     * comparar directamente y migrar el valor, o verificar como hash
     * normalmente.
     */
    boolean isHashed(String storedValue);

    /** True when a successfully verified value should be upgraded. */
    default boolean needsRehash(String storedValue) { return false; }
}
