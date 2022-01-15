package net.pauljackals.springblog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import net.pauljackals.springblog.domain.Author;
import net.pauljackals.springblog.domain.Post;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {
    @Override
    @Query("select p from Post p order by p.id")
    public List<Post> findAll();

    public List<Post> findAllByAuthorsContainingOrderById(Author author);
}
