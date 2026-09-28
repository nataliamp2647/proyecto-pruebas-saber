package co.edu.unicauca.lisw2t5g02.microkernel.plugins;

import co.edu.unicauca.lisw2t5g02.domain.question.Question;
import co.edu.unicauca.lisw2t5g02.domain.question.Question.EstadoPregunta;
import co.edu.unicauca.lisw2t5g02.domain.question.QuestionDistractors;
import co.edu.unicauca.lisw2t5g02.microkernel.QuestionPlugin;
import co.edu.unicauca.lisw2t5g02.microkernel.QuestionRequest;

import java.util.UUID;

/**
 * @class CaseQuestionPlugin
 * @brief Genera preguntas de análisis de casos.
 *
 * No usa el pipeline completo (la guía solo obliga a uno de los plugins a
 * hacerlo): valida únicamente lo que le compete a este tipo de pregunta.
 *
 * @author Grupo LISW2 T5 G02
 */
public class CaseQuestionPlugin implements QuestionPlugin {

    @Override
    public String getName() {
        return "case-analysis";
    }

    @Override
    public boolean supports(String tipo) {
        return "CASE".equalsIgnoreCase(tipo);
    }

    @Override
    public Question generate(QuestionRequest request) {
        if (request.getEnunciado() == null || request.getEnunciado().trim().isEmpty()) {
            throw new IllegalArgumentException("El caso a analizar no puede estar vacío.");
        }

        Question pregunta = new Question(
                "P-" + UUID.randomUUID().toString().substring(0, 8),
                request.getNombre(),
                request.getEnunciado(),
                request.getRespuestaCorrecta() == null ? "" : request.getRespuestaCorrecta(),
                EstadoPregunta.BORRADOR);

        char letra = 'A';
        for (String textoOpcion : request.getOpciones()) {
            pregunta.agregarOpcion(new QuestionDistractors(String.valueOf(letra), textoOpcion));
            letra++;
        }
        return pregunta;
    }
}
