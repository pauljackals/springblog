package net.pauljackals.springblog.validators.multipartFileCheck;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;
 
public class MultipartFileCheckValidator implements ConstraintValidator<MultipartFileCheck, List<MultipartFile>> {

    public void initialize(MultipartFileCheck constraint) {
    }
 
    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext constraintValidatorContext) {
        int filesSize = files.size();
        if(filesSize==0 || filesSize==1 && files.get(0).getOriginalFilename().length()==0) {
            return true;
        }
        String message = null;
        List<String> empty = new ArrayList<>();
        for (MultipartFile file : files) {
            if(file.getSize()==0) {
                empty.add(file.getOriginalFilename());
            }
        }
        if(empty.size()>0) {
            message = String.join(", ", empty) + " empty";

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