package net.pauljackals.springblog.domain.helpers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorExtras {
    private String passwordRepeat;
}
