package co.edu.unicauca.lisw2t5g02.presentation;

import co.edu.unicauca.lisw2t5g02.domain.question.Question.EstadoPregunta;
import co.edu.unicauca.lisw2t5g02.domain.question.QuestionService;
import co.edu.unicauca.lisw2t5g02.infra.Observer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @class GUIObserver2
 * @brief Vista Gráfica: dibuja un diagrama de pastel con la distribución
 * de preguntas por estado (Borrador, Pendiente de revisión, Eliminada).
 *
 * Implementa {@link Observer} para redibujarse automáticamente cada vez
 * que el {@code QuestionService} (Subject) notifica un cambio de estado.
 * <p>
 * La vista consulta al servicio de dominio y se encarga exclusivamente del
 * renderizado del gráfico y de reaccionar a las notificaciones del Observer.
 *
 * @author Grupo LISW2 T4 G02
 */
public class GUIObserver2 extends JFrame implements Observer {

    /** Puerto de solo lectura para obtener la distribución por estado. */
    private final QuestionService servicio;
    private PanelGraficoPastel panelGrafico;

    /**
     * @brief Construye la vista gráfica.
     * @param servicio servicio de dominio que proporciona la distribución de preguntas.
     */
    public GUIObserver2(QuestionService servicio) {
        this.servicio = servicio;
        setTitle("Vista Gráfica");
        initComponents();
        actualizarVista();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("Distribución de preguntas", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));

        panelGrafico = new PanelGraficoPastel();

        add(titulo, BorderLayout.NORTH);
        add(panelGrafico, BorderLayout.CENTER);

        setSize(360, 380);
    }

    private void actualizarVista() {
        panelGrafico.setDatos(servicio.obtenerDistribucionPorEstado());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Object o) {
        actualizarVista();
    }

    /**
     * @class PanelGraficoPastel
     * @brief Panel interno encargado de dibujar el pastel (pie chart)
     * usando Graphics2D, a partir de la distribución de preguntas por
     * estado.
     *
     * Su única responsabilidad (SRP) es el renderizado gráfico; no
     * conoce cómo se obtienen los datos ni el patrón Observer.
     */
    private static class PanelGraficoPastel extends JPanel {

        private static final Color[] COLORES = {
            new Color(66, 133, 244),
            new Color(251, 188, 5),
            new Color(234, 67, 53),
            new Color(52, 168, 83)
        };

        private Map<EstadoPregunta, Integer> datos = new LinkedHashMap<>();

        /**
         * @brief Actualiza los datos a graficar y solicita el repintado.
         * @param datos distribución de preguntas por estado.
         */
        void setDatos(Map<EstadoPregunta, Integer> datos) {
            this.datos = datos;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int total = 0;
            for (int cantidad : datos.values()) {
                total += cantidad;
            }

            int diametro = Math.min(getWidth() - 40, getHeight() - 130);
            diametro = Math.max(diametro, 60);
            int x = (getWidth() - diametro) / 2;
            int y = 15;

            if (total == 0) {
                g2.drawString("Sin datos para graficar", x, y + diametro / 2);
                return;
            }

            double anguloInicio = 0;
            int indiceColor = 0;
            for (Map.Entry<EstadoPregunta, Integer> entrada : datos.entrySet()) {
                double proporcion = entrada.getValue() / (double) total;
                double anguloExtent = proporcion * 360.0;
                g2.setColor(COLORES[indiceColor % COLORES.length]);
                g2.fillArc(x, y, diametro, diametro, (int) Math.round(anguloInicio), (int) Math.round(anguloExtent));
                anguloInicio += anguloExtent;
                indiceColor++;
            }

            g2.setColor(Color.DARK_GRAY);
            g2.drawOval(x, y, diametro, diametro);

            // Leyenda con porcentajes
            int leyendaY = y + diametro + 20;
            indiceColor = 0;
            for (Map.Entry<EstadoPregunta, Integer> entrada : datos.entrySet()) {
                double porcentaje = entrada.getValue() * 100.0 / total;
                g2.setColor(COLORES[indiceColor % COLORES.length]);
                g2.fillRect(15, leyendaY, 12, 12);
                g2.setColor(Color.BLACK);
                g2.drawString(String.format("%s: %.0f%%", entrada.getKey().getEtiqueta(), porcentaje), 32, leyendaY + 11);
                leyendaY += 18;
                indiceColor++;
            }
        }
    }
}
