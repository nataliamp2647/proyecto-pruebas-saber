package co.edu.unicauca.microkernel.core;

import java.util.HashMap;
import java.util.Map;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.microkernel.common.QuestionPlugin;
import co.edu.unicauca.microkernel.common.QuestionRequest;

public class QuestionMicrokernel {

    private Map<String, Question> questions;

    private QuestionPluginManager pluginManager;

    public QuestionMicrokernel() {

        questions = new HashMap<>();

        pluginManager =
                QuestionPluginManager.getInstance();
    }

    public Question generateQuestion(
            QuestionRequest request) {

        QuestionPlugin plugin =
                pluginManager.getPlugin(
                        request.getType()
                );

        if (plugin == null) {

            throw new IllegalArgumentException(
                    "No existe un plugin para el tipo: "
                    + request.getType()
            );
        }

        Question question =
                plugin.generate(request);

        questions.put(
                String.valueOf(question.getId()),
                question
        );

        return question;
    }

    public Question getQuestion(int id) {

        return questions.get(
                String.valueOf(id)
        );
    }

    public Map<String, Question> getQuestions() {

        return questions;
    }
}