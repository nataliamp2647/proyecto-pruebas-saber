package co.edu.unicauca.lisw2t5g02.presentation;

import co.edu.unicauca.lisw2t5g02.domain.question.Question;
import co.edu.unicauca.lisw2t5g02.domain.question.Question.EstadoPregunta;
import co.edu.unicauca.lisw2t5g02.domain.question.QuestionService;
import co.edu.unicauca.lisw2t5g02.domain.user.User;
import co.edu.unicauca.lisw2t5g02.infra.Observer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/** Vista del administrador para asignar revisores (HU-2.1). Se actualiza sola vía Observer. */
public final class AdminReviewView extends JFrame implements Observer {
    private final QuestionService service;
    private final QuestionController controller;
    private final JComboBox<Question> questions = new JComboBox<>();
    private final JList<User> reviewers = new JList<>();
    private final JLabel status = new JLabel(" ");
    private boolean updating;

    public AdminReviewView(QuestionService service) {
        super("Administrador - Asignar revisores");
        this.service = service; controller = new QuestionController(service);
        setLayout(new BorderLayout(8, 8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Pregunta pendiente:"));
        questions.addActionListener(e -> { if (!updating) showCurrent(); });
        top.add(questions);
        JButton reload = new JButton("REFRESCAR");
        reload.addActionListener(e -> refresh());
        top.add(reload);
        add(top, BorderLayout.NORTH);
        reviewers.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        reviewers.setVisibleRowCount(10);
        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createTitledBorder("Docentes/revisores disponibles (seleccione uno o más)"));
        center.add(new JScrollPane(reviewers), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout());
        status.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        bottom.add(status, BorderLayout.NORTH);
        JButton save = new JButton("FINALIZAR ASIGNACIÓN");
        save.addActionListener(e -> assign());
        bottom.add(save, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        setSize(820, 520); setLocationRelativeTo(null); setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        service.addObserver(this);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) { service.removeObserver(AdminReviewView.this); }
        });
        refresh();
    }

    /** Observer: cuando el autor crea o envía una pregunta, la lista se recarga sola. */
    @Override public void update(Object o) { SwingUtilities.invokeLater(this::refresh); }

    private void refresh() {
        updating = true;
        try {
            Question previous = (Question) questions.getSelectedItem();
            DefaultComboBoxModel<Question> model = new DefaultComboBoxModel<>();
            Question toSelect = null;
            for (Question q : controller.listarTodas()) {
                if (q.getEstado() != EstadoPregunta.PENDIENTE_REVISION) continue;
                model.addElement(q);
                if (previous != null && previous.getId().equals(q.getId())) toSelect = q;
            }
            questions.setModel(model);
            if (toSelect != null) questions.setSelectedItem(toSelect);
            DefaultListModel<User> users = new DefaultListModel<>();
            for (User u : controller.revisoresDisponibles()) users.addElement(u);
            reviewers.setModel(users);
            status.setText(model.getSize() == 0 ? "No hay preguntas en estado PENDIENTE DE REVISIÓN."
                    : model.getSize() + " pregunta(s) pendiente(s) de revisión.");
        } finally { updating = false; }
        showCurrent();
    }

    private void showCurrent() {
        reviewers.clearSelection();
        Question q = (Question) questions.getSelectedItem();
        if (q == null) return;
        List<Integer> assigned = controller.revisoresAsignados(q.getId()).stream().map(User::getId).toList();
        for (int i = 0; i < reviewers.getModel().getSize(); i++)
            if (assigned.contains(reviewers.getModel().getElementAt(i).getId())) reviewers.addSelectionInterval(i, i);
    }

    private void assign() {
        Question q = (Question) questions.getSelectedItem();
        try {
            if (q == null) throw new IllegalStateException("Seleccione una pregunta pendiente.");
            List<String> warnings = controller.asignarRevisores(q, reviewers.getSelectedValuesList());
            if (warnings == null || warnings.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Asignación registrada y correo enviado a los revisores seleccionados.",
                        "Asignación exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "La asignación quedó registrada, pero no se pudo notificar por correo a:\n- "
                        + String.join("\n- ", warnings), "Asignación registrada con advertencias", JOptionPane.WARNING_MESSAGE);
            }
            refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "No se pudo asignar", JOptionPane.WARNING_MESSAGE);
        }
    }
}
