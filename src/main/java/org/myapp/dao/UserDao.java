package org.myapp.dao;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import org.myapp.entity.User;

@ApplicationScoped
public class UserDao implements PanacheRepository<User> {

    public Optional<User> findByUsername(String username) {
        return find("username = ?1", username).firstResultOptional();
    }

    public Optional<User> findByEmail(String email) {
        return find("email = ?1", email).firstResultOptional();
    }

    public boolean existsByUsername(String username) {
        return count("username = ?1", username) > 0;
    }

    public boolean existsByEmail(String email) {
        return count("email = ?1", email) > 0;
    }
}
