package net.pauljackals.springblog.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/api/author/{id}/post")
    public ResponseEntity<?> getAuthorPosts(@PathVariable String id) {
        Author author = authorManager.getAuthor(id);

        if(author==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Author not found");
        }

        List<Post> posts = postManager.getPosts(author);
        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }
}
