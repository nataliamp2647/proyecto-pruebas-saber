package co.edu.unicauca.microkernel.plugins;

import java.util.UUID;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.domain.QuestionDistractors;
import co.edu.unicauca.microkernel.common.QuestionPlugin;
import co.edu.unicauca.microkernel.common.QuestionRequest;

public class MultimediaQuestionPlugin implements QuestionPlugin {

    @Override
    public String getName() {
        return "multimedia";
    }

    @Override
    public boolean supports(String type) {
        return "MULTIMEDIA".equalsIgnoreCase(type);
    }

    @Override
    public Question generate(QuestionRequest request) {

        System.out.println(
                "Generando pregunta multimedia..."
        );

        QuestionDistractors distractors =
                new QuestionDistractors(
                        "Recurso multimedia A",
                        "Recurso multimedia B",
                        "Recurso multimedia C",
                        "Recurso multimedia D"
                );

        return new Question(
                Math.abs(UUID.randomUUID().hashCode()),
                request.getName(),
                request.getQuestion(),
                distractors,
                "A",
                "Borrador"
        );
    }
}