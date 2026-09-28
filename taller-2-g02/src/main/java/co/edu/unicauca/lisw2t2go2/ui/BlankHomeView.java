package co.edu.unicauca.lisw2t2go2.ui;

import co.edu.unicauca.lisw2t2go2.domain.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Pantalla de inicio para roles distintos de Administrador
 * (Autor de preguntas, Revisor, Docente, Estudiante).
 *
 * Queda intencionalmente en blanco: cada uno de esos roles tendra su
 * propia vista en una iteracion futura. Se crea como una clase separada
 * (no una condicion dentro de LoginView) para que agregar la pantalla
 * real de cada rol despues no obligue a modificar el login (OCP).
 *
 * @author David Santiago Cruz Varón, Juan Felipe Gallardo Orozco
 */
public class BlankHomeView {

    private final User user;

    public BlankHomeView(User user) {
        this.user = user;
    }

    public void show(Stage stage) {
        Label welcome = new Label("Bienvenido, " + user.getName());
        welcome.getStyleClass().add("title-label");

        Label roleLabel = new Label("Rol: " + user.getRole() + " (interfaz pendiente por implementar)");

        VBox root = new VBox(12, welcome, roleLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        Scene scene = new Scene(root, 480, 320);

        stage.setTitle("Gestion de usuarios");
        stage.setScene(scene);
        stage.show();
    }
}
