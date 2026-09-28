package co.edu.unicauca.lisw2t2go2;

import co.edu.unicauca.lisw2t2go2.security.PasswordPolicy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias de la politica de complejidad de contrasenas:
 * minimo 6 caracteres, al menos un digito, al menos un caracter
 * especial y al menos una mayuscula.
 *
 * @author David Santiago Cruz Varón, Juan Felipe Gallardo Orozco
 */
class PasswordPolicyTest {

    @Test
    void deberiaAceptarUnaContrasenaQueCumpleTodosLosRequisitos() {
        assertTrue(PasswordPolicy.isValid("Clave123#"));
    }

    @Test
    void deberiaRechazarContrasenaMuyCorta() {
        assertFalse(PasswordPolicy.isValid("A1#bc"));
    }

    @Test
    void deberiaRechazarContrasenaSinDigito() {
        assertFalse(PasswordPolicy.isValid("Clavesegura#"));
    }

    @Test
    void deberiaRechazarContrasenaSinMayuscula() {
        assertFalse(PasswordPolicy.isValid("clave123#"));
    }

    @Test
    void deberiaRechazarContrasenaSinCaracterEspecial() {
        assertFalse(PasswordPolicy.isValid("Clave123"));
    }

    @Test
    void deberiaLanzarExcepcionConMensajeEspecifico() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> PasswordPolicy.validate("clave123#"));

        assertTrue(ex.getMessage().toLowerCase().contains("mayuscula"));
    }

    @Test
    void deberiaAceptarContrasenasRealesDelProyecto() {
        // Las contrasenas usadas en los datos de ejemplo del proyecto
        // (Admin123#, User123#) deben seguir siendo validas.
        assertTrue(PasswordPolicy.isValid("Admin123#"));
        assertTrue(PasswordPolicy.isValid("User123#"));
    }
}
