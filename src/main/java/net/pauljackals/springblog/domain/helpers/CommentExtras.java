package net.pauljackals.springblog.domain.helpers;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentExtras {
    @NotNull(message = "username is required")
    @Pattern(regexp = "^\\w{1,32}$", message = "username must not be empty and no longer than 32 letters/numbers")
    private String username;
}
