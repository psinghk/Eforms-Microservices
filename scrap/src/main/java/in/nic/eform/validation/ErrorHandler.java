package in.nic.eform.validation;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path.Node;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//@RestControllerAdvice
public class ErrorHandler {
	@ExceptionHandler(ConstraintViolationException.class)
	  //@ResponseStatus(HttpStatus.BAD_REQUEST)
	  public ErrorResponse onConstraintValidationException(
	      ConstraintViolationException e) {
		System.out.println("#######ErrorHandler###########");
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

	  @ExceptionHandler(MethodArgumentNotValidException.class)
	  @ResponseStatus(HttpStatus.BAD_REQUEST)
	  ErrorResponse onMethodArgumentNotValidException(
	      MethodArgumentNotValidException e) {
	    ErrorResponse error = new ErrorResponse();
	    for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
	      error.getErrors().add(
	        new Error(fieldError.getField(), fieldError.getDefaultMessage()));
	    }
	    return error;
	  }
	  
	 
}
