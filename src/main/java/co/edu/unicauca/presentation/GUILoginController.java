
package co.edu.unicauca.presentation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

import co.unicauca.iso2.taller2.users.domain.User;
import co.unicauca.iso2.taller2.users.domain.access.IUserRepository;
import co.unicauca.iso2.taller2.users.domain.access.InMemoryUserRepository;
import co.unicauca.iso2.taller2.users.domain.service.AuthService;
import co.unicauca.iso2.taller2.users.domain.service.Pbkdf2PasswordHasher;
import co.unicauca.iso2.taller2.users.domain.service.UserValidator;


public class GUILoginController {

    private GUILogin view;
    private AuthService authService;

    public GUILoginController(GUILogin view) {

        this.view = view;

        IUserRepository repository =
                new InMemoryUserRepository();

        authService =
                new AuthService(
                        repository,
                        new Pbkdf2PasswordHasher(),
                        new UserValidator()
                );

        this.view.addLoginListener(
                new LoginListener()
        );

        createInitialUser();
    }

    private void createInitialUser() {

        authService.registerUser(
                "admin",
                "Administrador del sistema",
                co.unicauca.iso2.taller2.users.domain.Role.ADMINISTRADOR,
                "Admin123!"
        );

        authService.registerUser(
                "autor",
                "Autor de preguntas",
                co.unicauca.iso2.taller2.users.domain.Role.AUTOR_PREGUNTAS,
                "Autor123!"
        );

        authService.registerUser(
                "revisor",
                "Revisor de preguntas",
                co.unicauca.iso2.taller2.users.domain.Role.REVISOR,
                "Revisor123!"
        );

        authService.registerUser(
                "docente",
                "Docente",
                co.unicauca.iso2.taller2.users.domain.Role.DOCENTE,
                "Docente123!"
        );

        authService.registerUser(
                "estudiante",
                "Estudiante",
                co.unicauca.iso2.taller2.users.domain.Role.ESTUDIANTE,
                "Estudiante123!"
        );
    }

    private class LoginListener
            implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            String login =
                    view.getLogin();

            String password =
                    view.getPassword();

            Optional<User> user =
                    authService.login(
                            login,
                            password
                    );

            if (user.isPresent()) {

                view.showMessage(
                        "Inicio de sesión exitoso. Bienvenido "
                        + user.get().getFullName()
                );

                view.dispose();

                GUIQuestion questionView =
                        new GUIQuestion(user.get());

                questionView.setVisible(true);

            } else {

                view.showMessage(
                        "Usuario o contraseña incorrectos."
                );
            }
        }
    }
}

