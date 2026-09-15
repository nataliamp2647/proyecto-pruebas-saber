package co.edu.unicauca.lisw2t5g02.microkernel.pipeline;

import co.edu.unicauca.lisw2t5g02.microkernel.QuestionRequest;

/**
 * @interface QuestionFilter
 * @brief Un filtro del pipeline: recibe la solicitud, la valida y/o la
 * enriquece, y la devuelve para que continúe al siguiente filtro.
 *
 * @author Grupo LISW2 T5 G02
 */
public interface QuestionFilter {
    QuestionRequest process(QuestionRequest request) throws QuestionValidationException;
}
