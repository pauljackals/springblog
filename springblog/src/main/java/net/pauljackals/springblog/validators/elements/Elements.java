package net.pauljackals.springblog.validators.elements;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ElementsValidator.class)
public @interface Elements {
    String message() default "elements limit is {limit}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int limit();
}