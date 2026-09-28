package co.edu.unicauca.liswiit2g9.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void deberiaCrearUsuarioCorrectamente() {

        User user = new User(
                "natalia",
                "Natalia Muñoz",
                Role.ESTUDIANTE,
                UserStatus.ACTIVO,
                "Hola123!"
        );

        assertEquals("natalia", user.getUsername());
        assertEquals("Natalia Muñoz", user.getFullName());
        assertEquals(Role.ESTUDIANTE, user.getRole());
        assertEquals(UserStatus.ACTIVO, user.getStatus());
        assertEquals("Hola123!", user.getPassword());
    }

    @Test
    void usuarioNuevoDeberiaTenerIdCero() {

        User user = new User(
                "natalia",
                "Natalia Muñoz",
                Role.ESTUDIANTE,
                UserStatus.ACTIVO,
                "Hola123!"
        );

        assertEquals(0, user.getId());
    }

    @Test
    void deberiaPermitirCambiarElEstado() {

        User user = new User(
                "natalia",
                "Natalia Muñoz",
                Role.ESTUDIANTE,
                UserStatus.ACTIVO,
                "Hola123!"
        );

        user.setStatus(UserStatus.INACTIVO);

        assertEquals(UserStatus.INACTIVO, user.getStatus());
    }
}