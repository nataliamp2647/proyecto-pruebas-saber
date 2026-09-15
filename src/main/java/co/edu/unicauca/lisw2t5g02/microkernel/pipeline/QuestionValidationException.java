package co.edu.unicauca.lisw2t5g02.microkernel.pipeline;

/**
 * @class QuestionValidationException
 * @brief Se lanza cuando un filtro del pipeline rechaza la pregunta.
 *
 * @author Grupo LISW2 T5 G02
 */
public class QuestionValidationException extends Exception {
    public QuestionValidationException(String message) {
        super(message);
    }
}
