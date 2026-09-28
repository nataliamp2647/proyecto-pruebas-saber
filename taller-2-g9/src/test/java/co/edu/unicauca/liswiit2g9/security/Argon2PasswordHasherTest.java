package co.edu.unicauca.liswiit2g9.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class Argon2PasswordHasherTest {

    @Test
    void testHashPassword() {

        PasswordHasher hasher = new Argon2PasswordHasher();

        String hashedPassword = hasher.hash("Password1!");

        assertNotNull(hashedPassword);
        assertNotEquals("Password1!", hashedPassword);
        assertTrue(hashedPassword.startsWith("$argon2"));
    }

    @Test
    void testVerifyCorrectPassword() {

        PasswordHasher hasher = new Argon2PasswordHasher();

        String hashedPassword = hasher.hash("Password1!");

        boolean result = hasher.matches(
                "Password1!",
                hashedPassword
        );

        assertTrue(result);
    }

    @Test
    void testRejectIncorrectPassword() {

        PasswordHasher hasher = new Argon2PasswordHasher();

        String hashedPassword = hasher.hash("Password1!");

        boolean result = hasher.matches(
                "PasswordIncorrecta!",
                hashedPassword
        );

        assertFalse(result);
    }
}