package co.edu.unicauca.microkernel.pipeline;

import co.edu.unicauca.domain.Question;

public class ClassificationFilter implements Filter<Question> {

    @Override
    public Question process(Question question) {

        if (question.getStatus() == null
                || question.getStatus().trim().isEmpty()) {

            question.setStatus("Clasificada");
        }

        return question;
    }
}
