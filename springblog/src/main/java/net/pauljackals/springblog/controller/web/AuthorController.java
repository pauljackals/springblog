package net.pauljackals.springblog.controller.web;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import net.pauljackals.springblog.domain.Author;
import net.pauljackals.springblog.domain.Post;
import net.pauljackals.springblog.domain.helpers.SearchSettings;
import net.pauljackals.springblog.exceptions.ResourceNotFoundException;
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
    public String getUsers(
        @Valid @ModelAttribute SearchSettings searchSettings,
        Errors errors,
        Model model
    ) {
        model.addAllAttributes(Map.ofEntries(
            Map.entry("searchSettings", searchSettings),
            Map.entry("title", "Authors"),
            Map.entry("userURL", "/author")
        ));

        if(errors.hasErrors()) {
            List<Author> users = authorManager.getAuthors();
            model.addAttribute("users", users);
            return "users";
        }

        String username = searchSettings.getUsername();
        List<Author> authors = authorManager.getAuthors(username);
        model.addAttribute("users", authors);

        return "users";
    }

    @GetMapping("/author/{id}")
    public String getAuthor(@PathVariable String id, Model model) {
        Author author = authorManager.getAuthor(id);

        if(author==null) {
            throw new ResourceNotFoundException();
        }

        List<Post> posts = postManager.getPosts(author);

        model.addAllAttributes(Map.ofEntries(
            Map.entry("author", author),
            Map.entry("posts", posts)
        ));
        return "author";
    }
}
