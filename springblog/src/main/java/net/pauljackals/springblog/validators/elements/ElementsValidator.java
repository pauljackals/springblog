package net.pauljackals.springblog.validators.elements;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
 
public class ElementsValidator implements ConstraintValidator<Elements, String> {
    private int limit;

    public void initialize(Elements constraint) {
        limit = constraint.limit();
    }
 
    @Override
    public boolean isValid(String stringToSplit, ConstraintValidatorContext constraintValidatorContext) {
        if(stringToSplit==null) {
            return true;
        }
        return stringToSplit.split(" ").length <= limit;
    }
}