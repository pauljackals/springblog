package net.pauljackals.springblog.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
    public Post getPost(@PathVariable("id") String id) {
        Post post = postManager.getPost(id);
        return post;
    }

    @GetMapping("/api/post")
    public List<Post> getPosts() {
        return postManager.getPosts();
    }
}
