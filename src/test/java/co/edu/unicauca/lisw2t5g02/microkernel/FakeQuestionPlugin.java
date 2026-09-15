package co.edu.unicauca.lisw2t5g02.microkernel;

import co.edu.unicauca.lisw2t5g02.domain.question.Question;
import co.edu.unicauca.lisw2t5g02.domain.question.Question.EstadoPregunta;

/**
 * Plugin mínimo para probar el núcleo sin depender de plugins reales.
 * Es público y con constructor sin argumentos, como exige la carga por reflexión.
 */
public class FakeQuestionPlugin implements QuestionPlugin {

    @Override
    public String getName() {
        return "fake";
    }

    @Override
    public boolean supports(String tipo) {
        return "FAKE".equalsIgnoreCase(tipo);
    }

    @Override
    public Question generate(QuestionRequest request) {
        return new Question("fake-id", request.getNombre(), request.getEnunciado(),
                request.getRespuestaCorrecta(), EstadoPregunta.BORRADOR);
    }
}
