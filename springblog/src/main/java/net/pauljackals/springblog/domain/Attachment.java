package net.pauljackals.springblog.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Attachment {
    @NonNull private String id;
    @NonNull private String filename;
    private int idPost = -1;

    public Attachment(int idPost, @NonNull String filename) {
        this.filename = filename;
        this.idPost = idPost;
    }
}
