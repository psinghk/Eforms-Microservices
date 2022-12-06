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
@Constraint(validatedBy = FinalIdVaidator.class)
@Documented
public @interface FinalIdValid {

	String message() default "Please enter valid Final ID";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}