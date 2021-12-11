package net.pauljackals.springblog.domain.helpers;

import javax.validation.constraints.Pattern;

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
    
    @Pattern(regexp = "^[a-f0-9]{8}-[a-f0-9]{4}-4[a-f0-9]{3}-[89ab][a-f0-9]{3}-[a-f0-9]{12}$", message = "wrong id format")
    String id;
}
