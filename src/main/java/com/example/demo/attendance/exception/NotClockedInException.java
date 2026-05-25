package com.example.demo.attendance.exception;

public class NotClockedInException extends RuntimeException {
    public NotClockedInException(String message) {
        super(message);
    }
}