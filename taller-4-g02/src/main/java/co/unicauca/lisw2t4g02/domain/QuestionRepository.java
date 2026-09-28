package co.unicauca.lisw2t4g02.domain;

import java.util.List;
import java.util.Map;
import co.unicauca.lisw2t4g02.domain.Question.EstadoPregunta;

/**
 * @interface QuestionRepository
 * @brief Contrato del repositorio de preguntas.
 *
 * La capa de dominio define la abstracción; la implementación JDBC/SQLite
 * pertenece a la capa access.
 *
 * @author Grupo LISW2 T4 G02
 */
public interface QuestionRepository {
    /** @return todas las preguntas. */
    List<Question> obtenerTodas();
    /** @param id identificador @return pregunta o null si no existe. */
    Question obtenerPorId(String id);
    /** Actualiza el estado de una pregunta. */
    void actualizarEstado(String id, EstadoPregunta nuevoEstado);
    /** @return cantidad de preguntas agrupadas por estado. */
    Map<EstadoPregunta, Integer> contarPorEstado();
}
