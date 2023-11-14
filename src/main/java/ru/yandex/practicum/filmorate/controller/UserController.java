package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User find(@PathVariable(name = "id") Long id) throws UserNotFoundException {
        return userService.find(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable(name = "id") Long id,
                           @PathVariable(name = "friendId") Long friendId) throws UserNotFoundException {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable(name = "id") Long id,
                              @PathVariable(name = "friendId") Long friendId) throws UserNotFoundException {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findFriends(@PathVariable(name = "id") Long id) throws UserNotFoundException {
        return userService.findFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    private List<User> findCommonFriends(@PathVariable("id") Long id,
                                         @PathVariable("otherId") Long otherId) throws UserNotFoundException {
        return userService.findCommonFriends(id, otherId);
    }
}