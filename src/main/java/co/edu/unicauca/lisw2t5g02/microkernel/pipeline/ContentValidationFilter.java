package co.edu.unicauca.lisw2t5g02.microkernel.pipeline;

import co.edu.unicauca.lisw2t5g02.microkernel.QuestionRequest;

/**
 * @class ContentValidationFilter
 * @brief Filtro 1: valida que la pregunta tenga texto válido.
 * Reglas: texto no vacío, longitud mínima, formato correcto.
 *
 * @author Grupo LISW2 T5 G02
 */
public class ContentValidationFilter implements QuestionFilter {

    private static final int LONGITUD_MINIMA = 10;

    @Override
    public QuestionRequest process(QuestionRequest request) throws QuestionValidationException {
        String enunciado = request.getEnunciado();

        if (enunciado == null || enunciado.trim().isEmpty()) {
            throw new QuestionValidationException("El enunciado de la pregunta no puede estar vacío.");
        }
        if (enunciado.trim().length() < LONGITUD_MINIMA) {
            throw new QuestionValidationException(
                    "El enunciado debe tener al menos " + LONGITUD_MINIMA + " caracteres.");
        }
        String limpio = enunciado.trim();
        char ultimo = limpio.charAt(limpio.length() - 1);
        if (ultimo != '?' && ultimo != '.') {
            throw new QuestionValidationException(
                    "El enunciado debe terminar en '?' o '.' para tener formato de pregunta.");
        }
        return request;
    }
}
