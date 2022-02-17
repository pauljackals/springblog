package net.pauljackals.springblog.validators.duplicates;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DuplicatesValidator.class)
public @interface Duplicates {
    String message() default "field must not contain duplicates";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}