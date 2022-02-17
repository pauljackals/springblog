package net.pauljackals.springblog.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
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

    @NotEmpty(message = "comment must not be empty")
    @Size(max = 1024, message = "comment must be no longer than {max} characters")
    private String commentContent;
    
    @ManyToOne
    private User user;

    public Comment(String commentContent, User user) {
        this.commentContent = commentContent;
        this.user = user;
    }
}
