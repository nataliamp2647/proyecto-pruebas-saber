package co.edu.unicauca.lisw2t5g02.presentation;

import co.edu.unicauca.lisw2t5g02.domain.question.QuestionService;
import co.edu.unicauca.lisw2t5g02.domain.user.Role;
import co.edu.unicauca.lisw2t5g02.domain.user.User;
import co.edu.unicauca.lisw2t5g02.domain.user.UserService;
import co.edu.unicauca.lisw2t5g02.microkernel.QuestionMicrokernel;

import javax.swing.*;
import java.awt.*;

/**
 * @class MainMenuView
 * @brief Menú principal de la aplicación, posterior al inicio de sesión.
 *
 * Es el punto donde se unifican los tres talleres:
 * <ul>
 *   <li>Administración de usuarios (Taller 2 — SOLID), solo Administrador.</li>
 *   <li>Gestión de preguntas con MVC + Observer (Taller 4).</li>
 *   <li>Generación de preguntas por plugins (Taller 5 — Microkernel).</li>
 * </ul>
 *
 * Las opciones visibles dependen del rol del usuario autenticado
 * (RNF-07: restringir el acceso según el rol).
 *
 * @author Grupo LISW2 T5 G02
 */
public class MainMenuView extends JFrame {

    private final User usuario;
    private final UserService userService;
    private final QuestionService questionService;
    private final QuestionMicrokernel microkernel;

    /** Ventana de gestión de preguntas, para poder refrescarla desde el microkernel. */
    private GUIQuestions ventanaPreguntas;

    public MainMenuView(User usuario, UserService userService,
                        QuestionService questionService, QuestionMicrokernel microkernel) {
        super("Banco de Preguntas Saber PRO - Menú principal");
        this.usuario = usuario;
        this.userService = userService;
        this.questionService = questionService;
        this.microkernel = microkernel;
        construirUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 380);
        setLocationRelativeTo(null);
    }

    private void construirUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel cabecera = new JPanel(new GridLayout(2, 1));
        cabecera.setBorder(BorderFactory.createEmptyBorder(14, 14, 6, 14));
        JLabel lblBienvenida = new JLabel("Bienvenido, " + usuario.getName());
        lblBienvenida.setFont(lblBienvenida.getFont().deriveFont(Font.BOLD, 16f));
        JLabel lblRol = new JLabel("Rol: " + usuario.getRole());
        lblRol.setForeground(Color.DARK_GRAY);
        cabecera.add(lblBienvenida);
        cabecera.add(lblRol);
        add(cabecera, BorderLayout.NORTH);

        JPanel opciones = new JPanel(new GridLayout(0, 1, 8, 8));
        opciones.setBorder(BorderFactory.createEmptyBorder(6, 24, 6, 24));

        // Taller 2: solo el Administrador administra usuarios (RNF-07).
        if (usuario.getRole() == Role.ADMINISTRADOR) {
            JButton btnUsuarios = new JButton("Administración de usuarios");
            btnUsuarios.addActionListener(e -> new UserAdminView(userService).setVisible(true));
            opciones.add(btnUsuarios);
        }

        // Taller 4: gestión de preguntas con MVC + Observer.
        JButton btnPreguntas = new JButton("Gestión de preguntas (MVC + Observer)");
        btnPreguntas.addActionListener(e -> abrirGestionDePreguntas());
        opciones.add(btnPreguntas);

        // Taller 5: generación por plugins. Autores y administradores.
        if (usuario.getRole() == Role.ADMINISTRADOR || usuario.getRole() == Role.AUTOR_PREGUNTAS) {
            JButton btnMicrokernel = new JButton("Generar preguntas por plugin (Microkernel)");
            btnMicrokernel.addActionListener(e -> abrirMicrokernel());
            opciones.add(btnMicrokernel);
        }

        JButton btnSalir = new JButton("Cerrar sesión");
        btnSalir.addActionListener(e -> System.exit(0));
        opciones.add(btnSalir);

        add(opciones, BorderLayout.CENTER);
    }

    private void abrirGestionDePreguntas() {
        if (ventanaPreguntas == null || !ventanaPreguntas.isDisplayable()) {
            ventanaPreguntas = new GUIQuestions(questionService);
            GUIObserver1 observer1 = new GUIObserver1(questionService);
            GUIObserver2 observer2 = new GUIObserver2(questionService);

            questionService.addObserver(observer1);
            questionService.addObserver(observer2);

            ventanaPreguntas.setLocation(60, 60);
            observer1.setLocation(700, 60);
            observer2.setLocation(700, 360);

            observer1.setVisible(true);
            observer2.setVisible(true);
        }
        ventanaPreguntas.setVisible(true);
        ventanaPreguntas.toFront();
    }

    private void abrirMicrokernel() {
        // Al generar una pregunta, se refresca el comboBox de la ventana de
        // gestión (si está abierta) y se notifica a las vistas observadoras
        // para que sus estadísticas incluyan la pregunta nueva.
        Runnable alGenerar = () -> {
            if (ventanaPreguntas != null && ventanaPreguntas.isDisplayable()) {
                ventanaPreguntas.cargarComboPreguntas();
            }
            questionService.notifyAllObserves();
        };
        new MicrokernelView(microkernel, alGenerar).setVisible(true);
    }
}
