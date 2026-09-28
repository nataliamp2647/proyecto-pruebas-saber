package co.edu.unicauca.microkernel.pipeline;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.domain.QuestionDistractors;

public class ClassificationFilterTest {

    private Question crearPregunta(String status) {

        return new Question(
                1,
                "Pregunta",
                "¿Qué es una arquitectura de software?",
                new QuestionDistractors(
                        "A", "B", "C", "D"
                ),
                "A",
                status
        );
    }

    @Test
    void debeClasificarPreguntaSinEstado() {

        ClassificationFilter filter =
                new ClassificationFilter();

        Question question =
                crearPregunta(null);

        filter.process(question);

        assertEquals(
                "Clasificada",
                question.getStatus()
        );
    }

    @Test
    void debeMantenerEstadoExistente() {

        ClassificationFilter filter =
                new ClassificationFilter();

        Question question =
                crearPregunta("Borrador");

        filter.process(question);

        assertEquals(
                "Borrador",
                question.getStatus()
        );
    }

    @Test
    void debeClasificarEstadoVacio() {

        ClassificationFilter filter =
                new ClassificationFilter();

        Question question =
                crearPregunta("");

        filter.process(question);

        assertEquals(
                "Clasificada",
                question.getStatus()
        );
    }

    @Test
    void debeClasificarEstadoConEspacios() {

        ClassificationFilter filter =
                new ClassificationFilter();

        Question question =
                crearPregunta("   ");

        filter.process(question);

        assertEquals(
                "Clasificada",
                question.getStatus()
        );
    }
}