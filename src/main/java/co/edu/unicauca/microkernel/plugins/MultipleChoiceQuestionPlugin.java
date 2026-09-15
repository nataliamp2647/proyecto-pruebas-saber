package co.edu.unicauca.microkernel.plugins;

import java.util.Arrays;
import java.util.UUID;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.domain.QuestionDistractors;
import co.edu.unicauca.microkernel.common.QuestionPlugin;
import co.edu.unicauca.microkernel.common.QuestionRequest;
import co.edu.unicauca.microkernel.pipeline.ClassificationFilter;
import co.edu.unicauca.microkernel.pipeline.ContentValidationFilter;
import co.edu.unicauca.microkernel.pipeline.CorrectAnswerValidationFilter;
import co.edu.unicauca.microkernel.pipeline.Filter;
import co.edu.unicauca.microkernel.pipeline.OptionsValidationFilter;
import co.edu.unicauca.microkernel.pipeline.Pipeline;

public class MultipleChoiceQuestionPlugin
        implements QuestionPlugin {

    @Override
    public String getName() {
        return "multiple-choice";
    }

    @Override
    public boolean supports(String type) {
        return "MULTIPLE_CHOICE".equalsIgnoreCase(type);
    }

    @Override
    public Question generate(QuestionRequest request) {

        System.out.println(
                "Generando pregunta de selección múltiple..."
        );

        QuestionDistractors distractors =
                new QuestionDistractors(
                        "Opción A",
                        "Opción B",
                        "Opción C",
                        "Opción D"
                );

        Question question =
                new Question(
                        Math.abs(UUID.randomUUID().hashCode()),
                        request.getName(),
                        request.getQuestion(),
                        distractors,
                        "A",
                        "Borrador"
                );

        Filter<Question> contentValidation =
                new ContentValidationFilter();

        Filter<Question> optionsValidation =
                new OptionsValidationFilter();

        Filter<Question> classification =
                new ClassificationFilter();

        Filter<Question> correctAnswerValidation =
                new CorrectAnswerValidationFilter();

        Pipeline<Question> pipeline =
                new Pipeline<>(
                        Arrays.asList(
                                contentValidation,
                                optionsValidation,
                                classification,
                                correctAnswerValidation
                        )
                );

        return pipeline.process(question);
    }
}
