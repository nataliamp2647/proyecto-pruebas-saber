package co.edu.unicauca.lisw2t5g02.presentation;

import co.edu.unicauca.lisw2t5g02.domain.question.Question;
import co.edu.unicauca.lisw2t5g02.microkernel.QuestionMicrokernel;
import co.edu.unicauca.lisw2t5g02.microkernel.QuestionPlugin;
import co.edu.unicauca.lisw2t5g02.microkernel.QuestionRequest;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * @class MicrokernelView
 * @brief Pantalla para generar preguntas nuevas a través del microkernel.
 *
 * Solo conoce {@link QuestionMicrokernel}: nunca instancia un plugin
 * directamente. Los plugins ya fueron cargados por reflexión desde
 * plugins.properties antes de abrir esta ventana.
 *
 * @author Grupo LISW2 T5 G02
 */
public class MicrokernelView extends JFrame {

    private final QuestionMicrokernel microkernel;
    private final Runnable alGenerarPregunta;

    private JComboBox<String> cbTipo;
    private JTextField txtNombre;
    private JTextArea txtEnunciado;
    private JTextField txtOpciones;
    private JTextField txtRespuestaCorrecta;
    private JTextArea txtResultado;

    /**
     * @param alGenerarPregunta callback que se ejecuta tras generar una
     *        pregunta, para que otras vistas se refresquen.
     */
    public MicrokernelView(QuestionMicrokernel microkernel, Runnable alGenerarPregunta) {
        super("Microkernel - Generar preguntas por plugin");
        this.microkernel = microkernel;
        this.alGenerarPregunta = alGenerarPregunta;
        construirUI();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(760, 640);
        setLocationRelativeTo(null);
    }

    private void construirUI() {
        setLayout(new BorderLayout(8, 8));

        JTextArea listaPlugins = new JTextArea(3, 40);
        listaPlugins.setEditable(false);
        StringBuilder sb = new StringBuilder();
        for (QuestionPlugin plugin : microkernel.listarPlugins()) {
            sb.append("• ").append(plugin.getName())
              .append("   (").append(plugin.getClass().getSimpleName()).append(")\n");
        }
        listaPlugins.setText(sb.toString());

        JPanel panelPlugins = new JPanel(new BorderLayout());
        panelPlugins.setBorder(BorderFactory.createTitledBorder(
                "Plugins registrados (cargados por reflexión desde plugins.properties)"));
        panelPlugins.add(new JScrollPane(listaPlugins), BorderLayout.CENTER);
        add(panelPlugins, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Datos de la pregunta"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbTipo = new JComboBox<>(new String[]{"MULTIPLE_CHOICE", "CASE", "MULTIMEDIA"});
        txtNombre = new JTextField(30);
        txtEnunciado = new JTextArea(4, 30);
        txtEnunciado.setLineWrap(true);
        txtEnunciado.setWrapStyleWord(true);
        txtOpciones = new JTextField(30);
        txtOpciones.setToolTipText("Separadas por coma. Obligatorias para MULTIPLE_CHOICE.");
        txtRespuestaCorrecta = new JTextField(30);
        txtRespuestaCorrecta.setToolTipText("Debe coincidir con el texto de una de las opciones.");

        int fila = 0;
        fila = agregarFila(form, gbc, fila, "Tipo:", cbTipo);
        fila = agregarFila(form, gbc, fila, "Nombre:", txtNombre);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        fila = agregarFila(form, gbc, fila, "Enunciado:", new JScrollPane(txtEnunciado));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        fila = agregarFila(form, gbc, fila, "Opciones (coma):", txtOpciones);
        fila = agregarFila(form, gbc, fila, "Respuesta correcta:", txtRespuestaCorrecta);

        JButton btnGenerar = new JButton("Generar y guardar pregunta");
        btnGenerar.addActionListener(e -> generarPregunta());
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2;
        form.add(btnGenerar, gbc);

        add(form, BorderLayout.CENTER);

        txtResultado = new JTextArea(7, 40);
        txtResultado.setEditable(false);
        txtResultado.setLineWrap(true);
        txtResultado.setWrapStyleWord(true);
        JPanel panelResultado = new JPanel(new BorderLayout());
        panelResultado.setBorder(BorderFactory.createTitledBorder("Resultado"));
        panelResultado.add(new JScrollPane(txtResultado), BorderLayout.CENTER);
        add(panelResultado, BorderLayout.SOUTH);
    }

    private int agregarFila(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, Component campo) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(campo, gbc);
        return fila + 1;
    }

    private void generarPregunta() {
        try {
            String tipo = (String) cbTipo.getSelectedItem();

            QuestionRequest request = new QuestionRequest();
            request.setNombre(txtNombre.getText().trim());
            request.setEnunciado(txtEnunciado.getText().trim());
            request.setTipo(tipo);
            request.setRespuestaCorrecta(txtRespuestaCorrecta.getText().trim());

            List<String> opciones = Arrays.stream(txtOpciones.getText().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
            request.setOpciones(opciones);

            Question pregunta = microkernel.generarPregunta(tipo, request);

            txtResultado.setText("Pregunta generada y guardada en el banco.\n\n"
                    + "Id: " + pregunta.getId() + "\n"
                    + "Nombre: " + pregunta.getNombre() + "\n"
                    + "Estado: " + pregunta.getEstado() + "\n"
                    + "Opciones: " + pregunta.getOpciones().size() + "\n"
                    + "Respuesta correcta: " + pregunta.getRespuestaCorrecta());

            if (alGenerarPregunta != null) {
                alGenerarPregunta.run();
            }
        } catch (Exception ex) {
            txtResultado.setText("Error: " + ex.getMessage());
        }
    }
}
