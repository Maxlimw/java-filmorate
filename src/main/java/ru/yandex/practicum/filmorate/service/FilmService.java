package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film create(Film film) throws ValidationException {
        validate(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) throws FilmNotFoundException, ValidationException {
        if (!filmStorage.getAll().containsKey(film.getId())) {
            log.warn("Фильм с id = " + film.getId() + " не найден!");
            throw new FilmNotFoundException("Фильм с id = " + film.getId() + " не найден!");
        }
        validate(film);
        return filmStorage.update(film);
    }

    public Film find(Long id) throws FilmNotFoundException {
        return filmStorage.get(id);
    }

    public Collection<Film> findAll() {
        return filmStorage.getAll().values();
    }

    public void like(Long id, Long userId) throws FilmNotFoundException, UserNotFoundException {
        Film film = find(id);
        User user = userService.find(userId);

        if (!user.getLikedFilms().contains(id)) {
            film.setRate(film.getRate() + 1);
        }
        user.addLikedFilm(id);
    }

    public void unlike(Long id, Long userId) throws FilmNotFoundException, UserNotFoundException {
        Film film = find(id);
        User user = userService.find(userId);

        if (user.getLikedFilms().contains(id)) {
            film.setRate(film.getRate() - 1);
        }
        user.removeLikedFilm(id);
    }

    public List<Film> findMostPopular(int count) {
        return findAll().stream()
                .sorted(Comparator.comparingInt(Film::getRate).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validate(Film film) throws ValidationException {
        if (film.getDescription().length() > 200) {
            log.warn("Превышено максимальное кол-во символов в описании!");
            throw new ValidationException("Превышено максимальное кол-во символов в описании!");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.warn("Указанная дата релиза раньше дня рождения кино!");
            throw new ValidationException("Указанная дата релиза раньше дня рождения кино!");
        }
        if (film.getDuration() <= 0) {
            log.warn("Продолжительность не может быть отрицательной!");
            throw new ValidationException("Продолжительность не может быть отрицательной!");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Название не может быть пустым!");
            throw new ValidationException("Название не может быть пустым!");
        }
    }
}
