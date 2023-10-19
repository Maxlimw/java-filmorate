package ru.yandex.practicum.filmorate.exceptions;

public class FilmNotFoundException extends Exception {
    public FilmNotFoundException(String msg) {
        super(msg);
    }
}