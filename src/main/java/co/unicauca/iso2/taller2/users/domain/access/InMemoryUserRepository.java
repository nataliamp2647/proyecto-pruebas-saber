package co.unicauca.iso2.taller2.users.domain.access;

import co.unicauca.iso2.taller2.users.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación en memoria de IUserRepository.
 * Permite utilizar la gestión de usuarios sin depender de una
 * tecnología de persistencia concreta.
 *
 * @author Mani
 */
public class InMemoryUserRepository implements IUserRepository {

    private final List<User> users = new ArrayList<>();

    @Override
    public boolean save(User user) {
        if (user == null || existsByLogin(user.getLogin())) {
            return false;
        }

        users.add(user);
        return true;
    }

    @Override
    public boolean update(User user) {
        if (user == null) {
            return false;
        }

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getLogin().equals(user.getLogin())) {
                users.set(i, user);
                return true;
            }
        }

        return false;
    }

    @Override
    public Optional<User> findByLogin(String login) {
        if (login == null) {
            return Optional.empty();
        }

        return users.stream()
                .filter(user -> login.equals(user.getLogin()))
                .findFirst();
    }

    @Override
    public List<User> list() {
        return new ArrayList<>(users);
    }

    @Override
    public boolean existsByLogin(String login) {
        return findByLogin(login).isPresent();
    }
}