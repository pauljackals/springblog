package net.pauljackals.springblog.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Getter;
import net.pauljackals.springblog.domain.Comment;

@Service
@Getter
public class CommentManager {
    private List<Comment> comments;
    
    public CommentManager(@Autowired List<Comment> comments) {
        this.comments = Collections.synchronizedList(new ArrayList<>());
        for (Comment comment : comments) {
            addComment(comment, true);
        }
    }

    public Comment addComment(Comment comment, boolean isFromCSV) {
        Comment commentNew;
        if(!isFromCSV) {
            commentNew = new Comment(
                UUID.randomUUID().toString(),
                comment.getUsername(),
                comment.getCommentContent()
            );
        } else {
            comment.setId(UUID.randomUUID().toString());
            commentNew = comment;
        }
        comments.add(commentNew);
        return commentNew;
    }
    public Comment addComment(Comment comment) {
        return addComment(comment, false);
    }
}
