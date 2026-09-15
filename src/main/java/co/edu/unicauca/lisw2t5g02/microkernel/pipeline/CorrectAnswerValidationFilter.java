package co.edu.unicauca.lisw2t5g02.microkernel.pipeline;

import co.edu.unicauca.lisw2t5g02.microkernel.QuestionRequest;

/**
 * @class CorrectAnswerValidationFilter
 * @brief Filtro 4: valida que exista una respuesta correcta, que
 * pertenezca a las opciones, y que la pregunta ya esté clasificada.
 *
 * @author Grupo LISW2 T5 G02
 */
public class CorrectAnswerValidationFilter implements QuestionFilter {

    @Override
    public QuestionRequest process(QuestionRequest request) throws QuestionValidationException {
        String correcta = request.getRespuestaCorrecta();

        if (correcta == null || correcta.trim().isEmpty()) {
            throw new QuestionValidationException("Debe indicarse una respuesta correcta.");
        }

        boolean pertenece = request.getOpciones().stream()
                .anyMatch(op -> op.trim().equalsIgnoreCase(correcta.trim()));
        if (!pertenece) {
            throw new QuestionValidationException(
                    "La respuesta correcta ('" + correcta + "') debe ser una de las opciones registradas.");
        }

        if (request.getCompetencia() == null || request.getNivelDificultad() == null) {
            throw new QuestionValidationException(
                    "La pregunta debe haber sido clasificada antes de validar la respuesta.");
        }
        return request;
    }
}
