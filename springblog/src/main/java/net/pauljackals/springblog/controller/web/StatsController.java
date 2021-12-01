package net.pauljackals.springblog.controller.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import net.pauljackals.springblog.service.AttachmentManager;
import net.pauljackals.springblog.service.AuthorManager;
import net.pauljackals.springblog.service.CommentManager;
import net.pauljackals.springblog.service.PostManager;

@Controller
public class StatsController {
    private AuthorManager authorManager;
    private PostManager postManager;
    private CommentManager commentManager;
    private AttachmentManager attachmentManager;

    public StatsController(@Autowired AuthorManager authorManager, @Autowired PostManager postManager, @Autowired CommentManager commentManager, @Autowired AttachmentManager attachmentManager) {
        this.authorManager = authorManager;
        this.postManager = postManager;
        this.commentManager = commentManager;
        this.attachmentManager = attachmentManager;
    }

    @GetMapping("/stats")
    public String getStats(Model model) {
        model.addAllAttributes(Map.ofEntries(
            Map.entry("posts", postManager.getPosts()),
            Map.entry("comments", commentManager.getComments()),
            Map.entry("authors", authorManager.getAuthors()),
            Map.entry("attachments", attachmentManager.getAttachments())
        ));
        
        return "stats";
    }
}
