package co.edu.unicauca.lisw2t2go2.ui;

import co.edu.unicauca.lisw2t2go2.domain.Role;
import co.edu.unicauca.lisw2t2go2.domain.UserState;
import co.edu.unicauca.lisw2t2go2.exception.UserAlreadyExistsException;
import co.edu.unicauca.lisw2t2go2.repository.IUserRepository;
import co.edu.unicauca.lisw2t2go2.repository.impl.SQLiteUserRepository;
import co.edu.unicauca.lisw2t2go2.security.IPasswordHasher;
import co.edu.unicauca.lisw2t2go2.security.Sha256PasswordHasher;
import co.edu.unicauca.lisw2t2go2.service.AuthService;
import co.edu.unicauca.lisw2t2go2.service.UserService;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Punto de entrada de la aplicacion JavaFX.
 *
 * Es el "composition root": el unico lugar del proyecto que decide que
 * implementaciones concretas usar (SQLiteUserRepository, Sha256PasswordHasher)
 * y las inyecta en los servicios. La capa de UI (LoginView, UserAdminView,
 * etc.) solo conoce UserService/AuthService, nunca la base de datos
 * directamente (DIP).
 *
 * @author David Santiago Cruz Varón, Juan Felipe Gallardo Orozco
 */
public class AppLauncher extends Application {

    @Override
    public void start(Stage primaryStage) {
        IUserRepository userRepository = new SQLiteUserRepository("data.db");
        IPasswordHasher passwordHasher = new Sha256PasswordHasher();

        UserService userService = new UserService(userRepository, passwordHasher);
        AuthService authService = new AuthService(userRepository, passwordHasher);

        seedAdminIfEmpty(userService);

        LoginView loginView = new LoginView(authService, userService, primaryStage);
        loginView.show(primaryStage);
    }

    /**
     * Crea un usuario administrador por defecto la primera vez que se
     * ejecuta la aplicacion (base de datos vacia), para que siempre haya
     * una forma de entrar al modulo de administracion.
     *
     * Credenciales iniciales: admin / Admin123#
     * (cumple la politica de contrasenas: mayuscula, digito y caracter especial)
     */
    private void seedAdminIfEmpty(UserService userService) {
        if (userService.listUsers().isEmpty()) {
            try {
                userService.registerUser("admin", "Administrador del Sistema", "Admin123#", Role.ADMINISTRADOR);
            } catch (UserAlreadyExistsException ignored) {
                // ya existe, no pasa nada
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
