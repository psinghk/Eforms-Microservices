package in.nic.eform.validations;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path.Node;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {
	@ExceptionHandler(ConstraintViolationException.class)

	  public ErrorResponse onConstraintValidationException(
		      ConstraintViolationException e) {
		System.out.println("Entering Error Advice");
		    ErrorResponse error = new ErrorResponse();
		    for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
		    	String field = null;
		    	for (Node node : violation.getPropertyPath()) {
		    	    field = node.getName();
		    	}
		      error.getErrors().add(
		  	        new Error(field, violation.getMessage()));
		    }
		    return error;
		  }

}
