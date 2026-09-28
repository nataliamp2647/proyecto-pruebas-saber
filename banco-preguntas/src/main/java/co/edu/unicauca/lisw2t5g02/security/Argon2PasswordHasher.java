package co.edu.unicauca.lisw2t5g02.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

/** Password hashing with Argon2id. Existing SHA-256 hashes remain verifiable
 * once and are upgraded to Argon2id on the user's next successful login. */
public final class Argon2PasswordHasher implements IPasswordHasher {
    private static final int ITERATIONS = 3;
    private static final int MEMORY_KIB = 65536;
    private static final int PARALLELISM = 1;
    private final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
    private final Sha256PasswordHasher legacy = new Sha256PasswordHasher();

    @Override public String hash(String plainPassword) {
        return argon2.hash(ITERATIONS, MEMORY_KIB, PARALLELISM, plainPassword.toCharArray());
    }
    @Override public boolean verify(String plainPassword, String storedHash) {
        if (storedHash == null) return false;
        if (storedHash.startsWith("$argon2id$")) {
            try { return argon2.verify(storedHash, plainPassword.toCharArray()); }
            catch (RuntimeException ex) { return false; }
        }
        return legacy.isHashed(storedHash) && legacy.verify(plainPassword, storedHash);
    }
    @Override public boolean isHashed(String value) {
        return value != null && (value.startsWith("$argon2id$") || legacy.isHashed(value));
    }
    @Override public boolean needsRehash(String value) {
        return value == null || !value.startsWith("$argon2id$");
    }
}
