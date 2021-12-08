package net.pauljackals.springblog.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Getter;
import net.pauljackals.springblog.domain.Comment;
import net.pauljackals.springblog.domain.User;

@Service
@Getter
public class CommentManager {
    private List<Comment> comments;
    private UserManager userManager;
    
    public CommentManager(
        @Autowired List<Comment> comments,
        @Autowired UserManager userManager
    ) {
        this.comments = Collections.synchronizedList(new ArrayList<>());
        this.userManager = userManager;
        for (Comment comment : comments) {
            addComment(comment, true);
        }
    }

    public Comment getComment(String id) {
        Comment commentToReturn = null;
        for (Comment comment : comments) {
            if(id.equals(comment.getId())) {
                commentToReturn = comment;
                break;
            }
        }
        return commentToReturn;
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

        User user = userManager.createUserIfNew(commentNew.getUsername());
        commentNew.setUser(user);
        
        comments.add(commentNew);
        return commentNew;
    }
    public Comment addComment(Comment comment) {
        return addComment(comment, false);
    }

    public Comment removeComment(String id) {
        Comment comment = getComment(id);
        if(comment != null) {
            comments.remove(comment);
        }
        return comment;
    }

    public Comment updateComment(String id, Comment commentUpdated) {
        Comment comment = getComment(id);
        if(comment != null) {
            comment.setCommentContent(commentUpdated.getCommentContent());
        }
        return comment;
    }
}
