package net.pauljackals.springblog.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import net.pauljackals.springblog.domain.Author;
import net.pauljackals.springblog.repository.AuthorRepository;

@Service
@Transactional
public class AuthorManager {
    private AuthorRepository authorRepository;

    public AuthorManager(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public void setup(List<Author> authors) {
        authorRepository.deleteAll();
        authorRepository.saveAll(authors);
    }

    public Author addAuthor(Author author) {
        return authorRepository.save(author);
    }

    public List<Author> getAuthors() {
        return (List<Author>) authorRepository.findAll();
    }

    public List<Author> getAuthors(String usernamePart) {
        if(usernamePart==null || usernamePart.length()==0) {
            return getAuthors();
        }
        return authorRepository.findByUsernameContaining(usernamePart);
    }

    public Author getAuthor(Long id) {
        Optional<Author> author = authorRepository.findById(id);
        if(author.isPresent()) {
            return author.get();
        
        } else {
            return null;
        }
    }
    public Author getAuthorByUsername(String username) {
        Optional<Author> author = authorRepository.findByUsername(username);
        if(author.isPresent()) {
            return author.get();
        
        } else {
            return null;
        }
    }
}
