package net.pauljackals.springblog.controller.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import net.pauljackals.springblog.domain.Author;
import net.pauljackals.springblog.domain.Post;
import net.pauljackals.springblog.service.AuthorManager;
import net.pauljackals.springblog.service.PostManager;

@Controller
public class AuthorController {
    private AuthorManager authorManager;
    private PostManager postManager;

    public AuthorController(
        @Autowired AuthorManager authorManager,
        @Autowired PostManager postManager
    ) {
        this.authorManager = authorManager;
        this.postManager = postManager;
    }

    @GetMapping("/author")
    public String getUsers(@RequestParam(required = false) String username, Model model) {
        List<Author> authors = authorManager.getAuthors(username);

        model.addAllAttributes(Map.ofEntries(
            Map.entry("users", authors),
            Map.entry("title", "Authors"),
            Map.entry("userURL", "/author"),
            Map.entry("username", username!=null ? username : "")
        ));
        return "users";
    }

    @GetMapping("/author/{id}")
    public String getAuthor(@PathVariable String id, Model model) {
        Author author = authorManager.getAuthor(id);
        List<Post> posts = postManager.getPosts(author);

        model.addAllAttributes(Map.ofEntries(
            Map.entry("author", author),
            Map.entry("posts", posts)
        ));
        return "author";
    }
}
