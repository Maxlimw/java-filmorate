package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private long nextId = 1;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("filmDb") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film create(Film film) throws ValidationException {
        validate(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) throws FilmNotFoundException, ValidationException {
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
        filmStorage.addLike(id, userId);
    }

    public void unlike(Long id, Long userId) throws FilmNotFoundException, UserNotFoundException {
        filmStorage.deleteLike(id, userId);
    }

    public List<Film> findMostPopular(int count) {
        return filmStorage.getAll().values().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    private long getNextId() {
        return nextId++;
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
