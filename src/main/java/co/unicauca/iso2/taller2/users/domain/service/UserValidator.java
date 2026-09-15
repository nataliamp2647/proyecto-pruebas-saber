package co.unicauca.iso2.taller2.users.domain.service;

import co.unicauca.iso2.taller2.users.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Reglas de validación del registro de usuario, según el enunciado del
 * Taller 2: login y nombre completo obligatorios, rol obligatorio, y
 * contraseña de mínimo 6 caracteres con al menos un dígito, un carácter
 * especial y una mayúscula.
 *
 * @author Mani
 */
public class UserValidator implements IUserValidator {

    private static final Pattern DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern UPPERCASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern SPECIAL_CHAR = Pattern.compile(".*[^a-zA-Z0-9].*");
    private static final int MIN_PASSWORD_LENGTH = 6;

    @Override
    public ValidationResult validate(User user, String plainPassword) {
        List<String> errors = new ArrayList<>();

        if (user == null) {
            errors.add("El usuario no puede ser nulo.");
            return ValidationResult.fail(errors);
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            errors.add("El nombre de usuario (login) es obligatorio.");
        }
        if (user.getFullName() == null || user.getFullName().isBlank()) {
            errors.add("El nombre completo es obligatorio.");
        }
        if (user.getRole() == null) {
            errors.add("El rol es obligatorio.");
        }
        if (user.getStatus() == null) {
            errors.add("El estado del usuario es obligatorio.");
        }

        errors.addAll(validatePassword(plainPassword));

        return errors.isEmpty() ? ValidationResult.ok() : ValidationResult.fail(errors);
    }

    private List<String> validatePassword(String plainPassword) {
        List<String> errors = new ArrayList<>();
        if (plainPassword == null || plainPassword.length() < MIN_PASSWORD_LENGTH) {
            errors.add("La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres.");
            return errors;
        }
        if (!DIGIT.matcher(plainPassword).matches()) {
            errors.add("La contraseña debe contener al menos un dígito.");
        }
        if (!UPPERCASE.matcher(plainPassword).matches()) {
            errors.add("La contraseña debe contener al menos una mayúscula.");
        }
        if (!SPECIAL_CHAR.matcher(plainPassword).matches()) {
            errors.add("La contraseña debe contener al menos un carácter especial.");
        }
        return errors;
    }
}
