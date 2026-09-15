package co.edu.unicauca.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import co.edu.unicauca.access.QuestionImplRepository;
import co.edu.unicauca.infra.Observer;

public class QuestionServiceTest {

    @Test
    void shouldReturnAllQuestions() {

        QuestionRepository repository = new QuestionImplRepository();

        QuestionService service = new QuestionService(repository);

        assertEquals(3,service.getQuestions().size());
    }

    @Test
    void shouldFindExistingQuestion() {

        QuestionRepository repository = new QuestionImplRepository();

        QuestionService service = new QuestionService(repository);

        Question question = service.findQuestion(1);

        assertNotNull(question);

        assertEquals(1,question.getId());
    }

    @Test
    void shouldReturnNullWhenQuestionDoesNotExist() {

        QuestionRepository repository = new QuestionImplRepository();

        QuestionService service = new QuestionService(repository);

        Question question = service.findQuestion(999);

        assertNull(question);
    }

    @Test
    void shouldUpdateQuestionStatus() {

        QuestionRepository repository = new QuestionImplRepository();

        QuestionService service = new QuestionService(repository);

        service.updateQuestionStatus(1,"Eliminada");

        Question question = service.findQuestion(1);

        assertEquals("Eliminada",question.getStatus());
    }

    @Test
    void shouldKeepUpdatedStatusInRepository() {

        QuestionRepository repository = new QuestionImplRepository();

        QuestionService service = new QuestionService(repository);

        service.updateQuestionStatus(2,"Borrador");

        Question question = repository.findById(2);

        assertEquals("Borrador",question.getStatus());
    }

    @Test
    void shouldNotifyObserverWhenStatusChanges() {

        QuestionRepository repository = new QuestionImplRepository();

        QuestionService service = new QuestionService(repository);

        TestObserver observer = new TestObserver();

        service.addObserver(observer);

        service.updateQuestionStatus(1,"Pendiente de revisión");

        assertEquals(1,observer.getNotificationCount());
    }

    private static class TestObserver implements Observer {

        private int notificationCount = 0;

        @Override
        public void update() {
            notificationCount++;
        }

        public int getNotificationCount() {
            return notificationCount;
        }
    }
}

