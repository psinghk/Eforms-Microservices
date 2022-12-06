package in.nic.eForms.customValidation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UidValidator.class)
@Documented
public @interface Uid {

	String message() default "Please enter valid Uid";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}