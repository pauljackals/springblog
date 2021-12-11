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

import net.pauljackals.springblog.domain.PostsWithComments;
import net.pauljackals.springblog.domain.User;
import net.pauljackals.springblog.domain.UsernameFilter;
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

    @GetMapping("/user")
    public String getUsers(
        @Valid @ModelAttribute UsernameFilter usernameFilter,
        Errors errors,
        Model model
    ) {
        model.addAllAttributes(Map.ofEntries(
            Map.entry("usernameFilter", usernameFilter),
            Map.entry("title", "Users"),
            Map.entry("userURL", "/user")
        ));

        if(errors.hasErrors()) {
            List<User> users = userManager.getUsers();
            model.addAttribute("users", users);
            return "users";
        }

        String username = usernameFilter.getUsername();
        List<User> users = userManager.getUsers(username);
        model.addAttribute("users", users);
        
        return "users";
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
