package co.unicauca.lisw2t4g02.domain;

import co.unicauca.lisw2t4g02.domain.Question.EstadoPregunta;
import co.unicauca.lisw2t4g02.infra.Observer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class QuestionServiceTest {

    private FakeQuestionRepository repositorio;
    private QuestionService servicio;

    @BeforeEach
    void configurar() {
        repositorio = new FakeQuestionRepository();
        repositorio.agregar(new Question("P-001", "n1", "e1", "A", EstadoPregunta.BORRADOR));
        repositorio.agregar(new Question("P-002", "n2", "e2", "B", EstadoPregunta.PENDIENTE_REVISION));
        servicio = new QuestionService(repositorio);
    }

    @Test
    void listarPreguntas_delegaEnElRepositorioYDevuelveTodasLasPreguntas() {
        assertEquals(2, servicio.listarPreguntas().size());
    }

    @Test
    void cargarPregunta_delegaEnElRepositorioConElIdSolicitado() {
        Question pregunta = servicio.cargarPregunta("P-002");

        assertNotNull(pregunta);
        assertEquals("n2", pregunta.getNombre());
    }

    @Test
    void cargarPregunta_devuelveNullSiElIdNoExiste() {
        assertNull(servicio.cargarPregunta("NO-EXISTE"));
    }

    @Test
    void actualizarEstado_delegaLaActualizacionEnElRepositorio() {
        servicio.actualizarEstado("P-001", EstadoPregunta.ELIMINADA);

        assertEquals(1, repositorio.invocacionesActualizarEstado);
        assertEquals(EstadoPregunta.ELIMINADA, servicio.cargarPregunta("P-001").getEstado());
    }

    @Test
    void actualizarEstado_notificaATodosLosObservadoresRegistrados() {
        RegistroObserver observer1 = new RegistroObserver();
        RegistroObserver observer2 = new RegistroObserver();
        servicio.addObserver(observer1);
        servicio.addObserver(observer2);

        servicio.actualizarEstado("P-001", EstadoPregunta.ELIMINADA);

        assertEquals(1, observer1.notificaciones);
        assertEquals(1, observer2.notificaciones);
    }

    @Test
    void actualizarEstado_sinObservadoresRegistrados_noLanzaExcepcion() {
        assertDoesNotThrow(() -> servicio.actualizarEstado("P-001", EstadoPregunta.ELIMINADA));
    }

    @Test
    void obtenerDistribucionPorEstado_delegaEnElRepositorio() {
        Map<EstadoPregunta, Integer> distribucion = servicio.obtenerDistribucionPorEstado();

        assertEquals(1, distribucion.get(EstadoPregunta.BORRADOR));
        assertEquals(1, distribucion.get(EstadoPregunta.PENDIENTE_REVISION));
        assertEquals(0, distribucion.get(EstadoPregunta.ELIMINADA));
    }

    /** Observador de prueba que solo cuenta cuántas veces fue notificado. */
    private static class RegistroObserver implements Observer {
        int notificaciones = 0;

        @Override
        public void update(Object o) {
            notificaciones++;
        }
    }
}
