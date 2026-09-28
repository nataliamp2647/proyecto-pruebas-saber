package co.edu.unicauca.microkernel.pipeline;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.domain.QuestionDistractors;

public class CorrectAnswerValidationFilterTest {

    private Question crearPregunta(String respuesta) {

        return new Question(
                1,
                "Pregunta",
                "¿Qué es una arquitectura de software?",
                new QuestionDistractors(
                        "A", "B", "C", "D"
                ),
                respuesta,
                "Borrador"
        );
    }

    @Test
    void debeAceptarRespuestaA() {

        CorrectAnswerValidationFilter filter =
                new CorrectAnswerValidationFilter();

        assertDoesNotThrow(
                () -> filter.process(crearPregunta("A"))
        );
    }

    @Test
    void debeAceptarRespuestaB() {

        CorrectAnswerValidationFilter filter =
                new CorrectAnswerValidationFilter();

        assertDoesNotThrow(
                () -> filter.process(crearPregunta("B"))
        );
    }

    @Test
    void debeAceptarRespuestaC() {

        CorrectAnswerValidationFilter filter =
                new CorrectAnswerValidationFilter();

        assertDoesNotThrow(
                () -> filter.process(crearPregunta("C"))
        );
    }

    @Test
    void debeAceptarRespuestaD() {

        CorrectAnswerValidationFilter filter =
                new CorrectAnswerValidationFilter();

        assertDoesNotThrow(
                () -> filter.process(crearPregunta("D"))
        );
    }

    @Test
    void debeRechazarRespuestaVacia() {

        CorrectAnswerValidationFilter filter =
                new CorrectAnswerValidationFilter();

        assertThrows(
                IllegalArgumentException.class,
                () -> filter.process(crearPregunta(""))
        );
    }

    @Test
    void debeRechazarRespuestaInvalida() {

        CorrectAnswerValidationFilter filter =
                new CorrectAnswerValidationFilter();

        assertThrows(
                IllegalArgumentException.class,
                () -> filter.process(crearPregunta("E"))
        );
    }
}