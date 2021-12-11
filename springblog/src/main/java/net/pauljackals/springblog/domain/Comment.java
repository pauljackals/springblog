package net.pauljackals.springblog.domain;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Comment {
    private String id;

    @NotEmpty(message = "username must not be empty")
    @Size(max = 32, message = "username must have no more than 32 characters")
    private String username;

    @NotEmpty(message = "comment must not be empty")
    @Size(max = 512, message = "comment must have no more than 512 characters")
    private String commentContent;
    
    private User user;
    private int idCSV = -1;
    private int idPost = -1;

    public Comment(String id, String commentContent, User user) {
        this.id = id;
        this.commentContent = commentContent;
        this.user = user;
    }

    public Comment(int idCSV, int idPost, String username, String commentContent) {
        this.username = username;
        this.commentContent = commentContent;
        this.idCSV = idCSV;
        this.idPost = idPost;
    }
}
