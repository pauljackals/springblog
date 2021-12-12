package net.pauljackals.springblog.validators.authors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;
 
public class AuthorsValidator implements ConstraintValidator<Authors, String> {
   public void initialize(Authors constraint) {
   }
 
    @Override
    public boolean isValid(String authors, ConstraintValidatorContext constraintValidatorContext) {
        if(authors==null) {
            return true;
        }
        try {
            Set.of(authors.split(" "));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}