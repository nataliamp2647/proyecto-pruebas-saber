package co.edu.unicauca.lisw2t2go2;

import co.edu.unicauca.lisw2t2go2.domain.Role;
import co.edu.unicauca.lisw2t2go2.domain.User;
import co.edu.unicauca.lisw2t2go2.exception.InvalidCredentialsException;
import co.edu.unicauca.lisw2t2go2.exception.UserAlreadyExistsException;
import co.edu.unicauca.lisw2t2go2.repository.IUserRepository;
import co.edu.unicauca.lisw2t2go2.repository.impl.InMemoryUserRepository;
import co.edu.unicauca.lisw2t2go2.security.IPasswordHasher;
import co.edu.unicauca.lisw2t2go2.security.Sha256PasswordHasher;
import co.edu.unicauca.lisw2t2go2.service.AuthService;
import co.edu.unicauca.lisw2t2go2.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias.
 *
 * Se usa InMemoryUserRepository (no SQLite) precisamente porque
 * UserService/AuthService no dependen de la implementacion concreta:
 * eso es la prueba viva del principio DIP aplicado en este proyecto.
 *
 * @author Claude
 */
class UserServiceTest {

    private IUserRepository repository;
    private IPasswordHasher hasher;
    private UserService userService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        hasher = new Sha256PasswordHasher();
        userService = new UserService(repository, hasher);
        authService = new AuthService(repository, hasher);
    }

    @Test
    void deberiaRegistrarUnUsuarioNuevo() {
        User user = userService.registerUser("ana", "Ana Torres", "Clave123#", Role.ESTUDIANTE);

        assertTrue(user.getId() > 0);
        assertEquals(Role.ESTUDIANTE, user.getRole());
        assertNotEquals("Clave123#", user.getPasswordHash(), "la contrasena nunca debe guardarse en texto plano");
    }

    @Test
    void noDeberiaPermitirUsernameDuplicado() {
        userService.registerUser("ana", "Ana Torres", "Clave123#", Role.ESTUDIANTE);

        assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser("ana", "Otra Ana", "OtraClave9#", Role.DOCENTE));
    }

    @Test
    void deberiaAutenticarConCredencialesCorrectas() {
        userService.registerUser("ana", "Ana Torres", "Clave123#", Role.ESTUDIANTE);

        User autenticado = authService.authenticate("ana", "Clave123#");

        assertEquals("ana", autenticado.getUsername());
    }

    @Test
    void noDeberiaAutenticarConContrasenaIncorrecta() {
        userService.registerUser("ana", "Ana Torres", "Clave123#", Role.ESTUDIANTE);

        assertThrows(InvalidCredentialsException.class,
                () -> authService.authenticate("ana", "Clave-Erronea9#"));
    }

    @Test
    void deberiaCambiarElRolDeUnUsuario() {
        User user = userService.registerUser("ana", "Ana Torres", "Clave123#", Role.ESTUDIANTE);

        User actualizado = userService.changeRole(user.getId(), Role.REVISOR);

        assertEquals(Role.REVISOR, actualizado.getRole());
    }

    @Test
    void deberiaActualizarDatosDeUsuarioSinCambiarContrasena() {
        User user = userService.registerUser("ana", "Ana Torres", "Clave123#", Role.ESTUDIANTE);
        String hashOriginal = user.getPasswordHash();

        User actualizado = userService.updateUser(user.getId(), "ana2", "Ana T. Ramirez", Role.DOCENTE, null);

        assertEquals("ana2", actualizado.getUsername());
        assertEquals("Ana T. Ramirez", actualizado.getName());
        assertEquals(Role.DOCENTE, actualizado.getRole());
        assertEquals(hashOriginal, actualizado.getPasswordHash());
    }

    @Test
    void deberiaEliminarUnUsuario() {
        User user = userService.registerUser("ana", "Ana Torres", "Clave123#", Role.ESTUDIANTE);

        userService.deleteUser(user.getId());

        assertTrue(userService.listUsers().isEmpty());
    }

    @Test
    void deberiaMigrarContrasenaEnTextoPlanoAlPrimerLoginExitoso() {
        // Simula un usuario importado de una base de datos existente,
        // con la contrasena guardada tal cual (sin hashear).
        User legacyUser = new User();
        legacyUser.setUsername("admin");
        legacyUser.setName("Admin Juan");
        legacyUser.setRole(Role.ADMINISTRADOR);
        legacyUser.setState(co.edu.unicauca.lisw2t2go2.domain.UserState.ACTIVO);
        legacyUser.setPasswordHash("Admin123#"); // texto plano, formato legacy
        repository.create(legacyUser);

        User autenticado = authService.authenticate("admin", "Admin123#");
        assertEquals("admin", autenticado.getUsername());

        // Tras el login exitoso, la contrasena ya debe estar hasheada en el repositorio.
        User recargado = repository.findByUsername("admin").orElseThrow();
        assertTrue(hasher.isHashed(recargado.getPasswordHash()),
                "la contrasena deberia haberse migrado a formato hasheado");
        assertNotEquals("Admin123#", recargado.getPasswordHash());

        // Un segundo login con la misma contrasena sigue funcionando (ya via hash).
        User segundoLogin = authService.authenticate("admin", "Admin123#");
        assertEquals("admin", segundoLogin.getUsername());
    }
}
