package net.pauljackals.springblog.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import net.pauljackals.springblog.domain.Author;
import net.pauljackals.springblog.domain.Post;
import net.pauljackals.springblog.service.AuthorManager;
import net.pauljackals.springblog.service.PostManager;

@RestController
public class AuthorRestController {
    private PostManager postManager;
    private AuthorManager authorManager;

    public AuthorRestController(@Autowired AuthorManager authorManager, @Autowired PostManager postManager) {
        this.postManager = postManager;
        this.authorManager = authorManager;
    }

    @GetMapping("/api/author/{username}/post")
    public List<Post> getAuthorPosts(@PathVariable("username") String username) {
        Author author = authorManager.getAuthorByUsername(username);
        List<Post> posts = postManager.getPostsByAuthor(author);
        return posts;
    }
}
