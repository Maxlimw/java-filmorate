package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDb") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) throws ValidationException {
        validate(user);
        return userStorage.create(user);
    }

    public User update(User user) throws UserNotFoundException, ValidationException {
        validate(user);
        userStorage.update(user);
        return user;
    }

    public Collection<User> findAll() {
        return userStorage.findAll().values();
    }

    public User find(Long id) throws UserNotFoundException {
        return userStorage.getById(id);
    }

    public void addFriend(Long id, Long friendId) throws UserNotFoundException {
        userStorage.addFriend(id, friendId);
    }

    public void removeFriend(Long id, Long friendId) throws UserNotFoundException {
        userStorage.removeFromFriends(id, friendId);
    }

    public List<User> findFriends(Long id) throws UserNotFoundException {
        return userStorage.getAllFriends(id);
    }

    public List<User> findCommonFriends(Long id, Long otherId) throws UserNotFoundException {
        return userStorage.getMutualFriends(id, otherId);
    }

    private void validate(User user) throws ValidationException {
        if (user.getLogin().contains(" ")) {
            log.warn("Неверный формат login! Поле не должно содержать пробелы!");
            throw new ValidationException("Неверный формат login! Поле не должно содержать пробелы!");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getEmail().isEmpty()) {
            log.warn("Электронная почта не может быть пустой!");
            throw new ValidationException("Электронная почта не может быть пустой!");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Электронная почта должна содержать символ @");
            throw new ValidationException("Электронная почта должна содержать символ @");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getLogin().contains(" ") || user.getLogin().isEmpty()) {
            log.warn("Логин не может содержать пробелы!");
            throw new ValidationException("Логин не может содержать пробелы!");
        }
    }
}
