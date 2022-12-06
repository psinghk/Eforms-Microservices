package in.nic.eform.Profile.exception;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path.Node;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import in.nic.eform.Profile.exception.custom.CustomException;

@RestControllerAdvice
public class ErrorHandler {
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse onConstraintValidationException(ConstraintViolationException e) {
		ErrorResponse error = new ErrorResponse();
		for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
			String field = null;
			for (Node node : violation.getPropertyPath()) {
				field = node.getName();
			}
			error.getErros().add(new Error(field, violation.getMessage()));
		}
		return error;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		ErrorResponse error = new ErrorResponse();
		for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
			error.getErros().add(new Error(fieldError.getField(), fieldError.getDefaultMessage()));
		}
		return error;
	}
	
	@ExceptionHandler(HttpClientErrorException.class)
	ErrorResponse onHttpRequestInvalidException(HttpClientErrorException e) {
		ErrorResponse error = new ErrorResponse();
		error.erros.add(new Error("", e.getResponseBodyAsString()));
		
		return error;
	}

	@ExceptionHandler(CustomException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ErrorResponse exception(CustomException e) {
		ErrorResponse error = new ErrorResponse();
		error.getErros().add(new Error("", e.getMessage()));
		return error;
	}
	

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ErrorResponse globalException(Exception e) {
		ErrorResponse error = new ErrorResponse();
		error.getErros().add(new Error("Username", e.getMessage()));
		return error;
	}
}
