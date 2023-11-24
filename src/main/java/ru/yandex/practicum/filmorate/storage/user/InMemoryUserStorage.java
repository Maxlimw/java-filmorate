package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;

@Slf4j
@Component("inMemoryUserStorage")
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
    public User getById(Long id) throws UserNotFoundException {
        User user = users.get(id);
        if (user == null) {
            log.warn("Пользователь с id = " + id + " не найден!");
            throw new UserNotFoundException("Пользователь с id = " + id + " не найден!");
        }
        return user;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        User user = getById(userId);
        User friend = getById(friendId);
        if (user != null && friend != null) {
            user.getFriends().add(friendId);
            friend.getFriends().add(userId);
        }
    }

    @Override
    public void removeFromFriends(long userId, long friendId) {
        User user = getById(userId);
        User friend = getById(friendId);
        if (user != null && friend != null) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId);
        }
    }

    @Override
    public List<User> getMutualFriends(long userId, long otherUserId) {
        List<User> mutualFriends = new ArrayList<>();
        User user = getById(userId);
        User otherUser = getById(otherUserId);
        if (user != null && otherUser != null) {
            Set<Long> mutualFriendsIds = new HashSet<>(user.getFriends());
            mutualFriendsIds.retainAll(otherUser.getFriends());
            for (Long id : mutualFriendsIds) {
                mutualFriends.add(getById(id));
            }
        }
        return mutualFriends;
    }

    @Override
    public List<User> getAllFriends(long userId) {
        List<User> friends = new ArrayList<>();
        User user = getById(userId);
        if (user != null) {
            for (Long id : user.getFriends()) {
                friends.add(getById(id));
            }
        }
        return friends;
    }
}