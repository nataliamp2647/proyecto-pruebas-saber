package co.unicauca.lisw2t4g02.presentation;

import co.unicauca.lisw2t4g02.domain.Question.EstadoPregunta;
import co.unicauca.lisw2t4g02.domain.QuestionService;
import co.unicauca.lisw2t4g02.infra.Observer;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import java.util.Map;

/**
 * @class GUIObserver1
 * @brief Vista de Estadísticas: muestra el número de preguntas por cada
 * estado.
 *
 * Implementa {@link Observer} para reaccionar automáticamente cada vez
 * que el {@code QuestionService} (Subject) notifica un cambio de estado.
 * <p>
 * Consulta al servicio de dominio para obtener las estadísticas y solo
 * implementa el contrato transversal {@link Observer}.
 * @author Grupo LISW2 T4 G02
 */
public class GUIObserver1 extends JFrame implements Observer {

    /** Puerto de solo lectura para obtener la distribución por estado. */
    private final QuestionService servicio;
    private JTextArea areaEstadisticas;

    /**
     * @brief Construye la vista de estadísticas.
     * @param servicio servicio de dominio que proporciona la distribución de preguntas.
     */
    public GUIObserver1(QuestionService servicio) {
        this.servicio = servicio;
        setTitle("Vista de Estadísticas");
        initComponents();
        actualizarVista();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("Preguntas por estado", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        areaEstadisticas = new JTextArea();
        areaEstadisticas.setEditable(false);
        areaEstadisticas.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        areaEstadisticas.setMargin(new Insets(10, 10, 10, 10));

        add(titulo, BorderLayout.NORTH);
        add(new JScrollPane(areaEstadisticas), BorderLayout.CENTER);

        setSize(320, 260);
    }

    private void actualizarVista() {
        Map<EstadoPregunta, Integer> conteo = servicio.obtenerDistribucionPorEstado();

        int total = 0;
        for (int cantidad : conteo.values()) {
            total += cantidad;
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<EstadoPregunta, Integer> entrada : conteo.entrySet()) {
            sb.append(String.format("%-24s%d%n", entrada.getKey().getEtiqueta() + ":", entrada.getValue()));
        }
        sb.append(System.lineSeparator());
        sb.append(String.format("%-24s%d%n", "Total:", total));

        areaEstadisticas.setText(sb.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Object o) {
        actualizarVista();
    }
}
