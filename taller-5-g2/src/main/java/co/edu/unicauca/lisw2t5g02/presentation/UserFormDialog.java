package co.edu.unicauca.lisw2t5g02.presentation;

import co.edu.unicauca.lisw2t5g02.domain.user.Role;
import co.edu.unicauca.lisw2t5g02.domain.user.User;
import co.edu.unicauca.lisw2t5g02.domain.user.UserService;
import co.edu.unicauca.lisw2t5g02.security.PasswordPolicy;

import javax.swing.*;
import java.awt.*;

/**
 * @class UserFormDialog
 * @brief Formulario modal reutilizable para crear o editar un usuario.
 *
 * Reescrito en Swing desde la versión JavaFX del Taller 2.
 *
 * Valida la contraseña contra {@link PasswordPolicy} antes de llamar al
 * servicio, para poder mostrar el mensaje concreto del requisito que
 * falta sin duplicar la regla de negocio (la política vive en una sola
 * clase; aquí solo se consulta).
 *
 * @author Grupo LISW2 T5 G02
 */
public class UserFormDialog extends JDialog {

    private final UserService userService;
    private final User usuarioEditado;

    private JTextField txtUsername;
    private JTextField txtNombre;
    private JComboBox<Role> cbRol;
    private JPasswordField txtPassword;
    private JLabel lblMensaje;

    private boolean guardado = false;

    /** @param usuarioEditado usuario a editar, o null para crear uno nuevo. */
    public UserFormDialog(Window padre, UserService userService, User usuarioEditado) {
        super(padre, usuarioEditado == null ? "Nuevo usuario" : "Editar usuario",
                ModalityType.APPLICATION_MODAL);
        this.userService = userService;
        this.usuarioEditado = usuarioEditado;
        construirUI();
        setSize(460, 300);
        setLocationRelativeTo(padre);
    }

    public boolean fueGuardado() {
        return guardado;
    }

    private void construirUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtUsername = new JTextField(20);
        txtNombre = new JTextField(20);
        cbRol = new JComboBox<>(Role.values());
        txtPassword = new JPasswordField(20);

        int fila = 0;
        fila = agregarFila(panel, gbc, fila, "Usuario:", txtUsername);
        fila = agregarFila(panel, gbc, fila, "Nombre completo:", txtNombre);
        fila = agregarFila(panel, gbc, fila, "Rol:", cbRol);
        fila = agregarFila(panel, gbc, fila,
                usuarioEditado == null ? "Contraseña:" : "Nueva contraseña:", txtPassword);

        JLabel ayuda = new JLabel("<html><small>Mínimo 6 caracteres, una mayúscula, "
                + "un dígito y un carácter especial."
                + (usuarioEditado != null ? " Dejar vacío para conservar la actual." : "")
                + "</small></html>");
        ayuda.setForeground(Color.GRAY);
        gbc.gridx = 0;
        gbc.gridy = fila++;
        gbc.gridwidth = 2;
        panel.add(ayuda, gbc);

        lblMensaje = new JLabel(" ");
        lblMensaje.setForeground(new Color(180, 30, 30));
        gbc.gridy = fila++;
        panel.add(lblMensaje, gbc);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());
        botones.add(btnGuardar);
        botones.add(btnCancelar);
        gbc.gridy = fila;
        panel.add(botones, gbc);

        if (usuarioEditado != null) {
            txtUsername.setText(usuarioEditado.getUsername());
            txtNombre.setText(usuarioEditado.getName());
            cbRol.setSelectedItem(usuarioEditado.getRole());
        }

        add(panel);
        getRootPane().setDefaultButton(btnGuardar);
    }

    private int agregarFila(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, Component campo) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(campo, gbc);
        return fila + 1;
    }

    private void guardar() {
        String username = txtUsername.getText().trim();
        String nombre = txtNombre.getText().trim();
        Role rol = (Role) cbRol.getSelectedItem();
        String password = new String(txtPassword.getPassword());

        try {
            if (usuarioEditado == null) {
                PasswordPolicy.validate(password);
                userService.registerUser(username, nombre, password, rol);
            } else {
                userService.updateUser(usuarioEditado.getId(), username, nombre, rol,
                        password.isBlank() ? null : password);
            }
            guardado = true;
            dispose();
        } catch (RuntimeException ex) {
            lblMensaje.setText(ex.getMessage());
        }
    }
}
