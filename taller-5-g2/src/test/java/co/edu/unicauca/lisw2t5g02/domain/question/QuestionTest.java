package co.edu.unicauca.lisw2t5g02.domain.question;

import co.edu.unicauca.lisw2t5g02.domain.question.Question.EstadoPregunta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    @Test
    void constructorVacio_inicializaListaDeOpcionesVacia() {
        Question pregunta = new Question();

        assertNotNull(pregunta.getOpciones());
        assertTrue(pregunta.getOpciones().isEmpty());
    }

    @Test
    void constructorConDatos_asignaTodosLosCamposPrincipales() {
        Question pregunta = new Question("P-010", "Nombre", "Enunciado",
                "A", EstadoPregunta.BORRADOR);

        assertEquals("P-010", pregunta.getId());
        assertEquals("Nombre", pregunta.getNombre());
        assertEquals("Enunciado", pregunta.getEnunciado());
        assertEquals("A", pregunta.getRespuestaCorrecta());
        assertEquals(EstadoPregunta.BORRADOR, pregunta.getEstado());
    }

    @Test
    void constructorConDatos_tambienInicializaListaDeOpcionesVacia() {
        Question pregunta = new Question("P-010", "Nombre", "Enunciado",
                "A", EstadoPregunta.BORRADOR);

        assertNotNull(pregunta.getOpciones());
        assertTrue(pregunta.getOpciones().isEmpty());
    }

    @Test
    void agregarOpcion_incrementaElTamanoDeLaListaDeOpciones() {
        Question pregunta = new Question();

        pregunta.agregarOpcion(new QuestionDistractors("A", "Opción A"));
        pregunta.agregarOpcion(new QuestionDistractors("B", "Opción B"));

        assertEquals(2, pregunta.getOpciones().size());
    }

    @Test
    void setId_modificaElIdentificadorDeLaPregunta() {
        Question pregunta = new Question();

        pregunta.setId("P-099");

        assertEquals("P-099", pregunta.getId());
    }

    @Test
    void setEstado_modificaElEstadoDeLaPregunta() {
        Question pregunta = new Question("P-001", "n", "e", "A", EstadoPregunta.BORRADOR);

        pregunta.setEstado(EstadoPregunta.ELIMINADA);

        assertEquals(EstadoPregunta.ELIMINADA, pregunta.getEstado());
    }

    @Test
    void toString_incluyeIdYNombreDeLaPregunta() {
        Question pregunta = new Question("P-001", "Pregunta de ejemplo", "e", "A", EstadoPregunta.BORRADOR);

        String resultado = pregunta.toString();

        assertTrue(resultado.contains("P-001"));
        assertTrue(resultado.contains("Pregunta de ejemplo"));
    }
}
