package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ru.yandex.practicum.filmorate.controller.ErrorHandler;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import java.time.LocalDate;


@WebMvcTest
@AutoConfigureWebMvc
@ContextConfiguration(classes = {FilmController.class, FilmService.class, InMemoryFilmStorage.class, UserService.class,
        InMemoryUserStorage.class, ErrorHandler.class,})
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long counter = 1L;

    @Autowired
    private FilmService filmService;

    static class TestConfig {
        @Bean
        public FilmService filmService() {
            return new FilmService(new InMemoryFilmStorage(), new UserService(new InMemoryUserStorage()));
        }
    }

    @Test
    public void createFilmPositive() {
        Film film = new Film("Смешарики в стране ОЗ", "Спин-офф", LocalDate.of(2000, 11, 11), 100,0);
        try {
            mockMvc.perform(
                            post("/films")
                                    .content(objectMapper.writeValueAsString(film))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createFilmNegativeTest() throws Exception {
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString("{}"))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFilmInvalidDescriptionLengthTest() throws Exception {
        Film film = new Film("Пипины дочки", "a".repeat(201), LocalDate.of(2000, 11, 11), 90,0);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createFilmNegativeInvalidReleaseDateTest() throws Exception {
        Film film = new Film("Сырок Александров", "Вкусно", LocalDate.of(1890, 1, 1), 90,0);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void createFilmNegativeNegativeDurationTest() throws Exception {
        Film film = new Film("Ernest Merkel", "Pi-pi", LocalDate.of(2022, 1, 1), -30,0);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createFilmNegativeEmptyName() throws Exception {
        Film film = new Film("", "Billy Herrington RIP", LocalDate.of(2022, 1, 1), 90,0);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}