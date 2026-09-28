package co.unicauca.iso2.taller2.users.domain.service;

import co.unicauca.iso2.taller2.users.domain.Role;
import co.unicauca.iso2.taller2.users.domain.User;
import co.unicauca.iso2.taller2.users.domain.UserStatus;
import co.unicauca.iso2.taller2.users.domain.access.IUserRepository;
import java.util.List;
import java.util.Optional;

/**
 * Orquesta el registro y el inicio de sesión de usuarios. No sabe si el
 * repositorio es SQLite o en memoria, ni qué algoritmo de hash se usa: sólo
 * conoce las abstracciones (Dependency Inversion), igual que la clase
 * Service del ejemplo 5 sólo conocía IProductRepository.
 *
 * @author Mani
 */
public class AuthService {

    private final IUserRepository repository;
    private final IPasswordHasher passwordHasher;
    private final IUserValidator validator;

    public AuthService(IUserRepository repository, IPasswordHasher passwordHasher, IUserValidator validator) {
        this.repository = repository;
        this.passwordHasher = passwordHasher;
        this.validator = validator;
    }

    /**
     * Registra un nuevo usuario. Valida los datos, verifica que el login no
     * exista ya, cifra la contraseña y la persiste.
     */
    public ValidationResult registerUser(String login, String fullName, Role role, String plainPassword) {
        User candidate = new User(0, login, fullName, role, UserStatus.ACTIVO, null);
        ValidationResult validation = validator.validate(candidate, plainPassword);
        if (!validation.isValid()) {
            return validation;
        }
        if (repository.existsByLogin(login)) {
            return ValidationResult.fail(List.of("Ya existe un usuario con el login '" + login + "'."));
        }
        candidate.setPasswordHash(passwordHasher.hash(plainPassword));
        boolean saved = repository.save(candidate);
        return saved ? ValidationResult.ok()
                : ValidationResult.fail(List.of("No fue posible guardar el usuario."));
    }

    /**
     * Intenta iniciar sesión. Devuelve el usuario si las credenciales son
     * correctas y el usuario está activo; en caso contrario, un Optional
     * vacío.
     */
    public Optional<User> login(String login, String plainPassword) {
        Optional<User> found = repository.findByLogin(login);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        User user = found.get();
        if (user.getStatus() != UserStatus.ACTIVO) {
            return Optional.empty();
        }
        if (!passwordHasher.verify(plainPassword, user.getPasswordHash())) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    public List<User> listUsers() {
        return repository.list();
    }

    public boolean setUserStatus(String login, UserStatus status) {
        Optional<User> found = repository.findByLogin(login);
        if (found.isEmpty()) {
            return false;
        }
        User user = found.get();
        user.setStatus(status);
        return repository.update(user);
    }
}
