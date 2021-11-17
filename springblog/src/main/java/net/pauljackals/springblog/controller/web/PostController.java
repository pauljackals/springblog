package net.pauljackals.springblog.controller.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import net.pauljackals.springblog.service.PostManager;

@Controller
public class PostController {
    private PostManager postManager;

    public PostController(@Autowired PostManager postManager) {
        this.postManager = postManager;
    }

    @GetMapping("/")
    public String posts(Model model) {
        model.addAllAttributes(Map.ofEntries(
            Map.entry("title", "Posts"),
            Map.entry("textComments", "comments"),
            Map.entry("posts", postManager.getPosts())
        ));
        return "posts";
    }
}
