package co.edu.unicauca.lisw2t5g02.microkernel;

import co.edu.unicauca.lisw2t5g02.domain.question.Question;
import co.edu.unicauca.lisw2t5g02.microkernel.plugins.CaseQuestionPlugin;
import co.edu.unicauca.lisw2t5g02.microkernel.plugins.MultimediaQuestionPlugin;
import co.edu.unicauca.lisw2t5g02.microkernel.plugins.MultipleChoiceQuestionPlugin;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de los tres plugins del proyecto. */
class PluginsTest {

    private final MultipleChoiceQuestionPlugin multiple = new MultipleChoiceQuestionPlugin();
    private final CaseQuestionPlugin caso = new CaseQuestionPlugin();
    private final MultimediaQuestionPlugin multimedia = new MultimediaQuestionPlugin();

    @Test
    void cadaPluginSoportaSoloSuPropioTipo() {
        assertTrue(multiple.supports("MULTIPLE_CHOICE"));
        assertFalse(multiple.supports("CASE"));
        assertTrue(caso.supports("CASE"));
        assertFalse(caso.supports("MULTIMEDIA"));
        assertTrue(multimedia.supports("MULTIMEDIA"));
        assertFalse(multimedia.supports("MULTIPLE_CHOICE"));
    }

    @Test
    void supportsIgnoraMayusculasYMinusculas() {
        assertTrue(multiple.supports("multiple_choice"));
    }

    @Test
    void seleccionMultipleGeneraLaPreguntaConSusOpcionesEtiquetadas() {
        QuestionRequest r = new QuestionRequest("Patrones", "¿Cuál patrón corresponde?", "MULTIPLE_CHOICE");
        r.setOpciones(List.of("Microkernel", "Singleton", "Proxy"));
        r.setRespuestaCorrecta("Microkernel");

        Question pregunta = multiple.generate(r);

        assertEquals(3, pregunta.getOpciones().size());
        assertEquals("A", pregunta.getOpciones().get(0).getLetra());
        assertEquals("C", pregunta.getOpciones().get(2).getLetra());
    }

    @Test
    void seleccionMultipleRechazaUnaSolicitudQueElPipelineInvalida() {
        QuestionRequest r = new QuestionRequest("Mala", "¿Enunciado válido para probar?", "MULTIPLE_CHOICE");
        r.setOpciones(List.of("A", "B"));
        r.setRespuestaCorrecta("Z"); // no pertenece a las opciones

        assertThrows(IllegalArgumentException.class, () -> multiple.generate(r));
    }

    @Test
    void casoRechazaEnunciadoVacio() {
        QuestionRequest r = new QuestionRequest("Caso", "   ", "CASE");

        assertThrows(IllegalArgumentException.class, () -> caso.generate(r));
    }

    @Test
    void casoGeneraLaPreguntaCuandoHayEnunciado() {
        QuestionRequest r = new QuestionRequest("Caso", "Una empresa enfrenta un problema.", "CASE");

        Question pregunta = caso.generate(r);

        assertNotNull(pregunta.getId());
        assertEquals("Caso", pregunta.getNombre());
    }

    @Test
    void multimediaExigeUnRecursoEnElEnunciado() {
        QuestionRequest sinRecurso = new QuestionRequest("M", "Sin ningún recurso adjunto.", "MULTIMEDIA");

        assertThrows(IllegalArgumentException.class, () -> multimedia.generate(sinRecurso));
    }

    @Test
    void multimediaGeneraLaPreguntaCuandoHayUnaUrl() {
        QuestionRequest r = new QuestionRequest("M", "Observe el diagrama: https://ejemplo.com/img.png", "MULTIMEDIA");

        assertNotNull(multimedia.generate(r).getId());
    }

    @Test
    void losIdsGeneradosSonUnicos() {
        QuestionRequest r = new QuestionRequest("Caso", "Un caso de prueba cualquiera.", "CASE");

        assertNotEquals(caso.generate(r).getId(), caso.generate(r).getId());
    }
}
