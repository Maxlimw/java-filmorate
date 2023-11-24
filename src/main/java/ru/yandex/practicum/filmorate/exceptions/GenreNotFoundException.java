package ru.yandex.practicum.filmorate.exceptions;

public class GenreNotFoundException extends RuntimeException {
    public GenreNotFoundException(String msg) {
        super(msg);
    }

}
