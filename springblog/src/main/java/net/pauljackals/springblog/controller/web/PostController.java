package net.pauljackals.springblog.controller.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import net.pauljackals.springblog.domain.Comment;
import net.pauljackals.springblog.domain.Post;
import net.pauljackals.springblog.domain.SearchSettings;
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
            Map.entry("post", post),
            Map.entry("commentTemplate", new Comment())
        ));
        return "post";
    }

    @GetMapping("/")
    public String getPosts(@ModelAttribute SearchSettings searchSettings, Model model) {
        List<Post> posts = postManager.getPosts(searchSettings);

        model.addAllAttributes(Map.ofEntries(
            Map.entry("posts", posts),
            Map.entry("searchSettings", searchSettings)
        ));
        return "posts";
    }
}
