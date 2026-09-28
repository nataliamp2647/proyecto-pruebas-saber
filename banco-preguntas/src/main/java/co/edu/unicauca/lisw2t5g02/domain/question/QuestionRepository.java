package co.edu.unicauca.lisw2t5g02.domain.question;
import java.util.List;import java.util.Map;import co.edu.unicauca.lisw2t5g02.domain.question.Question.EstadoPregunta;
public interface QuestionRepository {
 List<Question> obtenerTodas(); Question obtenerPorId(String id); void guardar(Question p); void actualizarEstado(String id,EstadoPregunta e); Map<EstadoPregunta,Integer> contarPorEstado();
 default List<Question> obtenerPorAutor(int autorId, EstadoPregunta estado,int page,int pageSize){return obtenerTodas();}
 default int contarPorAutor(int autorId,EstadoPregunta estado){return obtenerPorAutor(autorId,estado,0,Integer.MAX_VALUE).size();}
 default List<Question> obtenerTodasFiltradas(EstadoPregunta estado,int page,int pageSize){
  return obtenerTodas().stream().filter(q->estado==null||q.getEstado()==estado).skip((long)page*pageSize).limit(pageSize).toList();}
 default int contarTodas(EstadoPregunta estado){return (int)obtenerTodas().stream().filter(q->estado==null||q.getEstado()==estado).count();}
 default void actualizar(Question p){throw new UnsupportedOperationException();}
 default void asignarRevisores(String id,List<Integer> revisores){throw new UnsupportedOperationException();}
 default List<Integer> obtenerRevisores(String id){return List.of();}
}
