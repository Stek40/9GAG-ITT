package com.example.springproject.exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;


public class NotFoundException extends RuntimeException{

    public NotFoundException(String msg){
      super(msg);
    }
}
