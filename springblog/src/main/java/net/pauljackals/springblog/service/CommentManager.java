package net.pauljackals.springblog.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import net.pauljackals.springblog.domain.Comment;
import net.pauljackals.springblog.repository.CommentRepository;

@Service
@Transactional
public class CommentManager {
    private CommentRepository commentRepository;
    
    public CommentManager(
        CommentRepository commentRepository
    ) {
        this.commentRepository = commentRepository;
    }
    
    public void setup(List<Comment> comments) {
        commentRepository.saveAll(comments);
    }

    public List<Comment> getComments() {
        return (List<Comment>) commentRepository.findAll();
    }

    public Comment getComment(Long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if(comment.isPresent()) {
            return comment.get();
        
        } else {
            return null;
        }
    }

    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public Comment removeComment(Long id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if(commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            commentRepository.delete(comment);
            return comment;

        } else {
            return null;
        }
    }
    
    public Comment removeComment(Comment comment) {
        if(commentRepository.existsById(comment.getId())) {
            commentRepository.delete(comment);
            return comment;

        } else {
            return null;
        }
    }

    public Comment updateComment(Comment comment, Comment commentUpdated) {
        comment.setCommentContent(commentUpdated.getCommentContent());
        return commentRepository.save(comment);
    }
}
