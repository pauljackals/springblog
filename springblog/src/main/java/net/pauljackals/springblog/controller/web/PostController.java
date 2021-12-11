package net.pauljackals.springblog.controller.web;

import java.util.ArrayList;
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

import net.pauljackals.springblog.domain.Author;
import net.pauljackals.springblog.domain.Comment;
import net.pauljackals.springblog.domain.Post;
import net.pauljackals.springblog.domain.SearchSettings;
import net.pauljackals.springblog.service.AuthorManager;
import net.pauljackals.springblog.service.PostManager;

@Controller
public class PostController {
    private PostManager postManager;
    private AuthorManager authorManager;

    public PostController(
        @Autowired PostManager postManager,
        @Autowired AuthorManager authorManager
    ) {
        this.postManager = postManager;
        this.authorManager = authorManager;
    }

    private List<Author> getAuthorsByUsernames(String[] usernames) {
        List<Author> authors = new ArrayList<>();
        for (String username : usernames) {
            Author author = authorManager.getAuthorByUsername(username);
            authors.add(author);
        }
        return authors;
    }

    @GetMapping("/post")
    public String addPostForm(Model model) {
        model.addAllAttributes(Map.ofEntries(
            Map.entry("post", new Post()),
            Map.entry("authorsString", "")
        ));

        return "postForm";
    }

    @PostMapping("/post")
    public String addPost(@ModelAttribute Post post, @RequestParam String authorsString, Model model) {
        List<Author> authors = getAuthorsByUsernames(authorsString.split(" "));
        post.addAuthors(authors);
        Post postNew = postManager.addPost(post);

        return String.format("redirect:/post/%s", postNew.getId());
    }

    @GetMapping("/post/{id}/edit")
    public String editPostForm(@PathVariable String id, Model model) {
        Post post = postManager.getPost(id);
        List<String> authorsUsernames = new ArrayList<>();
        for (Author author : post.getAuthors()) {
            authorsUsernames.add(author.getUsername());
        }

        model.addAllAttributes(Map.ofEntries(
            Map.entry("post", post),
            Map.entry("authorsString", String.join(" ", authorsUsernames)),
            Map.entry("isEdited", true)
        ));
        return "postForm";
    }

    @PostMapping("/post/{id}/edit")
    public String editPost(@PathVariable String id, @ModelAttribute Post post, @RequestParam String authorsString, Model model) {
        List<Author> authors = getAuthorsByUsernames(authorsString.split(" "));
        post.addAuthors(authors);
        Post postUpdated = postManager.updatePost(id, post);

        return String.format("redirect:/post/%s", postUpdated.getId());
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

    @GetMapping("/")
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

        return "redirect:/";
    }
}
