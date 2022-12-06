package in.nic.eForms.customValidation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy =TypeFromValidator.class)
@Documented
public @interface TypeFrom {

	String message() default "Enter Server Location [characters,dot(.) and whitespace]";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
	
	
}
