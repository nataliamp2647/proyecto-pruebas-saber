package co.edu.unicauca.lisw2t5g02.domain.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserStateTest {

    @Test
    void fromDbValue_deberiaReconocerEstados() {
        assertEquals(UserState.ACTIVO, UserState.fromDbValue("activo"));
        assertEquals(UserState.INACTIVO, UserState.fromDbValue("inactivo"));
    }

    @Test
    void fromDbValue_deberiaIgnorarMayusculas() {
        assertEquals(UserState.ACTIVO, UserState.fromDbValue("ACTIVO"));
    }

    @Test
    void fromDbValue_deberiaRechazarEstadoDesconocido() {
        assertThrows(IllegalArgumentException.class,
                () -> UserState.fromDbValue("pendiente"));
    }

    @Test
    void toString_deberiaRetornarElValorDeBaseDeDatos() {
        assertEquals("inactivo", UserState.INACTIVO.toString());
    }
}
