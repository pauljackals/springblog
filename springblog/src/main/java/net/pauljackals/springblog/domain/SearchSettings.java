package net.pauljackals.springblog.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchSettings {
    private String tag;
    private String authors;
    private String word;
}
