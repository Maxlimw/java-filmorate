package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    User create(User user);

    User update(User user);

    Map<Long, User> findAll();

    User getById(Long id) throws UserNotFoundException;

    void addFriend(long userId, long friendId);

    void removeFromFriends(long userId, long friendId);

    List<User> getMutualFriends(long userId, long otherUserId);

    List<User> getAllFriends(long userId);
}
