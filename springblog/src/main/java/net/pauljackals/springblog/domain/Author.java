package net.pauljackals.springblog.domain;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Author {
    @NonNull private String id;
    @NonNull private String firstName;
    @NonNull private String lastName;
    @NonNull private String username;
    private int idCSV;

    public Author(int idCSV, @NonNull String firstName, @NonNull String lastName, @NonNull String username) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.idCSV = idCSV;
    }
}
