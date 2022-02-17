package net.pauljackals.springblog.validators.duplicates;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;
 
public class DuplicatesValidator implements ConstraintValidator<Duplicates, String> {
    public void initialize(Duplicates constraint) {
    }
 
    @Override
    public boolean isValid(String stringToSplit, ConstraintValidatorContext constraintValidatorContext) {
        if(stringToSplit==null) {
            return true;
        }
        try {
            Set.of(stringToSplit.split(" "));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}