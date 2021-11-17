package net.pauljackals.springblog.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostAuthor {
    int idAuthor = -1;
    int idPost = -1;
}
