package co.edu.unicauca.lisw2t5g02.microkernel;

import co.edu.unicauca.lisw2t5g02.domain.question.Question;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del núcleo: registro por reflexión, ejecución de plugins,
 * ciclo de vida y persistencia delegada al repositorio.
 */
class QuestionMicrokernelTest {

    @Test
    void registrarPluginPorReflexionInstanciaLaClaseIndicada() throws ReflectiveOperationException {
        QuestionMicrokernel kernel = new QuestionMicrokernel();

        kernel.registrarPluginPorReflexion("fake", "co.edu.unicauca.lisw2t5g02.microkernel.FakeQuestionPlugin");

        assertEquals(1, kernel.listarPlugins().size());
        assertEquals("fake", kernel.listarPlugins().get(0).getName());
    }

    @Test
    void registrarPluginPorReflexionRechazaClaseQueNoImplementaQuestionPlugin() {
        QuestionMicrokernel kernel = new QuestionMicrokernel();

        assertThrows(IllegalArgumentException.class, () ->
                kernel.registrarPluginPorReflexion("malo", "co.edu.unicauca.lisw2t5g02.microkernel.NoEsUnPlugin"));
    }

    @Test
    void registrarPluginPorReflexionFallaSiLaClaseNoExiste() {
        QuestionMicrokernel kernel = new QuestionMicrokernel();

        assertThrows(ReflectiveOperationException.class, () ->
                kernel.registrarPluginPorReflexion("x", "co.edu.unicauca.lisw2t5g02.microkernel.ClaseInexistente"));
    }

    @Test
    void registrarPluginsDesdeArchivoCargaLosTresPluginsDelProyecto() throws Exception {
        QuestionMicrokernel kernel = new QuestionMicrokernel();

        kernel.registrarPluginsDesdeArchivo("plugins.properties");

        assertEquals(3, kernel.listarPlugins().size());
    }

    @Test
    void registrarPluginsDesdeArchivoFallaSiElArchivoNoExiste() {
        QuestionMicrokernel kernel = new QuestionMicrokernel();

        assertThrows(java.io.IOException.class, () ->
                kernel.registrarPluginsDesdeArchivo("no-existe.properties"));
    }

    @Test
    void generarPreguntaDelegaEnElPluginQueSoportaElTipo() {
        QuestionMicrokernel kernel = new QuestionMicrokernel();
        kernel.registrarPlugin("fake", new FakeQuestionPlugin());

        Question pregunta = kernel.generarPregunta("FAKE", new QuestionRequest("n", "e", "FAKE"));

        assertEquals("fake-id", pregunta.getId());
    }

    @Test
    void generarPreguntaFallaSiNingunPluginSoportaElTipo() {
        QuestionMicrokernel kernel = new QuestionMicrokernel();
        kernel.registrarPlugin("fake", new FakeQuestionPlugin());

        assertThrows(IllegalStateException.class, () ->
                kernel.generarPregunta("OTRO_TIPO", new QuestionRequest("n", "e", "OTRO_TIPO")));
    }

    @Test
    void generarPreguntaGuardaLaPreguntaEnElBancoEnMemoria() {
        QuestionMicrokernel kernel = new QuestionMicrokernel();
        kernel.registrarPlugin("fake", new FakeQuestionPlugin());

        kernel.generarPregunta("FAKE", new QuestionRequest("n", "e", "FAKE"));

        assertEquals(1, kernel.getQuestions().size());
    }

    @Test
    void generarPreguntaPersisteEnElRepositorioCuandoSeLeInyectaUno() {
        FakeQuestionRepository repositorio = new FakeQuestionRepository();
        QuestionMicrokernel kernel = new QuestionMicrokernel(repositorio);
        kernel.registrarPlugin("fake", new FakeQuestionPlugin());

        kernel.generarPregunta("FAKE", new QuestionRequest("n", "e", "FAKE"));

        assertEquals(1, repositorio.invocacionesGuardar);
        assertNotNull(repositorio.obtenerPorId("fake-id"));
    }

    @Test
    void quitarPluginLoEliminaDelRegistro() {
        QuestionMicrokernel kernel = new QuestionMicrokernel();
        kernel.registrarPlugin("fake", new FakeQuestionPlugin());

        kernel.quitarPlugin("fake");

        assertTrue(kernel.listarPlugins().isEmpty());
    }

    @Test
    void listarPluginsDevuelveUnaCopiaInmutable() {
        QuestionMicrokernel kernel = new QuestionMicrokernel();
        kernel.registrarPlugin("fake", new FakeQuestionPlugin());

        List<QuestionPlugin> lista = kernel.listarPlugins();

        assertThrows(UnsupportedOperationException.class, () -> lista.add(new FakeQuestionPlugin()));
    }
}
