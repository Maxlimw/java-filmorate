package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Long, Film> films = new HashMap<>();
    private Long counter = 1L;

    @Override
    public Film create(Film film) {
        film.setId(counter);
        films.put(film.getId(), film);
        counter++;
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Фильм с таким id " + film.getId() + " не найден!");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Map<Long, Film> getAll() {
        return films;
    }

    @Override
    public Film get(Long id) throws FilmNotFoundException {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Фильм с id = " + id + " не найден!");
        }
        return films.get(id);
    }
}
