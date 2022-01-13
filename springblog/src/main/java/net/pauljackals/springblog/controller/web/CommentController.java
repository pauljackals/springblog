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
import net.pauljackals.springblog.exceptions.ResourceNotFoundException;
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
        Post post;
        try {
            Long idParsed = Long.parseLong(idPost);
            post = postManager.getPost(idParsed);

        } catch (Exception e) {
            post = null;
        }

        if(post==null) {
            throw new ResourceNotFoundException();
        }

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
        Post post;
        try {
            Long idParsed = Long.parseLong(idPost);
            post = postManager.getPost(idParsed);

        } catch (Exception e) {
            post = null;
        }

        if(post==null) {
            throw new ResourceNotFoundException();
        }

        Comment comment;
        try {
            Long idParsed = Long.parseLong(id);
            comment = commentManager.getComment(idParsed);
        
        } catch(NumberFormatException e) {
            comment = null;
        }

        if(comment==null || !post.removeComment(comment)) {
            throw new ResourceNotFoundException();
        }

        commentManager.removeComment(comment);

        return String.format("redirect:/post/%s", post.getId());
    }

    @GetMapping("/post/{idPost}/comment/{id}/edit")
    public String getCommentEdit(@PathVariable String idPost, @PathVariable String id, Model model) {
        Post post;
        try {
            Long idParsed = Long.parseLong(idPost);
            post = postManager.getPost(idParsed);

        } catch (Exception e) {
            post = null;
        }

        Comment comment;
        try {
            Long idParsed = Long.parseLong(id);
            comment = commentManager.getComment(idParsed);
        
        } catch(NumberFormatException e) {
            comment = null;
        }

        if(post==null || comment==null) {
            throw new ResourceNotFoundException();
        }

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
        Post post;
        try {
            Long idParsed = Long.parseLong(idPost);
            post = postManager.getPost(idParsed);

        } catch (Exception e) {
            post = null;
        }

        Comment comment;
        Long idParsed = null;
        try {
            idParsed = Long.parseLong(id);
            comment = commentManager.getComment(idParsed);
        
        } catch(NumberFormatException e) {
            comment = null;
        }

        if(post==null || comment==null) {
            throw new ResourceNotFoundException();
        }

        if(errors.hasErrors()) {
            commentEdited.setId(idParsed);
            commentEdited.setUser(comment.getUser());
            model.addAllAttributes(Map.ofEntries(
                Map.entry("post", post),
                Map.entry("commentTemplate", new Comment())
            ));
            return "post";
        }
        Comment commentUpdated = commentManager.updateComment(idParsed, commentEdited);

        return String.format("redirect:/post/%s#c_%s", post.getId(), commentUpdated.getId());
    }
}
