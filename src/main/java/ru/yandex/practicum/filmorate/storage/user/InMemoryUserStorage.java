package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> users = new HashMap<>();
    private Long counter = 1L;

    @Override
    public User create(User user) {
        user.setId(counter);
        users.put(user.getId(), user);
        counter++;
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователь с id " + user.getId() + " не найден!");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Map<Long, User> findAll() {
        return users;
    }

    @Override
    public User get(Long id) throws UserNotFoundException {
        User user = users.get(id);
        if (user == null) {
            log.warn("Пользователь с id = " + id + " не найден!");
            throw new UserNotFoundException("Пользователь с id = " + id + " не найден!");
        }
        return user;
    }

}
