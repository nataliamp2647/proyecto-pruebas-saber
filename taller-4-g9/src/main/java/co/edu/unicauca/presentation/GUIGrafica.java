package co.edu.unicauca.presentation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.domain.QuestionService;
import co.edu.unicauca.infra.Observer;

public class GUIGrafica extends JFrame implements Observer {

    private QuestionService service;
    private JPanel panelGrafica;

    private int borrador;
    private int pendiente;
    private int eliminada;

    public GUIGrafica(QuestionService service) {
        this.service = service;
        initComponents();
        service.addObserver(this);
        update();
    }

    private void initComponents() {

        setTitle("Vista Gráfica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panelGrafica = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                dibujarGrafica(g);
            }
        };

        panelGrafica.setPreferredSize(
                new java.awt.Dimension(500, 400)
        );

        add(panelGrafica);

        pack();

        setLocationRelativeTo(null);
    }

    @Override
    public void update() {

        borrador = 0;
        pendiente = 0;
        eliminada = 0;

        for (Question question :
                service.getQuestions()) {

            switch (question.getStatus()) {

                case "Borrador":
                    borrador++;
                    break;

                case "Pendiente de revisión":
                    pendiente++;
                    break;

                case "Eliminada":
                    eliminada++;
                    break;
            }
        }

        if (panelGrafica != null) {
            panelGrafica.repaint();
        }
    }

    private void dibujarGrafica(Graphics g) {

        Graphics2D g2 =
                (Graphics2D) g;

        int total =
                borrador + pendiente + eliminada;

        if (total == 0) {
            return;
        }

        int x = 50;
        int y = 40;
        int width = 250;
        int height = 250;

        double anguloBorrador = 360.0 * borrador / total;

        double anguloPendiente = 360.0 * pendiente / total;

        double anguloEliminada = 360.0 * eliminada / total;

        g2.setColor(Color.BLUE);

        g2.fill(new Arc2D.Double(
                x,
                y,
                width,
                height,
                0,
                anguloBorrador,
                Arc2D.PIE
        ));

        g2.setColor(Color.ORANGE);

        g2.fill(new Arc2D.Double(
                x,
                y,
                width,
                height,
                anguloBorrador,
                anguloPendiente,
                Arc2D.PIE
        ));

        g2.setColor(Color.RED);

        g2.fill(new Arc2D.Double(
                x,
                y,
                width,
                height,
                anguloBorrador + anguloPendiente,
                anguloEliminada,
                Arc2D.PIE
        ));

        int porcentajeBorrador =
                borrador * 100 / total;

        int porcentajePendiente =
                pendiente * 100 / total;

        int porcentajeEliminada =
                eliminada * 100 / total;

        g2.setColor(Color.BLACK);

        g2.drawString(
                "Borrador: "
                        + porcentajeBorrador
                        + "%",
                330,
                100
        );

        g2.drawString(
                "Pendiente de revisión: "
                        + porcentajePendiente
                        + "%",
                330,
                150
        );

        g2.drawString(
                "Eliminada: "
                        + porcentajeEliminada
                        + "%",
                330,
                200
        );

        g2.drawString(
                "Distribución de preguntas",
                150,
                330
        );
    }
}

