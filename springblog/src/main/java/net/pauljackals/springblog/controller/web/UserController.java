package net.pauljackals.springblog.controller.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import net.pauljackals.springblog.domain.PostsWithComments;
import net.pauljackals.springblog.domain.User;
import net.pauljackals.springblog.service.PostManager;
import net.pauljackals.springblog.service.UserManager;

@Controller
public class UserController {
    private UserManager userManager;
    private PostManager postManager;

    public UserController(
        @Autowired UserManager userManager,
        @Autowired PostManager postManager
    ) {
        this.userManager = userManager;
        this.postManager = postManager;
    }

    @GetMapping("/user/{id}")
    public String getUser(@PathVariable String id, Model model) {
        User user = userManager.getUser(id);
        PostsWithComments postsWithComments = postManager.getPostsWithComments(user);

        model.addAllAttributes(Map.ofEntries(
            Map.entry("user", user),
            Map.entry("posts", postsWithComments.getPosts()),
            Map.entry("comments", postsWithComments.getComments()),
            Map.entry("commentsAll", postsWithComments.getCommentsAll())
        ));
        return "user";
    }
}
