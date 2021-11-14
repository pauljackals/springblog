package net.pauljackals.springblog.domain;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Comment {
    @NonNull private String id;
    @NonNull private String username;
    @NonNull private String idPost;
    @NonNull private String commentContent;
    private int idCSV;
    private int idPostCSV;

    public Comment(int idCSV, int idPostCSV, @NonNull String username, @NonNull String commentContent) {
        this.username = username;
        this.commentContent = commentContent;
        this.idCSV = idCSV;
        this.idPostCSV = idPostCSV;
    }
}
