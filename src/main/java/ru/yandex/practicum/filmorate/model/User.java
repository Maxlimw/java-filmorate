package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Long id;
    @NotNull
    @Email
    @NotBlank
    private String email;
    @NotNull
    @NotBlank
    private String login;
    private String name;
    @NotNull
    @PastOrPresent
    private LocalDate birthday;
    private Set<Long> friends;
    private Set<Long> likedFilms;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = new HashSet<>();
        this.likedFilms = new HashSet<>();
    }

    public void addFriend(Long friendId) {
        friends.add(friendId);
    }

    public void addLikedFilm(Long filmId) {
        likedFilms.add(filmId);
    }

    public void removeLikedFilm(Long filmId) {
        likedFilms.remove(filmId);
    }
}