package net.pauljackals.springblog.domain.helpers;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchSettings {
    @Size(max = 32, message = "tag must be no longer than 32 characters")
    @Pattern(regexp = "^|\\S+$", message = "tag must not contain any whitespace characters")
    private String tag;

    @Size(max = 263, message = "field must be no longer than 263 characters")
    @Pattern(regexp = "^|\\w+( \\w+)*$", message = "usernames contain only letters and numbers")
    private String authors;

    @Size(max = 128, message = "word must be no longer than 128 characters")
    @Pattern(regexp = "^|\\S+$", message = "word must not contain any whitespace characters")
    private String word;

    private Sorting sortByComments;
    private Sorting sortByAuthors;
    private Sorting sortByContent;
    
    @Pattern(regexp = "^[a-f0-9]{8}-[a-f0-9]{4}-4[a-f0-9]{3}-[89ab][a-f0-9]{3}-[a-f0-9]{12}$", message = "wrong id format")
    String id;
}
