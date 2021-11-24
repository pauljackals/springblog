package net.pauljackals.springblog.controller.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import net.pauljackals.springblog.domain.Post;
import net.pauljackals.springblog.service.PostManager;

@Controller
public class PostController {
    private PostManager postManager;

    public PostController(@Autowired PostManager postManager) {
        this.postManager = postManager;
    }

    @GetMapping("/post/{id}")
    public String getPost(@PathVariable("id") String id, Model model) {
        Post post = postManager.getPost(id);

        model.addAllAttributes(Map.ofEntries(
            Map.entry("title", "Post"),
            Map.entry("post", post)
        ));
        return "post";
    }

    @GetMapping("/")
    public String getPosts(Model model) {
        model.addAllAttributes(Map.ofEntries(
            Map.entry("title", "Posts"),
            Map.entry("textComments", "comments"),
            Map.entry("posts", postManager.getPosts())
        ));
        return "posts";
    }
}