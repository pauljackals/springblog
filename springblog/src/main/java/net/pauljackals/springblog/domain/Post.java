package net.pauljackals.springblog.domain;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Post {
    @NonNull private String id;
    @NonNull private String postContent;
    @NonNull private String tags;
    private int idCSV;

    public Post(int idCSV, @NonNull String postContent, @NonNull String tags) {
        this.postContent = postContent;
        this.tags = tags;
        this.idCSV = idCSV;
    }
}
