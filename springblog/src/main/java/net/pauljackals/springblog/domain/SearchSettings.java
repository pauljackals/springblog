package net.pauljackals.springblog.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchSettings {
    private String tag;
    private String authors;
    private String word;
    private Sorting sortByComments;
    private Sorting sortByAuthors;
    private Sorting sortByContent;
}
