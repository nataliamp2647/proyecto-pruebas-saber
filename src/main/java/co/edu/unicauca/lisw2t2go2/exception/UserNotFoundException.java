package co.edu.unicauca.lisw2t2go2.exception;

/**
 * Se lanza cuando se busca un usuario que no existe en el repositorio.
 *
 * @author David Santiago Cruz Varón, Juan Felipe Gallardo Orozco
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String detail) {
        super("Usuario no encontrado: " + detail);
    }
}
