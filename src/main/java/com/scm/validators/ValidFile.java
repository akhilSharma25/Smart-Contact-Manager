package com.scm.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import lombok.ToString;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileValidator.class)
public @interface ValidFile {
    String message() default  "Invalid file";
    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default {};

}
