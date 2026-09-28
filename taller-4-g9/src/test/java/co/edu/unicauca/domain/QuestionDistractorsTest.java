package co.edu.unicauca.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class QuestionDistractorsTest {

    @Test
    void shouldReturnOptionA() {

        QuestionDistractors distractors = createDistractors();

        assertEquals("Opción A",distractors.getOptionA());
    }

    @Test
    void shouldReturnOptionB() {

        QuestionDistractors distractors = createDistractors();

        assertEquals("Opción B",distractors.getOptionB());
    }

    @Test
    void shouldReturnOptionC() {

        QuestionDistractors distractors = createDistractors();

        assertEquals("Opción C",distractors.getOptionC());
    }

    @Test
    void shouldReturnOptionD() {

        QuestionDistractors distractors = createDistractors();

        assertEquals("Opción D",distractors.getOptionD());
    }

    private QuestionDistractors createDistractors() {
        return new QuestionDistractors("Opción A","Opción B","Opción C","Opción D");
    }
}

