package co.edu.unicauca.lisw2t5g02;

import co.edu.unicauca.lisw2t5g02.access.QuestionImplRepository;
import co.edu.unicauca.lisw2t5g02.access.SQLiteUserRepository;
import co.edu.unicauca.lisw2t5g02.domain.question.QuestionRepository;
import co.edu.unicauca.lisw2t5g02.domain.question.QuestionService;
import co.edu.unicauca.lisw2t5g02.domain.user.AuthService;
import co.edu.unicauca.lisw2t5g02.domain.user.IUserRepository;
import co.edu.unicauca.lisw2t5g02.domain.user.Role;
import co.edu.unicauca.lisw2t5g02.domain.user.UserService;
import co.edu.unicauca.lisw2t5g02.microkernel.QuestionMicrokernel;
import co.edu.unicauca.lisw2t5g02.presentation.LoginView;
import co.edu.unicauca.lisw2t5g02.presentation.MainMenuView;
import co.edu.unicauca.lisw2t5g02.security.IPasswordHasher;
import co.edu.unicauca.lisw2t5g02.security.Sha256PasswordHasher;

import javax.swing.UIManager;
import java.awt.EventQueue;

/**
 * @class Application
 * @brief Punto de entrada y composición de la aplicación unificada.
 *
 * No pertenece a ninguna capa funcional: es el único lugar que decide qué
 * implementaciones concretas se usan (SQLite, SHA-256) y las inyecta en
 * los servicios. Gracias a eso, ninguna otra clase depende de una
 * implementación concreta (DIP).
 *
 * Flujo: login -> menú principal -> (usuarios | preguntas | microkernel).
 *
 * @author Grupo LISW2 T5 G02
 */
public final class Application {

    /** Usuario administrador semilla, creado si la base está vacía. */
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "Admin123#";

    private Application() { }

    public static void main(String[] args) {
        aplicarLookAndFeelNimbus();

        // --- Capa de acceso a datos (implementaciones concretas) ---
        IUserRepository userRepository = new SQLiteUserRepository("data.db");
        QuestionRepository questionRepository = new QuestionImplRepository();
        IPasswordHasher passwordHasher = new Sha256PasswordHasher();

        // --- Capa de dominio (servicios) ---
        UserService userService = new UserService(userRepository, passwordHasher);
        AuthService authService = new AuthService(userRepository, passwordHasher);
        QuestionService questionService = new QuestionService(questionRepository);

        // --- Microkernel: carga sus plugins por reflexión ---
        QuestionMicrokernel microkernel = new QuestionMicrokernel(questionRepository);
        try {
            microkernel.registrarPluginsDesdeArchivo("plugins.properties");
        } catch (Exception e) {
            System.err.println("No se pudieron cargar los plugins: " + e.getMessage());
        }

        crearAdminSemillaSiHaceFalta(userService);

        // --- Capa de presentación ---
        EventQueue.invokeLater(() -> new LoginView(authService, usuario ->
                new MainMenuView(usuario, userService, questionService, microkernel).setVisible(true)
        ).setVisible(true));
    }

    /**
     * Si no hay ningún usuario en la base, crea un administrador inicial
     * para poder entrar la primera vez.
     */
    private static void crearAdminSemillaSiHaceFalta(UserService userService) {
        try {
            if (userService.listUsers().isEmpty()) {
                userService.registerUser(ADMIN_USERNAME, "Administrador del sistema",
                        ADMIN_PASSWORD, Role.ADMINISTRADOR);
                System.out.println("Usuario administrador creado: "
                        + ADMIN_USERNAME + " / " + ADMIN_PASSWORD);
            }
        } catch (RuntimeException e) {
            System.err.println("No se pudo crear el administrador semilla: " + e.getMessage());
        }
    }

    private static void aplicarLookAndFeelNimbus() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) { }
    }
}
