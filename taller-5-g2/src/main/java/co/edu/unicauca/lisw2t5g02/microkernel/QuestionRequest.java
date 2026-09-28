package co.edu.unicauca.lisw2t5g02.microkernel;

import java.util.ArrayList;
import java.util.List;

/**
 * @class QuestionRequest
 * @brief Datos crudos que entran al microkernel para generar una pregunta.
 *
 * Es lo que viaja por el pipeline de Tuberías y Filtros: cada filtro puede
 * validarlo y/o completarlo (por ejemplo, el filtro de clasificación llena
 * competencia y nivel de dificultad si vienen vacíos).
 *
 * @author Grupo LISW2 T5 G02
 */
public class QuestionRequest {

    private String nombre;
    private String enunciado;
    private String tipo;
    private List<String> opciones = new ArrayList<>();
    private String respuestaCorrecta;
    private String competencia;
    private String nivelDificultad;

    public QuestionRequest() { }

    public QuestionRequest(String nombre, String enunciado, String tipo) {
        this.nombre = nombre;
        this.enunciado = enunciado;
        this.tipo = tipo;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public List<String> getOpciones() { return opciones; }
    public void setOpciones(List<String> opciones) { this.opciones = opciones; }
    public String getRespuestaCorrecta() { return respuestaCorrecta; }
    public void setRespuestaCorrecta(String respuestaCorrecta) { this.respuestaCorrecta = respuestaCorrecta; }
    public String getCompetencia() { return competencia; }
    public void setCompetencia(String competencia) { this.competencia = competencia; }
    public String getNivelDificultad() { return nivelDificultad; }
    public void setNivelDificultad(String nivelDificultad) { this.nivelDificultad = nivelDificultad; }
}
