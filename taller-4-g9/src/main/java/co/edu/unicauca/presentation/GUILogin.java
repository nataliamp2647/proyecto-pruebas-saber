package co.edu.unicauca.presentation;

import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class GUILogin extends JFrame {

    private JTextField txtLogin;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public GUILogin() {

        initComponents();

        setTitle("Inicio de sesión - Banco de Preguntas Saber PRO");

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setSize(400, 220);

        setLocationRelativeTo(null);
    }

    private void initComponents() {

        JPanel panel =
                new JPanel(
                        new GridLayout(
                                3,
                                2,
                                10,
                                10
                        )
                );

        panel.setBorder(
                javax.swing.BorderFactory.createEmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );

        JLabel lblLogin =
                new JLabel("Usuario:");

        JLabel lblPassword =
                new JLabel("Contraseña:");

        txtLogin =
                new JTextField();

        txtPassword =
                new JPasswordField();

        btnLogin =
                new JButton("Iniciar sesión");

        panel.add(lblLogin);
        panel.add(txtLogin);

        panel.add(lblPassword);
        panel.add(txtPassword);

        panel.add(new JLabel());

        panel.add(btnLogin);

        add(panel);
    }

    public String getLogin() {

        return txtLogin.getText();
    }

    public String getPassword() {

        return new String(
                txtPassword.getPassword()
        );
    }

    public void addLoginListener(
            ActionListener listener) {

        btnLogin.addActionListener(
                listener
        );
    }

    public void showMessage(String message) {

        JOptionPane.showMessageDialog(
                this,
                message
        );
    }
}