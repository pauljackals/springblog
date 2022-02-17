package net.pauljackals.springblog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import net.pauljackals.springblog.domain.User;

public interface UserRepository extends CrudRepository<User, Long> {
    public Optional<User> findByUsername(String username);
    public List<User> findByUsernameContaining(String usernamePart);
}
