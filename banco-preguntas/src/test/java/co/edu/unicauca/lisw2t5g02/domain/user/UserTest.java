package co.edu.unicauca.lisw2t5g02.domain.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void constructorConDatos_asignaLosCamposPrincipales() {
        User user = new User(1, "ana", "Ana Torres", Role.ESTUDIANTE,
                UserState.ACTIVO, "hash123");

        assertEquals(1, user.getId());
        assertEquals("ana", user.getUsername());
        assertEquals("Ana Torres", user.getName());
        assertEquals(Role.ESTUDIANTE, user.getRole());
        assertEquals(UserState.ACTIVO, user.getState());
        assertEquals("hash123", user.getPasswordHash());
    }

    @Test
    void setters_modificanLosCamposDelUsuario() {
        User user = new User();

        user.setId(10);
        user.setUsername("carlos");
        user.setName("Carlos Pérez");
        user.setEmail("carlos@example.com");
        user.setRole(Role.REVISOR);
        user.setState(UserState.INACTIVO);
        user.setPasswordHash("hashNuevo");

        assertEquals(10, user.getId());
        assertEquals("carlos", user.getUsername());
        assertEquals("Carlos Pérez", user.getName());
        assertEquals("carlos@example.com", user.getEmail());
        assertEquals(Role.REVISOR, user.getRole());
        assertEquals(UserState.INACTIVO, user.getState());
        assertEquals("hashNuevo", user.getPasswordHash());
    }

    @Test
    void equals_comparaUsuariosPorId() {
        User primero = new User(7, "ana", "Ana", Role.ESTUDIANTE,
                UserState.ACTIVO, "h1");
        User segundo = new User(7, "otra", "Otra persona", Role.DOCENTE,
                UserState.INACTIVO, "h2");

        assertEquals(primero, segundo);
        assertEquals(primero.hashCode(), segundo.hashCode());
    }

    @Test
    void equals_rechazaUsuariosConIdDiferente() {
        User primero = new User(7, "ana", "Ana", Role.ESTUDIANTE,
                UserState.ACTIVO, "h1");
        User segundo = new User(8, "ana", "Ana", Role.ESTUDIANTE,
                UserState.ACTIVO, "h1");

        assertNotEquals(primero, segundo);
    }

    @Test
    void toString_incluyeNombreYRol() {
        User user = new User(1, "ana", "Ana Torres", Role.DOCENTE,
                UserState.ACTIVO, "hash");

        assertEquals("Ana Torres (Docente)", user.toString());
    }
}
