package net.pauljackals.springblog.domain.helpers;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.pauljackals.springblog.validators.multipartFileCheck.MultipartFileCheck;
import net.pauljackals.springblog.validators.stateFilesCheck.StateFilesCheck;

@Data
@NoArgsConstructor
public class StateFiles {
    @MultipartFileCheck
    @StateFilesCheck(filenames = {
        "Posts.csv",
        "Authors.csv",
        "Posts_Authors.csv",
        "Comments.csv",
        "Attachments.csv"
    })
    private List< MultipartFile> files;
}
