package net.pauljackals.springblog.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping("/post/{idPostPath}/comment")
    public String addComment(@PathVariable("idPostPath") String idPost, @ModelAttribute Comment comment, Model model) {
        Comment commentNew = commentManager.addComment(comment);
        Post post = postManager.getPost(idPost);
        post.addComment(commentNew);

        return String.format("redirect:/post/%s", post.getId());
    }

    @GetMapping("/post/{idPostPath}/comment/{id}/delete")
    public String removeComment(@PathVariable("idPostPath") String idPost, @PathVariable("id") String id, Model model) {
        Comment comment = commentManager.removeComment(id);
        Post post = postManager.getPost(idPost);
        post.removeComment(comment);

        return String.format("redirect:/post/%s", post.getId());
    }
}
