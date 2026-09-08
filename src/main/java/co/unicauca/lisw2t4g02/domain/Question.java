package co.unicauca.lisw2t4g02.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @class Question
 * @brief Entidad de dominio que representa una pregunta del sistema.
 *
 * No conoce Swing, JDBC ni SQLite. Solo representa información del dominio.
 *
 * @author Grupo LISW2 T4 G02
 */
public class Question {

    /** Estados posibles de una pregunta. */
    public enum EstadoPregunta {
        /** Pregunta en elaboración. */
        BORRADOR("Borrador"),
        /** Pregunta pendiente de revisión. */
        PENDIENTE_REVISION("Pendiente de revisión"),
        /** Pregunta dada de baja lógicamente. */
        ELIMINADA("Eliminada");

        private final String etiqueta;

        EstadoPregunta(String etiqueta) {
            this.etiqueta = etiqueta;
        }

        /** @return etiqueta legible del estado. */
        public String getEtiqueta() {
            return etiqueta;
        }

        @Override
        public String toString() {
            return etiqueta;
        }

        /**
         * @brief Convierte una etiqueta almacenada en BD al enum correspondiente.
         * @param etiqueta etiqueta persistida.
         * @return estado correspondiente.
         * @throws IllegalArgumentException si la etiqueta es desconocida.
         */
        public static EstadoPregunta fromEtiqueta(String etiqueta) {
            for (EstadoPregunta estado : values()) {
                if (estado.etiqueta.equalsIgnoreCase(etiqueta)) {
                    return estado;
                }
            }
            throw new IllegalArgumentException("Estado desconocido: " + etiqueta);
        }
    }

    private String id;
    private String nombre;
    private String enunciado;
    private String respuestaCorrecta;
    private EstadoPregunta estado;
    private List<QuestionDistractors> opciones;

    /** Construye una pregunta vacía. */
    public Question() {
        opciones = new ArrayList<>();
    }

    /**
     * @brief Construye una pregunta con sus datos principales.
     */
    public Question(String id, String nombre, String enunciado,
            String respuestaCorrecta, EstadoPregunta estado) {
        this();
        this.id = id;
        this.nombre = nombre;
        this.enunciado = enunciado;
        this.respuestaCorrecta = respuestaCorrecta;
        this.estado = estado;
    }

    /** @param opcion opción que se agrega. */
    public void agregarOpcion(QuestionDistractors opcion) {
        opciones.add(opcion);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }
    public String getRespuestaCorrecta() { return respuestaCorrecta; }
    public void setRespuestaCorrecta(String respuestaCorrecta) { this.respuestaCorrecta = respuestaCorrecta; }
    public EstadoPregunta getEstado() { return estado; }
    public void setEstado(EstadoPregunta estado) { this.estado = estado; }
    public List<QuestionDistractors> getOpciones() { return opciones; }
    public void setOpciones(List<QuestionDistractors> opciones) { this.opciones = opciones; }

    @Override
    public String toString() {
        return id + " - " + nombre;
    }
}
