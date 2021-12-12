package net.pauljackals.springblog.validators.authors;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AuthorsValidator.class)
public @interface Authors {
 
    String message() default "usernames must not be duplicated";
 
    Class<?>[] groups() default {};
 
    Class<? extends Payload>[] payload() default {};
 
}