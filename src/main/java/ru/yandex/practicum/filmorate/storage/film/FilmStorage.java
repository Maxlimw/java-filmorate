package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.Map;


public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Map<Long, Film> getAll();

    Film get(Long id) throws FilmNotFoundException;

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);
}
