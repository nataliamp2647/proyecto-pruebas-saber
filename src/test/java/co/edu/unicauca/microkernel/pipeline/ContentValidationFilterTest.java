package co.edu.unicauca.microkernel.pipeline;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.domain.QuestionDistractors;

public class ContentValidationFilterTest {

    private Question crearPregunta(String nombre, String contenido) {
        return new Question(
                1,
                nombre,
                contenido,
                new QuestionDistractors(
                        "A", "B", "C", "D"
                ),
                "A",
                "Borrador"
        );
    }

    @Test
    void debeAceptarPreguntaValida() {

        ContentValidationFilter filter =
                new ContentValidationFilter();

        Question question =
                crearPregunta(
                        "Pregunta válida",
                        "¿Qué es una arquitectura de software?"
                );

        assertDoesNotThrow(
                () -> filter.process(question)
        );
    }

    @Test
    void debeRechazarNombreVacio() {

        ContentValidationFilter filter =
                new ContentValidationFilter();

        Question question =
                crearPregunta(
                        "",
                        "¿Qué es una arquitectura de software?"
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> filter.process(question)
        );
    }

    @Test
    void debeRechazarNombreNulo() {

        ContentValidationFilter filter =
                new ContentValidationFilter();

        Question question =
                crearPregunta(
                        null,
                        "¿Qué es una arquitectura de software?"
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> filter.process(question)
        );
    }

    @Test
    void debeRechazarPreguntaVacia() {

        ContentValidationFilter filter =
                new ContentValidationFilter();

        Question question =
                crearPregunta(
                        "Pregunta",
                        ""
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> filter.process(question)
        );
    }

    @Test
    void debeRechazarPreguntaMuyCorta() {

        ContentValidationFilter filter =
                new ContentValidationFilter();

        Question question =
                crearPregunta(
                        "Pregunta",
                        "Hola"
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> filter.process(question)
        );
    }
}
