package co.edu.unicauca.lisw2t2go2.security;

import java.util.regex.Pattern;

/**
 * Politica de complejidad de contrasenas del sistema.
 *
 * Requisitos:
 *  - Minimo 6 caracteres.
 *  - Al menos un digito (0-9).
 *  - Al menos un caracter especial (cualquiera que no sea letra ni digito).
 *  - Al menos una letra mayuscula.
 *
 * SRP: esta regla de negocio (que hace valida a una contrasena) se separa
 * de UserService a proposito. Antes esa validacion vivia duplicada dentro
 * de UserService.registerUser y UserService.updateUser; ahora ambas
 * llaman a esta unica clase, por lo que un cambio en la politica (por
 * ejemplo subir el minimo a 8 caracteres) se hace en un solo lugar.
 *
 * OCP: si en el futuro se necesita una politica distinta (por ejemplo,
 * mas estricta para el rol Administrador), se puede crear otra
 * implementacion sin modificar esta clase, siempre que ambas compartan
 * la misma forma de uso (metodo estatico simple por ahora; se puede
 * extraer a una interfaz IPasswordPolicy si llegan a coexistir varias).
 *
 * @author David Santiago Cruz Varón, Juan Felipe Gallardo Orozco
 */
public final class PasswordPolicy {

    private static final int MIN_LENGTH = 6;
    private static final Pattern DIGIT = Pattern.compile("[0-9]");
    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    // "Caracter especial" = cualquier cosa que no sea letra (may/min) ni digito.
    private static final Pattern SPECIAL_CHAR = Pattern.compile("[^a-zA-Z0-9]");

    private PasswordPolicy() {
        // clase de utilidades, no se instancia
    }

    /**
     * Valida que la contrasena cumpla la politica de complejidad.
     *
     * @throws IllegalArgumentException con un mensaje especifico del
     *         requisito que falta, para que la UI lo pueda mostrar tal cual.
     */
    public static void validate(String plainPassword) {
        if (plainPassword == null || plainPassword.length() < MIN_LENGTH) {
            throw new IllegalArgumentException(
                    "La contrasena debe tener al menos " + MIN_LENGTH + " caracteres.");
        }
        if (!DIGIT.matcher(plainPassword).find()) {
            throw new IllegalArgumentException("La contrasena debe contener al menos un digito.");
        }
        if (!UPPERCASE.matcher(plainPassword).find()) {
            throw new IllegalArgumentException("La contrasena debe contener al menos una letra mayuscula.");
        }
        if (!SPECIAL_CHAR.matcher(plainPassword).find()) {
            throw new IllegalArgumentException("La contrasena debe contener al menos un caracter especial.");
        }
    }

    /**
     * Version booleana, util para validaciones en vivo en la UI (por
     * ejemplo, deshabilitar un boton mientras la contrasena no cumpla).
     */
    public static boolean isValid(String plainPassword) {
        try {
            validate(plainPassword);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
