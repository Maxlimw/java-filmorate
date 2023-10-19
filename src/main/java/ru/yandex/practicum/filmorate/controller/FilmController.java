package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
    private int counter;
    @PostMapping
    private Film create(@Valid @RequestBody Film film) throws ValidationException {
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
    private Collection<Film> findAll() {
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
    }
}