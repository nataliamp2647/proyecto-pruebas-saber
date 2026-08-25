package co.edu.unicauca.liswiit2g9.access;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import co.edu.unicauca.liswiit2g9.database.DatabaseConnection;
import co.edu.unicauca.liswiit2g9.domain.Role;
import co.edu.unicauca.liswiit2g9.domain.User;
import co.edu.unicauca.liswiit2g9.domain.UserStatus;

class SQLiteUserRepositoryTest {

    @Test
    void testSaveAndFindUserByUsername() {

        DatabaseConnection databaseConnection =
                new DatabaseConnection("jdbc:sqlite::memory:");

        UserRepository repository =
                new SQLiteUserRepository(databaseConnection);

        User user = new User(
                "natalia",
                "Natalia Muñoz",
                Role.ESTUDIANTE,
                UserStatus.ACTIVO,
                "Password1!"
        );

        repository.save(user);

        Optional<User> result =
                repository.findByUsername("natalia");

        assertTrue(result.isPresent());
        assertEquals("natalia", result.get().getUsername());
        assertEquals("Natalia Muñoz", result.get().getFullName());
        assertEquals(Role.ESTUDIANTE, result.get().getRole());
        assertEquals(UserStatus.ACTIVO, result.get().getStatus());
        assertEquals("Password1!", result.get().getPassword());
    }

    @Test
    void testReturnEmptyWhenUserDoesNotExist() {

        DatabaseConnection databaseConnection = new DatabaseConnection("jdbc:sqlite::memory:");

        UserRepository repository = new SQLiteUserRepository(databaseConnection);

        Optional<User> result = repository.findByUsername("usuarioNoExiste");
        assertTrue(result.isEmpty());
    }

    @Test
    void testNotAllowDuplicateUsername() {

        DatabaseConnection databaseConnection = new DatabaseConnection("jdbc:sqlite::memory:");

        UserRepository repository = new SQLiteUserRepository(databaseConnection);

        User user1 = new User(
                "natalia",
                "Natalia Muñoz",
                Role.ESTUDIANTE,
                UserStatus.ACTIVO,
                "Password1!"
        );

        User user2 = new User(
                "natalia",
                "Otra Persona",
                Role.DOCENTE,
                UserStatus.ACTIVO,
                "Password2!"
        );

        repository.save(user1);

        assertThrows(RuntimeException.class, () -> repository.save(user2));
    }
}