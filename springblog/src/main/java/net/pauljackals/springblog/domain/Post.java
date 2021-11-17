package net.pauljackals.springblog.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Post {
    @NonNull private String id;
    @NonNull private String postContent;
    @NonNull private String tags;
    private List<Attachment> attachments = Collections.synchronizedList(new ArrayList<>());;
    private int idCSV = -1;

    public Post(int idCSV, @NonNull String postContent, @NonNull String tags) {
        this.postContent = postContent;
        this.tags = tags;
        this.idCSV = idCSV;
    }

    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
    }
}
