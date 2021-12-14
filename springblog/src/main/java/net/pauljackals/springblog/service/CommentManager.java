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
        setup(comments, userManager);
    }
    
    public void setup(List<Comment> comments, UserManager userManager) {
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
        User user = userManager.createUserIfNew(comment.getUsername());
        if(!isFromCSV) {
            commentNew = new Comment(
                UUID.randomUUID().toString(),
                comment.getCommentContent(),
                user
            );
        } else {
            comment.setId(UUID.randomUUID().toString());
            comment.setUser(user);
            commentNew = comment;
        }
        
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
    public Comment removeComment(Comment comment) {
        return comments.remove(comment) ? comment : null;
    }

    public Comment updateComment(String id, Comment commentUpdated) {
        Comment comment = getComment(id);
        if(comment != null) {
            comment.setCommentContent(commentUpdated.getCommentContent());
        }
        return comment;
    }
}
