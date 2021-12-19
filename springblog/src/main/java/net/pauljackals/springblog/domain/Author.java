package net.pauljackals.springblog.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Author {
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private int idCSV = -1;
    
    public Author(String id, String firstName, String lastName, String username) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
    }

    public Author(int idCSV, String firstName, String lastName, String username) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.idCSV = idCSV;
    }
}
