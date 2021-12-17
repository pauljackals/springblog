package net.pauljackals.springblog.domain.helpers;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.pauljackals.springblog.validators.duplicates.Duplicates;
import net.pauljackals.springblog.validators.multipartFileCheck.MultipartFilesCheck;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostExtras {
    @NotNull(message = "authors are required")
    @Pattern(regexp = "^\\w{1,32}( \\w{1,32}){0,7}$", message = "there must be no more than 8 usernames, each not empty and not longer than 32 letters/numbers")
    @Duplicates
    private String authorsString;

    @Size(max = 8, message = "there must be no more than {max} attachments")
    @MultipartFilesCheck
    private List<MultipartFile> attachmentsFiles;

    // private List<Boolean> attachmentsUploaded;
}
