package net.pauljackals.springblog.controller.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import net.pauljackals.springblog.domain.Author;
import net.pauljackals.springblog.domain.Comment;
import net.pauljackals.springblog.domain.Post;
import net.pauljackals.springblog.service.AuthorManager;
import net.pauljackals.springblog.service.CommentManager;
import net.pauljackals.springblog.service.PostManager;

@Controller
public class AuthorController {
    private AuthorManager authorManager;
    private PostManager postManager;
    private CommentManager commentManager;

    public AuthorController(@Autowired AuthorManager authorManager, @Autowired PostManager postManager, @Autowired CommentManager commentManager) {
        this.authorManager = authorManager;
        this.postManager = postManager;
        this.commentManager = commentManager;
    }

    @GetMapping("/author/{username}")
    public String getAuthor(@PathVariable String username, Model model) {
        Author author = authorManager.getAuthorByUsername(username);
        List<Post> posts = postManager.getPostsByAuthor(author);
        List<Comment> comments = commentManager.getCommentsByUsername(author.getUsername());

        model.addAllAttributes(Map.ofEntries(
            Map.entry("author", author),
            Map.entry("posts", posts),
            Map.entry("comments", comments)
        ));
        return "author";
    }
}
