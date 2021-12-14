package net.pauljackals.springblog.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Getter;
import net.pauljackals.springblog.domain.Author;

@Getter
@Service
public class AuthorManager {
    private List<Author> authors;

    public AuthorManager(@Autowired List<Author> authors) {
        setup(authors);
    }

    public void setup(List<Author> authors) {
        this.authors = Collections.synchronizedList(new ArrayList<>());
        for (Author author : authors) {
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

    public List<Author> getAuthors(String usernamePart) {
        if(usernamePart==null || usernamePart.length()==0) {
            return this.authors;
        }
        List<Author> authors = new ArrayList<>();
        for (Author author : this.authors) {
            if(author.getUsername().contains(usernamePart)) {
                authors.add(author);
            }
        }
        return authors;
    }

    public Author getAuthor(String id) {
        Author authorToReturn = null;
        for (Author author : authors) {
            if(author.getId().equals(id)) {
                authorToReturn = author;
                break;
            }
        }
        return authorToReturn;
    }
    public Author getAuthorByUsername(String username) {
        Author authorToReturn = null;
        for (Author author : authors) {
            if(author.getUsername().equals(username)) {
                authorToReturn = author;
                break;
            }
        }
        return authorToReturn;
    }
}
