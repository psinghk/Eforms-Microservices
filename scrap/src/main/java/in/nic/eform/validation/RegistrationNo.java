package in.nic.eform.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RegistrationNoValidator.class)
@Documented
public @interface RegistrationNo {

	String message() default "Invalid format";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
