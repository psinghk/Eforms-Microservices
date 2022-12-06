package in.nic.eForms.exceptions;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DorValidator.class)
@Documented
public @interface DorValid {

	String message() default "year of retirement can not exceed 60 years from the DOB year.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}