package com.example.demo.attendance.exception;

public class AlreadyClockedInException extends RuntimeException {
    public AlreadyClockedInException(String message) {
        super(message);
    }
}