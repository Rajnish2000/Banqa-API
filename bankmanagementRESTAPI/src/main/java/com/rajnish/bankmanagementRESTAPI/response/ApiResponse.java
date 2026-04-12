package com.rajnish.bankmanagementRESTAPI.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ApiResponse<T> {
    private HttpStatus statusCode;
    private boolean success;
    private T data;
    private LocalDateTime timestamp;

    public ApiResponse(){
        this.timestamp = LocalDateTime.now();
        this.success = true;
    }
    public ApiResponse(T data){
        this();
        this.data = data;
    }
}
