package in.nic.ashwini.eForms.exceptions;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateValidator.class)
@Documented
public @interface IsWithinAMonth {

	String message() default "Migration date must be within 1 month.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}