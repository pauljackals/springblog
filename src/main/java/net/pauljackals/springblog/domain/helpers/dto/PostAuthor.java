package net.pauljackals.springblog.domain.helpers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostAuthor {
    Long idAuthor;
    Long idPost;
}
