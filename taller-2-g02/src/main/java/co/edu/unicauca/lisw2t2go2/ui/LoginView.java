package co.edu.unicauca.lisw2t2go2.ui;

import co.edu.unicauca.lisw2t2go2.domain.Role;
import co.edu.unicauca.lisw2t2go2.domain.User;
import co.edu.unicauca.lisw2t2go2.exception.InvalidCredentialsException;
import co.edu.unicauca.lisw2t2go2.service.AuthService;
import co.edu.unicauca.lisw2t2go2.service.UserService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Pantalla de inicio de sesion (RF-02).
 *
 * Solo conoce AuthService (para validar credenciales) y UserService (para
 * pasarselo a la pantalla de administracion si el rol es Administrador).
 * No sabe nada de SQLite, hashing, etc. (DIP: depende de abstracciones de
 * la capa de servicio, no de detalles de infraestructura).
 *
 * @author David Santiago Cruz Varón, Juan Felipe Gallardo Orozco
 */
public class LoginView {

    private final AuthService authService;
    private final UserService userService;
    private final Stage stage;

    private TextField usernameField;
    private PasswordField passwordField;
    private Label errorLabel;

    public LoginView(AuthService authService, UserService userService, Stage stage) {
        this.authService = authService;
        this.userService = userService;
        this.stage = stage;
    }

    public void show(Stage stage) {
        Label title = new Label("Iniciar sesion");
        title.getStyleClass().add("title-label");

        usernameField = new TextField();
        usernameField.setPromptText("Usuario");
        usernameField.setMaxWidth(260);

        passwordField = new PasswordField();
        passwordField.setPromptText("Contrasena");
        passwordField.setMaxWidth(260);
        passwordField.setOnAction(e -> handleLogin());

        Button loginButton = new Button("Ingresar");
        loginButton.setDefaultButton(true);
        loginButton.setMaxWidth(260);
        loginButton.setOnAction(e -> handleLogin());

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #c0392b;");

        VBox root = new VBox(12, title, usernameField, passwordField, loginButton, errorLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        Scene scene = new Scene(root, 380, 320);

        stage.setTitle("Gestion de usuarios - Iniciar sesion");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void handleLogin() {
        errorLabel.setText("");
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Ingresa usuario y contrasena.");
            return;
        }

        try {
            User user = authService.authenticate(username, password);
            onLoginSuccess(user);
        } catch (InvalidCredentialsException ex) {
            errorLabel.setText(ex.getMessage());
            passwordField.clear();
        }
    }

    private void onLoginSuccess(User user) {
        if (user.getRole() == Role.ADMINISTRADOR) {
            UserAdminView adminView = new UserAdminView(userService, user, stage);
            adminView.show(stage);
        } else {
            // RF-03: los demas roles quedan pendientes por ahora.
            BlankHomeView blankView = new BlankHomeView(user);
            blankView.show(stage);
        }
    }
}
