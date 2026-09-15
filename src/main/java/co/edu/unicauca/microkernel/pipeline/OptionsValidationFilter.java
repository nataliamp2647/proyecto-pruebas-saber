package co.edu.unicauca.microkernel.pipeline;

import java.util.HashSet;
import java.util.Set;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.domain.QuestionDistractors;

public class OptionsValidationFilter implements Filter<Question> {

    @Override
    public Question process(Question question) {

        QuestionDistractors distractors =
                question.getDistractors();

        if (distractors == null) {
            throw new IllegalArgumentException(
                    "La pregunta debe tener opciones."
            );
        }

        String[] options = {
            distractors.getOptionA(),
            distractors.getOptionB(),
            distractors.getOptionC(),
            distractors.getOptionD()
        };

        Set<String> uniqueOptions = new HashSet<>();

        for (String option : options) {

            if (option == null
                    || option.trim().isEmpty()) {

                throw new IllegalArgumentException(
                        "Las opciones no pueden estar vacías."
                );
            }

            if (!uniqueOptions.add(option.trim().toLowerCase())) {

                throw new IllegalArgumentException(
                        "No puede haber opciones repetidas."
                );
            }
        }

        return question;
    }
}
