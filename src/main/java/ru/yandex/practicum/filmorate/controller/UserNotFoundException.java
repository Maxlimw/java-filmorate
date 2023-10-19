package ru.yandex.practicum.filmorate.controller;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String msg) {
        super(msg);
    }
}