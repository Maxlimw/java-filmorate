package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();
    private int counter = 1;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        validate(film);
        film.setId(counter);
        films.put(film.getId(), film);
        counter++;
        return film;
    }

    @PutMapping
    private Film update(@Valid @RequestBody Film film) throws ValidationException, FilmNotFoundException {
        if (!films.containsKey(film.getId())) {
            log.warn("Фильм с id=" + film.getId() + " не найден!");
            throw new FilmNotFoundException("Фильм с таким id не найден!");
        }
        validate(film);
        films.put(film.getId(), film);
        return film;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
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

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(ValidationException e) {
        log.warn("Validation Exception: " + e.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return errorResponse;
    }

    @ExceptionHandler(FilmNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleFilmNotFoundException(FilmNotFoundException e) {
        log.warn("Film Not Found Exception: " + e.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return errorResponse;
    }
}