package co.edu.unicauca.lisw2t5g02.domain.question;

import co.edu.unicauca.lisw2t5g02.domain.question.Question.EstadoPregunta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EstadoPreguntaTest {

    @Test
    void getEtiqueta_devuelveElTextoLegibleDelEstado() {
        assertEquals("Pendiente de revisión", EstadoPregunta.PENDIENTE_REVISION.getEtiqueta());
    }

    @Test
    void toString_devuelveLaMismaEtiquetaQueGetEtiqueta() {
        assertEquals(EstadoPregunta.BORRADOR.getEtiqueta(), EstadoPregunta.BORRADOR.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Borrador", "Pendiente de revisión", "Eliminada"})
    void fromEtiqueta_reconoceCadaUnaDeLasEtiquetasValidas(String etiqueta) {
        assertDoesNotThrow(() -> EstadoPregunta.fromEtiqueta(etiqueta));
    }

    @Test
    void fromEtiqueta_esInsensibleAMayusculasYMinusculas() {
        assertEquals(EstadoPregunta.BORRADOR, EstadoPregunta.fromEtiqueta("BORRADOR"));
    }

    @Test
    void fromEtiqueta_lanzaExcepcionSiLaEtiquetaNoExiste() {
        assertThrows(IllegalArgumentException.class,
                () -> EstadoPregunta.fromEtiqueta("Publicada"));
    }
}
