package co.edu.unicauca.lisw2t2go2.ui;

import co.edu.unicauca.lisw2t2go2.domain.Role;
import co.edu.unicauca.lisw2t2go2.domain.User;
import co.edu.unicauca.lisw2t2go2.security.PasswordPolicy;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.Optional;

/**
 * Formulario modal reutilizable para crear o editar un usuario.
 *
 * Se usa el mismo dialogo para ambos casos: si recibe un User existente
 * (modo edicion) precarga los campos y oculta la contrasena obligatoria;
 * si no recibe nada (modo creacion) exige contrasena.
 *
 * Esta clase solo recolecta datos de la UI; no sabe nada de repositorios
 * ni de reglas de negocio (esas viven en UserService). Es responsabilidad
 * unica: "presentar y validar el formulario" (SRP).
 *
 * @author Claude
 */
public class UserFormDialog {

    /** Datos capturados en el formulario, listos para pasarle a UserService. */
    public static class UserFormResult {
        public final String username;
        public final String name;
        public final String password; // puede ser null/blank en edicion si no se cambia
        public final Role role;

        public UserFormResult(String username, String name, String password, Role role) {
            this.username = username;
            this.name = name;
            this.password = password;
            this.role = role;
        }
    }

    private final boolean editMode;

    public UserFormDialog(boolean editMode) {
        this.editMode = editMode;
    }

    /**
     * Muestra el dialogo y devuelve el resultado si el usuario confirma,
     * o Optional.empty() si cancela.
     */
    public Optional<UserFormResult> showAndWait(User existing) {
        Dialog<UserFormResult> dialog = new Dialog<>();
        dialog.setTitle(editMode ? "Editar usuario" : "Agregar usuario");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType(editMode ? "Guardar" : "Crear", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField usernameField = new TextField();
        usernameField.setPromptText("nombre de usuario");

        TextField nameField = new TextField();
        nameField.setPromptText("nombre completo");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(editMode ? "dejar en blanco para no cambiarla" : "contrasena (min. 6 caracteres)");

        ComboBox<Role> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll(Role.values());

        if (existing != null) {
            usernameField.setText(existing.getUsername());
            nameField.setText(existing.getName());
            roleCombo.setValue(existing.getRole());
        } else {
            roleCombo.setValue(Role.ESTUDIANTE);
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 20));

        grid.add(new Label("Usuario:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Nombre:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Contrasena:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Rol:"), 0, 3);
        grid.add(roleCombo, 1, 3);

        Label hintLabel = new Label("Min. 6 caracteres, 1 mayuscula, 1 digito y 1 caracter especial.");
        hintLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");
        grid.add(hintLabel, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                String username = usernameField.getText().trim();
                String name = nameField.getText().trim();
                String password = passwordField.getText();
                Role role = roleCombo.getValue();

                if (username.isEmpty() || name.isEmpty() || role == null) {
                    showValidationError("Usuario, nombre y rol son obligatorios.");
                    return null;
                }

                boolean passwordProvided = password != null && !password.isEmpty();
                if (!editMode || passwordProvided) {
                    try {
                        PasswordPolicy.validate(password);
                    } catch (IllegalArgumentException ex) {
                        showValidationError(ex.getMessage());
                        return null;
                    }
                }

                return new UserFormResult(username, name, password, role);
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
