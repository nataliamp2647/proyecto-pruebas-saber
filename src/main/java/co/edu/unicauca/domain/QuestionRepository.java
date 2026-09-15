package co.edu.unicauca.domain;

import java.util.List;

public interface QuestionRepository {

    List<Question> getAll();

    Question findById(int id);

    void update(Question question);
}

