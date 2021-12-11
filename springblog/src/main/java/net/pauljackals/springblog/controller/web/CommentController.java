package net.pauljackals.springblog.controller.web;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
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
    private CommentManager commentManager;
    private PostManager postManager;

    public CommentController(@Autowired PostManager postManager, @Autowired CommentManager commentManager) {
        this.commentManager = commentManager;
        this.postManager = postManager;
    }

    @PostMapping("/post/{idPostPath}")
    public String addComment(
        @PathVariable("idPostPath") String idPost,
        @Valid @ModelAttribute("commentTemplate") Comment comment,
        Errors errors,
        Model model
    ) {
        Post post = postManager.getPost(idPost);
        if(errors.hasErrors()) {
            model.addAttribute("post", post);
            return "post";
        }
        Comment commentNew = commentManager.addComment(comment);
        post.addComment(commentNew);

        return String.format("redirect:/post/%s", post.getId());
    }

    @GetMapping("/post/{idPostPath}/comment/{id}/delete")
    public String removeComment(@PathVariable("idPostPath") String idPost, @PathVariable("id") String id) {
        Post post = postManager.getPost(idPost);
        Comment comment = commentManager.removeComment(id);
        post.removeComment(comment);

        return String.format("redirect:/post/%s", post.getId());
    }

    @GetMapping("/post/{idPost}/comment/{id}/edit")
    public String getCommentEdit(@PathVariable String idPost, @PathVariable String id, Model model) {
        Post post = postManager.getPost(idPost);
        Comment comment = commentManager.getComment(id);

        model.addAllAttributes(Map.ofEntries(
            Map.entry("post", post),
            Map.entry("commentTemplate", new Comment()),
            Map.entry("commentEdited", comment)
        ));
        return "post";
    }

    @PostMapping("/post/{idPostPath}/comment/{idPath}/edit")
    public String updateComment(
        @PathVariable("idPostPath") String idPost,
        @PathVariable("idPath") String id,
        @Valid @ModelAttribute("commentEdited") Comment commentEdited,
        Errors errors,
        Model model
    ) {
        Post post = postManager.getPost(idPost);
        Comment comment = commentManager.getComment(id);
        if(errors.hasErrors()) {
            commentEdited.setId(id);
            commentEdited.setUser(comment.getUser());
            model.addAllAttributes(Map.ofEntries(
                Map.entry("post", post),
                Map.entry("commentTemplate", new Comment())
            ));
            return "post";
        }
        Comment commentUpdated = commentManager.updateComment(id, commentEdited);

        return String.format("redirect:/post/%s#c_%s", post.getId(), commentUpdated.getId());
    }
}
