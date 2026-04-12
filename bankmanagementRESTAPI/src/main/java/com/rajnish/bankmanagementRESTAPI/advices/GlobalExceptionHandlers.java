package com.rajnish.bankmanagementRESTAPI.advices;

import com.rajnish.bankmanagementRESTAPI.response.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandlers {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNameNotFoundException(UsernameNotFoundException ex){
        ApiError apiError = new ApiError("user email not found: "+ex.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex){
        ApiError apiError = new ApiError("something went wrong : "+ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.ok(apiError);
    }
}
