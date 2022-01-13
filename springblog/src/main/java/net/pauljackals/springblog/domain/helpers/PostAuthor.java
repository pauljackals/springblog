package net.pauljackals.springblog.domain.helpers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostAuthor {
    int idAuthor = -1;
    int idPost = -1;
}
