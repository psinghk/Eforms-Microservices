package in.nic.ashwini.ldap.validation;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path.Node;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<?> onConstraintValidationException(ConstraintViolationException e) {
		ErrorResponse error = new ErrorResponse();
		for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
			String field = null;
			for (Node node : violation.getPropertyPath()) {
				field = node.getName();
			}
			error.getErros().add(new Error(field, violation.getMessage()));
		}
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<?> onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		ErrorResponse error = new ErrorResponse();
		for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
			error.getErros().add(new Error(fieldError.getField(), fieldError.getDefaultMessage()));
		}
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	ResponseEntity<?> onMissingServletRequestParameterException(MissingServletRequestParameterException e) {
		ErrorResponse error = new ErrorResponse();
		error.getErros().add(new Error(e.getParameterName(), "Missing required parameter"));
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(CustomException.class)
	ResponseEntity<?> exception(CustomException e) {
		ErrorResponse error = new ErrorResponse();
		error.getErros().add(new Error(e.getAttribute(), e.getMessage()));
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
}
