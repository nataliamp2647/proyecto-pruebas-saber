package co.edu.unicauca.lisw2t5g02.presentation;

import co.edu.unicauca.lisw2t5g02.domain.user.AuthService;
import co.edu.unicauca.lisw2t5g02.domain.user.User;
import co.edu.unicauca.lisw2t5g02.exception.InvalidCredentialsException;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * @class LoginView
 * @brief Pantalla de inicio de sesión (RF-02).
 *
 * Reescrita en Java Swing para unificar el toolkit gráfico de toda la
 * aplicación (la versión del Taller 2 usaba JavaFX).
 *
 * Solo conoce AuthService: no sabe de SQLite, de hashing ni de roles
 * concretos. Al autenticar correctamente, entrega el usuario al callback
 * recibido por constructor, que es quien decide qué pantalla abrir.
 *
 * @author Grupo LISW2 T5 G02
 */
public class LoginView extends JFrame {

    private final AuthService authService;
    private final Consumer<User> alAutenticar;

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JLabel lblMensaje;

    public LoginView(AuthService authService, Consumer<User> alAutenticar) {
        super("Banco de Preguntas Saber PRO - Iniciar sesión");
        this.authService = authService;
        this.alAutenticar = alAutenticar;
        construirUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 260);
        setLocationRelativeTo(null);
    }

    private void construirUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Banco de Preguntas Saber PRO");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titulo, gbc);

        gbc.gridwidth = 1;
        txtUsuario = new JTextField(18);
        txtPassword = new JPasswordField(18);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(txtUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(txtPassword, gbc);

        JButton btnEntrar = new JButton("Iniciar sesión");
        btnEntrar.addActionListener(e -> intentarAutenticar());
        getRootPane().setDefaultButton(btnEntrar);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(btnEntrar, gbc);

        lblMensaje = new JLabel(" ");
        lblMensaje.setForeground(new Color(180, 30, 30));
        gbc.gridy = 4;
        panel.add(lblMensaje, gbc);

        add(panel);
    }

    private void intentarAutenticar() {
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            lblMensaje.setText("Usuario y contraseña son obligatorios.");
            return;
        }

        try {
            User autenticado = authService.authenticate(usuario, password);
            lblMensaje.setText(" ");
            dispose();
            alAutenticar.accept(autenticado);
        } catch (InvalidCredentialsException ex) {
            lblMensaje.setText("Credenciales inválidas o usuario inactivo.");
            txtPassword.setText("");
        } catch (RuntimeException ex) {
            lblMensaje.setText("Error: " + ex.getMessage());
        }
    }
}
