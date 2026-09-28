package co.edu.unicauca.lisw2t2go2.exception;

/**
 * Se lanza cuando el usuario o la contrasena no coinciden (RF-02).
 * El mensaje es intencionalmente generico para no revelar si el usuario
 * existe o si la contrasena fue la incorrecta (buena practica de seguridad).
 *
 * @author David Santiago Cruz Varón, Juan Felipe Gallardo Orozco
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Usuario o contrasena incorrectos.");
    }
}
