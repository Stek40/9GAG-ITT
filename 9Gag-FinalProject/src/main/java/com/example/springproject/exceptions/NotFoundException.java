package com.example.springproject.exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.function.Supplier;


public class NotFoundException extends RuntimeException  {

    public NotFoundException(String msg){
      super(msg);
    }
}
