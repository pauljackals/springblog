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

    public AuthorController(@Autowired AuthorManager authorManager, @Autowired PostManager postManager) {
        this.authorManager = authorManager;
        this.postManager = postManager;
    }

    @GetMapping("/author/{username}")
    public String getAuthor(@PathVariable("username") String username, Model model) {
        Author author = authorManager.getAuthorByUsername(username);
        List<Post> posts = postManager.getPostsByAuthor(author);

        model.addAllAttributes(Map.ofEntries(
            Map.entry("author", author),
            Map.entry("postsSize", posts.size())
        ));
        return "author";
    }
}
