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
@Constraint(validatedBy = DomainValidator.class)
@Documented
public @interface DomainValid {

	String message() default "Please enter valid Domain";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}