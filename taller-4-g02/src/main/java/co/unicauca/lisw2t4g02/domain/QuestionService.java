package co.unicauca.lisw2t4g02.domain;

import co.unicauca.lisw2t4g02.domain.Question.EstadoPregunta;
import co.unicauca.lisw2t4g02.infra.Observer;
import co.unicauca.lisw2t4g02.infra.Subject;

import java.util.List;
import java.util.Map;

/**
 * @class QuestionService
 * @brief Contiene los casos de uso de gestión de preguntas.
 *
 * Coordina el repositorio y actúa como Subject del patrón Observer.
 * La presentación solo necesita conocer este servicio y el dominio.
 *
 * @author Grupo LISW2 T4 G02
 */
public class QuestionService extends Subject {
    private final QuestionRepository repositorio;

    /** @param repositorio repositorio definido por el dominio. */
    public QuestionService(QuestionRepository repositorio) {
        this.repositorio = repositorio;
    }

    /** @return lista de preguntas. */
    public List<Question> listarPreguntas() {
        return repositorio.obtenerTodas();
    }

    /** @param id identificador @return pregunta solicitada. */
    public Question cargarPregunta(String id) {
        return repositorio.obtenerPorId(id);
    }

    /**
     * @brief Actualiza el estado y notifica a las vistas observadoras.
     */
    public void actualizarEstado(String id, EstadoPregunta nuevoEstado) {
        repositorio.actualizarEstado(id, nuevoEstado);
        notifyAllObserves();
    }

    /** @return distribución de preguntas por estado. */
    public Map<EstadoPregunta, Integer> obtenerDistribucionPorEstado() {
        return repositorio.contarPorEstado();
    }
}
