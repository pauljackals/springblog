package net.pauljackals.springblog.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Getter;
import net.pauljackals.springblog.domain.Author;
import net.pauljackals.springblog.domain.Post;
import net.pauljackals.springblog.domain.PostAuthor;

@Getter
@Service
public class AuthorManager {
    private List<Author> authors;

    public AuthorManager(
        @Autowired List<Author> authors,
        @Autowired List<PostAuthor> postsAuthors,
        @Autowired PostManager postManager
    ) {
        this.authors = Collections.synchronizedList(new ArrayList<>());
        for (Author author : authors) {
            int authorIdCSV = author.getIdCSV();
            List<Integer> authorCurrentPostsIds = new ArrayList<>();
            for (PostAuthor postAuthor : postsAuthors) {
                if(authorIdCSV == postAuthor.getIdAuthorCSV()) {
                    authorCurrentPostsIds.add(postAuthor.getIdPostCSV());
                }
            }
            if(authorCurrentPostsIds.size() > 0) {
                List<Post> posts = postManager.getPosts();
                int counter = authorCurrentPostsIds.size();
                for (int i=0; i<posts.size(); i++) {
                    Post post = posts.get(i);
                    int postIdCSV = post.getIdCSV();
                    if(authorCurrentPostsIds.contains(postIdCSV)) {
                        author.addPost(post);
                        counter--;
                    }
                    if(counter == 0) {
                        break;
                    }
                }
            }
    
            addAuthor(author, true);
        }
    }

    public Author addAuthor(Author author, boolean isFromCSV) {
        Author authorNew;
        if(!isFromCSV) {
            authorNew = new Author(
                UUID.randomUUID().toString(),
                author.getFirstName(),
                author.getLastName(),
                author.getUsername()
            );
        } else {
            author.setId(UUID.randomUUID().toString());
            authorNew = author;
        }
        authors.add(authorNew);
        return authorNew;
    }
    public Author addAuthor(Author author) {
        return addAuthor(author, false);
    }
}
