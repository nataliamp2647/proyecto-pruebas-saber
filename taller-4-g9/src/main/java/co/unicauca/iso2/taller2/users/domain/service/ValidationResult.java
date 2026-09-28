package co.unicauca.iso2.taller2.users.domain.service;

import java.util.Collections;
import java.util.List;

/**
 * Resultado de una validación: si es válida, y en caso contrario, la lista
 * de errores encontrados.
 *
 * @author Mani
 */
public class ValidationResult {

    private final boolean valid;
    private final List<String> errors;

    private ValidationResult(boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = errors;
    }

    public static ValidationResult ok() {
        return new ValidationResult(true, Collections.emptyList());
    }

    public static ValidationResult fail(List<String> errors) {
        return new ValidationResult(false, errors);
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> getErrors() {
        return errors;
    }
}
