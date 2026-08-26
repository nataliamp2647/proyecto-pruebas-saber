package co.edu.unicauca.lisw2t2go2.ui;

import co.edu.unicauca.lisw2t2go2.domain.Role;
import co.edu.unicauca.lisw2t2go2.domain.User;
import co.edu.unicauca.lisw2t2go2.domain.UserState;
import co.edu.unicauca.lisw2t2go2.exception.UserAlreadyExistsException;
import co.edu.unicauca.lisw2t2go2.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Pantalla de administracion de usuarios: Agregar, Ver, Editar, Eliminar
 * (RF-01, RF-03). Solo el rol Administrador llega a esta vista (ver
 * LoginView).
 *
 * Esta clase solo se ocupa de la presentacion: delega toda la logica de
 * negocio (validaciones, unicidad de username, hashing, persistencia) en
 * UserService (SRP). No conoce IUserRepository ni SQLite en absoluto.
 *
 * @author David Santiago Cruz Varón, Juan Felipe Gallardo Orozco
 */
public class UserAdminView {

    private final UserService userService;
    private final User currentAdmin;
    private final Stage stage;

    private final TableView<User> table = new TableView<>();
    private final ObservableList<User> data = FXCollections.observableArrayList();

    public UserAdminView(UserService userService, User currentAdmin, Stage stage) {
        this.userService = userService;
        this.currentAdmin = currentAdmin;
        this.stage = stage;
    }

    public void show(Stage stage) {
        Label title = new Label("Gestion de usuarios - Administrador: " + currentAdmin.getName());
        title.getStyleClass().add("title-label");

        setupTable();
        refreshData();

        Button addButton = new Button("Agregar");
        addButton.setOnAction(e -> handleAdd());

        Button editButton = new Button("Editar");
        editButton.setOnAction(e -> handleEdit());

        Button deleteButton = new Button("Eliminar");
        deleteButton.setOnAction(e -> handleDelete());

        Button refreshButton = new Button("Refrescar");
        refreshButton.setOnAction(e -> refreshData());

        HBox buttonBar = new HBox(10, addButton, editButton, deleteButton, refreshButton);
        buttonBar.setAlignment(Pos.CENTER_LEFT);
        buttonBar.setPadding(new Insets(10, 0, 0, 0));

        VBox top = new VBox(8, title);
        top.setPadding(new Insets(15, 15, 0, 15));

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(wrapTable());
        root.setBottom(wrapBottom(buttonBar));

        Scene scene = new Scene(root, 720, 460);

        stage.setTitle("Gestion de usuarios - Panel de administracion");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    private VBox wrapTable() {
        VBox box = new VBox(table);
        box.setPadding(new Insets(10, 15, 0, 15));
        VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);
        return box;
    }

    private VBox wrapBottom(HBox buttonBar) {
        VBox box = new VBox(buttonBar);
        box.setPadding(new Insets(0, 15, 15, 15));
        return box;
    }

    @SuppressWarnings("unchecked")
    private void setupTable() {
        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<User, String> usernameCol = new TableColumn<>("Usuario");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(140);

        TableColumn<User, String> nameCol = new TableColumn<>("Nombre");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<User, Role> roleCol = new TableColumn<>("Rol");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(160);

        TableColumn<User, UserState> stateCol = new TableColumn<>("Estado");
        stateCol.setCellValueFactory(new PropertyValueFactory<>("state"));
        stateCol.setPrefWidth(90);

        table.getColumns().setAll(idCol, usernameCol, nameCol, roleCol, stateCol);
        table.setItems(data);
        table.setPlaceholder(new Label("No hay usuarios registrados."));
    }

    private void refreshData() {
        data.setAll(userService.listUsers());
    }

    private void handleAdd() {
        UserFormDialog dialog = new UserFormDialog(false);
        Optional<UserFormDialog.UserFormResult> result = dialog.showAndWait(null);

        result.ifPresent(r -> {
            try {
                userService.registerUser(r.username, r.name, r.password, r.role);
                refreshData();
            } catch (UserAlreadyExistsException | IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });
    }

    private void handleEdit() {
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Selecciona un usuario de la tabla para editar.");
            return;
        }

        UserFormDialog dialog = new UserFormDialog(true);
        Optional<UserFormDialog.UserFormResult> result = dialog.showAndWait(selected);

        result.ifPresent(r -> {
            try {
                userService.updateUser(selected.getId(), r.username, r.name, r.role, r.password);
                refreshData();
            } catch (UserAlreadyExistsException | IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });
    }

    private void handleDelete() {
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Selecciona un usuario de la tabla para eliminar.");
            return;
        }

        if (selected.getId() == currentAdmin.getId()) {
            showError("No puedes eliminar el usuario con el que iniciaste sesion.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar al usuario '" + selected.getUsername() + "'? Esta accion no se puede deshacer.",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.YES) {
                userService.deleteUser(selected.getId());
                refreshData();
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
