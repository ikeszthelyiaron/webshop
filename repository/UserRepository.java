package hu.progmasters.webshop.repository;

import hu.progmasters.webshop.domain.Role;
import hu.progmasters.webshop.domain.User;
import org.springframework.data.repository.ListCrudRepository;


import java.util.Optional;


public interface UserRepository extends ListCrudRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    boolean existsByRole(Role role);

    boolean existsByUsername(String username);

}
