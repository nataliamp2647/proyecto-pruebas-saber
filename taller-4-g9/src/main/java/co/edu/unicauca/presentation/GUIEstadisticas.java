package co.edu.unicauca.presentation;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.domain.QuestionService;
import co.edu.unicauca.infra.Observer;

public class GUIEstadisticas extends JFrame implements Observer {

    private QuestionService service;

    private JLabel lblTitle;
    private JLabel lblDraft;
    private JLabel lblPending;
    private JLabel lblDeleted;

    public GUIEstadisticas(QuestionService service) {

        this.service = service;

        initComponents();

        service.addObserver(this);

        update();
    }

    private void initComponents() {

        setTitle("Vista de Estadísticas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();

        lblTitle =
                new JLabel("Preguntas por estado");

        lblDraft =
                new JLabel("Borrador: 0");

        lblPending =
                new JLabel("Pendiente de revisión: 0");

        lblDeleted =
                new JLabel("Eliminada: 0");

        panel.add(lblTitle);
        panel.add(lblDraft);
        panel.add(lblPending);
        panel.add(lblDeleted);

        add(panel);

        pack();

        setLocationRelativeTo(null);
    }

    @Override
    public void update() {

        int draft = 0;
        int pending = 0;
        int deleted = 0;

        for (Question question :
                service.getQuestions()) {

            switch (question.getStatus()) {

                case "Borrador":
                    draft++;
                    break;

                case "Pendiente de revisión":
                    pending++;
                    break;

                case "Eliminada":
                    deleted++;
                    break;
            }
        }

        lblDraft.setText(
                "Borrador: " + draft
        );

        lblPending.setText(
                "Pendiente de revisión: " + pending
        );

        lblDeleted.setText(
                "Eliminada: " + deleted
        );
    }
}

