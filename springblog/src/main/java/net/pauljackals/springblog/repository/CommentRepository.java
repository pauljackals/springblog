package net.pauljackals.springblog.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import net.pauljackals.springblog.domain.Comment;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {
    
}
