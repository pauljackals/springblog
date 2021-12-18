package net.pauljackals.springblog.controller.web;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import net.pauljackals.springblog.service.AttachmentManager;
import net.pauljackals.springblog.service.AuthorManager;
import net.pauljackals.springblog.service.CommentManager;
import net.pauljackals.springblog.service.PostManager;
import net.pauljackals.springblog.service.UserManager;
import net.pauljackals.springblog.service.storage.StorageService;

@Controller
public class StatsController {
    private AuthorManager authorManager;
    private PostManager postManager;
    private CommentManager commentManager;
    private AttachmentManager attachmentManager;
    private UserManager userManager;
    private StorageService storageService;

    public StatsController(
        @Autowired AuthorManager authorManager,
        @Autowired PostManager postManager,
        @Autowired CommentManager commentManager,
        @Autowired AttachmentManager attachmentManager,
        @Autowired UserManager userManager,
        @Autowired StorageService storageService
    ) {
        this.authorManager = authorManager;
        this.postManager = postManager;
        this.commentManager = commentManager;
        this.attachmentManager = attachmentManager;
        this.userManager = userManager;
        this.storageService = storageService;
    }

    @GetMapping("/stats")
    public String getStats(Model model) {
        int attachmentsSize = 0;
        for(Resource file : storageService.loadAllAsResources()) {
            try {
                attachmentsSize += file.getFile().length();
            } catch(IOException e) {
            }
        }


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
            )),
            Map.entry("attachmentsSize", (new BigDecimal(Double.toString(attachmentsSize/1024.0/1024.0))).setScale(2, RoundingMode.HALF_UP).doubleValue())
        ));
        
        return "stats";
    }
}
