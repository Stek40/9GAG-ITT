package com.example.springproject.controller;

import com.example.springproject.dto.ErrorDto;
import com.example.springproject.exceptions.BadRequestException;
import com.example.springproject.exceptions.DateTimeParseException;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.exceptions.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto handlerBadRequestException (Exception e){
       ErrorDto dto = new ErrorDto();
       dto.setMsg(e.getMessage());
       dto.setStatus(HttpStatus.BAD_REQUEST.value());
       return dto;
    }

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDto handlerNotFoundException(Exception e){
        ErrorDto dto = new ErrorDto();
        dto.setMsg(e.getMessage());
        dto.setStatus(HttpStatus.NOT_FOUND.value());
        return dto;
    }
    @ExceptionHandler(value = UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorDto unauthorizedException(Exception e){
        ErrorDto dto = new ErrorDto();
        dto.setMsg(e.getMessage());
        dto.setStatus(HttpStatus.UNAUTHORIZED.value());
        return dto;
    }
    @ExceptionHandler
    @ResponseStatus
    @ResponseBody
    public ErrorDto allException(Exception e){
        ErrorDto dto = new ErrorDto();
        e.printStackTrace();
        dto.setMsg(e.getMessage());
        dto.setStatus(HttpStatus.UNAUTHORIZED.value());
        return dto;
    }
    @ExceptionHandler(value = DateTimeParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto handlerDateException (Exception e){
        ErrorDto dto = new ErrorDto();
        dto.setMsg(e.getMessage());
        dto.setStatus(HttpStatus.BAD_REQUEST.value());
        return dto;
    }
    @ExceptionHandler(value = HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorDto handlerForbiddenException (Exception e){
        ErrorDto dto = new ErrorDto();
        dto.setMsg(e.getMessage());
        dto.setStatus(HttpStatus.FORBIDDEN.value());
        return dto;
    }


}
