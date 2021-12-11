package net.pauljackals.springblog.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Post {
    private String id;
    private String postContent;
    private String tags;
    private List<Author> authors = Collections.synchronizedList(new ArrayList<>());
    private List<Attachment> attachments = Collections.synchronizedList(new ArrayList<>());
    private List<Comment> comments = Collections.synchronizedList(new ArrayList<>());
    private int idCSV = -1;

    public Post(String id, String postContent, String tags, List<Author> authors) {
        this.id = id;
        this.postContent = postContent;
        this.tags = tags;
        this.authors.addAll(authors);
    }

    public Post(int idCSV, String postContent, String tags) {
        this.postContent = postContent;
        this.tags = tags;
        this.idCSV = idCSV;
    }

    public void addAuthor(Author author) {
        authors.add(author);
    }
    public void addAuthors(List<Author> authors) {
        this.authors.addAll(authors);
    }

    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }
    public void removeComment(Comment comment) {
        comments.remove(comment);
    }
}
