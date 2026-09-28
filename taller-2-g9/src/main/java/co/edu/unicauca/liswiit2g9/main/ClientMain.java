/**
 * Aplicación de consola para el Taller 2: registro e inicio de sesion de
 * usuarios, con un menu/tablero segun el rol autenticado.
 *
 * @author Manuel Sebastian Rosero Torres - Natalia Muñoz Prado
 */

package co.edu.unicauca.liswiit2g9.main;

import java.util.Scanner;

import co.edu.unicauca.liswiit2g9.access.SQLiteUserRepository;
import co.edu.unicauca.liswiit2g9.access.UserRepository;
import co.edu.unicauca.liswiit2g9.database.DatabaseConnection;
import co.edu.unicauca.liswiit2g9.domain.Role;
import co.edu.unicauca.liswiit2g9.domain.User;
import co.edu.unicauca.liswiit2g9.domain.UserStatus;
import co.edu.unicauca.liswiit2g9.security.Argon2PasswordHasher;
import co.edu.unicauca.liswiit2g9.security.PasswordHasher;
import co.edu.unicauca.liswiit2g9.security.PasswordValidator;
import co.edu.unicauca.liswiit2g9.service.UserService;

public class ClientMain {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        DatabaseConnection databaseConnection =
                new DatabaseConnection("jdbc:sqlite:users.db");

        UserRepository userRepository =
                new SQLiteUserRepository(databaseConnection);

        PasswordHasher passwordHasher =
                new Argon2PasswordHasher();

        PasswordValidator passwordValidator =
                new PasswordValidator();

        UserService userService =
                new UserService(
                        userRepository,
                        passwordHasher,
                        passwordValidator
                );

        boolean running = true;

        while (running) {

            showMainMenu();

            int option = readInt("Seleccione una opción: ");

            switch (option) {

                case 1:
                    registerUser(userService);
                    break;

                case 2:
                    login(userService);
                    break;

                case 3:
                    running = false;
                    System.out.println();
                    System.out.println("¡Hasta luego!");
                    break;

                default:
                    System.out.println();
                    System.out.println("Opción inválida.");
            }
        }

        scanner.close();
    }

    private static void showMainMenu() {

        System.out.println();
        System.out.println("======================================");
        System.out.println("     SISTEMA DE GESTIÓN DE USUARIOS");
        System.out.println("======================================");
        System.out.println("1. Registrarse");
        System.out.println("2. Iniciar sesión");
        System.out.println("3. Salir");
        System.out.println("======================================");
    }

    private static void registerUser(UserService userService) {

        System.out.println();
        System.out.println("========== REGISTRO DE USUARIO ==========");

        String username = readText("Nombre de usuario: ");
        String fullName = readText("Nombre completo: ");

        Role role = selectRole();
        UserStatus status = selectStatus();

        String password = readText("Contraseña: ");
        String confirmPassword = readText("Confirmar contraseña: ");

        if (!password.equals(confirmPassword)) {
            System.out.println();
            System.out.println("Las contraseñas no coinciden.");
            return;
        }

        User user = new User(
                username,
                fullName,
                role,
                status,
                password
        );

        try {

            userService.register(user);

            System.out.println();
            System.out.println("Usuario registrado correctamente.");

        } catch (IllegalArgumentException e) {

            System.out.println();
            System.out.println("No se pudo registrar el usuario:");
            System.out.println(e.getMessage());

        } catch (RuntimeException e) {

            System.out.println();
            System.out.println("Ocurrió un error al registrar el usuario.");
            System.out.println(e.getMessage());
        }
    }

    private static void login(UserService userService) {

        System.out.println();
        System.out.println("========== INICIAR SESIÓN ==========");

        String username = readText("Nombre de usuario: ");
        String password = readText("Contraseña: ");

        try {

            User user = userService.login(username, password);

            System.out.println();
            System.out.println("Inicio de sesión exitoso.");
            System.out.println("Bienvenido, " + user.getFullName());
            System.out.println("Rol: " + user.getRole());

            showRoleMenu(user);

        } catch (IllegalArgumentException e) {

            System.out.println();
            System.out.println("No se pudo iniciar sesión:");
            System.out.println(e.getMessage());

        } catch (RuntimeException e) {

            System.out.println();
            System.out.println("Ocurrió un error al iniciar sesión.");
            System.out.println(e.getMessage());
        }
    }

    private static void showRoleMenu(User user) {

        System.out.println();
        System.out.println("========== MENÚ DEL USUARIO ==========");
        System.out.println("Usuario: " + user.getUsername());
        System.out.println("Rol: " + user.getRole());
        System.out.println();

        switch (user.getRole()) {

            case ADMINISTRADOR:
                showAdministratorMenu();
                break;

            case AUTOR_DE_PREGUNTAS:
                showQuestionAuthorMenu();
                break;

            case REVISOR:
                showReviewerMenu();
                break;

            case DOCENTE:
                showTeacherMenu();
                break;

            case ESTUDIANTE:
                showStudentMenu();
                break;

            default:
                System.out.println("Rol no reconocido.");
        }
    }

    private static void showAdministratorMenu() {

        System.out.println("1. Gestión de usuarios");
        System.out.println("2. Consultar usuarios");
        System.out.println("3. Cerrar sesión");
    }

    private static void showQuestionAuthorMenu() {

        System.out.println("1. Crear preguntas");
        System.out.println("2. Consultar preguntas");
        System.out.println("3. Cerrar sesión");
    }

    private static void showReviewerMenu() {

        System.out.println("1. Revisar preguntas");
        System.out.println("2. Consultar preguntas");
        System.out.println("3. Cerrar sesión");
    }

    private static void showTeacherMenu() {

        System.out.println("1. Gestionar evaluaciones");
        System.out.println("2. Consultar evaluaciones");
        System.out.println("3. Cerrar sesión");
    }

    private static void showStudentMenu() {

        System.out.println("1. Ver evaluaciones");
        System.out.println("2. Presentar evaluación");
        System.out.println("3. Cerrar sesión");
    }

    private static Role selectRole() {

        System.out.println();
        System.out.println("Seleccione el rol:");
        System.out.println("1. Administrador");
        System.out.println("2. Autor de preguntas");
        System.out.println("3. Revisor");
        System.out.println("4. Docente");
        System.out.println("5. Estudiante");

        int option = readInt("Opción: ");

        switch (option) {

            case 1:
                return Role.ADMINISTRADOR;

            case 2:
                return Role.AUTOR_DE_PREGUNTAS;

            case 3:
                return Role.REVISOR;

            case 4:
                return Role.DOCENTE;

            case 5:
                return Role.ESTUDIANTE;

            default:
                throw new IllegalArgumentException("Rol inválido.");
        }
    }

    private static UserStatus selectStatus() {

        System.out.println();
        System.out.println("Seleccione el estado:");
        System.out.println("1. Activo");
        System.out.println("2. Inactivo");

        int option = readInt("Opción: ");

        switch (option) {

            case 1:
                return UserStatus.ACTIVO;

            case 2:
                return UserStatus.INACTIVO;

            default:
                throw new IllegalArgumentException("Estado inválido.");
        }
    }

    private static String readText(String message) {

        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private static int readInt(String message) {

        while (true) {

            try {

                System.out.print(message);

                int value = Integer.parseInt(
                        scanner.nextLine().trim()
                );

                return value;

            } catch (NumberFormatException e) {

                System.out.println(
                        "Por favor, ingrese un número válido."
                );
            }
        }
    }
}