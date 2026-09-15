package co.edu.unicauca.lisw2t5g02.microkernel.pipeline;

import co.edu.unicauca.lisw2t5g02.microkernel.QuestionRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de los cuatro filtros del pipeline de Tuberías y Filtros,
 * cada uno probado de forma aislada.
 */
class FiltrosTest {

    @Nested
    class ContenidoTest {
        private final ContentValidationFilter filtro = new ContentValidationFilter();

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "corto?"})
        void rechazaEnunciadosInvalidos(String enunciado) {
            QuestionRequest r = new QuestionRequest("n", enunciado, "MULTIPLE_CHOICE");
            assertThrows(QuestionValidationException.class, () -> filtro.process(r));
        }

        @Test
        void rechazaEnunciadoNulo() {
            QuestionRequest r = new QuestionRequest("n", null, "MULTIPLE_CHOICE");
            assertThrows(QuestionValidationException.class, () -> filtro.process(r));
        }

        @Test
        void rechazaEnunciadoSinSignoFinalValido() {
            QuestionRequest r = new QuestionRequest("n", "Enunciado sin cierre correcto", "MULTIPLE_CHOICE");
            assertThrows(QuestionValidationException.class, () -> filtro.process(r));
        }

        @Test
        void aceptaEnunciadoValido() {
            QuestionRequest r = new QuestionRequest("n", "¿Enunciado válido y suficientemente largo?", "MULTIPLE_CHOICE");
            assertDoesNotThrow(() -> filtro.process(r));
        }
    }

    @Nested
    class OpcionesTest {
        private final OptionsValidationFilter filtro = new OptionsValidationFilter();

        @Test
        void rechazaMenosDeDosOpciones() {
            QuestionRequest r = new QuestionRequest("n", "e", "MULTIPLE_CHOICE");
            r.setOpciones(List.of("Única"));
            assertThrows(QuestionValidationException.class, () -> filtro.process(r));
        }

        @Test
        void rechazaOpcionVacia() {
            QuestionRequest r = new QuestionRequest("n", "e", "MULTIPLE_CHOICE");
            r.setOpciones(List.of("A", "  "));
            assertThrows(QuestionValidationException.class, () -> filtro.process(r));
        }

        @Test
        void rechazaOpcionesDuplicadasIgnorandoMayusculas() {
            QuestionRequest r = new QuestionRequest("n", "e", "MULTIPLE_CHOICE");
            r.setOpciones(List.of("Java", "java"));
            assertThrows(QuestionValidationException.class, () -> filtro.process(r));
        }

        @Test
        void aceptaOpcionesValidas() {
            QuestionRequest r = new QuestionRequest("n", "e", "MULTIPLE_CHOICE");
            r.setOpciones(List.of("A", "B", "C"));
            assertDoesNotThrow(() -> filtro.process(r));
        }
    }

    @Nested
    class ClasificacionTest {
        private final ClassificationFilter filtro = new ClassificationFilter();

        @Test
        void asignaCompetenciaSegunElTipoCuandoNoVieneDada() {
            QuestionRequest r = new QuestionRequest("n", "corto", "CASE");

            assertEquals("Análisis de casos", filtro.process(r).getCompetencia());
        }

        @Test
        void noSobreescribeUnaCompetenciaYaAsignada() {
            QuestionRequest r = new QuestionRequest("n", "e", "MULTIPLE_CHOICE");
            r.setCompetencia("Competencia propia del plugin");

            assertEquals("Competencia propia del plugin", filtro.process(r).getCompetencia());
        }

        @Test
        void asignaDificultadBasicaParaEnunciadoCorto() {
            QuestionRequest r = new QuestionRequest("n", "corto", "MULTIPLE_CHOICE");

            assertEquals("Básico", filtro.process(r).getNivelDificultad());
        }
    }

    @Nested
    class RespuestaCorrectaTest {
        private final CorrectAnswerValidationFilter filtro = new CorrectAnswerValidationFilter();

        private QuestionRequest clasificado() {
            QuestionRequest r = new QuestionRequest("n", "e", "MULTIPLE_CHOICE");
            r.setOpciones(List.of("A", "B", "C"));
            r.setCompetencia("Comp");
            r.setNivelDificultad("Básico");
            return r;
        }

        @Test
        void rechazaSiNoHayRespuestaCorrecta() {
            assertThrows(QuestionValidationException.class, () -> filtro.process(clasificado()));
        }

        @Test
        void rechazaSiLaRespuestaNoEstaEntreLasOpciones() {
            QuestionRequest r = clasificado();
            r.setRespuestaCorrecta("Z");
            assertThrows(QuestionValidationException.class, () -> filtro.process(r));
        }

        @Test
        void rechazaSiLaPreguntaNoFueClasificadaAntes() {
            QuestionRequest r = new QuestionRequest("n", "e", "MULTIPLE_CHOICE");
            r.setOpciones(List.of("A", "B"));
            r.setRespuestaCorrecta("A");
            assertThrows(QuestionValidationException.class, () -> filtro.process(r));
        }

        @Test
        void aceptaRespuestaValida() {
            QuestionRequest r = clasificado();
            r.setRespuestaCorrecta("B");
            assertDoesNotThrow(() -> filtro.process(r));
        }
    }
}
