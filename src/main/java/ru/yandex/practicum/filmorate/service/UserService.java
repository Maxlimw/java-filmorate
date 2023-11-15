package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public UserService(UserStorage userStorage) {
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
        return userStorage.get(id);
    }

    public void addFriend(Long id, Long friendId) throws UserNotFoundException {
        User user = find(id);
        User friend = find(friendId);

        user.addFriend(friendId);
        friend.addFriend(id);
    }

    public void removeFriend(Long id, Long friendId) throws UserNotFoundException {
        find(id).getFriends().remove(friendId);
        find(id).getFriends().remove(id);
    }

    public List<User> findFriends(Long id) throws UserNotFoundException {
        List<User> friends = new ArrayList<>();

        for (Long friendId : find(id).getFriends()) {
            friends.add(find(friendId));
        }

        return friends;
    }

    public List<User> findCommonFriends(Long id, Long otherId) throws UserNotFoundException {
        Set<User> firstUserFriends = new HashSet<>(findFriends(id));
        Set<User> secondUserFriends = new HashSet<>(findFriends(otherId));

        firstUserFriends.retainAll(secondUserFriends);

        return new ArrayList<>(firstUserFriends);
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
