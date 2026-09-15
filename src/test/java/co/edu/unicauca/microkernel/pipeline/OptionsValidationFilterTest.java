package co.edu.unicauca.microkernel.pipeline;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.domain.QuestionDistractors;

public class OptionsValidationFilterTest {

    private Question crearPregunta(
            QuestionDistractors distractors) {

        return new Question(
                1,
                "Pregunta",
                "¿Qué es una arquitectura de software?",
                distractors,
                "A",
                "Borrador"
        );
    }

    @Test
    void debeAceptarOpcionesValidas() {

        OptionsValidationFilter filter =
                new OptionsValidationFilter();

        Question question =
                crearPregunta(
                        new QuestionDistractors(
                                "Opción A",
                                "Opción B",
                                "Opción C",
                                "Opción D"
                        )
                );

        assertDoesNotThrow(
                () -> filter.process(question)
        );
    }

    @Test
    void debeRechazarOpcionesNulas() {

        OptionsValidationFilter filter =
                new OptionsValidationFilter();

        Question question =
                crearPregunta(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> filter.process(question)
        );
    }

    @Test
    void debeRechazarOpcionVacia() {

        OptionsValidationFilter filter =
                new OptionsValidationFilter();

        Question question =
                crearPregunta(
                        new QuestionDistractors(
                                "Opción A",
                                "",
                                "Opción C",
                                "Opción D"
                        )
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> filter.process(question)
        );
    }

    @Test
    void debeRechazarOpcionesRepetidas() {

        OptionsValidationFilter filter =
                new OptionsValidationFilter();

        Question question =
                crearPregunta(
                        new QuestionDistractors(
                                "Opción A",
                                "Opción A",
                                "Opción C",
                                "Opción D"
                        )
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> filter.process(question)
        );
    }

    @Test
    void debeDetectarOpcionesRepetidasSinImportarMayusculas() {

        OptionsValidationFilter filter =
                new OptionsValidationFilter();

        Question question =
                crearPregunta(
                        new QuestionDistractors(
                                "Java",
                                "JAVA",
                                "Python",
                                "C++"
                        )
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> filter.process(question)
        );
    }
}
