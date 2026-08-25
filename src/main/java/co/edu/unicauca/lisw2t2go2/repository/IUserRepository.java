package co.edu.unicauca.lisw2t2go2.repository;

import co.edu.unicauca.lisw2t2go2.domain.User;

import java.util.List;
import java.util.Optional;

/**
 * Abstraccion de acceso a datos de usuarios.
 *
 * DIP (Dependency Inversion): los servicios de alto nivel (UserService,
 * AuthService) dependen de esta interfaz, nunca de una implementacion
 * concreta (SQLite, memoria, otra BD, un mock de pruebas, etc). Cambiar
 * el motor de persistencia no obliga a tocar la logica de negocio.
 *
 * ISP (Interface Segregation): esta interfaz solo tiene los metodos que
 * conciernen a "usuarios". Si manana el sistema tuviera Proyectos o
 * Preguntas, esas entidades tendrian su propio repositorio en vez de
 * agregar metodos aqui.
 *
 * @author Claude
 */
public interface IUserRepository {

    /**
     * Persiste un nuevo usuario.
     */
    void create(User user);

    /**
     * Busca un usuario por su username (usado en registro y login).
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca un usuario por su id.
     */
    Optional<User> findById(int id);

    /**
     * Lista todos los usuarios registrados.
     */
    List<User> listAll();

    /**
     * Actualiza los datos de un usuario existente (por ejemplo, su rol o estado).
     */
    void update(User user);

    /**
     * Elimina un usuario por su id.
     */
    void delete(int id);
}
