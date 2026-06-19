package com.yuldashov.spring.expection;

public class DublicateException extends RuntimeException {
    public DublicateException(String message) {
        super(message);
    }
}
