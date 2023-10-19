package ru.yandex.practicum.filmorate.controller;

public class FilmNotFoundException extends Exception {
    public FilmNotFoundException(String msg) {
        super(msg);
    }
}