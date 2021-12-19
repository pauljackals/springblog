package net.pauljackals.springblog.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Attachment {
    private String id;
    private String filename;
    private int idPost = -1;

    public Attachment(String id, String filename) {
        this.id = id;
        this.filename = filename;
    }

    public Attachment(int idPost, String filename) {
        this.filename = filename;
        this.idPost = idPost;
    }
}
