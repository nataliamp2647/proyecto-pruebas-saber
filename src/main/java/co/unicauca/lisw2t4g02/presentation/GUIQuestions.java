package co.unicauca.lisw2t4g02.presentation;

import co.unicauca.lisw2t4g02.domain.Question.EstadoPregunta;
import co.unicauca.lisw2t4g02.domain.QuestionService;
import co.unicauca.lisw2t4g02.domain.Question;
import co.unicauca.lisw2t4g02.domain.QuestionDistractors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

/**
 * @class GUIQuestions
 * @brief Ventana principal: "Gestión de preguntas".
 *
 * Combina el panel de "Seleccionar pregunta" y el "Formulario de
 * pregunta" descritos en el diagrama. Al actualizar el estado de una
 * pregunta, el {@code QuestionService} (que actúa como Subject) notifica
 * automáticamente a las vistas observadoras: {@link GUIObserver1}
 * (estadísticas) y {@link GUIObserver2} (gráfico de pastel).
 * <p>
 * <b>Responsabilidad:</b> construye y gestiona los componentes Swing y delega
 * las operaciones de preguntas al {@link QuestionService}.
 * @author Grupo LISW2 T4 G02
 */
public class GUIQuestions extends JFrame {

    /** Servicio de dominio utilizado por la ventana. */
    private final QuestionService servicio;

    private JComboBox<Question> cbPreguntas;
    private JButton btnCargar;

    private JLabel lblId;
    private JLabel lblNombre;
    private JTextArea txtPregunta;
    private JTextArea txtOpciones;
    private JLabel lblRespuestaCorrecta;
    private JLabel lblEstadoActual;
    private JComboBox<EstadoPregunta> cbNuevoEstado;
    private JButton btnActualizar;

    private Question preguntaCargada;

    /**
     * @brief Construye la ventana principal.
     * @param servicio servicio de dominio utilizado por la interfaz.
     */
    public GUIQuestions(QuestionService servicio) {
        this.servicio = servicio;
        setTitle("Gestión de Preguntas");
        initComponents();
        cargarComboPreguntas();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ---- Panel "Seleccionar pregunta" ----
        // GridBagLayout en vez de FlowLayout: así el botón SIEMPRE queda en la
        // misma fila que el comboBox, sin importar el ancho de la ventana
        // (FlowLayout envuelve a la siguiente línea cuando no hay espacio).
        JPanel panelSeleccion = new JPanel(new GridBagLayout());
        panelSeleccion.setBorder(BorderFactory.createTitledBorder("Seleccionar pregunta"));

        cbPreguntas = new JComboBox<>();
        btnCargar = new JButton("Cargar pregunta");
        btnCargar.addActionListener(e -> cargarPreguntaSeleccionada());

        GridBagConstraints gbcSeleccion = new GridBagConstraints();
        gbcSeleccion.insets = new Insets(4, 4, 4, 4);
        gbcSeleccion.anchor = GridBagConstraints.WEST;

        gbcSeleccion.gridx = 0;
        gbcSeleccion.gridy = 0;
        gbcSeleccion.weightx = 0;
        panelSeleccion.add(new JLabel("Pregunta:"), gbcSeleccion);

        gbcSeleccion.gridx = 1;
        gbcSeleccion.weightx = 1;
        gbcSeleccion.fill = GridBagConstraints.HORIZONTAL;
        panelSeleccion.add(cbPreguntas, gbcSeleccion);

        gbcSeleccion.gridx = 2;
        gbcSeleccion.weightx = 0;
        gbcSeleccion.fill = GridBagConstraints.NONE;
        panelSeleccion.add(btnCargar, gbcSeleccion);

        // ---- Panel "Formulario de pregunta" ----
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Formulario de pregunta"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblId = new JLabel("-");
        lblNombre = new JLabel("-");
        // Áreas más grandes y con fuente más grande: las originales (2 y 4
        // filas, fuente por defecto) quedaban ilegibles.
        // Más anchas que altas (rectángulo horizontal): así se lee más texto
        // por línea antes de que haga wrap, en vez de un cuadro casi cuadrado.
        txtPregunta = crearAreaSoloLectura(3, 60);
        txtOpciones = crearAreaSoloLectura(6, 60);
        lblRespuestaCorrecta = new JLabel("-");
        lblEstadoActual = new JLabel("-");
        lblEstadoActual.setFont(lblEstadoActual.getFont().deriveFont(Font.BOLD));
        cbNuevoEstado = new JComboBox<>(EstadoPregunta.values());
        btnActualizar = new JButton("Actualizar estado");
        btnActualizar.addActionListener(e -> actualizarEstado());

        int fila = 0;
        fila = agregarFila(panelFormulario, gbc, fila, "Id:", lblId);
        fila = agregarFila(panelFormulario, gbc, fila, "Nombre:", lblNombre);

        // Estas dos filas sí deben crecer: se les da peso vertical (weighty)
        // y fill=BOTH para que absorban el espacio extra de la ventana en
        // vez de que GridBagLayout lo deje como relleno en blanco arriba/abajo
        // (que es justo lo que se veía antes: cajas aplastadas y un hueco
        // vacío sobre "Id:").
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.35;
        fila = agregarFila(panelFormulario, gbc, fila, "Pregunta:", crearScrollConAltura(txtPregunta, 70));

        gbc.weighty = 0.65;
        fila = agregarFila(panelFormulario, gbc, fila, "Opciones:", crearScrollConAltura(txtOpciones, 140));

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        fila = agregarFila(panelFormulario, gbc, fila, "Respuesta correcta:", lblRespuestaCorrecta);
        fila = agregarFila(panelFormulario, gbc, fila, "Estado actual:", lblEstadoActual);
        fila = agregarFila(panelFormulario, gbc, fila, "Nuevo estado:", cbNuevoEstado);

        gbc.gridx = 1;
        gbc.gridy = fila;
        gbc.anchor = GridBagConstraints.EAST;
        panelFormulario.add(btnActualizar, gbc);

        add(panelSeleccion, BorderLayout.NORTH);
        add(panelFormulario, BorderLayout.CENTER);

        habilitarFormulario(false);
        setSize(780, 600);
        setMinimumSize(new java.awt.Dimension(700, 540));
    }

    private JTextArea crearAreaSoloLectura(int filas, int columnas) {
        JTextArea area = new JTextArea(filas, columnas);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(area.getFont().deriveFont(14f));
        return area;
    }

    /** Envuelve un área en JScrollPane garantizando una altura mínima visible. */
    private JScrollPane crearScrollConAltura(JTextArea area, int alturaMinima) {
        JScrollPane scroll = new JScrollPane(area);
        scroll.setMinimumSize(new java.awt.Dimension(200, alturaMinima));
        return scroll;
    }

    private int agregarFila(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, Component componente) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(componente, gbc);
        return fila + 1;
    }

    private void cargarComboPreguntas() {
        List<Question> preguntas = servicio.listarPreguntas();
        cbPreguntas.removeAllItems();
        for (Question pregunta : preguntas) {
            cbPreguntas.addItem(pregunta);
        }
    }

    private void cargarPreguntaSeleccionada() {
        Question seleccionada = (Question) cbPreguntas.getSelectedItem();
        if (seleccionada == null) {
            mostrarAdvertencia("Seleccione una pregunta de la lista.", "Aviso");
            return;
        }
        preguntaCargada = servicio.cargarPregunta(seleccionada.getId());
        mostrarPregunta(preguntaCargada);
    }

    private void mostrarPregunta(Question pregunta) {
        lblId.setText(pregunta.getId());
        lblNombre.setText(pregunta.getNombre());
        txtPregunta.setText(pregunta.getEnunciado());

        StringBuilder sb = new StringBuilder();
        for (QuestionDistractors opcion : pregunta.getOpciones()) {
            sb.append(opcion.toString()).append(System.lineSeparator());
        }
        txtOpciones.setText(sb.toString());

        lblRespuestaCorrecta.setText(pregunta.getRespuestaCorrecta());
        lblEstadoActual.setText(pregunta.getEstado().getEtiqueta());
        cbNuevoEstado.setSelectedItem(pregunta.getEstado());

        habilitarFormulario(true);
    }

    private void habilitarFormulario(boolean habilitado) {
        cbNuevoEstado.setEnabled(habilitado);
        btnActualizar.setEnabled(habilitado);
    }

    private void actualizarEstado() {
        if (preguntaCargada == null) {
            mostrarAdvertencia("Primero cargue una pregunta.", "Aviso");
            return;
        }

        EstadoPregunta nuevoEstado = (EstadoPregunta) cbNuevoEstado.getSelectedItem();
        servicio.actualizarEstado(preguntaCargada.getId(), nuevoEstado);

        preguntaCargada = servicio.cargarPregunta(preguntaCargada.getId());
        lblEstadoActual.setText(preguntaCargada.getEstado().getEtiqueta());

        mostrarExito("Estado actualizado correctamente.", "Éxito");
    }

    private void mostrarAdvertencia(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarExito(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }
}