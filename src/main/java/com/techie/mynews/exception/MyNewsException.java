package com.techie.mynews.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatusCode;
@Getter
@Setter
public class MyNewsException extends RuntimeException{
    private HttpStatusCode statusCode;
    private String message;

    public MyNewsException(HttpStatusCode statusCode,String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

}
