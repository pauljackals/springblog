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
import org.springframework.web.multipart.MultipartFile;

import net.pauljackals.springblog.domain.Attachment;
import net.pauljackals.springblog.domain.Author;
import net.pauljackals.springblog.domain.Comment;
import net.pauljackals.springblog.domain.Post;
import net.pauljackals.springblog.domain.helpers.PostExtras;
import net.pauljackals.springblog.domain.helpers.SearchSettings;
import net.pauljackals.springblog.exceptions.ResourceNotFoundException;
import net.pauljackals.springblog.service.AttachmentManager;
import net.pauljackals.springblog.service.AuthorManager;
import net.pauljackals.springblog.service.PostManager;
import net.pauljackals.springblog.service.storage.StorageService;

@Controller
public class PostController {
    private PostManager postManager;
    private AuthorManager authorManager;
    private AttachmentManager attachmentManager;
    private StorageService storageService;

    public PostController(
        @Autowired PostManager postManager,
        @Autowired AuthorManager authorManager,
        @Autowired AttachmentManager attachmentManager,
        @Autowired StorageService storageService
    ) {
        this.postManager = postManager;
        this.authorManager = authorManager;
        this.attachmentManager = attachmentManager;
        this.storageService = storageService;
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
    private List<Attachment> createAttachments(List<MultipartFile> files) {
        List<Attachment> attachments = new ArrayList<>();
        if(files.size()>1 || files.size()==1 && files.get(0).getOriginalFilename().length()>0) {
            for (MultipartFile file : files) {
                Attachment attachment = new Attachment();
                attachment.setFilename(file.getOriginalFilename());
                attachments.add(attachment);
            }
        }
        return attachments;
    }
    private void validateAttachments(List<Attachment> original, List<Attachment> toAdd, List<String> toDeleteIds, Errors errors, String field) {
        if(errors.hasFieldErrors(field)) {
            return;
        }
        List<Attachment> current = new ArrayList<>();
        if(toDeleteIds!=null && toDeleteIds.size()>0) {
            for(Attachment attachmentOriginal : original) {
                if(!toDeleteIds.contains(attachmentOriginal.getId())) {
                    current.add(attachmentOriginal);
                }
            }
        } else {
            current.addAll(original);
        }
        List<String> duplicates = new ArrayList<>();
        for(Attachment attachment : toAdd) {
            String filename = attachment.getFilename();
            if(current.stream().anyMatch(attachmentCurrent -> attachmentCurrent.getFilename().equals(filename))) {
                duplicates.add(filename);
            } else {
                current.add(attachment);
            }
        }
        if(duplicates.size()>0) {
            errors.rejectValue(field, "ATTACHMENTS_DUPLICATES", String.format("already present: %s", String.join(", ", duplicates)));
        } else if(current.size()>8) {
            errors.rejectValue(field, "ATTACHMENTS_TOO_MANY", "there must be no more than 8 attachments");
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

        List<MultipartFile> files = postExtras.getAttachmentsFiles();
        List<Attachment> attachments = attachmentManager.addAttachments(createAttachments(files));

        post.addAuthors(authors);
        post.addAttachments(attachments);
        Post postNew = postManager.addPost(post);

        if(attachments.size()>0) {
            String idPost = postNew.getId();
            for(MultipartFile file : files) {
                storageService.store(file, idPost, null);
            }
        }

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
            Map.entry("postExtras", new PostExtras(
                String.join(" ", authorsUsernames),
                new ArrayList<>(),
                new ArrayList<>()
            )),
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
        Post postOriginal = postManager.getPost(id);
        if(postOriginal==null) {
            throw new ResourceNotFoundException();
        }

        String authorsString = postExtras.getAuthorsString();

        List<Attachment> attachmentsOriginal = postOriginal.getAttachments();
        List<MultipartFile> files = postExtras.getAttachmentsFiles();
        List<Attachment> attachmentsToAdd = attachmentManager.addAttachments(createAttachments(files));
        List<Attachment> attachmentsUpdated = new ArrayList<>();
        List<String> attachmentsToDeleteIds = postExtras.getAttachmentsToDelete();
        validateAttachments(attachmentsOriginal, attachmentsToAdd, attachmentsToDeleteIds, errorsPostExtras, "attachmentsFiles");

        List<Author> authors = getAuthorsByUsernames(authorsString);
        validateAuthors(authorsString, authors, errorsPostExtras, "authorsString");

        if(errorsPost.hasErrors() || errorsPostExtras.hasErrors()) {
            post.setAttachments(attachmentsOriginal);
            model.addAttribute("isEdited", true);
            return "postForm";
        }

        if(attachmentsToDeleteIds!=null && attachmentsToDeleteIds.size()>0) {
            List<Attachment> attachmentsToDelete = new ArrayList<>();
            List<Attachment> attachmentsRemaining = new ArrayList<>();
            for(Attachment attachment : attachmentsOriginal) {
                if(attachmentsToDeleteIds.contains(attachment.getId())) {
                    attachmentsToDelete.add(attachment);
                } else {
                    attachmentsRemaining.add(attachment);
                }
            }
            attachmentsUpdated.addAll(attachmentsRemaining);
            for(Attachment attachment : attachmentsToDelete) {
                attachmentManager.removeAttachment(attachment, id);
            }
        } else {
            attachmentsUpdated.addAll(attachmentsOriginal);
        }
        attachmentsUpdated.addAll(attachmentsToAdd);

        post.setAttachments(attachmentsUpdated);

        post.setAuthors(authors);
        Post postUpdated = postManager.updatePost(id, post);

        if(attachmentsToAdd.size()>0) {
            String idPost = postUpdated.getId();
            for(MultipartFile file : files) {
                storageService.store(file, idPost, null);
            }
        }

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
