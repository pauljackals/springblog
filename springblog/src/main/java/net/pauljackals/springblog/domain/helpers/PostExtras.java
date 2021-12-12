package net.pauljackals.springblog.domain.helpers;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.pauljackals.springblog.validators.duplicates.Duplicates;
import net.pauljackals.springblog.validators.elements.Elements;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostExtras {
    @Size(max = 263, message = "usernames must be no longer than 263 characters")
    @Pattern(regexp = "^\\w+( \\w+)*$", message = "usernames must contain only letters and numbers")
    @Elements(limit = 8)
    @Duplicates
    private String authorsString;
}
