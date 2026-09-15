package co.edu.unicauca.microkernel.pipeline;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.domain.QuestionDistractors;

public class CorrectAnswerValidationFilter
        implements Filter<Question> {

    @Override
    public Question process(Question question) {

        String correctAnswer =
                question.getCorrectAnswer();

        if (correctAnswer == null
                || correctAnswer.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "La respuesta correcta no puede estar vacía."
            );
        }

        QuestionDistractors distractors =
                question.getDistractors();

        String answer =
                correctAnswer.trim().toUpperCase();

        boolean valid =
                answer.equals("A")
                || answer.equals("B")
                || answer.equals("C")
                || answer.equals("D");

        if (!valid) {

            throw new IllegalArgumentException(
                    "La respuesta correcta debe ser A, B, C o D."
            );
        }

        return question;
    }
}