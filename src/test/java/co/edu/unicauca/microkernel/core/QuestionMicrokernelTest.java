package co.edu.unicauca.microkernel.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.microkernel.common.QuestionPlugin;
import co.edu.unicauca.microkernel.common.QuestionRequest;

public class QuestionMicrokernelTest {

    @Test
    void debeRegistrarPluginMultipleChoice() {

        QuestionPluginManager manager =
                QuestionPluginManager.getInstance();

        QuestionPlugin plugin =
                manager.getPlugin("MULTIPLE_CHOICE");

        assertNotNull(plugin);
        assertEquals(
                "multiple-choice",
                plugin.getName()
        );
    }

    @Test
    void debeRegistrarPluginCase() {

        QuestionPluginManager manager =
                QuestionPluginManager.getInstance();

        QuestionPlugin plugin =
                manager.getPlugin("CASE");

        assertNotNull(plugin);
        assertEquals(
                "case",
                plugin.getName()
        );
    }

    @Test
    void debeRegistrarPluginMultimedia() {

        QuestionPluginManager manager =
                QuestionPluginManager.getInstance();

        QuestionPlugin plugin =
                manager.getPlugin("MULTIMEDIA");

        assertNotNull(plugin);
        assertEquals(
                "multimedia",
                plugin.getName()
        );
    }

    @Test
    void debeGenerarPreguntaConMicrokernel() {

        QuestionMicrokernel microkernel =
                new QuestionMicrokernel();

        QuestionRequest request =
                new QuestionRequest(
                        "Pregunta de prueba",
                        "¿Qué es el patrón Microkernel?",
                        "MULTIPLE_CHOICE"
                );

        Question question =
                microkernel.generateQuestion(request);

        assertNotNull(question);
        assertEquals(
                "Pregunta de prueba",
                question.getName()
        );
        assertEquals(
                "¿Qué es el patrón Microkernel?",
                question.getQuestion()
        );
    }

    @Test
    void debeAlmacenarPreguntaGenerada() {

        QuestionMicrokernel microkernel =
                new QuestionMicrokernel();

        QuestionRequest request =
                new QuestionRequest(
                        "Pregunta almacenada",
                        "¿Qué ventaja tiene utilizar plugins?",
                        "CASE"
                );

        Question question =
                microkernel.generateQuestion(request);

        Question storedQuestion =
                microkernel.getQuestion(question.getId());

        assertNotNull(storedQuestion);
        assertEquals(
                question.getId(),
                storedQuestion.getId()
        );
        assertEquals(
                question.getName(),
                storedQuestion.getName()
        );
    }
}
