package co.edu.unicauca.microkernel.common;

import co.edu.unicauca.domain.Question;

public interface QuestionPlugin {

    String getName();

    boolean supports(String type);

    Question generate(QuestionRequest request);
}