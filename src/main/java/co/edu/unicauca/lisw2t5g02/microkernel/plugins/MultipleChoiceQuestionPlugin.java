package co.edu.unicauca.lisw2t5g02.microkernel.plugins;

import co.edu.unicauca.lisw2t5g02.domain.question.Question;
import co.edu.unicauca.lisw2t5g02.domain.question.Question.EstadoPregunta;
import co.edu.unicauca.lisw2t5g02.domain.question.QuestionDistractors;
import co.edu.unicauca.lisw2t5g02.microkernel.QuestionPlugin;
import co.edu.unicauca.lisw2t5g02.microkernel.QuestionRequest;
import co.edu.unicauca.lisw2t5g02.microkernel.pipeline.QuestionPipeline;
import co.edu.unicauca.lisw2t5g02.microkernel.pipeline.QuestionValidationException;

import java.util.UUID;

/**
 * @class MultipleChoiceQuestionPlugin
 * @brief Genera preguntas de selección múltiple.
 *
 * <b>Este es el plugin que implementa el pipeline de Tuberías y Filtros</b>
 * exigido por la guía: antes de construir la pregunta final hace pasar la
 * solicitud por los 4 filtros. Si alguno la rechaza, el plugin lanza
 * IllegalArgumentException y el núcleo nunca llega a guardar una pregunta
 * inválida.
 *
 * @author Grupo LISW2 T5 G02
 */
public class MultipleChoiceQuestionPlugin implements QuestionPlugin {

    private final QuestionPipeline pipeline = new QuestionPipeline();

    @Override
    public String getName() {
        return "multiple-choice";
    }

    @Override
    public boolean supports(String tipo) {
        return "MULTIPLE_CHOICE".equalsIgnoreCase(tipo);
    }

    @Override
    public Question generate(QuestionRequest request) {
        QuestionRequest validada;
        try {
            validada = pipeline.ejecutar(request);
        } catch (QuestionValidationException e) {
            throw new IllegalArgumentException("Pregunta rechazada por el pipeline: " + e.getMessage(), e);
        }

        Question pregunta = new Question(
                "P-" + UUID.randomUUID().toString().substring(0, 8),
                validada.getNombre(),
                validada.getEnunciado(),
                validada.getRespuestaCorrecta(),
                EstadoPregunta.BORRADOR);

        char letra = 'A';
        for (String textoOpcion : validada.getOpciones()) {
            pregunta.agregarOpcion(new QuestionDistractors(String.valueOf(letra), textoOpcion));
            letra++;
        }
        return pregunta;
    }
}
