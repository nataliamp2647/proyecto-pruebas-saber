package co.unicauca.iso2.taller2.users.domain.access;

import co.unicauca.iso2.taller2.users.domain.User;
import java.util.List;
import java.util.Optional;

/**
 * Abstracción de la que depende la capa de servicio (Inversión de
 * Dependencias). Cualquier tecnología de persistencia (SQLite, memoria,
 * Postgres, etc.) puede implementar este contrato sin que Service ni
 * AuthService se enteren.
 *
 * @author Mani
 */
public interface IUserRepository {

    boolean save(User user);

    boolean update(User user);

    Optional<User> findByLogin(String login);

    List<User> list();

    boolean existsByLogin(String login);
}
