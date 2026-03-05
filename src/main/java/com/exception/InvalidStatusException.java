package com.exception;

// Exception cho trường hợp status không hợp lệ
public class InvalidStatusException extends RuntimeException {
    public InvalidStatusException(String message) { super(message); }
    public InvalidStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
