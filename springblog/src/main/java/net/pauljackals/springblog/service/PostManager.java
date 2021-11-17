package net.pauljackals.springblog.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Getter;
import net.pauljackals.springblog.domain.Attachment;
import net.pauljackals.springblog.domain.Author;
import net.pauljackals.springblog.domain.Comment;
import net.pauljackals.springblog.domain.Post;
import net.pauljackals.springblog.domain.PostAuthor;

@Service
@Getter
public class PostManager {
    private List<Post> posts;
    
    public PostManager(
        @Autowired List<Post> posts,
        @Autowired List<PostAuthor> postsAuthors,
        @Autowired AuthorManager authorManager,
        @Autowired AttachmentManager attachmentManager,
        @Autowired CommentManager commentManager
    ) {
        this.posts = Collections.synchronizedList(new ArrayList<>());
        List<Author> authors = authorManager.getAuthors();
        List<Attachment> attachments = attachmentManager.getAttachments();
        List<Comment> comments = commentManager.getComments();

        for (Post post : posts) {
            int idPost = post.getIdCSV();
            List<Integer> postCurrentAuthorsIds = new ArrayList<>();
            for (PostAuthor postAuthor : postsAuthors) {
                if(idPost == postAuthor.getIdPost()) {
                    postCurrentAuthorsIds.add(postAuthor.getIdAuthor());
                }
            }
            if(postCurrentAuthorsIds.size() > 0) {
                int counter = postCurrentAuthorsIds.size();
                for (int i=0; i<authors.size(); i++) {
                    Author author = authors.get(i);
                    int idAuthor = author.getIdCSV();
                    if(postCurrentAuthorsIds.contains(idAuthor)) {
                        post.addAuthor(author);
                        counter--;
                    }
                    if(counter == 0) {
                        break;
                    }
                }
            }

            for (Attachment attachment : attachments) {
                if(attachment.getIdPost() == idPost) {
                    post.addAttachment(attachment);
                }
            }
            for (Comment comment : comments) {
                if(comment.getIdPost() == idPost) {
                    post.addComment(comment);
                }
            }
            addPost(post, true);
        }
    }

    public Post addPost(Post post, boolean isFromCSV) {
        Post postNew;
        if(!isFromCSV) {
            postNew = new Post(
                UUID.randomUUID().toString(),
                post.getPostContent(),
                post.getTags()
            );
        } else {
            post.setId(UUID.randomUUID().toString());
            postNew = post;
        }
        posts.add(postNew);
        return postNew;
    }
    public Post addPost(Post post) {
        return addPost(post, false);
    }
}
