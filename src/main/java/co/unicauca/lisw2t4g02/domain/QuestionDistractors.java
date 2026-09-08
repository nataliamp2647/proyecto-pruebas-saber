package co.unicauca.lisw2t4g02.domain;

/**
 * @class QuestionDistractors
 * @brief Representa una opción de una pregunta.
 *
 * @author Grupo LISW2 T4 G02
 */
public class QuestionDistractors {
    private String letra;
    private String texto;

    /** Constructor vacío. */
    public QuestionDistractors() { }

    /** @param letra identificador de la opción @param texto contenido. */
    public QuestionDistractors(String letra, String texto) {
        this.letra = letra;
        this.texto = texto;
    }

    public String getLetra() { return letra; }
    public void setLetra(String letra) { this.letra = letra; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    @Override
    public String toString() {
        return letra + ". " + texto;
    }
}
