package net.pauljackals.springblog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import net.pauljackals.springblog.domain.Comment;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {
    @Override
    @Query("select c from Comment c order by c.id")
    public List<Comment> findAll();
}
