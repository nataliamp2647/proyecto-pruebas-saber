package co.edu.unicauca.access;

import java.util.ArrayList;
import java.util.List;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.domain.QuestionDistractors;
import co.edu.unicauca.domain.QuestionRepository;

public class QuestionImplRepository implements QuestionRepository {

    private List<Question> questions;

    public QuestionImplRepository() {

        questions = new ArrayList<>();
        loadQuestions();
    }

    private void loadQuestions() {

        QuestionDistractors distractors1 =
                new QuestionDistractors(
                        "Diseñar bases de datos",
                        "Modelar el dominio del negocio",
                        "Eliminar UML",
                        "Crear interfaces gráficas"
                );

        Question question1 =
                new Question(
                        1,
                        "Pregunta sobre DDD",
                        "¿Cuál es el objetivo principal de DDD?",
                        distractors1,
                        "B",
                        "Borrador"
                );

        QuestionDistractors distractors2 =
                new QuestionDistractors(
                        "Java",
                        "Python",
                        "C++",
                        "JavaScript"
                );

        Question question2 =
                new Question(
                        2,
                        "Pregunta sobre programación",
                        "¿Cuál de los siguientes es un lenguaje de programación?",distractors2,
                        "A",
                        "Pendiente de revisión"
                );

        QuestionDistractors distractors3 =
                new QuestionDistractors(
                        "MVC",
                        "Observer",
                        "Singleton",
                        "Adapter"
                );

        Question question3 =
                new Question(
                        3,
                        "Pregunta sobre patrones",
                        "¿Cuál es un patrón de comportamiento?",
                        distractors3,
                        "B",
                        "Eliminada"
                );

        questions.add(question1);
        questions.add(question2);
        questions.add(question3);
    }

    @Override
    public List<Question> getAll() {
        return questions;
    }

    @Override
    public Question findById(int id) {

        for (Question question : questions) {

            if (question.getId() == id) {
                return question;
            }
        }

        return null;
    }

    @Override
    public void update(Question question) {
    }
}

