package net.pauljackals.springblog.controller.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import net.pauljackals.springblog.service.AttachmentManager;
import net.pauljackals.springblog.service.AuthorManager;
import net.pauljackals.springblog.service.CommentManager;
import net.pauljackals.springblog.service.PostManager;
import net.pauljackals.springblog.service.UserManager;

@Controller
public class StatsController {
    private AuthorManager authorManager;
    private PostManager postManager;
    private CommentManager commentManager;
    private AttachmentManager attachmentManager;
    private UserManager userManager;

    public StatsController(
        @Autowired AuthorManager authorManager,
        @Autowired PostManager postManager,
        @Autowired CommentManager commentManager,
        @Autowired AttachmentManager attachmentManager,
        @Autowired UserManager userManager
    ) {
        this.authorManager = authorManager;
        this.postManager = postManager;
        this.commentManager = commentManager;
        this.attachmentManager = attachmentManager;
        this.userManager = userManager;
    }

    @GetMapping("/stats")
    public String getStats(Model model) {
        model.addAllAttributes(Map.ofEntries(
            Map.entry("lists", List.of(
                postManager.getPosts(),
                commentManager.getComments(),
                authorManager.getAuthors(),
                userManager.getUsers(),
                attachmentManager.getAttachments()
            )),
            Map.entry("names", List.of(
                "posts",
                "comments",
                "authors",
                "users",
                "attachments"
            ))
        ));
        
        return "stats";
    }
}
