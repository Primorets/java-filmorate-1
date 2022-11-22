package ru.yandex.practicum.filmorate.model.exception;

public class FilmsAndUsersValidationException extends IllegalArgumentException {

    public FilmsAndUsersValidationException(String message) {
        super(message);
    }
}
