package co.edu.unicauca.liswiit2g9.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class Argon2PasswordHasher implements PasswordHasher {

    private final Argon2 argon2;

    public Argon2PasswordHasher() {
        this.argon2 = Argon2Factory.create();
    }

    @Override
    public String hash(String password) {
        return argon2.hash(3, 65536, 1, password);
    }

    @Override
    public boolean matches(String password, String hashedPassword) {
        return argon2.verify(hashedPassword, password);
    }
}