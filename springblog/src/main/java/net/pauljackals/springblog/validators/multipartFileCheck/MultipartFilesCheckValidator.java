package net.pauljackals.springblog.validators.multipartFileCheck;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;
 
public class MultipartFilesCheckValidator implements ConstraintValidator<MultipartFilesCheck, List<MultipartFile>> {

    public void initialize(MultipartFilesCheck constraint) {
    }
 
    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext constraintValidatorContext) {
        if(files==null || files.size()==0 || files.size()==1 && files.get(0).getOriginalFilename().length()==0) {
            return true;
        }
        String message = null;
        List<String> empty = new ArrayList<>();
        List<String> tooLong = new ArrayList<>();
        for (MultipartFile file : files) {
            String name = file.getOriginalFilename();
            if(name.length()>128) {
                tooLong.add(name);
            } else if(file.isEmpty()) {
                empty.add(name);
            }
        }
        if(tooLong.size()>0) {
            message = "some files have names longer than 128 characters";

        } else if(empty.size()>0) {
            message = "empty: " + String.join(", ", empty);
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