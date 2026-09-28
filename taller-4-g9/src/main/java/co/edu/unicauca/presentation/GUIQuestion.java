package co.edu.unicauca.presentation;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import co.edu.unicauca.access.QuestionImplRepository;
import co.edu.unicauca.domain.QuestionRepository;
import co.edu.unicauca.domain.QuestionService;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.microkernel.common.QuestionRequest;
import co.edu.unicauca.microkernel.core.QuestionMicrokernel;

public class GUIQuestion extends JFrame {

    private QuestionService service;
    private GUIQuestionController controller;

    private co.unicauca.iso2.taller2.users.domain.User currentUser;

    private GUIEstadisticas estadisticas;
    private GUIGrafica grafica;

    private JComboBox<String> cmbQuestions;
    private JButton btnLoadQuestion;

    private JLabel lblId;
    private JLabel lblName;
    private JLabel lblQuestion;
    private JLabel lblOptions;
    private JLabel lblCorrectAnswer;
    private JLabel lblStatus;

    private JComboBox<String> cmbNewStatus;
    private JButton btnUpdateStatus;
    private JButton btnGenerateQuestion;


    public GUIQuestion() {

        initComponents();

        QuestionRepository repository = new QuestionImplRepository();

        service = new QuestionService(repository);

        controller =
                new GUIQuestionController(
                        this,
                        service
                );

        btnGenerateQuestion.addActionListener(
                e -> generateQuestionTaller5()
        );
        

        estadisticas = new GUIEstadisticas(service);

        grafica = new GUIGrafica(service);
        
        estadisticas.setVisible(true);
        grafica.setVisible(true);
    }


        public GUIQuestion(
                co.unicauca.iso2.taller2.users.domain.User currentUser) {

        this.currentUser = currentUser;

        initComponents();

        QuestionRepository repository =
                new QuestionImplRepository();

        service =
                new QuestionService(repository);

        controller =
                new GUIQuestionController(
                        this,
                        service,
                        currentUser
                        
                );

        btnGenerateQuestion.addActionListener(
                e -> generateQuestionTaller5()
        );
        

        estadisticas =
                new GUIEstadisticas(service);

        grafica =
                new GUIGrafica(service);

        estadisticas.setVisible(true);
        grafica.setVisible(true);

        setTitle(
                "Banco de Preguntas Saber PRO - "
                + currentUser.getRole()
        );
        }



    private void initComponents() {

        setTitle("Banco de Preguntas Saber PRO");

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLayout(new BorderLayout(10, 10));

        JPanel panelSeleccion =
                new JPanel(new FlowLayout());

        panelSeleccion.setBorder(
                BorderFactory.createTitledBorder(
                        "Seleccionar pregunta"
                )
        );

        cmbQuestions =
                new JComboBox<>();

        btnLoadQuestion =
                new JButton("Cargar pregunta");

        btnGenerateQuestion =
                new JButton("Generar pregunta");
        

        panelSeleccion.add(
                new JLabel("Pregunta:")
        );

        panelSeleccion.add(
                cmbQuestions
        );

        panelSeleccion.add(
                btnLoadQuestion
        );

        panelSeleccion.add(
                btnGenerateQuestion
        );


        add(
                panelSeleccion,
                BorderLayout.NORTH
        );

        JPanel panelInformacion =
                new JPanel(
                        new GridLayout(
                                6,
                                1,
                                5,
                                10
                        )
                );

        panelInformacion.setBorder(
                BorderFactory.createTitledBorder(
                        "Información de la pregunta"
                )
        );

        lblId = new JLabel("Id: ");

        lblName = new JLabel("Nombre: ");

        lblQuestion = new JLabel("Pregunta: ");

        lblOptions = new JLabel("Opciones: ");

        lblCorrectAnswer = new JLabel("Respuesta correcta: ");

        lblStatus = new JLabel("Estado actual: ");

        panelInformacion.add(lblId);
        panelInformacion.add(lblName);
        panelInformacion.add(lblQuestion);
        panelInformacion.add(lblOptions);
        panelInformacion.add(lblCorrectAnswer);
        panelInformacion.add(lblStatus);

        add(
                panelInformacion,
                BorderLayout.CENTER
        );

        JPanel panelEstado =
                new JPanel(new FlowLayout());

        panelEstado.setBorder(
                BorderFactory.createTitledBorder(
                        "Actualizar estado"
                )
        );

        cmbNewStatus =
                new JComboBox<>(
                        new String[]{
                                "Borrador",
                                "Pendiente de revisión",
                                "Eliminada"
                        }
                );

        btnUpdateStatus =
                new JButton(
                        "Actualizar estado"
                );

        panelEstado.add(
                new JLabel("Nuevo estado:")
        );

        panelEstado.add(
                cmbNewStatus
        );

        panelEstado.add(
                btnUpdateStatus
        );

        add(
                panelEstado,
                BorderLayout.SOUTH
        );

        setSize(800, 500);

        setLocationRelativeTo(null);
    }

    public void addLoadQuestionListener(
            ActionListener listener) {

        btnLoadQuestion.addActionListener(
                listener
        );
    }


    public void addGenerateQuestionListener(
        ActionListener listener) {

        btnGenerateQuestion.addActionListener(
            listener
        );
}



public void generateQuestionTaller5() {

    try {

        QuestionMicrokernel microkernel =
                new QuestionMicrokernel();

        QuestionRequest request =
                new QuestionRequest(
                        "Pregunta generada por plugin",
                        "¿Cuál es el objetivo principal de la arquitectura de software?",
                        "MULTIPLE_CHOICE"
                );

        Question question =
                microkernel.generateQuestion(request);

        showQuestion(
                question.getId(),
                question.getName(),
                question.getQuestion(),
                "A. " + question.getDistractors().getOptionA()
                + "\nB. " + question.getDistractors().getOptionB()
                + "\nC. " + question.getDistractors().getOptionC()
                + "\nD. " + question.getDistractors().getOptionD(),
                question.getCorrectAnswer(),
                question.getStatus()
        );

        showMessage(
                "Pregunta generada correctamente mediante Microkernel + Plugin + Pipeline."
        );

    } catch (Exception e) {

        showMessage(
                "Error generando la pregunta: "
                + e.getMessage()
        );
    }
}


    public void addUpdateStatusListener(
            ActionListener listener) {

        btnUpdateStatus.addActionListener(
                listener
        );
    }

    public String getSelectedQuestion() {

        return (String) cmbQuestions.getSelectedItem();
    }

    public String getSelectedStatus() {

        return (String) cmbNewStatus.getSelectedItem();
    }

    public void setQuestions(
            String[] questions) {

        cmbQuestions.removeAllItems();

        for (String question : questions) {

            cmbQuestions.addItem(question);
        }
    }

    public void showQuestion(
            int id,
            String name,
            String question,
            String options,
            String correctAnswer,
            String status) {

        lblId.setText(
                "Id: " + id
        );

        lblName.setText(
                "Nombre: " + name
        );

        lblQuestion.setText(
                "<html>"
                + "Pregunta:<br>"
                + question
                + "</html>"
        );

        String optionsHtml =
                "<html>"
                + "Opciones:<br>"
                + options.replace(
                        "\n",
                        "<br>"
                )
                + "</html>";

        lblOptions.setText(
                optionsHtml
        );

        lblCorrectAnswer.setText(
                "Respuesta correcta: "
                + correctAnswer
        );

        lblStatus.setText(
                "Estado actual: "
                + status
        );
    }

        public void setUpdateStatusEnabled(boolean enabled) {
        btnUpdateStatus.setEnabled(enabled);
        cmbNewStatus.setEnabled(enabled);
        }

        public void showMessage(String message) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                message
        );
        }



public static void main(String[] args) {

    java.awt.EventQueue.invokeLater(
            () -> {

                System.out.println(
                        "===== PRUEBA TALLER 5 ====="
                );

                QuestionMicrokernel microkernel =
                        new QuestionMicrokernel();

                QuestionRequest request =
                        new QuestionRequest(
                                "Pregunta generada por plugin",
                                "¿Cuál es el objetivo principal de la arquitectura de software?",
                                "MULTIPLE_CHOICE"
                        );

                Question question =
                        microkernel.generateQuestion(request);

                System.out.println(
                        "Pregunta generada correctamente:"
                );

                System.out.println(
                        "ID: " + question.getId()
                );

                System.out.println(
                        "Nombre: " + question.getName()
                );

                System.out.println(
                        "Pregunta: " + question.getQuestion()
                );

                System.out.println(
                        "Estado: " + question.getStatus()
                );

                System.out.println(
                        "===== FIN PRUEBA TALLER 5 ====="
                );

                GUILogin loginView =
                        new GUILogin();

                new GUILoginController(
                        loginView
                );

                loginView.setVisible(true);
            }
    );
}
}

