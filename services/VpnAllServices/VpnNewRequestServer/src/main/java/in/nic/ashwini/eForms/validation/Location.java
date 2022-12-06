package in.nic.ashwini.eForms.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy =LocationValidator.class)
@Documented
public @interface Location {

	String message() default "Enter Server Location is not Correct";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
	
	
}
