package net.pauljackals.springblog.validators.stateFilesCheck;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;
 
public class StateFilesCheckValidator implements ConstraintValidator<StateFilesCheck, List<MultipartFile>> {
    private String[] filenames;

    public void initialize(StateFilesCheck constraint) {
        filenames = constraint.filenames();
    }
 
    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext constraintValidatorContext) {
        String message = null;
        int filenamesLength = filenames.length;
        if(files.size()!=filenamesLength) {
            message = String.format("there must be %d files", filenamesLength);

        } else {
            List<String> missing = new ArrayList<>();
            for (String filename : filenames) {
                if(files.stream().noneMatch(file -> filename.equals(file.getOriginalFilename()))) {
                    missing.add(filename);
                }
            }
            if(missing.size()>0) {
                message = String.format("%s missing", String.join(", ", missing));
            }
        }
        if(message!=null) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
            return false;

        } else {
            return true;
        }
    }
}