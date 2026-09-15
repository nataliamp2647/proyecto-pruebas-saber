package co.edu.unicauca.lisw2t5g02.microkernel.pipeline;

import co.edu.unicauca.lisw2t5g02.microkernel.QuestionRequest;

/**
 * @class ClassificationFilter
 * @brief Filtro 3: asigna competencia y nivel de dificultad.
 *
 * Si el plugin ya trajo esos datos, este filtro los respeta. Si vienen
 * vacíos, los infiere del tipo de pregunta y de la longitud del enunciado.
 *
 * @author Grupo LISW2 T5 G02
 */
public class ClassificationFilter implements QuestionFilter {

    @Override
    public QuestionRequest process(QuestionRequest request) {
        if (esVacio(request.getCompetencia())) {
            request.setCompetencia(inferirCompetencia(request.getTipo()));
        }
        if (esVacio(request.getNivelDificultad())) {
            request.setNivelDificultad(inferirDificultad(request.getEnunciado()));
        }
        return request;
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private String inferirCompetencia(String tipo) {
        if (tipo == null) {
            return "General";
        }
        switch (tipo.toUpperCase()) {
            case "MULTIPLE_CHOICE": return "Conocimiento general";
            case "CASE":            return "Análisis de casos";
            case "MULTIMEDIA":      return "Interpretación de recursos";
            default:                return "General";
        }
    }

    private String inferirDificultad(String enunciado) {
        int longitud = enunciado == null ? 0 : enunciado.trim().length();
        if (longitud < 60) {
            return "Básico";
        } else if (longitud < 150) {
            return "Intermedio";
        }
        return "Avanzado";
    }
}
