package co.edu.unicauca.lisw2t2go2.service;

import co.edu.unicauca.lisw2t2go2.domain.Role;
import co.edu.unicauca.lisw2t2go2.domain.User;
import co.edu.unicauca.lisw2t2go2.domain.UserState;
import co.edu.unicauca.lisw2t2go2.exception.UserAlreadyExistsException;
import co.edu.unicauca.lisw2t2go2.exception.UserNotFoundException;
import co.edu.unicauca.lisw2t2go2.repository.IUserRepository;
import co.edu.unicauca.lisw2t2go2.security.IPasswordHasher;
import co.edu.unicauca.lisw2t2go2.security.PasswordPolicy;

import java.util.List;

/**
 * Logica de negocio de administracion de usuarios.
 *
 * RF-01: registrar usuarios que interactuan con la aplicacion.
 * RF-03: administrar los diferentes roles del sistema.
 *
 * SRP: esta clase solo conoce reglas de negocio (validaciones, unicidad
 * del username, asignacion de roles). No sabe como se persisten los datos
 * (eso es responsabilidad de IUserRepository) ni como se hashean las
 * contrasenas (eso es responsabilidad de IPasswordHasher).
 *
 * DIP: depende de las abstracciones IUserRepository e IPasswordHasher,
 * inyectadas por constructor, nunca de una implementacion concreta.
 *
 * @author Claude
 */
public class UserService {

    private final IUserRepository userRepository;
    private final IPasswordHasher passwordHasher;

    public UserService(IUserRepository userRepository, IPasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    /**
     * RF-01: registra un nuevo usuario en el sistema.
     *
     * @param username     nombre de usuario, unico
     * @param name         nombre completo de la persona
     * @param plainPassword contrasena en texto plano (se hashea antes de guardar)
     * @param role         rol asignado (RF-03)
     * @return el usuario creado (con su id asignado)
     */
    public User registerUser(String username, String name, String plainPassword, Role role) {
        validateRegistration(username, name, plainPassword, role);

        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException(username);
        }

        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setRole(role);
        user.setState(UserState.ACTIVO);
        user.setPasswordHash(passwordHasher.hash(plainPassword));

        userRepository.create(user);
        return user;
    }

    /**
     * Actualiza los datos editables de un usuario existente (usado por la
     * pantalla de administracion). Si newPlainPassword es null o vacio,
     * la contrasena actual se conserva.
     */
    public User updateUser(int userId, String username, String name, Role role, String newPlainPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("id=" + userId));

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (role == null) {
            throw new IllegalArgumentException("El rol es obligatorio.");
        }

        userRepository.findByUsername(username)
                .filter(other -> other.getId() != userId)
                .ifPresent(other -> {
                    throw new UserAlreadyExistsException(username);
                });

        user.setUsername(username);
        user.setName(name);
        user.setRole(role);

        if (newPlainPassword != null && !newPlainPassword.isBlank()) {
            PasswordPolicy.validate(newPlainPassword);
            user.setPasswordHash(passwordHasher.hash(newPlainPassword));
        }

        userRepository.update(user);
        return user;
    }

    /**
     * RF-03: cambia el rol asignado a un usuario existente.
     */
    public User changeRole(int userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("id=" + userId));

        user.setRole(newRole);
        userRepository.update(user);
        return user;
    }

    /**
     * Activa/inactiva un usuario sin borrarlo del sistema.
     */
    public User changeState(int userId, UserState newState) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("id=" + userId));

        user.setState(newState);
        userRepository.update(user);
        return user;
    }

    public List<User> listUsers() {
        return userRepository.listAll();
    }

    public void deleteUser(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("id=" + userId));
        userRepository.delete(userId);
    }

    private void validateRegistration(String username, String name, String plainPassword, Role role) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        PasswordPolicy.validate(plainPassword);
        if (role == null) {
            throw new IllegalArgumentException("El rol es obligatorio.");
        }
    }
}
