package co.edu.unicauca.liswiit2g9.access;
import java.util.List;
import java.util.Optional;

import co.edu.unicauca.liswiit2g9.domain.User;



public interface UserRepository {

    void save(User user);

    Optional<User> findByUsername(String username);

    List<User> findAll();
}