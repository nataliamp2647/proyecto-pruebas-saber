package co.edu.unicauca.lisw2t2go2.main;

import co.edu.unicauca.lisw2t2go2.domain.Role;
import co.edu.unicauca.lisw2t2go2.domain.User;
import co.edu.unicauca.lisw2t2go2.exception.InvalidCredentialsException;
import co.edu.unicauca.lisw2t2go2.exception.UserAlreadyExistsException;
import co.edu.unicauca.lisw2t2go2.repository.IUserRepository;
import co.edu.unicauca.lisw2t2go2.repository.impl.InMemoryUserRepository;
import co.edu.unicauca.lisw2t2go2.repository.impl.SQLiteUserRepository;
import co.edu.unicauca.lisw2t2go2.security.IPasswordHasher;
import co.edu.unicauca.lisw2t2go2.security.Sha256PasswordHasher;
import co.edu.unicauca.lisw2t2go2.service.AuthService;
import co.edu.unicauca.lisw2t2go2.service.UserService;

/**
 * Punto de entrada de demostracion.
 *
 * Nota sobre SOLID/DIP en accion: la unica linea que decide "de donde
 * vienen los datos" es la que crea el objeto IUserRepository. Todo lo
 * demas (UserService, AuthService) programa contra la interfaz y no le
 * importa si por debajo hay una lista en memoria o una base SQLite.
 *
 * @author David Santiago Cruz Varón, Juan Felipe Gallardo Orozco
 */
public class Main {

    public static void main(String[] args) {

        // ------------------------------------------------------------
        // 1) Elige la implementacion del repositorio.
        //
        //    Para pruebas rapidas sin tocar disco:
        //        IUserRepository userRepository = new InMemoryUserRepository();
        //
        //    Para persistir en un archivo SQLite (recomendado):
        //        IUserRepository userRepository = new SQLiteUserRepository("data.db");
        //
        //    Cambiar de una a otra NO requiere tocar UserService ni
        //    AuthService: esa es la ventaja de programar contra la
        //    abstraccion IUserRepository (DIP).
        // ------------------------------------------------------------
        IUserRepository userRepository = new SQLiteUserRepository("data.db");

        IPasswordHasher passwordHasher = new Sha256PasswordHasher();

        UserService userService = new UserService(userRepository, passwordHasher);
        AuthService authService = new AuthService(userRepository, passwordHasher);

        // RF-01: registrar usuarios
        try {
            userService.registerUser("jhurtado", "Julio Hurtado", "Clave123#", Role.DOCENTE);
            userService.registerUser("lpantoja", "Libardo Pantoja", "Clave123#", Role.ADMINISTRADOR);
            userService.registerUser("estudiante1", "Ana Torres", "Clave123#", Role.ESTUDIANTE);
        } catch (UserAlreadyExistsException ex) {
            System.out.println("Aviso: " + ex.getMessage());
        }

        // RF-03: listar usuarios y sus roles
        System.out.println("\n--- Usuarios registrados ---");
        for (User u : userService.listUsers()) {
            System.out.println(u);
        }

        // RF-02: autenticar
        System.out.println("\n--- Prueba de autenticacion ---");
        try {
            User autenticado = authService.authenticate("jhurtado", "Clave123#");
            System.out.println("Login OK: " + autenticado.getName() + " (" + autenticado.getRole() + ")");
        } catch (InvalidCredentialsException ex) {
            System.out.println("Login fallido: " + ex.getMessage());
        }

        try {
            authService.authenticate("jhurtado", "Clave-Incorrecta9#");
        } catch (InvalidCredentialsException ex) {
            System.out.println("Login fallido (esperado): " + ex.getMessage());
        }

        // RF-03: cambiar rol de un usuario
        User primero = userService.listUsers().get(0);
        userService.changeRole(primero.getId(), Role.REVISOR);
        System.out.println("\nRol actualizado: " + userService.listUsers().get(0));
    }
}
