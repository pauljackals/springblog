package net.pauljackals.springblog.validators.multipartFileCheck;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MultipartFileCheckValidator.class)
public @interface MultipartFileCheck {
    String message() default "file is not valid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}