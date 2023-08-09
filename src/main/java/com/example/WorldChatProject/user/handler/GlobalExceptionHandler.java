package com.example.WorldChatProject.user.handler;

import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.ServletException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenExpiredException.class)
    public @ResponseBody ResponseEntity<String> handleTokenExpiredException(TokenExpiredException e) {
        System.out.println("Error: " + e.getMessage());
        return new ResponseEntity<>("Error: Token is expired", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({IOException.class, ServletException.class})
    public @ResponseBody ResponseEntity<String> handleIOExceptionAndServletException(Exception e) {
        System.out.println("Error: " + e.getMessage());
        return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}