package co.edu.unicauca.liswiit2g9.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import co.edu.unicauca.liswiit2g9.access.SQLiteUserRepository;
import co.edu.unicauca.liswiit2g9.access.UserRepository;
import co.edu.unicauca.liswiit2g9.database.DatabaseConnection;
import co.edu.unicauca.liswiit2g9.domain.Role;
import co.edu.unicauca.liswiit2g9.domain.User;
import co.edu.unicauca.liswiit2g9.domain.UserStatus;
import co.edu.unicauca.liswiit2g9.security.Argon2PasswordHasher;
import co.edu.unicauca.liswiit2g9.security.PasswordHasher;
import co.edu.unicauca.liswiit2g9.security.PasswordValidator;

class UserServiceTest {

    @Test
    void testRegisterUserInSQLite() {

        DatabaseConnection databaseConnection = new DatabaseConnection("jdbc:sqlite::memory:");

        UserRepository repository = new SQLiteUserRepository(databaseConnection);

        PasswordHasher hasher = new Argon2PasswordHasher();

        PasswordValidator validator = new PasswordValidator();

        UserService service = new UserService(repository, hasher, validator);

        User user = new User(
                "natalia",
                "Natalia Muñoz",
                Role.ESTUDIANTE,
                UserStatus.ACTIVO,
                "Password1!"
        );

        service.register(user);

        User savedUser = repository.findByUsername("natalia").orElseThrow();

        assertEquals("natalia", savedUser.getUsername());
        assertEquals("Natalia Muñoz", savedUser.getFullName());
        assertEquals(Role.ESTUDIANTE, savedUser.getRole());
        assertEquals(UserStatus.ACTIVO, savedUser.getStatus());

        assertNotEquals("Password1!", savedUser.getPassword());
        assertTrue(savedUser.getPassword().startsWith("$argon2"));
    }

    @Test
    void testRejectInvalidPassword() {

        DatabaseConnection databaseConnection = new DatabaseConnection("jdbc:sqlite::memory:");

        UserRepository repository = new SQLiteUserRepository(databaseConnection);

        PasswordHasher hasher = new Argon2PasswordHasher();

        PasswordValidator validator = new PasswordValidator();

        UserService service = new UserService(repository, hasher, validator);

        User user = new User(
                "usuario2",
                "Usuario Dos",
                Role.ESTUDIANTE,
                UserStatus.ACTIVO,
                "password"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(user)
        );
    }

    @Test
    void testLoginWithCorrectPassword() {

        DatabaseConnection databaseConnection = new DatabaseConnection("jdbc:sqlite::memory:");

        UserRepository repository = new SQLiteUserRepository(databaseConnection);

        PasswordHasher hasher = new Argon2PasswordHasher();

        PasswordValidator validator = new PasswordValidator();

        UserService service = new UserService(repository, hasher, validator);

        User user = new User(
                "natalia",
                "Natalia Muñoz",
                Role.ESTUDIANTE,
                UserStatus.ACTIVO,
                "Password1!"
        );

        service.register(user);

        User loggedUser =
                service.login("natalia", "Password1!");

        assertEquals("natalia", loggedUser.getUsername());
        assertEquals("Natalia Muñoz", loggedUser.getFullName());
    }

@Test
    void testRejectWrongPassword() {

        DatabaseConnection databaseConnection = new DatabaseConnection("jdbc:sqlite::memory:");

        UserRepository repository = new SQLiteUserRepository(databaseConnection);

        PasswordHasher hasher = new Argon2PasswordHasher();

        PasswordValidator validator = new PasswordValidator();

        UserService service = new UserService(repository, hasher, validator);

        User user = new User(
                "natalia",
                "Natalia Muñoz",
                Role.ESTUDIANTE,
                UserStatus.ACTIVO,
                "Password1!"
        );

        service.register(user);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.login("natalia", "PasswordIncorrecta1!")
        );
    }

@Test
    void testRejectInactiveUser() {

        DatabaseConnection databaseConnection =
                new DatabaseConnection("jdbc:sqlite::memory:");

        UserRepository repository =
                new SQLiteUserRepository(databaseConnection);

        PasswordHasher hasher =
                new Argon2PasswordHasher();

        PasswordValidator validator =
                new PasswordValidator();

        UserService service =
                new UserService(repository, hasher, validator);

        User user = new User(
                "natalia",
                "Natalia Muñoz",
                Role.ESTUDIANTE,
                UserStatus.INACTIVO,
                "Password1!"
        );

        service.register(user);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.login("natalia", "Password1!")
        );
    }
}