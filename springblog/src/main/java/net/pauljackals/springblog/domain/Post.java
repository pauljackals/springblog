package net.pauljackals.springblog.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.pauljackals.springblog.validators.duplicates.Duplicates;

@Data
@NoArgsConstructor
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "post must not be empty")
    @Size(max = 1024, message = "post must be no longer than {max} characters")
    private String postContent;

    @Pattern(regexp = "^\\S{1,32}( \\S{1,32}){0,15}$", message = "there must be between 1 and 16 tags, each no longer than 32 characters")
    @Duplicates
    private String tags;

    private List<Author> authors = Collections.synchronizedList(new ArrayList<>());
    private List<Attachment> attachments = Collections.synchronizedList(new ArrayList<>());
    private List<Comment> comments = Collections.synchronizedList(new ArrayList<>());

    public Post(String postContent, String tags, List<Author> authors, List<Attachment> attachments) {
        this.postContent = postContent;
        this.tags = tags;
        this.authors.addAll(authors);
        this.attachments.addAll(attachments);
    }

    public void addAuthor(Author author) {
        authors.add(author);
    }
    public void addAuthors(List<Author> authors) {
        this.authors.addAll(authors);
    }

    public void setAuthors(List<Author> authors) {
        this.authors.clear();
        addAuthors(authors);
    }

    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
    }
    public void addAttachments(List<Attachment> attachments) {
        this.attachments.addAll(attachments);
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments.clear();
        addAttachments(attachments);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }
    public boolean removeComment(Comment comment) {
        return comments.remove(comment);
    }
}
