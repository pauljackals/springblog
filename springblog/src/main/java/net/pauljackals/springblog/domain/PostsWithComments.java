package net.pauljackals.springblog.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostsWithComments {
    private List<Post> posts;
    private List<List<Comment>> comments;
    private List<Comment> commentsAll;
}
