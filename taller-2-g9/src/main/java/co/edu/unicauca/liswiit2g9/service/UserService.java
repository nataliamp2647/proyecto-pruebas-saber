package co.edu.unicauca.liswiit2g9.service;

import co.edu.unicauca.liswiit2g9.access.UserRepository;
import co.edu.unicauca.liswiit2g9.domain.User;
import co.edu.unicauca.liswiit2g9.domain.UserStatus;
import co.edu.unicauca.liswiit2g9.security.PasswordHasher;
import co.edu.unicauca.liswiit2g9.security.PasswordValidator;

public class UserService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final PasswordValidator passwordValidator;

    public UserService( UserRepository userRepository, PasswordHasher passwordHasher, PasswordValidator passwordValidator) {

        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.passwordValidator = passwordValidator;
    }

    public void register(User user) {

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException(
                    "El nombre de usuario ya existe"
            );
        }

        if (!passwordValidator.isValid(user.getPassword())) {
            throw new IllegalArgumentException(
                    "La contraseña no cumple los requisitos"
            );
        }

        String hashedPassword =
                passwordHasher.hash(user.getPassword());

        user.setPassword(hashedPassword);

        userRepository.save(user);
    }

    public User login(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuario o contraseña incorrectos"
                        ));

        if (!passwordHasher.matches(password, user.getPassword())) {
            throw new IllegalArgumentException(
                    "Usuario o contraseña incorrectos"
            );
        }

        if (user.getStatus() != UserStatus.ACTIVO) {
            throw new IllegalArgumentException(
                    "El usuario está inactivo"
            );
        }

        return user;
    }
}