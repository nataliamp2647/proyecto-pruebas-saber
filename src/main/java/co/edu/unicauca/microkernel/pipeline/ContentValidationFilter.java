package co.edu.unicauca.microkernel.pipeline;

import co.edu.unicauca.domain.Question;

public class ContentValidationFilter implements Filter<Question> {

    @Override
    public Question process(Question question) {

        if (question.getName() == null
                || question.getName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El nombre de la pregunta no puede estar vacío."
            );
        }

        if (question.getQuestion() == null
                || question.getQuestion().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El contenido de la pregunta no puede estar vacío."
            );
        }

        if (question.getQuestion().trim().length() < 10) {

            throw new IllegalArgumentException(
                    "La pregunta debe tener mínimo 10 caracteres."
            );
        }

        return question;
    }
}
