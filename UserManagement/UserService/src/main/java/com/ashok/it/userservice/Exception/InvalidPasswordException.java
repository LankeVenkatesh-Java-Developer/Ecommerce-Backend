package com.ashok.it.userservice.Exception;

public class InvalidPasswordException  extends RuntimeException{
    public InvalidPasswordException(String message) {
        super(message);
    }
}
