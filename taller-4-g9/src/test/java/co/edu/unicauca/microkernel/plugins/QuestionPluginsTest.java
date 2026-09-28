package co.edu.unicauca.microkernel.plugins;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.microkernel.common.QuestionRequest;

public class QuestionPluginsTest {

    @Test
    void debeGenerarPreguntaDeSeleccionMultiple() {

        MultipleChoiceQuestionPlugin plugin =
                new MultipleChoiceQuestionPlugin();

        QuestionRequest request =
                new QuestionRequest(
                        "Pregunta de selección múltiple",
                        "¿Cuál es una característica del patrón Microkernel?",
                        "MULTIPLE_CHOICE"
                );

        Question question =
                plugin.generate(request);

        assertNotNull(question);
        assertEquals(
                "Pregunta de selección múltiple",
                question.getName()
        );
        assertNotNull(question.getDistractors());
        assertEquals("A", question.getCorrectAnswer());
    }

    @Test
    void debeGenerarPreguntaDeCaso() {

        CaseQuestionPlugin plugin =
                new CaseQuestionPlugin();

        QuestionRequest request =
                new QuestionRequest(
                        "Caso de arquitectura",
                        "Una empresa necesita agregar nuevas funcionalidades sin modificar el núcleo.",
                        "CASE"
                );

        Question question =
                plugin.generate(request);

        assertNotNull(question);
        assertEquals(
                "Caso de arquitectura",
                question.getName()
        );
        assertNotNull(question.getDistractors());
        assertEquals("A", question.getCorrectAnswer());
    }

    @Test
    void debeGenerarPreguntaMultimedia() {

        MultimediaQuestionPlugin plugin =
                new MultimediaQuestionPlugin();

        QuestionRequest request =
                new QuestionRequest(
                        "Pregunta multimedia",
                        "Observe el recurso multimedia y determine la arquitectura utilizada.",
                        "MULTIMEDIA"
                );

        Question question =
                plugin.generate(request);

        assertNotNull(question);
        assertEquals(
                "Pregunta multimedia",
                question.getName()
        );
        assertNotNull(question.getDistractors());
        assertEquals("A", question.getCorrectAnswer());
    }
}