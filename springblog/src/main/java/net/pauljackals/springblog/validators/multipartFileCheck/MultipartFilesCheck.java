package net.pauljackals.springblog.validators.multipartFileCheck;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MultipartFilesCheckValidator.class)
public @interface MultipartFilesCheck {
    String message() default "files are not valid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}