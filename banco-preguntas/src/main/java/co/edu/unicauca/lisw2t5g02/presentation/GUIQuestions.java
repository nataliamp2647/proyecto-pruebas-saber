package co.edu.unicauca.lisw2t5g02.presentation;

import co.edu.unicauca.lisw2t5g02.domain.question.Question;
import co.edu.unicauca.lisw2t5g02.domain.question.Question.EstadoPregunta;
import co.edu.unicauca.lisw2t5g02.domain.question.QuestionDistractors;
import co.edu.unicauca.lisw2t5g02.domain.question.QuestionService;
import co.edu.unicauca.lisw2t5g02.domain.user.Role;
import co.edu.unicauca.lisw2t5g02.domain.user.User;
import co.edu.unicauca.lisw2t5g02.infra.Observer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * HU-1.2 / HU-1.3: listado, filtros, paginación, consulta, edición y envío a revisión.
 * El autor ve solo sus preguntas; el administrador ve todas en modo solo lectura.
 */
public class GUIQuestions extends JFrame implements Observer {
    private final QuestionService service;
    private final QuestionController controller;
    private final User user;
    private final boolean readOnly;
    private JComboBox<EstadoPregunta> filtro;
    private DefaultListModel<Question> model;
    private JList<Question> list;
    private JLabel pageLabel, hint;
    private int page = 0;
    private final int pageSize = 5;
    private Question selected;
    private boolean editing, adjusting;
    private JTextField nombre, competencia, tema, subtema, nivel;
    private JTextArea contexto, pregunta, justificacion, bibliografia;
    private final JTextField[] opciones = new JTextField[5];
    private JComboBox<String> correcta;
    private JButton guardar, modificar, enviar, ver;

    public GUIQuestions(QuestionService s, User u) {
        super(u.getRole() == Role.ADMINISTRADOR ? "Banco Saber Pro - Consultar preguntas" : "Banco Saber Pro - Mis preguntas");
        service = s; controller = new QuestionController(s); user = u;
        readOnly = u.getRole() == Role.ADMINISTRADOR;
        build();
        refresh(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 760); setLocationRelativeTo(null);
        service.addObserver(this);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) { service.removeObserver(GUIQuestions.this); }
        });
    }

    /** Observer: recarga el listado cuando cambian las preguntas (si no se está editando). */
    @Override public void update(Object o) { SwingUtilities.invokeLater(() -> { if (!editing) refresh(false); }); }

    private void build() {
        setLayout(new BorderLayout(8, 8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtro = new JComboBox<>(new EstadoPregunta[]{null, EstadoPregunta.BORRADOR, EstadoPregunta.PENDIENTE_REVISION});
        filtro.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean f) {
                return super.getListCellRendererComponent(l, v == null ? "Todos" : v, i, sel, f);
            }
        });
        top.add(new JLabel("Filtrar estado:")); top.add(filtro);
        JButton filtrar = new JButton("FILTRAR");
        filtrar.addActionListener(e -> { page = 0; refresh(true); });
        top.add(filtrar);
        JButton recargar = new JButton("REFRESCAR");
        recargar.addActionListener(e -> refresh(false));
        top.add(recargar);
        if (!readOnly) {
            JButton nuevo = new JButton("NUEVA PREGUNTA");
            nuevo.addActionListener(e -> new CreateQuestionView(controller, () -> { page = 0; refresh(false); }, user).setVisible(true));
            top.add(nuevo);
        }
        add(top, BorderLayout.NORTH);

        JPanel left = new JPanel(new BorderLayout(5, 5));
        left.setBorder(BorderFactory.createTitledBorder(readOnly ? "Preguntas del banco" : "Preguntas creadas por el autor"));
        model = new DefaultListModel<>();
        list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean focus) {
                JLabel x = (JLabel) super.getListCellRendererComponent(l, v, i, sel, focus);
                if (v instanceof Question q) {
                    String autor = readOnly ? " (autor #" + q.getAutorId() + ")" : "";
                    x.setText("<html>" + q.getId() + " - " + q.getNombre() + autor + "<br><b>" + q.getEstado().getEtiqueta() + "</b></html>");
                    if (!sel) x.setForeground(colorOf(q.getEstado()));
                }
                return x;
            }
        });
        list.addListSelectionListener(e -> { if (!e.getValueIsAdjusting() && !adjusting) loadSelected(); });
        left.add(new JScrollPane(list), BorderLayout.CENTER);
        JPanel nav = new JPanel();
        JButton prev = new JButton("< Anterior"), next = new JButton("Siguiente >");
        pageLabel = new JLabel();
        prev.addActionListener(e -> { if (page > 0) { page--; refresh(false); } });
        next.addActionListener(e -> { if ((page + 1) * pageSize < count()) { page++; refresh(false); } });
        nav.add(prev); nav.add(pageLabel); nav.add(next);
        left.add(nav, BorderLayout.SOUTH);
        left.setPreferredSize(new Dimension(350, 600));
        add(left, BorderLayout.WEST);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Pregunta"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3, 4, 3, 4); g.fill = GridBagConstraints.HORIZONTAL; g.anchor = GridBagConstraints.NORTHWEST;
        nombre = new JTextField(); competencia = new JTextField(); tema = new JTextField(); subtema = new JTextField(); nivel = new JTextField();
        contexto = area(); pregunta = area(); justificacion = area(); bibliografia = area();
        for (int i = 0; i < 5; i++) opciones[i] = new JTextField();
        correcta = new JComboBox<>(new String[]{"A", "B", "C", "D", "E"});
        int r = 0;
        r = row(form, g, r, "Nombre", nombre);
        r = row(form, g, r, "Contexto", new JScrollPane(contexto));
        r = row(form, g, r, "Pregunta directa", new JScrollPane(pregunta));
        for (int i = 0; i < 5; i++) r = row(form, g, r, "Opción " + (char) ('A' + i), opciones[i]);
        r = row(form, g, r, "Respuesta correcta", correcta);
        r = row(form, g, r, "Justificación", new JScrollPane(justificacion));
        r = row(form, g, r, "Bibliografía", new JScrollPane(bibliografia));
        r = row(form, g, r, "Competencia", competencia);
        r = row(form, g, r, "Tema", tema);
        r = row(form, g, r, "Subtema", subtema);
        r = row(form, g, r, "Nivel de dificultad", nivel);

        hint = new JLabel(" ");
        hint.setFont(hint.getFont().deriveFont(Font.ITALIC));
        g.gridx = 0; g.gridy = r++; g.gridwidth = 2; form.add(hint, g);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        guardar = new JButton("GUARDAR MODIFICACIÓN"); modificar = new JButton("MODIFICAR");
        enviar = new JButton("ENVIAR A REVISIÓN"); ver = new JButton("VER COMPLETA");
        guardar.addActionListener(e -> saveEdit());
        modificar.addActionListener(e -> enableEdit());
        enviar.addActionListener(e -> sendReview());
        ver.addActionListener(e -> { if (selected != null) showFull(selected); });
        buttons.add(ver);
        if (!readOnly) { buttons.add(modificar); buttons.add(enviar); buttons.add(guardar); }
        g.gridx = 0; g.gridy = r; g.gridwidth = 2; form.add(buttons, g);
        add(new JScrollPane(form), BorderLayout.CENTER);
        setEdit(false);
    }

    private static Color colorOf(EstadoPregunta e) {
        return switch (e) {
            case BORRADOR -> new Color(180, 120, 0);
            case PENDIENTE_REVISION -> new Color(30, 110, 60);
            default -> Color.GRAY;
        };
    }

    private JTextArea area() { JTextArea a = new JTextArea(3, 30); a.setLineWrap(true); a.setWrapStyleWord(true); return a; }

    private int row(JPanel p, GridBagConstraints g, int r, String lab, Component c) {
        g.gridy = r; g.gridx = 0; g.gridwidth = 1; g.weightx = 0; p.add(new JLabel(lab + ":"), g);
        g.gridx = 1; g.weightx = 1; p.add(c, g); return r + 1;
    }

    private EstadoPregunta currentFilter() { return (EstadoPregunta) filtro.getSelectedItem(); }

    private int count() { return readOnly ? controller.contarTodas(currentFilter()) : controller.contarPreguntas(user.getId(), currentFilter()); }

    /** @param notifyEmpty mostrar aviso si el filtro no devuelve resultados (criterio 4 de HU-1.3). */
    public void refresh(boolean notifyEmpty) {
        String keepId = selected != null ? selected.getId() : null;
        int total = count();
        int lastPage = Math.max(0, (total + pageSize - 1) / pageSize - 1);
        if (page > lastPage) page = lastPage;
        adjusting = true;
        try {
            model.clear();
            var items = readOnly ? controller.listarTodasPaginado(currentFilter(), page, pageSize)
                    : controller.listarPreguntas(user.getId(), currentFilter(), page, pageSize);
            for (Question q : items) model.addElement(q);
            if (keepId != null)
                for (int i = 0; i < model.size(); i++)
                    if (model.get(i).getId().equals(keepId)) { list.setSelectedIndex(i); break; }
        } finally { adjusting = false; }
        pageLabel.setText("Página " + (page + 1) + " de " + (lastPage + 1));
        if (list.getSelectedValue() == null) { clearForm(); }
        if (notifyEmpty && total == 0)
            JOptionPane.showMessageDialog(this, "No se encontraron preguntas con el estado seleccionado.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadSelected() {
        Question s = list.getSelectedValue();
        if (s == null) { clearForm(); return; }
        selected = controller.cargarPregunta(s.getId());
        fill(selected);
        setEdit(false);
    }

    private void fill(Question q) {
        nombre.setText(nvl(q.getNombre())); contexto.setText(nvl(q.getContexto())); pregunta.setText(nvl(q.getPreguntaDirecta()));
        justificacion.setText(nvl(q.getJustificacion())); bibliografia.setText(nvl(q.getBibliografia()));
        competencia.setText(nvl(q.getCompetencia())); tema.setText(nvl(q.getTema())); subtema.setText(nvl(q.getSubtema()));
        nivel.setText(nvl(q.getNivelDificultad()));
        for (int i = 0; i < 5; i++) opciones[i].setText(i < q.getOpciones().size() ? q.getOpciones().get(i).getTexto() : "");
        int idx = 0;
        for (int i = 0; i < q.getOpciones().size() && i < 5; i++)
            if (q.getOpciones().get(i).getTexto().equals(q.getRespuestaCorrecta())) { idx = i; break; }
        correcta.setSelectedIndex(idx);
    }

    private String nvl(String x) { return x == null ? "" : x; }

    private void clearForm() {
        selected = null;
        nombre.setText(""); contexto.setText(""); pregunta.setText(""); justificacion.setText(""); bibliografia.setText("");
        competencia.setText(""); tema.setText(""); subtema.setText(""); nivel.setText("");
        for (JTextField x : opciones) x.setText("");
        correcta.setSelectedIndex(0);
        setEdit(false);
    }

    private void setEdit(boolean b) {
        editing = b && !readOnly;
        boolean e = editing;
        nombre.setEditable(e); contexto.setEditable(e); pregunta.setEditable(e); justificacion.setEditable(e);
        bibliografia.setEditable(e); competencia.setEditable(e); tema.setEditable(e); subtema.setEditable(e); nivel.setEditable(e);
        for (JTextField x : opciones) x.setEditable(e);
        correcta.setEnabled(e);
        guardar.setEnabled(e && selected != null);
        modificar.setEnabled(!readOnly && selected != null && !editing);
        enviar.setEnabled(!readOnly && selected != null && selected.getEstado() == EstadoPregunta.BORRADOR && !editing);
        ver.setEnabled(selected != null);
        if (selected == null) hint.setText("Seleccione una pregunta de la lista.");
        else if (readOnly) hint.setText("Modo consulta: el administrador no modifica preguntas.");
        else if (editing) hint.setText("Edite los campos y pulse GUARDAR MODIFICACIÓN.");
        else if (selected.getEstado() == EstadoPregunta.BORRADOR) hint.setText("Pulse MODIFICAR para editar el contenido o ENVIAR A REVISIÓN.");
        else hint.setText("Solo las preguntas en BORRADOR pueden modificarse.");
    }

    private void enableEdit() {
        if (selected == null) return;
        if (selected.getEstado() != EstadoPregunta.BORRADOR) {
            JOptionPane.showMessageDialog(this, "Solo las preguntas en BORRADOR pueden ser modificadas.", "Acción no permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        setEdit(true);
    }

    private Question fromForm() {
        Question q = selected;
        q.setNombre(nombre.getText().trim()); q.setContexto(contexto.getText().trim()); q.setPreguntaDirecta(pregunta.getText().trim());
        q.setJustificacion(justificacion.getText().trim()); q.setBibliografia(bibliografia.getText().trim());
        q.setCompetencia(competencia.getText().trim()); q.setTema(tema.getText().trim()); q.setSubtema(subtema.getText().trim());
        q.setNivelDificultad(nivel.getText().trim());
        q.setRespuestaCorrecta(opciones[((String) correcta.getSelectedItem()).charAt(0) - 'A'].getText().trim());
        q.getOpciones().clear();
        char l = 'A';
        for (JTextField x : opciones) q.agregarOpcion(new QuestionDistractors("" + l++, x.getText().trim()));
        return q;
    }

    private void saveEdit() {
        try {
            if (selected == null) return;
            Question q = fromForm();
            controller.actualizarPregunta(q);
            selected = controller.cargarPregunta(q.getId());
            fill(selected);
            setEdit(false);
            refresh(false);
            JOptionPane.showMessageDialog(this, "Modificación guardada y validada correctamente.");
        } catch (Exception e) {
            // Si la validación falla, se conserva la edición en curso para que el autor corrija.
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void sendReview() {
        try {
            if (selected == null) return;
            controller.enviarARevision(controller.cargarPregunta(selected.getId()));
            selected = controller.cargarPregunta(selected.getId());
            refresh(false);
            setEdit(false);
            JOptionPane.showMessageDialog(this, "La pregunta pasó a PENDIENTE DE REVISIÓN.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "No se puede enviar", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showFull(Question q) {
        StringBuilder s = new StringBuilder();
        s.append("Contexto:\n").append(nvl(q.getContexto())).append("\n\nPregunta:\n").append(q.getPreguntaDirecta()).append("\n\nOpciones:\n");
        for (QuestionDistractors o : q.getOpciones()) s.append(o).append("\n");
        s.append("\nRespuesta: ").append(q.getRespuestaCorrecta()).append("\n\nJustificación:\n").append(nvl(q.getJustificacion()))
                .append("\n\nBibliografía:\n").append(nvl(q.getBibliografia())).append("\n\nCompetencia: ").append(q.getCompetencia())
                .append("\nTema: ").append(q.getTema()).append("\nSubtema: ").append(q.getSubtema()).append("\nNivel: ").append(q.getNivelDificultad());
        JTextArea a = new JTextArea(s.toString(), 20, 70);
        a.setEditable(false); a.setLineWrap(true); a.setWrapStyleWord(true);
        JOptionPane.showMessageDialog(this, new JScrollPane(a), q.getNombre(), JOptionPane.INFORMATION_MESSAGE);
    }
}
