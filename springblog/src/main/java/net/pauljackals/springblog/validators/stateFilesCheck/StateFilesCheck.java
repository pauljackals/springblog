package net.pauljackals.springblog.validators.stateFilesCheck;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StateFilesCheckValidator.class)
public @interface StateFilesCheck {
    String message() default "state files are invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String[] filenames();
}