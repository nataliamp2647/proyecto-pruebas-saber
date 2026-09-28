package co.edu.unicauca.lisw2t5g02.access;

import co.edu.unicauca.lisw2t5g02.domain.user.User;
import co.edu.unicauca.lisw2t5g02.domain.user.IUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementacion en memoria de {@link IUserRepository}.
 *
 * Sirve para:
 *  - Probar UserService/AuthService sin depender de una base de datos real.
 *  - Ejemplificar OCP/DIP: se puede sustituir por {@link SQLiteUserRepository}
 *    sin cambiar ni una linea de la logica de negocio.
 *
 * @author David Santiago Cruz Varón, Juan Felipe Gallardo Orozco
 */
public class InMemoryUserRepository implements IUserRepository {

    private final List<User> users = new ArrayList<>();
    private final AtomicInteger sequence = new AtomicInteger(1);

    @Override
    public void create(User user) {
        user.setId(sequence.getAndIncrement());
        users.add(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    @Override
    public Optional<User> findById(int id) {
        return users.stream()
                .filter(u -> u.getId() == id)
                .findFirst();
    }

    @Override
    public List<User> listAll() {
        return new ArrayList<>(users);
    }

    @Override
    public void update(User user) {
        delete(user.getId());
        users.add(user);
    }

    @Override
    public void delete(int id) {
        users.removeIf(u -> u.getId() == id);
    }
}
