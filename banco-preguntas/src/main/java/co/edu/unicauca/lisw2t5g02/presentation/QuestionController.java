package co.edu.unicauca.lisw2t5g02.presentation;

import co.edu.unicauca.lisw2t5g02.domain.question.Question;
import co.edu.unicauca.lisw2t5g02.domain.question.QuestionDistractors;
import co.edu.unicauca.lisw2t5g02.domain.question.QuestionService;
import co.edu.unicauca.lisw2t5g02.domain.question.QuestionValidator;
import co.edu.unicauca.lisw2t5g02.domain.user.User;
import co.edu.unicauca.lisw2t5g02.domain.question.Question.EstadoPregunta;
import java.util.List;
import java.util.UUID;

/** MVC controller: translates presentation actions into question use cases. */
public final class QuestionController {
    private final QuestionService service;
    private final QuestionValidator validator;
    public QuestionController(QuestionService service) { this(service, new QuestionValidator()); }
    public QuestionController(QuestionService service, QuestionValidator validator) {
        this.service = service; this.validator = validator;
    }
    public Question createDraft(String name, String context, String prompt, List<String> options,
            int correctIndex, String justification, String bibliography, String competency,
            String topic, String subtopic, String difficulty, int authorId) {
        if (options == null || options.size() != 5 || correctIndex < 0 || correctIndex >= 5)
            throw new IllegalArgumentException("Se requieren cinco opciones y una respuesta correcta.");
        Question q = new Question("Q-" + UUID.randomUUID(), name, prompt, options.get(correctIndex), EstadoPregunta.BORRADOR);
        q.setContexto(context); q.setPreguntaDirecta(prompt); q.setJustificacion(justification);
        q.setBibliografia(bibliography); q.setCompetencia(competency); q.setTema(topic);
        q.setSubtema(subtopic); q.setNivelDificultad(difficulty); q.setAutorId(authorId);
        for (int i = 0; i < options.size(); i++) q.agregarOpcion(new QuestionDistractors("" + (char)('A' + i), options.get(i)));
        validator.validate(q);
        service.crearPregunta(q);
        return q;
    }
    public List<Question> list(int authorId, EstadoPregunta state, int page, int pageSize) {
        return service.listarPreguntas(authorId, state, page, pageSize);
    }
    public Question find(String id) { return service.cargarPregunta(id); }
    public void update(Question q) { service.actualizarPregunta(q); }
    public void sendForReview(Question q) { service.enviarARevision(q); }
    public int count(int authorId, EstadoPregunta state) { return service.contarPreguntas(authorId, state); }
    public List<Question> listarPreguntas(int authorId, EstadoPregunta state, int page, int pageSize) { return list(authorId,state,page,pageSize); }
    public int contarPreguntas(int authorId, EstadoPregunta state) { return count(authorId,state); }
    public Question cargarPregunta(String id) { return find(id); }
    public void actualizarPregunta(Question q) { update(q); }
    public void enviarARevision(Question q) { sendForReview(q); }
    public List<Question> listarTodas() { return service.listarPreguntas(); }
    public List<Question> listarTodasPaginado(EstadoPregunta state, int page, int pageSize) { return service.listarTodas(state, page, pageSize); }
    public int contarTodas(EstadoPregunta state) { return service.contarTodas(state); }
    public List<User> revisoresDisponibles() { return service.obtenerRevisoresDisponibles(); }
    public List<User> revisoresAsignados(String id) { return service.obtenerRevisoresAsignados(id); }
    public List<String> asignarRevisores(Question q,List<User> users) { return service.asignarRevisores(q,users); }
}
