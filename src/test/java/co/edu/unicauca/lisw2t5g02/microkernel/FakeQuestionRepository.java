package co.edu.unicauca.lisw2t5g02.microkernel;

import co.edu.unicauca.lisw2t5g02.domain.question.Question;
import co.edu.unicauca.lisw2t5g02.domain.question.Question.EstadoPregunta;
import co.edu.unicauca.lisw2t5g02.domain.question.QuestionRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositorio en memoria para probar el microkernel sin tocar SQLite.
 */
public class FakeQuestionRepository implements QuestionRepository {

    private final List<Question> preguntas = new ArrayList<>();
    public int invocacionesGuardar = 0;

    @Override
    public List<Question> obtenerTodas() {
        return new ArrayList<>(preguntas);
    }

    @Override
    public Question obtenerPorId(String id) {
        return preguntas.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void actualizarEstado(String id, EstadoPregunta nuevoEstado) {
        Question p = obtenerPorId(id);
        if (p != null) {
            p.setEstado(nuevoEstado);
        }
    }

    @Override
    public Map<EstadoPregunta, Integer> contarPorEstado() {
        Map<EstadoPregunta, Integer> conteo = new LinkedHashMap<>();
        for (EstadoPregunta e : EstadoPregunta.values()) {
            conteo.put(e, 0);
        }
        for (Question p : preguntas) {
            conteo.merge(p.getEstado(), 1, Integer::sum);
        }
        return conteo;
    }

    @Override
    public void guardar(Question pregunta) {
        invocacionesGuardar++;
        preguntas.add(pregunta);
    }
}
