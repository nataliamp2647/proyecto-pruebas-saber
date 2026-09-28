package co.edu.unicauca.lisw2t5g02.domain.user;

import co.edu.unicauca.lisw2t5g02.access.InMemoryUserRepository;
import co.edu.unicauca.lisw2t5g02.exception.InvalidCredentialsException;
import co.edu.unicauca.lisw2t5g02.security.IPasswordHasher;
import co.edu.unicauca.lisw2t5g02.security.Sha256PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private IUserRepository repository;
    private IPasswordHasher hasher;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        hasher = new Sha256PasswordHasher();
        authService = new AuthService(repository, hasher);
    }

    @Test
    void authenticate_deberiaPermitirCredencialesCorrectas() {
        User user = new User();
        user.setUsername("ana");
        user.setName("Ana Torres");
        user.setRole(Role.ESTUDIANTE);
        user.setState(UserState.ACTIVO);
        user.setPasswordHash(hasher.hash("Clave123#"));
        repository.create(user);

        User autenticado = authService.authenticate("ana", "Clave123#");

        assertSame(user, autenticado);
    }

    @Test
    void authenticate_deberiaRechazarUsuarioInexistente() {
        assertThrows(InvalidCredentialsException.class,
                () -> authService.authenticate("noExiste", "Clave123#"));
    }

    @Test
    void authenticate_deberiaRechazarContrasenaIncorrecta() {
        User user = new User();
        user.setUsername("ana");
        user.setName("Ana Torres");
        user.setRole(Role.ESTUDIANTE);
        user.setState(UserState.ACTIVO);
        user.setPasswordHash(hasher.hash("Clave123#"));
        repository.create(user);

        assertThrows(InvalidCredentialsException.class,
                () -> authService.authenticate("ana", "Incorrecta9#"));
    }

    @Test
    void authenticate_deberiaRechazarUsuarioInactivo() {
        User user = new User();
        user.setUsername("ana");
        user.setName("Ana Torres");
        user.setRole(Role.ESTUDIANTE);
        user.setState(UserState.INACTIVO);
        user.setPasswordHash(hasher.hash("Clave123#"));
        repository.create(user);

        assertThrows(InvalidCredentialsException.class,
                () -> authService.authenticate("ana", "Clave123#"));
    }

    @Test
    void authenticate_deberiaMigrarContrasenaLegacy() {
        User user = new User();
        user.setUsername("legacy");
        user.setName("Usuario Legacy");
        user.setRole(Role.ADMINISTRADOR);
        user.setState(UserState.ACTIVO);
        user.setPasswordHash("Clave123#");
        repository.create(user);

        authService.authenticate("legacy", "Clave123#");

        User actualizado = repository.findByUsername("legacy").orElseThrow();
        assertTrue(hasher.isHashed(actualizado.getPasswordHash()));
        assertNotEquals("Clave123#", actualizado.getPasswordHash());
    }
}
