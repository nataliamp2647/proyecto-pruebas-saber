package co.edu.unicauca.domain;

import java.util.List;

import co.edu.unicauca.infra.Subject;

public class QuestionService extends Subject {

    private QuestionRepository repository;

    public QuestionService(QuestionRepository repository) {

        this.repository = repository;
    }

    public List<Question> getQuestions() {

        return repository.getAll();
    }

    public Question findQuestion(int id) {

        return repository.findById(id);
    }

    public void updateQuestionStatus(
            int id,
            String newStatus) {

        Question question =
                repository.findById(id);

        if (question != null) {

            question.setStatus(newStatus);

            repository.update(question);

            notifyObservers();
        }
    }
}

