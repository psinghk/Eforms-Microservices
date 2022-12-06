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
@Constraint(validatedBy = IpValidator.class)
@Documented
public @interface Ip {

	String message() default "Please enter valid IPV4/IPV6 address";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}