package in.nic.ashwini.eForms.custumvalidation;

import java.lang.annotation.Documented;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CityValidator.class)
@Documented
public @interface CityValid {

	String message() default "Please enter valid owner city";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
