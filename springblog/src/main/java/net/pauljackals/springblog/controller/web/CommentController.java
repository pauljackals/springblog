package net.pauljackals.springblog.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import net.pauljackals.springblog.domain.Comment;
import net.pauljackals.springblog.domain.Post;
import net.pauljackals.springblog.service.CommentManager;
import net.pauljackals.springblog.service.PostManager;

@Controller
public class CommentController {
    CommentManager commentManager;
    PostManager postManager;

    public CommentController(
        @Autowired PostManager postManager,
        @Autowired CommentManager commentManager
    ) {
        this.commentManager = commentManager;
        this.postManager = postManager;
    }

    @PostMapping("/comment/{idPostPath}")
    public String addComment(@PathVariable("idPostPath") String idPost, @ModelAttribute Comment comment, Model model) {
        Post post = postManager.getPost(idPost);
        Comment commentNew = commentManager.addComment(comment);
        post.addComment(commentNew);

        return String.format("redirect:/post/%s", post.getId());
    }
}
