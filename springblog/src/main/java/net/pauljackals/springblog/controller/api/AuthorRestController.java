package net.pauljackals.springblog.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import net.pauljackals.springblog.domain.Author;
import net.pauljackals.springblog.service.AuthorManager;

@RestController
public class AuthorRestController {
    private AuthorManager authorManager;

    public AuthorRestController(@Autowired AuthorManager authorManager) {
        this.authorManager = authorManager;
    }

    @GetMapping("/api/author")
    public List<Author> getAuthors() {
        return authorManager.getAuthors();
    }
    @PostMapping("/api/author")
    public Author addAuthor(@RequestBody Author author) {
        return authorManager.addAuthor(author);
    }
}
