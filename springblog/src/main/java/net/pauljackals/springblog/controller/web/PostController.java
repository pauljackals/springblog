package net.pauljackals.springblog.controller.web;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PostMapping;

import net.pauljackals.springblog.domain.Author;
import net.pauljackals.springblog.domain.Comment;
import net.pauljackals.springblog.domain.Post;
import net.pauljackals.springblog.domain.helpers.PostExtras;
import net.pauljackals.springblog.domain.helpers.SearchSettings;
import net.pauljackals.springblog.exceptions.ResourceNotFoundException;
import net.pauljackals.springblog.service.AuthorManager;
import net.pauljackals.springblog.service.PostManager;

@Controller
public class PostController {
    private PostManager postManager;
    private AuthorManager authorManager;

    public PostController(
        @Autowired PostManager postManager,
        @Autowired AuthorManager authorManager
    ) {
        this.postManager = postManager;
        this.authorManager = authorManager;
    }

    private List<Author> getAuthorsByUsernames(String usernamesRaw) {
        String[] usernames = usernamesRaw.split(" ");
        List<Author> authors = new ArrayList<>();
        for (String username : usernames) {
            Author author = authorManager.getAuthorByUsername(username);
            authors.add(author);
        }
        return authors;
    }
    private void validateAuthors(String usernamesRaw, List<Author> authors, Errors errors, String field) {
        if(errors.hasFieldErrors(field)) {
            return;
        }
        String[] usernames = usernamesRaw.split(" ");
        List<String> usernamesMissing = new ArrayList<>();
        for (int i = 0; i < authors.size(); i++) {
            if(authors.get(i)==null) {
                usernamesMissing.add(usernames[i]);
            }
        }
        if(usernamesMissing.size()>0) {
            errors.rejectValue(field, "AUTHORS_NOT_FOUND", String.format("authors do not exist: %s", String.join(", ", usernamesMissing)));
        }
    }

    @GetMapping("/post")
    public String addPostForm(Model model) {
        model.addAllAttributes(Map.ofEntries(
            Map.entry("post", new Post()),
            Map.entry("postExtras", new PostExtras())
        ));

        return "postForm";
    }

    @PostMapping("/post")
    public String addPost(
        @Valid @ModelAttribute Post post,
        Errors errorsPost,
        @Valid @ModelAttribute PostExtras postExtras,
        Errors errorsPostExtras,
        Model model
    ) {
        String authorsString = postExtras.getAuthorsString();
        List<Author> authors = getAuthorsByUsernames(authorsString);
        validateAuthors(authorsString, authors, errorsPostExtras, "authorsString");

        if(errorsPost.hasErrors() || errorsPostExtras.hasErrors()) {
            return "postForm";
        }

        post.addAuthors(authors);
        Post postNew = postManager.addPost(post);

        return String.format("redirect:/post/%s", postNew.getId());
    }

    @GetMapping("/post/{id}/edit")
    public String editPostForm(@PathVariable String id, Model model) {
        Post post = postManager.getPost(id);

        if(post==null) {
            throw new ResourceNotFoundException();
        }

        List<String> authorsUsernames = new ArrayList<>();
        for (Author author : post.getAuthors()) {
            authorsUsernames.add(author.getUsername());
        }

        model.addAllAttributes(Map.ofEntries(
            Map.entry("post", post),
            Map.entry("postExtras", new PostExtras(String.join(" ", authorsUsernames))),
            Map.entry("isEdited", true)
        ));
        return "postForm";
    }

    @PostMapping("/post/{id}/edit")
    public String editPost(
        @PathVariable String id,
        @Valid @ModelAttribute Post post,
        Errors errorsPost,
        @Valid @ModelAttribute PostExtras postExtras,
        Errors errorsPostExtras,
        Model model
    ) {
        if(postManager.getPost(id)==null) {
            throw new ResourceNotFoundException();
        }

        String authorsString = postExtras.getAuthorsString();
        List<Author> authors = getAuthorsByUsernames(authorsString);
        validateAuthors(authorsString, authors, errorsPostExtras, "authorsString");

        if(errorsPost.hasErrors() || errorsPostExtras.hasErrors()) {
            model.addAttribute("isEdited", true);
            return "postForm";
        }

        post.addAuthors(authors);
        Post postUpdated = postManager.updatePost(id, post);

        return String.format("redirect:/post/%s", postUpdated.getId());
    }

    @GetMapping("/post/{id}")
    public String getPost(@PathVariable String id, Model model) {
        Post post = postManager.getPost(id);

        if(post==null) {
            throw new ResourceNotFoundException();
        }

        model.addAllAttributes(Map.ofEntries(
            Map.entry("post", post),
            Map.entry("commentTemplate", new Comment())
        ));
        return "post";
    }

    @GetMapping("/")
    public String getPosts(
        @Valid @ModelAttribute SearchSettings searchSettings,
        Errors errors,
        Model model
    ) {
        String id = searchSettings.getId();
        List<Post> posts;
        
        if(errors.hasErrors()) {
            posts = postManager.getPosts();

        } else if(id!=null && id.length()>0) {
            return String.format("redirect:/post/%s", id);

        } else {
            posts = postManager.getPosts(searchSettings);
        }

        model.addAllAttributes(Map.ofEntries(
            Map.entry("posts", posts),
            Map.entry("searchSettings", searchSettings)
        ));
        return "posts";
    }

    @GetMapping("/post/{id}/delete")
    public String removePost(@PathVariable String id) {
        if(postManager.removePost(id)==null) {
            throw new ResourceNotFoundException();
        }

        return "redirect:/";
    }
}
