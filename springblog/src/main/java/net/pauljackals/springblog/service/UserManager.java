package net.pauljackals.springblog.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import net.pauljackals.springblog.domain.User;
import net.pauljackals.springblog.repository.UserRepository;

@Service
@Transactional
public class UserManager {
    private UserRepository userRepository;

    public UserManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setup() {
        userRepository.deleteAll();
    }

    public List<User> getUsers() {
        return (List<User>) userRepository.findAll();
    }

    public List<User> getUsers(String usernamePart) {
        if(usernamePart==null || usernamePart.length()==0) {
            return getUsers();
        }
        return userRepository.findByUsernameContaining(usernamePart);
    }

    public User getUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()) {
            return user.get();
        
        } else {
            return null;
        }
    }

    public User getUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isPresent()) {
            return user.get();
            
        } else {
            return null;
        }
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public User createUserIfNew(String username) {
        User userFound = getUser(username);

        if(userFound!=null) {
            return userFound;
        } else {
            return addUser(new User(username));
        }
    }
}
