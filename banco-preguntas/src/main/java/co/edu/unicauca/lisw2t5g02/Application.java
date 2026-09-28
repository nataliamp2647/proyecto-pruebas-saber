package co.edu.unicauca.lisw2t5g02;

import co.edu.unicauca.lisw2t5g02.access.QuestionImplRepository;
import co.edu.unicauca.lisw2t5g02.access.SQLiteUserRepository;
import co.edu.unicauca.lisw2t5g02.domain.question.QuestionRepository;
import co.edu.unicauca.lisw2t5g02.domain.question.QuestionService;
import co.edu.unicauca.lisw2t5g02.domain.user.AuthService;
import co.edu.unicauca.lisw2t5g02.domain.user.IUserRepository;
import co.edu.unicauca.lisw2t5g02.domain.user.Role;
import co.edu.unicauca.lisw2t5g02.domain.user.UserService;
import co.edu.unicauca.lisw2t5g02.presentation.LoginView;
import co.edu.unicauca.lisw2t5g02.presentation.MainMenuView;
import co.edu.unicauca.lisw2t5g02.security.IPasswordHasher;
import co.edu.unicauca.lisw2t5g02.security.Argon2PasswordHasher;

import javax.swing.UIManager;
import java.awt.EventQueue;

/**
 * @class Application
 * @brief Punto de entrada y composición de la aplicación unificada.
 *
 * No pertenece a ninguna capa funcional: es el único lugar que decide qué
 * implementaciones concretas se usan (SQLite, Argon2id) y las inyecta en
 * los servicios. Gracias a eso, ninguna otra clase depende de una
 * implementación concreta (DIP).
 *
 * Flujo: login -> menú principal -> (usuarios | preguntas | revisión).
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
        IPasswordHasher passwordHasher = new Argon2PasswordHasher();

        // --- Capa de dominio (servicios) ---
        UserService userService = new UserService(userRepository, passwordHasher);
        AuthService authService = new AuthService(userRepository, passwordHasher);
        QuestionService questionService = new QuestionService(questionRepository, userRepository, new co.edu.unicauca.lisw2t5g02.domain.question.NotificationService());

        crearUsuariosDemoSiHaceFalta(userService);

        // --- Capa de presentación ---
        EventQueue.invokeLater(() -> new LoginView(authService, usuario ->
                new MainMenuView(usuario, userService, questionService).setVisible(true)
        ).setVisible(true));
    }

    /** Crea usuarios de demostración para poder probar los tres roles del primer corte. */
    private static void crearUsuariosDemoSiHaceFalta(UserService userService) {
        try {
            if (userService.listUsers().stream().noneMatch(u -> u.getUsername().equals("admin")))
                userService.registerUser("admin", "Administrador", "admin@demo.local", "Admin123#", Role.ADMINISTRADOR);
            if (userService.listUsers().stream().noneMatch(u -> u.getUsername().equals("autor")))
                userService.registerUser("autor", "Autor de preguntas", "autor@demo.local", "Autor123#", Role.AUTOR_PREGUNTAS);
            if (userService.listUsers().stream().noneMatch(u -> u.getUsername().equals("revisor")))
                userService.registerUser("revisor", "Docente Revisor", "revisor@demo.local", "Revisor123#", Role.REVISOR);
        } catch (RuntimeException e) { System.err.println("No se pudieron crear usuarios demo: " + e.getMessage()); }
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
