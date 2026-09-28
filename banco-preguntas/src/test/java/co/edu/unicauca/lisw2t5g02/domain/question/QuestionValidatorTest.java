package co.edu.unicauca.lisw2t5g02.domain.question;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuestionValidatorTest {
    private final QuestionValidator validator=new QuestionValidator();
    private Question validQuestion() {
        Question q=new Question("Q-1","Pregunta ejemplo","¿Cuánto es 2+2?","4",Question.EstadoPregunta.BORRADOR);
        q.setContexto("Una operación aritmética."); q.setJustificacion("La suma es cuatro."); q.setBibliografia("Texto de matemáticas.");
        q.setCompetencia("Razonamiento");q.setTema("Aritmética");q.setSubtema("Suma");q.setNivelDificultad("Básico");
        String[] values={"3","4","5","6","7"};for(int i=0;i<values.length;i++)q.agregarOpcion(new QuestionDistractors(""+(char)('A'+i),values[i]));return q;
    }
    @Test void acceptsExactlyFiveDistinctOptionsAndOneCorrectAnswer(){assertDoesNotThrow(()->validator.validate(validQuestion()));}
    @Test void rejectsMissingRequiredField(){Question q=validQuestion();q.setContexto(" ");assertThrows(IllegalArgumentException.class,()->validator.validate(q));}
    @Test void rejectsDuplicateOptions(){Question q=validQuestion();q.getOpciones().get(4).setTexto("4");assertThrows(IllegalArgumentException.class,()->validator.validate(q));}
    @Test void rejectsForbiddenOptionPhrases(){Question q=validQuestion();q.getOpciones().get(4).setTexto("Ninguna de las anteriores");assertThrows(IllegalArgumentException.class,()->validator.validate(q));}
    @Test void rejectsIncorrectAnswerNotPresentInOptions(){Question q=validQuestion();q.setRespuestaCorrecta("9");assertThrows(IllegalArgumentException.class,()->validator.validate(q));}
}
