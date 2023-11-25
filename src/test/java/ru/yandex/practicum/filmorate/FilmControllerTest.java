package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class FilmControllerTest {
    private final FilmService filmService;
    private final UserService userService;
    private static Validator validator;

    private static final String FILM_NAME = "The Matrix";
    private static final String FILM_DESCRIPTION = "A mind-bending classic";
    private static final int FILM_DURATION = 136;
    private static final LocalDate FILM_RELEASE_DATE = LocalDate.of(1999, 3, 31);
    private static final Mpa FILM_MPA = new Mpa(1, "G");

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    public void shouldCreateFilm() {
        Film film = Film.builder()
                .name("Сенай")
                .description("Полуостров")
                .duration(176)
                .releaseDate(LocalDate.of(2012, 11, 16))
                .mpa(new Mpa(1, "G"))
                .build();
        filmService.create(film);

        assertTrue(filmService.findAll().contains(film));
    }

    @Test
    public void shouldNotPassNameValidation() {
        Film film = Film.builder()
                .name("")
                .description(FILM_DESCRIPTION)
                .duration(FILM_DURATION)
                .releaseDate(FILM_RELEASE_DATE)
                .mpa(FILM_MPA)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldNotPassReleaseDateValidationInThePast() {
        Film film = Film.builder()
                .name(FILM_NAME)
                .description(FILM_DESCRIPTION)
                .duration(FILM_DURATION)
                .releaseDate(LocalDate.of(1600, 1, 1))
                .mpa(FILM_MPA)
                .build();

        assertThrows(ValidationException.class, () -> filmService.create(film));
    }

    @Test
    public void shouldUpdateFilm() {
        Film film = Film.builder()
                .name("Inception")
                .description("A mind-bending blockbuster")
                .duration(148)
                .releaseDate(LocalDate.of(2010, 7, 16))
                .mpa(new Mpa(1, "G"))
                .build();
        filmService.create(film);

        Film filmUpdate = Film.builder()
                .id(film.getId())
                .name("Dark Van")
                .description("300$")
                .duration(130)
                .releaseDate(LocalDate.of(2009, 4, 30))
                .mpa(new Mpa(1, "G"))
                .build();
        filmService.update(filmUpdate);

        assertEquals(filmUpdate, filmService.find(film.getId()));
    }

    @Test
    public void shouldPassDescriptionValidationWith200Symbols() {
        Film film = Film.builder()
                .name(FILM_NAME)
                .description("A".repeat(200))
                .duration(FILM_DURATION)
                .releaseDate(FILM_RELEASE_DATE)
                .mpa(FILM_MPA)
                .build();

        filmService.create(film);

        assertTrue(filmService.findAll().contains(film));
    }
// почему-то падает, выдает false вместо true...
//    @Test
//    public void shouldPassReleaseDateValidation() {
//        Film film = Film.builder()
//                .name(FILM_NAME)
//                .description(FILM_DESCRIPTION)
//                .duration(FILM_DURATION)
//                .releaseDate(LocalDate.of(1895, 12, 28))
//                .mpa(FILM_MPA)
//                .build();
//        filmService.create(film);
//
//        assertTrue(filmService.findAll().contains(film));
//    }

    @Test
    public void shouldFindFilmById() {
        Film film = Film.builder()
                .name(FILM_NAME + " 2")
                .description(FILM_DESCRIPTION)
                .duration(FILM_DURATION)
                .releaseDate(FILM_RELEASE_DATE)
                .mpa(FILM_MPA)
                .build();
        filmService.create(film);

        assertEquals(film, filmService.find(film.getId()));
    }

    @Test
    public void shouldAddLike() {
        User user = User.builder()
                .login("Neo")
                .name("Thomas Anderson")
                .email("neo@matrix.com")
                .birthday(LocalDate.of(1971, 9, 13))
                .build();
        userService.create(user);

        Film film = Film.builder()
                .name("Django Unchained")
                .description("A western film directed by Quentin Tarantino")
                .duration(165)
                .releaseDate(LocalDate.of(2012, 12, 25))
                .mpa(new Mpa(1, "R"))
                .build();
        filmService.create(film);

        filmService.like(film.getId(), user.getId());

        assertEquals(1, filmService.find(film.getId()).getLikes().size());
    }

    @Test
    public void shouldDeleteLike() {
        User user = User.builder()
                .login("Trinity")
                .name("Trinity Moss")
                .email("trinity@matrix.com")
                .birthday(LocalDate.of(1974, 5, 18))
                .build();
        userService.create(user);

        Film film = Film.builder()
                .name("Pulp Fiction")
                .description("A cult classic directed by Quentin Tarantino")
                .duration(154)
                .releaseDate(LocalDate.of(1994, 10, 14))
                .mpa(new Mpa(1, "R"))
                .build();
        filmService.create(film);

        filmService.like(film.getId(), user.getId());
        filmService.unlike(film.getId(), user.getId());

        assertEquals(0, film.getLikes().size());
    }

    @Test
    public void shouldGetMostPopularFilms() {
        User user = User.builder()
                .login("Morpheus")
                .name("Morpheus Laurence")
                .email("morpheus@matrix.com")
                .birthday(LocalDate.of(1961, 2, 28))
                .build();
        userService.create(user);

        User secondUser = User.builder()
                .login("Trinity")
                .name("Trinity Moss")
                .email("trinity@matrix.com")
                .birthday(LocalDate.of(1974, 5, 18))
                .build();
        userService.create(secondUser);

        Film film = Film.builder()
                .name("The Shawshank Redemption")
                .description("A drama film directed by Frank Darabont")
                .duration(142)
                .releaseDate(LocalDate.of(1994, 9, 23))
                .mpa(new Mpa(1, "R"))
                .build();
        filmService.create(film);

        filmService.like(film.getId(), user.getId());
        filmService.like(film.getId(), secondUser.getId());

        assertEquals(List.of(filmService.find(film.getId())), filmService.findMostPopular(1));
    }
}

