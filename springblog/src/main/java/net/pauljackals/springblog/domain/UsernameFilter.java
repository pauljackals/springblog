package net.pauljackals.springblog.domain;

import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsernameFilter {
    @Size(max = 32, message = "usernames are not longer than 32 characters")
    String username;
}
