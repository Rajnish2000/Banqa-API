package com.rajnish.bankmanagementRESTAPI.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ApiError {
    private HttpStatus statusCode;
    private boolean success;
    private String error;
    private LocalDateTime timestamp;

    public ApiError(){this.timestamp = LocalDateTime.now();}

    public ApiError(String error, HttpStatus statusCode){
        this();
        this.error = error;
        this.statusCode = statusCode;
        this.success = false;
    }
}
