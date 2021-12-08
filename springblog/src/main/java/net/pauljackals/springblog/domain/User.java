package net.pauljackals.springblog.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private String id;
    private String username;

    public User(String username) {
        this.username = username;
    }
}
