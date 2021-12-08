package net.pauljackals.springblog.controller.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import net.pauljackals.springblog.domain.Comment;
import net.pauljackals.springblog.domain.User;
import net.pauljackals.springblog.service.CommentManager;
import net.pauljackals.springblog.service.UserManager;

@Controller
public class UserController {
    private UserManager userManager;
    private CommentManager commentManager;

    public UserController(
        @Autowired UserManager userManager,
        @Autowired CommentManager commentManager
    ) {
        this.userManager = userManager;
        this.commentManager = commentManager;
    }

    @GetMapping("/user/{id}")
    public String getUser(@PathVariable String id, Model model) {
        User user = userManager.getUser(id);
        List<Comment> comments = commentManager.getCommentsByUser(user);

        model.addAllAttributes(Map.ofEntries(
            Map.entry("user", user),
            Map.entry("comments", comments)
        ));
        return "user";
    }
}
