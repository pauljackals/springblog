package net.pauljackals.springblog.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import net.pauljackals.springblog.domain.Author;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {
    public Optional<Author> findByUsername(String username);
    public List<Author> findByUsernameContaining(String usernamePart);
}
