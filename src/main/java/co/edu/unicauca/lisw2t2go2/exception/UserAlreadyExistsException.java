package co.edu.unicauca.lisw2t2go2.exception;

/**
 * Se lanza cuando se intenta registrar un usuario cuyo username ya existe.
 *
 * @author David Santiago Cruz Varón, Juan Felipe Gallardo Orozco
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String username) {
        super("Ya existe un usuario registrado con el nombre de usuario: " + username);
    }
}
