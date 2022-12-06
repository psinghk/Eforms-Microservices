package in.nic.ashwini.eForms.exceptions;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequestForValidator.class)
@Documented
public @interface RequestForValid {
	
	// String message() default "{error.address}";

	String message() default "Only valid IPs are allowed. And, for production server, Please upload the relavant file.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}