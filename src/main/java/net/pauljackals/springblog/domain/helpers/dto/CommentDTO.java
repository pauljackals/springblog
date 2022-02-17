package net.pauljackals.springblog.domain.helpers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    Long id;
    String username;
    Long idPost;
    String commentContent;
}
