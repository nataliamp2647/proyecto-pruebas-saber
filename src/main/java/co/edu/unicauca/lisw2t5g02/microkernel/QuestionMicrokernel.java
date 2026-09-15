package co.edu.unicauca.lisw2t5g02.microkernel;

import co.edu.unicauca.lisw2t5g02.domain.question.Question;
import co.edu.unicauca.lisw2t5g02.domain.question.QuestionRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @class QuestionMicrokernel
 * @brief Núcleo del sistema (patrón Microkernel).
 *
 * Responsabilidades exigidas por el taller:
 * <ul>
 *   <li>Almacenar el banco de preguntas (Map en memoria + persistencia).</li>
 *   <li>Registrar plugins leyendo {@code plugins.properties}.</li>
 *   <li>Ejecutar plugins.</li>
 *   <li>Gestionar el ciclo de vida de los plugins.</li>
 * </ul>
 *
 * <b>Punto clave:</b> esta clase nunca hace {@code new XxxPlugin()} ni
 * importa ninguna clase de plugin concreta. Solo conoce la interfaz
 * {@link QuestionPlugin} y el nombre de clase que lee del archivo de
 * propiedades, instanciándolo por <b>reflexión</b>. Por eso agregar un
 * tipo de pregunta nuevo no obliga a tocar el núcleo.
 *
 * Se le inyecta opcionalmente un {@link QuestionRepository} para que las
 * preguntas generadas queden persistidas en la misma base de datos que
 * usa el resto de la aplicación (DIP: depende de la abstracción, no de
 * la implementación SQLite).
 *
 * @author Grupo LISW2 T5 G02
 */
public class QuestionMicrokernel {

    /** Banco de preguntas en memoria, indexado por id. */
    private final Map<String, Question> questions = new LinkedHashMap<>();

    /** Plugins registrados, indexados por su identificador del .properties. */
    private final Map<String, QuestionPlugin> plugins = new LinkedHashMap<>();

    /** Repositorio para persistir lo generado. Puede ser null (modo solo memoria). */
    private final QuestionRepository repositorio;

    public QuestionMicrokernel() {
        this(null);
    }

    public QuestionMicrokernel(QuestionRepository repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * @brief Registra plugins leyendo un archivo .properties del classpath.
     *
     * Formato de cada línea: {@code identificador=paquete.completo.NombreClase}.
     * Cada clase se instancia por reflexión.
     *
     * @param rutaClasspath ruta del archivo dentro del classpath.
     */
    public void registrarPluginsDesdeArchivo(String rutaClasspath)
            throws IOException, ReflectiveOperationException {
        Properties propiedades = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(rutaClasspath)) {
            if (in == null) {
                throw new IOException("No se encontró el archivo de plugins: " + rutaClasspath);
            }
            propiedades.load(in);
        }
        for (String identificador : propiedades.stringPropertyNames()) {
            registrarPluginPorReflexion(identificador, propiedades.getProperty(identificador));
        }
    }

    /**
     * @brief Instancia y registra un plugin por reflexión.
     * @param identificador clave con la que queda registrado.
     * @param nombreClase nombre completo de la clase a instanciar.
     */
    public void registrarPluginPorReflexion(String identificador, String nombreClase)
            throws ReflectiveOperationException {
        Class<?> clase = Class.forName(nombreClase);
        Object instancia = clase.getDeclaredConstructor().newInstance();

        if (!(instancia instanceof QuestionPlugin)) {
            throw new IllegalArgumentException(
                    "La clase " + nombreClase + " no implementa QuestionPlugin.");
        }
        plugins.put(identificador, (QuestionPlugin) instancia);
    }

    /** Registro directo, sin reflexión (útil para pruebas con dobles). */
    public void registrarPlugin(String identificador, QuestionPlugin plugin) {
        plugins.put(identificador, plugin);
    }

    /** Ciclo de vida: da de baja un plugin registrado. */
    public void quitarPlugin(String identificador) {
        plugins.remove(identificador);
    }

    /** @return los plugins actualmente registrados. */
    public List<QuestionPlugin> listarPlugins() {
        return List.copyOf(plugins.values());
    }

    /**
     * @brief Ejecuta el plugin que soporte el tipo pedido, guarda el
     * resultado en el banco y lo persiste si hay repositorio.
     *
     * @throws IllegalStateException si ningún plugin soporta ese tipo.
     */
    public Question generarPregunta(String tipo, QuestionRequest request) {
        QuestionPlugin plugin = buscarPluginPara(tipo);
        if (plugin == null) {
            throw new IllegalStateException("No hay ningún plugin registrado que soporte el tipo: " + tipo);
        }

        Question pregunta = plugin.generate(request);
        questions.put(pregunta.getId(), pregunta);

        if (repositorio != null) {
            repositorio.guardar(pregunta);
        }
        return pregunta;
    }

    private QuestionPlugin buscarPluginPara(String tipo) {
        for (QuestionPlugin plugin : plugins.values()) {
            if (plugin.supports(tipo)) {
                return plugin;
            }
        }
        return null;
    }

    /** @return banco de preguntas generadas en esta sesión. */
    public Map<String, Question> getQuestions() {
        return questions;
    }
}
