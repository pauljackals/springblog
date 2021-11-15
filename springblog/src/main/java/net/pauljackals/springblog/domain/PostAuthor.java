package net.pauljackals.springblog.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostAuthor {
    int idAuthorCSV = -1;
    int idPostCSV = -1;
}
