package co.edu.unicauca.lisw2t5g02.microkernel.pipeline;

import co.edu.unicauca.lisw2t5g02.microkernel.QuestionRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas del pipeline completo (los 4 filtros encadenados). */
class QuestionPipelineTest {

    private QuestionRequest requestValido() {
        QuestionRequest r = new QuestionRequest("Título", "¿Enunciado válido de la pregunta?", "MULTIPLE_CHOICE");
        r.setOpciones(List.of("A", "B"));
        r.setRespuestaCorrecta("A");
        return r;
    }

    @Test
    void ejecutaLosCuatroFiltrosYEnriqueceLaSolicitud() throws QuestionValidationException {
        QuestionRequest resultado = new QuestionPipeline().ejecutar(requestValido());

        assertNotNull(resultado.getCompetencia());
        assertNotNull(resultado.getNivelDificultad());
    }

    @Test
    void seDetieneEnElPrimerFiltroQueFalla() {
        QuestionRequest r = new QuestionRequest("Título", "", "MULTIPLE_CHOICE");

        QuestionValidationException ex = assertThrows(QuestionValidationException.class,
                () -> new QuestionPipeline().ejecutar(r));
        assertTrue(ex.getMessage().toLowerCase().contains("enunciado"));
    }

    @Test
    void permiteArmarUnPipelinePersonalizadoConMenosFiltros() {
        QuestionPipeline soloContenido = new QuestionPipeline(List.of(new ContentValidationFilter()));
        QuestionRequest r = new QuestionRequest("t", "¿Enunciado suficientemente largo?", "MULTIPLE_CHOICE");

        // Sin opciones ni respuesta: pasaría igual, porque esos filtros no están en este pipeline.
        assertDoesNotThrow(() -> soloContenido.ejecutar(r));
    }
}
