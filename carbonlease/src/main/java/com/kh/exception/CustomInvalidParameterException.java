package com.kh.exception;

public class CustomInvalidParameterException extends RuntimeException {
    public CustomInvalidParameterException(String message) {
        super(message);
    }

}
