package net.pauljackals.springblog.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // !!!!!!!!!!!! MOVE TO EXTRAS !!!!!!!!!!!!!
    @NotNull(message = "username is required")
    @Pattern(regexp = "^\\w{1,32}$", message = "username must not be empty and no longer than 32 letters/numbers")
    private String username;

    @NotEmpty(message = "comment must not be empty")
    @Size(max = 512, message = "comment must be no longer than {max} characters")
    private String commentContent;
    
    private User user;

    public Comment(String commentContent, User user) {
        this.commentContent = commentContent;
        this.user = user;
    }
}
