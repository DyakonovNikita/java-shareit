package ru.practicum.shareit.exception;

public class AccessIsNotAllowedException extends RuntimeException {
    public AccessIsNotAllowedException(String message) {
        super(message);
    }
}
