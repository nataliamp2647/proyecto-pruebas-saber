package co.edu.unicauca.lisw2t5g02.domain.question;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionDistractorsTest {

    @Test
    void constructorConDatos_asignaLetraYTexto() {
        QuestionDistractors opcion = new QuestionDistractors("A", "Modelar el dominio del negocio");

        assertEquals("A", opcion.getLetra());
        assertEquals("Modelar el dominio del negocio", opcion.getTexto());
    }

    @Test
    void constructorVacio_permiteAsignarValoresConSetters() {
        QuestionDistractors opcion = new QuestionDistractors();

        opcion.setLetra("B");
        opcion.setTexto("Otra opción");

        assertEquals("B", opcion.getLetra());
        assertEquals("Otra opción", opcion.getTexto());
    }

    @Test
    void toString_devuelveFormatoLetraPuntoTexto() {
        QuestionDistractors opcion = new QuestionDistractors("C", "Eliminar UML");

        assertEquals("C. Eliminar UML", opcion.toString());
    }
}
