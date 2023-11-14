package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {
    User create(User user);

    User update(User user);

    Map<Long, User> findAll();

    User get(Long id) throws UserNotFoundException;
}
