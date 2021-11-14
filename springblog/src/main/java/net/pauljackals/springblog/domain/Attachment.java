package net.pauljackals.springblog.domain;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Attachment {
    @NonNull private String id;
    @NonNull private String filename;
    private int idCSV;

    public Attachment(int idCSV, @NonNull String filename) {
        this.filename = filename;
        this.idCSV = idCSV;
    }
}
