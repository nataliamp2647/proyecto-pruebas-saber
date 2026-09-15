package co.edu.unicauca.lisw2t5g02.microkernel;

import co.edu.unicauca.lisw2t5g02.domain.question.Question;

/**
 * @interface QuestionPlugin
 * @brief Contrato común que debe cumplir todo generador de preguntas.
 *
 * El núcleo ({@link QuestionMicrokernel}) SOLO conoce esta interfaz, nunca
 * una clase de plugin concreta. Gracias a eso se pueden agregar nuevos
 * tipos de pregunta sin modificar el núcleo.
 *
 * Los plugins devuelven la entidad {@link Question} del dominio, de modo
 * que las preguntas generadas se puedan persistir y consultar con la misma
 * infraestructura que ya usa el resto del sistema.
 *
 * @author Grupo LISW2 T5 G02
 */
public interface QuestionPlugin {

    /** @return nombre identificador del plugin. */
    String getName();

    /** @param tipo tipo solicitado @return true si este plugin lo soporta. */
    boolean supports(String tipo);

    /** @param request datos crudos @return pregunta lista para el banco. */
    Question generate(QuestionRequest request);
}
