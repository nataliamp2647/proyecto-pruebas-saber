package co.edu.unicauca.lisw2t5g02.microkernel.plugins;

import co.edu.unicauca.lisw2t5g02.domain.question.Question;
import co.edu.unicauca.lisw2t5g02.domain.question.Question.EstadoPregunta;
import co.edu.unicauca.lisw2t5g02.domain.question.QuestionDistractors;
import co.edu.unicauca.lisw2t5g02.microkernel.QuestionPlugin;
import co.edu.unicauca.lisw2t5g02.microkernel.QuestionRequest;

import java.util.UUID;

/**
 * @class MultimediaQuestionPlugin
 * @brief Genera preguntas que incluyen un recurso multimedia (imagen,
 * audio o video) referenciado por una URL dentro del enunciado.
 *
 * @author Grupo LISW2 T5 G02
 */
public class MultimediaQuestionPlugin implements QuestionPlugin {

    @Override
    public String getName() {
        return "multimedia";
    }

    @Override
    public boolean supports(String tipo) {
        return "MULTIMEDIA".equalsIgnoreCase(tipo);
    }

    @Override
    public Question generate(QuestionRequest request) {
        if (request.getEnunciado() == null || !request.getEnunciado().contains("http")) {
            throw new IllegalArgumentException(
                    "Una pregunta multimedia debe referenciar un recurso (URL) en su enunciado.");
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
