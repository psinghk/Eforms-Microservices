package in.nic.ashwini.eForms.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CustomErrorController implements ErrorController {
 
    @Value("${error.path:/error}")
    private String errorPath;
     
    @Override
    public String getErrorPath() {
        return errorPath;
    }
 
    @GetMapping(value = "${error.path:/error}")
    public @ResponseBody ResponseEntity<ErrorResponseBean> error(HttpServletRequest request) {
        final int status = getErrorStatus(request);
        String errorMessage = getErrorMessage(request);
        if(errorMessage.isEmpty()) {
        	errorMessage = (String)request.getAttribute("javax.servlet.error.message");
        }
        ErrorResponseBean errorResponse = new ErrorResponseBean();
        errorResponse.setErrorCode(status);
        errorResponse.setReason(errorMessage);
        return ResponseEntity.status(status).body(errorResponse);
    }
 
    private int getErrorStatus(HttpServletRequest request) {
        Integer statusCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
        return statusCode != null ? statusCode : HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
 
    private String getErrorMessage(HttpServletRequest request) {
        final Throwable exc = (Throwable) request.getAttribute("javax.servlet.error.exception");
        return exc != null ? exc.getMessage() : "Unexpected error occurred";
    }
}