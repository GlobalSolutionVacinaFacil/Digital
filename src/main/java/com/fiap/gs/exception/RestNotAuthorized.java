package com.fiap.gs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class RestNotAuthorized extends RuntimeException{
    
    public RestNotAuthorized (String message){
        super(message);
    }
}