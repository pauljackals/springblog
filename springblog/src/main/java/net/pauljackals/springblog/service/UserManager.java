package net.pauljackals.springblog.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.Getter;
import net.pauljackals.springblog.domain.User;

@Service
@Getter
public class UserManager {
    private List<User> users;

    public UserManager() {
        this.users = Collections.synchronizedList(new ArrayList<>());
    }

    public List<User> getUsers(String username) {
        if(username==null || username.length()==0) {
            return this.users;
        }
        List<User> users = new ArrayList<>();
        for (User user : this.users) {
            if(user.getUsername().contains(username)) {
                users.add(user);
            }
        }
        return users;
    }

    public User getUser(String id) {
        User userToReturn = null;
        for (User user : users) {
            if(user.getId().equals(id)) {
                userToReturn = user;
                break;
            }
        }
        return userToReturn;
    }

    public User addUser(User user) {
        User userNew = new User(
            UUID.randomUUID().toString(),
            user.getUsername()
        );
        users.add(userNew);
        return userNew;
    }

    public User createUserIfNew(String username) {
        User userFound = null;
        for (User user : users) {
            if(user.getUsername().equals(username)) {
                userFound = user;
                break;
            }
        }
        if(userFound!=null) {
            return userFound;
        } else {
            User user = addUser(new User(username));
            return user;
        }
    }
}
