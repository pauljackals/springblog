package net.pauljackals.springblog.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Author {
    @NonNull private String id;
    @NonNull private String firstName;
    @NonNull private String lastName;
    @NonNull private String username;
    // private List<Post> posts = Collections.synchronizedList(new ArrayList<>());
    private int idCSV = -1;

    public Author(int idCSV, @NonNull String firstName, @NonNull String lastName, @NonNull String username) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.idCSV = idCSV;
    }

    // public void addPost(Post post) {
    //     posts.add(post);
    // }
}
