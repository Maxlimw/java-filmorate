package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film find(@PathVariable(name = "id") Long id) throws FilmNotFoundException {
        return filmService.find(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable(name = "id") Long id,
                      @PathVariable(name = "userId") Long userId) throws FilmNotFoundException, UserNotFoundException {
        filmService.like(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlike(@PathVariable(name = "id") Long id,
                        @PathVariable(name = "userId") Long userId) throws FilmNotFoundException, UserNotFoundException {
        filmService.unlike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> findMostPopular(@RequestParam(name = "count", defaultValue = "10") int count) {
        return filmService.findMostPopular(count);
    }
}