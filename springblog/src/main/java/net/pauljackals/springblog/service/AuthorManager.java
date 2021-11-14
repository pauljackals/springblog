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
        this.authors = Collections.synchronizedList(new ArrayList<>());
        for (Author author : authors) {
            add(author, true);
        }
    }

    public Author add(Author author, boolean isFromCSV) {
        Author authorNew = new Author(
            UUID.randomUUID().toString(),
            author.getFirstName(),
            author.getLastName(),
            author.getUsername()
        );
        if(isFromCSV) {
            authorNew.setIdCSV(author.getIdCSV());
        }
        authors.add(authorNew);
        return authorNew;
    }
    public Author add(Author author) {
        return add(author, false);
    }
}
