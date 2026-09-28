package co.edu.unicauca.lisw2t5g02.presentation;

import co.edu.unicauca.lisw2t5g02.domain.user.User;
import co.edu.unicauca.lisw2t5g02.domain.user.UserService;
import co.edu.unicauca.lisw2t5g02.domain.user.UserState;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * @class UserAdminView
 * @brief Administración de usuarios (RF-01, RF-03), solo para el rol
 * Administrador.
 *
 * Reescrita en Swing desde la versión JavaFX del Taller 2. Igual que
 * aquella, solo habla con UserService: no conoce el repositorio SQLite ni
 * el algoritmo de hashing.
 *
 * @author Grupo LISW2 T5 G02
 */
public class UserAdminView extends JFrame {

    private final UserService userService;

    private JTable tabla;
    private DefaultTableModel modelo;

    public UserAdminView(UserService userService) {
        super("Administración de usuarios");
        this.userService = userService;
        construirUI();
        refrescar();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(720, 420);
        setLocationRelativeTo(null);
    }

    private void construirUI() {
        setLayout(new BorderLayout(8, 8));

        modelo = new DefaultTableModel(new Object[]{"Id", "Usuario", "Nombre", "Rol", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEstado = new JButton("Activar / Inactivar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRefrescar = new JButton("Refrescar");

        btnAgregar.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> editarSeleccionado());
        btnEstado.addActionListener(e -> alternarEstadoSeleccionado());
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
        btnRefrescar.addActionListener(e -> refrescar());

        botones.add(btnAgregar);
        botones.add(btnEditar);
        botones.add(btnEstado);
        botones.add(btnEliminar);
        botones.add(btnRefrescar);
        add(botones, BorderLayout.SOUTH);
    }

    private void refrescar() {
        modelo.setRowCount(0);
        List<User> usuarios = userService.listUsers();
        for (User u : usuarios) {
            modelo.addRow(new Object[]{u.getId(), u.getUsername(), u.getName(), u.getRole(), u.getState()});
        }
    }

    private User obtenerSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.");
            return null;
        }
        int id = (int) modelo.getValueAt(fila, 0);
        return userService.listUsers().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private void abrirFormulario(User usuario) {
        UserFormDialog dialogo = new UserFormDialog(this, userService, usuario);
        dialogo.setVisible(true);
        if (dialogo.fueGuardado()) {
            refrescar();
        }
    }

    private void editarSeleccionado() {
        User usuario = obtenerSeleccionado();
        if (usuario != null) {
            abrirFormulario(usuario);
        }
    }

    private void alternarEstadoSeleccionado() {
        User usuario = obtenerSeleccionado();
        if (usuario == null) {
            return;
        }
        UserState nuevo = usuario.getState() == UserState.ACTIVO ? UserState.INACTIVO : UserState.ACTIVO;
        try {
            userService.changeState(usuario.getId(), nuevo);
            refrescar();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void eliminarSeleccionado() {
        User usuario = obtenerSeleccionado();
        if (usuario == null) {
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Eliminar al usuario '" + usuario.getUsername() + "'?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                userService.deleteUser(usuario.getId());
                refrescar();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}
