package in.nic.eForms.exceptions;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DobValidator.class)
@Documented
public @interface DobValid {

	String message() default "minimum age is 18 years and maximum age is 67 years.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}