package net.pauljackals.springblog.domain.helpers;

import javax.validation.constraints.Pattern;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.pauljackals.springblog.validators.duplicates.Duplicates;

@Data
@NoArgsConstructor
public class SearchSettings {
    @Pattern(regexp = "^(\\S{1,32})?$", message = "tag must be no longer than 32 non-whitespace characters")
    private String tag;

    @Pattern(regexp = "^(\\w{1,32}( \\w{1,32}){0,7})?$", message = "there must be no more than 8 usernames, each no longer than 32 letters/numbers")
    @Duplicates
    private String authors;

    @Pattern(regexp = "^(\\S{1,128})?$", message = "word must be no longer than 128 non-whitespace characters")
    private String word;

    private Sorting sortByComments;
    private Sorting sortByAuthors;
    private Sorting sortByContent;
    
    @Pattern(regexp = "^[a-f0-9]{8}-[a-f0-9]{4}-4[a-f0-9]{3}-[89ab][a-f0-9]{3}-[a-f0-9]{12}$", message = "wrong id format")
    String id;
    
    @Pattern(regexp = "^(\\w{1,32})?$", message = "username must be no longer than 32 letters/numbers")
    private String username;
}
