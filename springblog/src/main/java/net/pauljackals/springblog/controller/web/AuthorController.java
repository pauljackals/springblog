package net.pauljackals.springblog.controller.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
