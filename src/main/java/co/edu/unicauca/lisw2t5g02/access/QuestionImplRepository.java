package co.edu.unicauca.lisw2t5g02.access;

import co.edu.unicauca.lisw2t5g02.domain.question.Question;
import co.edu.unicauca.lisw2t5g02.domain.question.Question.EstadoPregunta;
import co.edu.unicauca.lisw2t5g02.domain.question.QuestionDistractors;
import co.edu.unicauca.lisw2t5g02.domain.question.QuestionRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @class QuestionImplRepository
 * @brief Implementación del repositorio usando SQLite.
 *
 * Toda la persistencia queda encapsulada en la capa access. Al iniciar,
 * crea las tablas necesarias y agrega datos de ejemplo si la BD está vacía.
 *
 * @author Grupo LISW2 T4 G02
 */
public class QuestionImplRepository implements QuestionRepository {

    /** URL de la base de datos SQLite. */
    private static final String URL_BD = "jdbc:sqlite:preguntas.db";

    /**
     * @brief Constructor. Inicializa el esquema de SQLite.
     */
    public QuestionImplRepository() {
        inicializarBaseDeDatos();
    }

    private Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL_BD);
    }

    private void inicializarBaseDeDatos() {
        try (Connection con = obtenerConexion(); Statement st = con.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS pregunta ("
                    + "id TEXT PRIMARY KEY,"
                    + "nombre TEXT NOT NULL,"
                    + "enunciado TEXT NOT NULL,"
                    + "respuestaCorrecta TEXT NOT NULL,"
                    + "estado TEXT NOT NULL)");

            st.execute("CREATE TABLE IF NOT EXISTS opcion ("
                    + "idPregunta TEXT NOT NULL,"
                    + "letra TEXT NOT NULL,"
                    + "texto TEXT NOT NULL,"
                    + "FOREIGN KEY(idPregunta) REFERENCES pregunta(id))");

            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) AS total FROM pregunta")) {
                if (rs.next() && rs.getInt("total") == 0) {
                    sembrarDatosIniciales(con);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inicializando SQLite", e);
        }
    }

    @Override
    public void guardar(Question pregunta) {
        String sqlPregunta = "INSERT INTO pregunta (id, nombre, enunciado, respuestaCorrecta, estado) "
                + "VALUES (?, ?, ?, ?, ?)";
        String sqlOpcion = "INSERT INTO opcion (idPregunta, letra, texto) VALUES (?, ?, ?)";

        try (Connection con = obtenerConexion()) {
            try (PreparedStatement ps = con.prepareStatement(sqlPregunta)) {
                ps.setString(1, pregunta.getId());
                ps.setString(2, pregunta.getNombre());
                ps.setString(3, pregunta.getEnunciado());
                ps.setString(4, pregunta.getRespuestaCorrecta() == null ? "" : pregunta.getRespuestaCorrecta());
                ps.setString(5, pregunta.getEstado().getEtiqueta());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(sqlOpcion)) {
                for (QuestionDistractors opcion : pregunta.getOpciones()) {
                    ps.setString(1, pregunta.getId());
                    ps.setString(2, opcion.getLetra());
                    ps.setString(3, opcion.getTexto());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error guardando la pregunta " + pregunta.getId(), e);
        }
    }

    private void sembrarDatosIniciales(Connection con) throws SQLException {
        insertarPregunta(con, "P-001", "Lectura crítica - Argumentación",
                "Contexto: Un columnista afirma: \"Como el 90% de los accidentes de tránsito "
                + "ocurren cerca de casa, es más peligroso conducir cerca de casa que en carretera\". "
                + "Pregunta directa: ¿Cuál es la principal debilidad de este argumento?",
                "C", EstadoPregunta.BORRADOR,
                new String[][]{
                    {"A", "No cita la fuente estadística del dato"},
                    {"B", "Usa un porcentaje en lugar de un valor absoluto"},
                    {"C", "Confunde la frecuencia de un evento con su probabilidad, sin considerar que se conduce más tiempo cerca de casa"},
                    {"D", "Generaliza un caso particular a todos los conductores"}});
        insertarPregunta(con, "P-002", "Razonamiento cuantitativo - Proporciones",
                "Contexto: Un banco de preguntas tiene 240 preguntas, de las cuales el 35% "
                + "corresponde a Razonamiento Cuantitativo. Pregunta directa: ¿Cuántas preguntas "
                + "de ese banco corresponden a Razonamiento Cuantitativo?",
                "B", EstadoPregunta.BORRADOR,
                new String[][]{{"A", "72"}, {"B", "84"}, {"C", "96"}, {"D", "108"}});
        insertarPregunta(con, "P-003", "Competencias ciudadanas - Deliberación",
                "Contexto: En un cabildo estudiantil, un grupo propone eliminar el voto secreto "
                + "para 'agilizar' las decisiones. Pregunta directa: ¿Qué principio democrático se "
                + "vería más comprometido con esta propuesta?",
                "A", EstadoPregunta.PENDIENTE_REVISION,
                new String[][]{
                    {"A", "La libertad de voto, al exponer a presiones sociales sobre la decisión de cada persona"},
                    {"B", "La eficiencia administrativa del proceso electoral"},
                    {"C", "La representatividad numérica de las mayorías"},
                    {"D", "La duración total de la sesión del cabildo"}});
        insertarPregunta(con, "P-004", "Comunicación escrita - Cohesión textual",
                "Contexto: \"El proyecto se retrasó. ___ , el equipo decidió reducir el alcance de "
                + "la primera entrega.\" Pregunta directa: ¿Qué conector completa mejor la relación "
                + "lógica entre las dos oraciones?",
                "D", EstadoPregunta.ELIMINADA,
                new String[][]{{"A", "Sin embargo"}, {"B", "Es decir"}, {"C", "Por el contrario"}, {"D", "En consecuencia"}});
        insertarPregunta(con, "P-005", "Inglés - Reading comprehension",
                "Context: \"Although the module passed all unit tests, it failed during integration "
                + "because it relied on a database connection that was not available in the test "
                + "environment.\" Direct question: Why did the module fail during integration?",
                "B", EstadoPregunta.ELIMINADA,
                new String[][]{
                    {"A", "Because the unit tests were poorly written"},
                    {"B", "Because it depended on a resource that the test environment did not provide"},
                    {"C", "Because the integration environment had more tests than the unit environment"},
                    {"D", "Because the module was never tested before integration"}});
        insertarPregunta(con, "P-006", "Diseño de software - Arquitectura en capas",
                "Contexto: En una aplicación monolítica en capas, la capa de acceso a datos "
                + "necesita informar a la capa de dominio que ocurrió un error de conexión. "
                + "Pregunta directa: ¿Cuál es la forma correcta de hacerlo sin violar la dirección "
                + "de las dependencias entre capas?",
                "A", EstadoPregunta.PENDIENTE_REVISION,
                new String[][]{
                    {"A", "Lanzando una excepción que la capa de dominio captura, sin que acceso a datos conozca al dominio"},
                    {"B", "Haciendo que acceso a datos importe directamente las clases del dominio para notificarlas"},
                    {"C", "Guardando el error en una variable global accesible desde cualquier capa"},
                    {"D", "Deteniendo la aplicación completa ante cualquier error de conexión"}});
        insertarPregunta(con, "P-007", "Diseño de software - Patrón Observer",
                "Contexto: Un sistema de banco de preguntas debe actualizar dos vistas distintas "
                + "(estadísticas y gráfico) cada vez que cambia el estado de una pregunta, sin que "
                + "el servicio de dominio conozca las clases concretas de esas vistas. Pregunta "
                + "directa: ¿Qué patrón de diseño resuelve mejor este requisito?",
                "C", EstadoPregunta.BORRADOR,
                new String[][]{
                    {"A", "Singleton"}, {"B", "Factory Method"},
                    {"C", "Observer, porque desacopla al sujeto de los observadores mediante una interfaz común"},
                    {"D", "Adapter"}});
        insertarPregunta(con, "P-008", "Razonamiento cuantitativo - Interpretación de datos",
                "Contexto: De 500 estudiantes que presentaron un simulacro, 300 aprobaron "
                + "Lectura Crítica y 260 aprobaron Razonamiento Cuantitativo; 180 aprobaron ambas "
                + "áreas. Pregunta directa: ¿Cuántos estudiantes no aprobaron ninguna de las dos áreas?",
                "B", EstadoPregunta.ELIMINADA,
                new String[][]{{"A", "80"}, {"B", "120"}, {"C", "140"}, {"D", "160"}});
        insertarPregunta(con, "P-009", "Diseño de software - Principios SOLID",
                "Contexto: Una clase QuestionService recibe la interfaz QuestionRepository por "
                + "su constructor, en lugar de instanciar directamente QuestionImplRepository. "
                + "Pregunta directa: ¿Qué principio SOLID se está aplicando principalmente?",
                "D", EstadoPregunta.PENDIENTE_REVISION,
                new String[][]{
                    {"A", "Principio de responsabilidad única"}, {"B", "Principio de segregación de interfaces"},
                    {"C", "Principio de sustitución de Liskov"},
                    {"D", "Principio de inversión de dependencias, porque el servicio depende de una abstracción y no de una implementación concreta"}});
    }

    private void insertarPregunta(Connection con, String id, String nombre, String enunciado,
            String respuestaCorrecta, EstadoPregunta estado, String[][] opciones) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO pregunta (id,nombre,enunciado,respuestaCorrecta,estado) VALUES (?,?,?,?,?)")) {
            ps.setString(1, id);
            ps.setString(2, nombre);
            ps.setString(3, enunciado);
            ps.setString(4, respuestaCorrecta);
            ps.setString(5, estado.getEtiqueta());
            ps.executeUpdate();
        }
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO opcion (idPregunta,letra,texto) VALUES (?,?,?)")) {
            for (String[] opcion : opciones) {
                ps.setString(1, id);
                ps.setString(2, opcion[0]);
                ps.setString(3, opcion[1]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    @Override
    public List<Question> obtenerTodas() {
        List<Question> preguntas = new ArrayList<>();
        String sql = "SELECT id,nombre,enunciado,respuestaCorrecta,estado FROM pregunta ORDER BY id";
        try (Connection con = obtenerConexion(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                preguntas.add(mapearPregunta(con, rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error consultando las preguntas", e);
        }
        return preguntas;
    }

    @Override
    public Question obtenerPorId(String id) {
        String sql = "SELECT id,nombre,enunciado,respuestaCorrecta,estado FROM pregunta WHERE id=?";
        try (Connection con = obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearPregunta(con, rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error consultando la pregunta " + id, e);
        }
        return null;
    }

    private Question mapearPregunta(Connection con, ResultSet rs) throws SQLException {
        Question pregunta = new Question(rs.getString("id"), rs.getString("nombre"),
                rs.getString("enunciado"), rs.getString("respuestaCorrecta"),
                EstadoPregunta.fromEtiqueta(rs.getString("estado")));
        String sql = "SELECT letra,texto FROM opcion WHERE idPregunta=? ORDER BY letra";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pregunta.getId());
            try (ResultSet opciones = ps.executeQuery()) {
                while (opciones.next()) {
                    pregunta.agregarOpcion(new QuestionDistractors(
                            opciones.getString("letra"), opciones.getString("texto")));
                }
            }
        }
        return pregunta;
    }

    @Override
    public void actualizarEstado(String id, EstadoPregunta nuevoEstado) {
        String sql = "UPDATE pregunta SET estado=? WHERE id=?";
        try (Connection con = obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado.getEtiqueta());
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando el estado de " + id, e);
        }
    }

    @Override
    public Map<EstadoPregunta, Integer> contarPorEstado() {
        Map<EstadoPregunta, Integer> conteo = new LinkedHashMap<>();
        for (EstadoPregunta estado : EstadoPregunta.values()) conteo.put(estado, 0);

        String sql = "SELECT estado,COUNT(*) AS total FROM pregunta GROUP BY estado";
        try (Connection con = obtenerConexion(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                conteo.put(EstadoPregunta.fromEtiqueta(rs.getString("estado")), rs.getInt("total"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error contando preguntas por estado", e);
        }
        return conteo;
    }
}
