package co.edu.unicauca.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class QuestionTest {

    @Test
    void shouldCreateQuestionCorrectly() {

        QuestionDistractors distractors =
            new QuestionDistractors("Opción A","Opción B","Opción C", "Opción D");

        Question question =
            new Question(1,"Pregunta de prueba","¿Cuál es una pregunta de prueba?",distractors,"B","Borrador");

        assertEquals(1, question.getId());

        assertEquals("Pregunta de prueba",question.getName());

        assertEquals("¿Cuál es una pregunta de prueba?",question.getQuestion());

        assertEquals("B",question.getCorrectAnswer());

        assertEquals("Borrador",question.getStatus());
    }

    @Test
    void shouldChangeQuestionStatus() {

        QuestionDistractors distractors = new QuestionDistractors("A","B","C", "D");

        Question question =
            new Question(1,"Pregunta","Pregunta de prueba",distractors,"A","Borrador");

        question.setStatus("Pendiente de revisión");

        assertEquals("Pendiente de revisión",question.getStatus());
    }

    @Test
    void shouldReturnQuestionId() {

        Question question = createQuestion();

        assertEquals(10,question.getId());
    }

    @Test
    void shouldReturnQuestionName() {

        Question question = createQuestion();

        assertEquals("Pregunta de prueba",question.getName());
    }

    @Test
    void shouldReturnQuestionText() {

        Question question = createQuestion();

        assertEquals("¿Cuál es la respuesta correcta?",question.getQuestion());
    }

    @Test
    void shouldReturnCorrectAnswer() {

        Question question = createQuestion();

        assertEquals("C",question.getCorrectAnswer());
    }

    private Question createQuestion() {

        QuestionDistractors distractors =
                new QuestionDistractors("A","B","C","D");

        return new Question(
                10,
                "Pregunta de prueba",
                "¿Cuál es la respuesta correcta?",
                distractors,
                "C",
                "Borrador"
        );
    }
}
