package com.ashok.it.userservice.Exception;

public class AddressNotFoundException extends RuntimeException{
    public AddressNotFoundException(String message) {
        super(message);
    }
}
