package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Map;

public interface GenreStorage {
    Map<Integer, Genre> getAllGenres();

    Genre findGenreById(Integer id);
}
