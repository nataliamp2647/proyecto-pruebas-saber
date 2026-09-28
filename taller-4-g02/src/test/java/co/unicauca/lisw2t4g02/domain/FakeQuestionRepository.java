package co.unicauca.lisw2t4g02.domain;

import co.unicauca.lisw2t4g02.domain.Question.EstadoPregunta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @class FakeQuestionRepository
 * @brief Doble de prueba de {@link QuestionRepository}.
 *
 * No toca SQLite ni ningún recurso externo: guarda las preguntas en una
 * lista en memoria. Sirve para probar QuestionService de forma aislada
 * (test unitario real, no de integración), y para verificar que los
 * métodos del servicio delegan correctamente en el repositorio.
 */
public class FakeQuestionRepository implements QuestionRepository {

    private final List<Question> preguntas = new ArrayList<>();

    /** Contador de invocaciones a actualizarEstado, para verificar delegación. */
    public int invocacionesActualizarEstado = 0;

    public void agregar(Question pregunta) {
        preguntas.add(pregunta);
    }

    @Override
    public List<Question> obtenerTodas() {
        return new ArrayList<>(preguntas);
    }

    @Override
    public Question obtenerPorId(String id) {
        for (Question pregunta : preguntas) {
            if (pregunta.getId().equals(id)) {
                return pregunta;
            }
        }
        return null;
    }

    @Override
    public void actualizarEstado(String id, EstadoPregunta nuevoEstado) {
        invocacionesActualizarEstado++;
        Question pregunta = obtenerPorId(id);
        if (pregunta != null) {
            pregunta.setEstado(nuevoEstado);
        }
    }

    @Override
    public Map<EstadoPregunta, Integer> contarPorEstado() {
        Map<EstadoPregunta, Integer> conteo = new LinkedHashMap<>();
        for (EstadoPregunta estado : EstadoPregunta.values()) {
            conteo.put(estado, 0);
        }
        for (Question pregunta : preguntas) {
            conteo.merge(pregunta.getEstado(), 1, Integer::sum);
        }
        return conteo;
    }
}
