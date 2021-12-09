package net.pauljackals.springblog.controller.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/post/add")
    public String addPostForm(Model model) {
        model.addAllAttributes(Map.ofEntries(
            Map.entry("formURL", "/post/add"),
            Map.entry("post", new Post()),
            Map.entry("submitText", "post")
        ));

        return "postForm";
    }

    @PostMapping("/post/add")
    public String addPost(@RequestParam String authorsString, @ModelAttribute Post post, Model model) {
        // model.addAllAttributes(Map.ofEntries(
        //     Map.entry("test", "test")
        // ));

        return "redirect:/post";
    }

    @GetMapping("/post/{id}")
    public String getPost(@PathVariable String id, Model model) {
        Post post = postManager.getPost(id);

        model.addAllAttributes(Map.ofEntries(
            Map.entry("post", post),
            Map.entry("commentTemplate", new Comment())
        ));
        return "post";
    }

    @GetMapping(path = {"/", "/post"})
    public String getPosts(@ModelAttribute SearchSettings searchSettings, @RequestParam(required = false) String id, Model model) {
        if(id!=null && id.length()>0) {
            return String.format("redirect:/post/%s", id);
        }

        List<Post> posts = postManager.getPosts(searchSettings);

        model.addAllAttributes(Map.ofEntries(
            Map.entry("posts", posts),
            Map.entry("searchSettings", searchSettings)
        ));
        return "posts";
    }

    @GetMapping("/post/{id}/delete")
    public String removePost(@PathVariable String id) {
        postManager.removePost(id);

        return "redirect:/post";
    }
}
