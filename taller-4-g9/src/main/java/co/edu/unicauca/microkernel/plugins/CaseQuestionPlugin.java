package co.edu.unicauca.microkernel.plugins;

import java.util.UUID;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.domain.QuestionDistractors;
import co.edu.unicauca.microkernel.common.QuestionPlugin;
import co.edu.unicauca.microkernel.common.QuestionRequest;

public class CaseQuestionPlugin implements QuestionPlugin {

    @Override
    public String getName() {
        return "case";
    }

    @Override
    public boolean supports(String type) {
        return "CASE".equalsIgnoreCase(type);
    }

    @Override
    public Question generate(QuestionRequest request) {

        System.out.println(
                "Generando pregunta basada en caso..."
        );

        QuestionDistractors distractors =
                new QuestionDistractors(
                        "Analizar el caso",
                        "Ignorar la información",
                        "Eliminar el problema",
                        "No tomar ninguna decisión"
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
