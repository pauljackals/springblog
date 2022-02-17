package net.pauljackals.springblog.domain.helpers;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.pauljackals.springblog.domain.Comment;
import net.pauljackals.springblog.domain.Post;

@Data
@AllArgsConstructor
public class PostsWithComments {
    private List<Post> posts;
    private List<List<Comment>> comments;
    private List<Comment> commentsAll;
}
