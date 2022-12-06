package in.nic.eForms.customValidation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;	
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Repeatable(ConditionalsEmployeeCode.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { ConditionalEmployeeCodeValidator.class })
public @interface ConditionalEmployeeCode {

	String message() default "This field is required.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String selected();

	String[] required();

	String[] values();
}