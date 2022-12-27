package ru.yandex.practicum.filmorate.exception;

public class ListIsEmptyException extends RuntimeException {

    public ListIsEmptyException(String message) {
        super(message);
    }
}
