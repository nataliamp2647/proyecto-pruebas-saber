package co.edu.unicauca.liswiit2g9.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class PasswordValidatorTest {

    private final PasswordValidator validator =
            new PasswordValidator();

    @Test
    void testAcceptValidPassword() {

        assertTrue(
                validator.isValid("Password1!")
        );
    }

    @Test
    void testRejectPasswordShorterThanSixCharacters() {

        assertFalse(
                validator.isValid("Pa1!")
        );
    }

    @Test
    void testRejectPasswordWithoutDigit() {

        assertFalse(
                validator.isValid("Password!")
        );
    }

    @Test
    void testRejectPasswordWithoutUppercase() {

        assertFalse(
                validator.isValid("password1!")
        );
    }

    @Test
    void testRejectPasswordWithoutSpecialCharacter() {

        assertFalse(
                validator.isValid("Password1")
        );
    }

    @Test
    void testRejectNullPassword() {

        assertFalse(
                validator.isValid(null)
        );
    }
}