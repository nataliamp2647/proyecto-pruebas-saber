package co.edu.unicauca.lisw2t5g02.microkernel.pipeline;

import co.edu.unicauca.lisw2t5g02.microkernel.QuestionRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @class QuestionPipeline
 * @brief La tubería: ejecuta los filtros uno tras otro, en el orden que
 * exige la guía.
 *
 * ContentValidation -> OptionsValidation -> Classification -> CorrectAnswerValidation
 *
 * Si un filtro lanza excepción, el pipeline se detiene ahí: ningún filtro
 * posterior se ejecuta sobre una pregunta inválida.
 *
 * @author Grupo LISW2 T5 G02
 */
public class QuestionPipeline {

    private final List<QuestionFilter> filtros;

    public QuestionPipeline() {
        this.filtros = new ArrayList<>(Arrays.asList(
                new ContentValidationFilter(),
                new OptionsValidationFilter(),
                new ClassificationFilter(),
                new CorrectAnswerValidationFilter()));
    }

    /** Permite armar un pipeline con otro orden/subconjunto (útil en pruebas). */
    public QuestionPipeline(List<QuestionFilter> filtrosPersonalizados) {
        this.filtros = new ArrayList<>(filtrosPersonalizados);
    }

    public QuestionRequest ejecutar(QuestionRequest request) throws QuestionValidationException {
        QuestionRequest actual = request;
        for (QuestionFilter filtro : filtros) {
            actual = filtro.process(actual);
        }
        return actual;
    }
}
