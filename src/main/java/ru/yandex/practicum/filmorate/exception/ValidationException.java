package ru.yandex.practicum.filmorate.exception;

public class ValidationException extends Exception {
    public ValidationException(String massage) {
        super(massage);
    }
}
