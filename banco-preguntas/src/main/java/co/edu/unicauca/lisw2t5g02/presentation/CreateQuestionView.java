package co.edu.unicauca.lisw2t5g02.presentation;

import co.edu.unicauca.lisw2t5g02.domain.user.User;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Vista Swing para crear una pregunta (HU-1.1). Toda la lógica pasa por el controlador MVC. */
public final class CreateQuestionView extends JFrame {
    private static final Color ERROR_BG = new Color(255, 220, 220);
    private final QuestionController controller;
    private final Runnable afterSave;
    private final User author;
    private final JTextField name = new JTextField(), competency = new JTextField(), topic = new JTextField(),
            subtopic = new JTextField(), difficulty = new JTextField();
    private final JTextArea context = area(), prompt = area(), justification = area(), bibliography = area();
    private final JTextField[] options = {new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField()};
    private final JComboBox<String> correct = new JComboBox<>(new String[]{"A", "B", "C", "D", "E"});
    private final JLabel message = new JLabel(" ");

    public CreateQuestionView(QuestionController controller, Runnable afterSave, User author) {
        super("HU-1.1 - Crear pregunta");
        this.controller = controller; this.afterSave = afterSave; this.author = author;
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6); g.fill = GridBagConstraints.HORIZONTAL; g.anchor = GridBagConstraints.NORTHWEST;
        int row = 0;
        row = add(form, g, row, "Nombre", name);
        row = add(form, g, row, "Contexto", new JScrollPane(context));
        row = add(form, g, row, "Pregunta directa", new JScrollPane(prompt));
        for (int i = 0; i < 5; i++) row = add(form, g, row, "Opción " + (char) ('A' + i), options[i]);
        row = add(form, g, row, "Respuesta correcta", correct);
        row = add(form, g, row, "Justificación", new JScrollPane(justification));
        row = add(form, g, row, "Bibliografía", new JScrollPane(bibliography));
        row = add(form, g, row, "Competencia", competency);
        row = add(form, g, row, "Tema", topic);
        row = add(form, g, row, "Subtema", subtopic);
        row = add(form, g, row, "Nivel de dificultad", difficulty);
        JButton save = new JButton("GUARDAR COMO BORRADOR");
        save.addActionListener(e -> save());
        g.gridx = 0; g.gridy = row; g.gridwidth = 2; form.add(save, g);
        message.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        setLayout(new BorderLayout());
        add(new JScrollPane(form), BorderLayout.CENTER);
        add(message, BorderLayout.SOUTH);
        setSize(900, 700); setLocationRelativeTo(null); setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private static JTextArea area() {
        JTextArea a = new JTextArea(3, 30); a.setLineWrap(true); a.setWrapStyleWord(true); return a;
    }

    private static int add(JPanel p, GridBagConstraints g, int r, String label, Component input) {
        g.gridy = r; g.gridx = 0; g.gridwidth = 1; g.weightx = 0; p.add(new JLabel(label + ":"), g);
        g.gridx = 1; g.weightx = 1; p.add(input, g); return r + 1;
    }

    /** Resalta los campos vacíos y devuelve sus nombres (criterios 2 y 3 de HU-1.1). */
    private List<String> highlightEmptyFields() {
        clearHighlights();
        List<String> missing = new ArrayList<>();
        check("Nombre", name, missing); check("Contexto", context, missing); check("Pregunta directa", prompt, missing);
        for (int i = 0; i < options.length; i++) check("Opción " + (char) ('A' + i), options[i], missing);
        check("Justificación", justification, missing); check("Bibliografía", bibliography, missing);
        check("Competencia", competency, missing); check("Tema", topic, missing);
        check("Subtema", subtopic, missing); check("Nivel de dificultad", difficulty, missing);
        return missing;
    }

    private void check(String label, javax.swing.text.JTextComponent c, List<String> missing) {
        if (c.getText().isBlank()) { c.setBackground(ERROR_BG); missing.add(label); }
    }

    private void clearHighlights() {
        Color normal = UIManager.getColor("TextField.background");
        for (JTextField f : new JTextField[]{name, competency, topic, subtopic, difficulty}) f.setBackground(normal);
        for (JTextField f : options) f.setBackground(normal);
        for (JTextArea a : new JTextArea[]{context, prompt, justification, bibliography}) a.setBackground(normal);
    }

    private void clearForm() {
        for (JTextField f : new JTextField[]{name, competency, topic, subtopic, difficulty}) f.setText("");
        for (JTextField f : options) f.setText("");
        for (JTextArea a : new JTextArea[]{context, prompt, justification, bibliography}) a.setText("");
        correct.setSelectedIndex(0);
        clearHighlights();
    }

    private void warn(String text, String title) {
        message.setForeground(new Color(170, 30, 30)); message.setText(text.replace("\n", " "));
        JOptionPane.showMessageDialog(this, text, title, JOptionPane.WARNING_MESSAGE);
    }

    private void save() {
        List<String> missing = highlightEmptyFields();
        if (missing.size() == 1 && missing.get(0).equals("Contexto")) {
            warn("El contexto de la pregunta es obligatorio.", "Contexto obligatorio"); return;
        }
        if (!missing.isEmpty()) {
            warn("Complete los campos obligatorios (resaltados en rojo):\n- " + String.join("\n- ", missing), "Campos obligatorios"); return;
        }
        try {
            controller.createDraft(name.getText().trim(), context.getText().trim(), prompt.getText().trim(),
                    Arrays.stream(options).map(x -> x.getText().trim()).toList(), correct.getSelectedIndex(),
                    justification.getText().trim(), bibliography.getText().trim(), competency.getText().trim(),
                    topic.getText().trim(), subtopic.getText().trim(), difficulty.getText().trim(), author.getId());
            message.setForeground(new Color(30, 110, 60)); message.setText("Pregunta guardada en estado BORRADOR.");
            JOptionPane.showMessageDialog(this, "La pregunta se guardó en estado BORRADOR.", "Pregunta creada", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            if (afterSave != null) afterSave.run();
        } catch (RuntimeException ex) {
            warn(ex.getMessage(), "No se pudo guardar la pregunta");
        }
    }
}
