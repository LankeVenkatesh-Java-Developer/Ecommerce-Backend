package com.ashok.it.userservice.Exception;

public class DuplicateUserException  extends RuntimeException{
    public DuplicateUserException(String message) {
        super(message);
    }
}
