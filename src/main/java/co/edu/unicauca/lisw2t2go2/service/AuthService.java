package co.edu.unicauca.lisw2t2go2.service;

import co.edu.unicauca.lisw2t2go2.domain.User;
import co.edu.unicauca.lisw2t2go2.domain.UserState;
import co.edu.unicauca.lisw2t2go2.exception.InvalidCredentialsException;
import co.edu.unicauca.lisw2t2go2.repository.IUserRepository;
import co.edu.unicauca.lisw2t2go2.security.IPasswordHasher;

/**
 * Logica de negocio de autenticacion.
 *
 * RF-02: el sistema debera permitir autenticacion mediante usuario y
 * contrasena.
 *
 * SRP: se separa de UserService a proposito. "Registrar/administrar
 * usuarios" y "autenticar" son dos razones de cambio distintas: las
 * reglas de una politica de login (intentos fallidos, bloqueo temporal,
 * MFA, etc.) pueden evolucionar de forma independiente a las reglas de
 * registro y administracion de usuarios.
 *
 * @author Claude
 */
public class AuthService {

    private final IUserRepository userRepository;
    private final IPasswordHasher passwordHasher;

    public AuthService(IUserRepository userRepository, IPasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    /**
     * Autentica un usuario por username y contrasena.
     *
     * Compatibilidad con datos existentes: si la contrasena guardada
     * todavia esta en texto plano (importada de una base de datos previa
     * que no aplicaba hashing), se compara directamente contra el texto
     * plano recibido. Si coincide, la contrasena se re-hashea y se
     * persiste de inmediato, de forma que ese usuario nunca vuelve a
     * pasar por la rama de texto plano en logins futuros.
     *
     * @throws InvalidCredentialsException si el usuario no existe, la
     *         contrasena no coincide, o el usuario esta inactivo.
     */
    public User authenticate(String username, String plainPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(InvalidCredentialsException::new);

        if (user.getState() == UserState.INACTIVO) {
            throw new InvalidCredentialsException();
        }

        String storedPassword = user.getPasswordHash();

        if (passwordHasher.isHashed(storedPassword)) {
            if (!passwordHasher.verify(plainPassword, storedPassword)) {
                throw new InvalidCredentialsException();
            }
        } else {
            // Contrasena legacy en texto plano (dato importado).
            if (!storedPassword.equals(plainPassword)) {
                throw new InvalidCredentialsException();
            }
            user.setPasswordHash(passwordHasher.hash(plainPassword));
            userRepository.update(user);
        }

        return user;
    }
}
