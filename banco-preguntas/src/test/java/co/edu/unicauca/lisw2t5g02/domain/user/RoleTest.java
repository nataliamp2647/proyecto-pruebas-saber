package co.edu.unicauca.lisw2t5g02.domain.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void fromDbValue_deberiaReconocerTodosLosRoles() {
        assertEquals(Role.ADMINISTRADOR, Role.fromDbValue("Administrador"));
        assertEquals(Role.AUTOR_PREGUNTAS, Role.fromDbValue("Autor de preguntas"));
        assertEquals(Role.REVISOR, Role.fromDbValue("Revisor"));
        assertEquals(Role.DOCENTE, Role.fromDbValue("Docente"));
        assertEquals(Role.ESTUDIANTE, Role.fromDbValue("Estudiante"));
    }

    @Test
    void fromDbValue_deberiaIgnorarMayusculas() {
        assertEquals(Role.DOCENTE, Role.fromDbValue("DOCENTE"));
    }

    @Test
    void fromDbValue_deberiaRechazarRolDesconocido() {
        assertThrows(IllegalArgumentException.class,
                () -> Role.fromDbValue("Rol inexistente"));
    }

    @Test
    void toString_deberiaRetornarElValorDeBaseDeDatos() {
        assertEquals("Autor de preguntas", Role.AUTOR_PREGUNTAS.toString());
    }
}
