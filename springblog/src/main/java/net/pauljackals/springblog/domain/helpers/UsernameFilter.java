package net.pauljackals.springblog.domain.helpers;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsernameFilter {
    @Size(max = 32, message = "usernames are not longer than 32 characters")
    @Pattern(regexp = "^|\\w+$", message = "usernames contain only letters and numbers")
    String username;
}
