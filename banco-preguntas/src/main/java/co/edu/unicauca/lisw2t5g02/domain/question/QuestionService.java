package co.edu.unicauca.lisw2t5g02.domain.question;
import co.edu.unicauca.lisw2t5g02.domain.question.Question.EstadoPregunta;import co.edu.unicauca.lisw2t5g02.domain.user.*;import co.edu.unicauca.lisw2t5g02.infra.Subject;import java.util.*;
/** Casos de uso de preguntas del primer corte. */
public class QuestionService extends Subject {
 private final QuestionRepository repositorio; private final IUserRepository usuarios; private final NotificationService notifier; private final QuestionValidator validator;
 public QuestionService(QuestionRepository r){this(r,null,new NotificationService(),new QuestionValidator());}
 public QuestionService(QuestionRepository r,IUserRepository u,NotificationService n){this(r,u,n,new QuestionValidator());}
 public QuestionService(QuestionRepository r,IUserRepository u,NotificationService n,QuestionValidator v){repositorio=r;usuarios=u;notifier=n==null?new NotificationService():n;validator=v;}
 public void crearPregunta(Question q){validator.validate(q);if(q.getId()==null||q.getId().isBlank())throw new IllegalArgumentException("La pregunta debe tener identificador.");repositorio.guardar(q);notifyAllObserves();}
 public List<Question> listarPreguntas(){return repositorio.obtenerTodas();}
 public List<Question> listarPreguntas(int autorId,EstadoPregunta estado,int pagina,int tamano){return repositorio.obtenerPorAutor(autorId,estado,pagina,tamano);}
 public List<Question> listarTodas(EstadoPregunta estado,int pagina,int tamano){return repositorio.obtenerTodasFiltradas(estado,pagina,tamano);}
 public int contarTodas(EstadoPregunta estado){return repositorio.contarTodas(estado);}
 public int contarPreguntas(int autorId,EstadoPregunta estado){return repositorio.contarPorAutor(autorId,estado);}
 public Question cargarPregunta(String id){return repositorio.obtenerPorId(id);}
 public void actualizarEstado(String id,EstadoPregunta nuevo){Question q=cargarPregunta(id);if(q==null)throw new IllegalArgumentException("La pregunta no existe.");if(nuevo==EstadoPregunta.PENDIENTE_REVISION&&q.getEstado()!=EstadoPregunta.BORRADOR)throw new IllegalStateException("Solo una pregunta en BORRADOR puede pasar a Pendiente de revisión.");if(nuevo!=EstadoPregunta.PENDIENTE_REVISION&&nuevo!=EstadoPregunta.BORRADOR&&nuevo!=EstadoPregunta.ELIMINADA)throw new IllegalArgumentException("Estado no válido.");repositorio.actualizarEstado(id,nuevo);notifyAllObserves();}
 public void enviarARevision(Question q){if(q==null||q.getEstado()!=EstadoPregunta.BORRADOR)throw new IllegalStateException("Solo las preguntas en BORRADOR pueden enviarse a revisión.");validarParaRevision(q);repositorio.actualizarEstado(q.getId(),EstadoPregunta.PENDIENTE_REVISION);notifyAllObserves();}
 private void validarParaRevision(Question q){validator.validate(q);}
 public void actualizarPregunta(Question q){if(q==null)throw new IllegalArgumentException("Pregunta inválida.");if(q.getEstado()!=EstadoPregunta.BORRADOR)throw new IllegalStateException("Solo las preguntas en BORRADOR pueden modificarse.");validarParaRevision(q);repositorio.actualizar(q);notifyAllObserves();}
 public List<User> obtenerRevisoresDisponibles(){if(usuarios==null)return List.of();List<User> r=new ArrayList<>();for(User u:usuarios.listAll())if((u.getRole()==Role.REVISOR||u.getRole()==Role.DOCENTE)&&u.getState()==UserState.ACTIVO)r.add(u);return r;}
 /** Registra la asignación y devuelve advertencias por cada correo que no se pudo enviar. */
 public List<String> asignarRevisores(Question q,List<User> revisores){if(q==null||q.getEstado()!=EstadoPregunta.PENDIENTE_REVISION)throw new IllegalStateException("Solo pueden asignarse revisores a preguntas PENDIENTE DE REVISIÓN.");if(revisores==null||revisores.isEmpty())throw new IllegalArgumentException("Debe asignarse al menos un revisor.");repositorio.asignarRevisores(q.getId(),revisores.stream().map(User::getId).toList());List<String> advertencias=new ArrayList<>();for(User u:revisores){try{notifier.notificarAsignacion(q,u);}catch(RuntimeException e){advertencias.add(u.getName()+": "+e.getMessage());}}notifyAllObserves();return advertencias;}
 public List<User> obtenerRevisoresAsignados(String id){if(usuarios==null)return List.of();Set<Integer> ids=new HashSet<>(repositorio.obtenerRevisores(id));List<User> r=new ArrayList<>();for(User u:usuarios.listAll())if(ids.contains(u.getId()))r.add(u);return r;}
 public Map<EstadoPregunta,Integer> obtenerDistribucionPorEstado(){return repositorio.contarPorEstado();}
}
