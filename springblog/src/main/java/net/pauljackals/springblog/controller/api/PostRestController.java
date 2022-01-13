package net.pauljackals.springblog.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import net.pauljackals.springblog.domain.Post;
import net.pauljackals.springblog.service.PostManager;

@RestController
public class PostRestController {
    private PostManager postManager;

    public PostRestController(@Autowired PostManager postManager) {
        this.postManager = postManager;
    }

    @GetMapping("/api/post/{id}")
    public ResponseEntity<?> getPost(@PathVariable String id) {
        Post post;
        try {
            Long idParsed = Long.parseLong(id);
            post = postManager.getPost(idParsed);
        
        } catch(NumberFormatException e) {
            post = null;
        }

        if(post==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }

    @GetMapping("/api/post")
    public List<Post> getPosts() {
        return postManager.getPosts();
    }
}
