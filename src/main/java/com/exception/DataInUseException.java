package com.exception;

// Exception cho trường hợp dữ liệu đang được tham chiếu
public class DataInUseException extends RuntimeException {
    public DataInUseException(String message) { super(message); }
    public DataInUseException(String message, Throwable cause) {
        super(message, cause);
    }
}
