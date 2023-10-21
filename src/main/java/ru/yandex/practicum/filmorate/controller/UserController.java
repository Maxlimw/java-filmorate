package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private Map<Integer, User> users = new HashMap<>();
    private int counter = 1;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validate(user);
        user.setId(counter);
        users.put(user.getId(), user);
        counter++;
        return user;
    }

    @PutMapping
    private User update(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь с id=" + user.getId() + " не найден!");
            throw new UserNotFoundException("Пользоватей с таким id не найден!");
        }
        validate(user);
        users.put(user.getId(), user);
        return user;
    }

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    private void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            return;
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