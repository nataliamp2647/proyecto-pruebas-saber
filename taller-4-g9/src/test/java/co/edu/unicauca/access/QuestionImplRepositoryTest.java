package co.edu.unicauca.access;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import co.edu.unicauca.domain.Question;

public class QuestionImplRepositoryTest {

    @Test
    void shouldLoadQuestionsInitially() {

        QuestionImplRepository repository = new QuestionImplRepository();
        assertEquals(3,repository.getAll().size());
    }

    @Test
    void shouldFindExistingQuestion() {

        QuestionImplRepository repository = new QuestionImplRepository();

        Question question = repository.findById(1);

        assertNotNull(question);

        assertEquals("Pregunta sobre DDD",question.getName());
    }

    @Test
    void shouldReturnNullForNonExistingQuestion() {
        QuestionImplRepository repository = new QuestionImplRepository();
        Question question = repository.findById(999);
        assertNull(question);
    }

    @Test
    void shouldReturnStoredQuestions() {

        QuestionImplRepository repository = new QuestionImplRepository();

        assertTrue(repository.getAll().contains(repository.findById(1)));
    }
}

