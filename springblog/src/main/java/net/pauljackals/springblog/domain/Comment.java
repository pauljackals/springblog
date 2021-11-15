package net.pauljackals.springblog.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Comment {
    @NonNull private String id;
    @NonNull private String username;
    @NonNull private Post post;
    @NonNull private String commentContent;
    private int idCSV = -1;
    private int idPostCSV = -1;

    public Comment(int idCSV, int idPostCSV, @NonNull String username, @NonNull String commentContent) {
        this.username = username;
        this.commentContent = commentContent;
        this.idCSV = idCSV;
        this.idPostCSV = idPostCSV;
    }
}
